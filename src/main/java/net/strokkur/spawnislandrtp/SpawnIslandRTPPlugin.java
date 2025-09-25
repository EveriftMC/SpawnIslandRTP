package net.strokkur.spawnislandrtp;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_ERROR_REQUEST_REASON;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_PlayerInfo;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP_Check;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public final class SpawnIslandRTPPlugin extends JavaPlugin implements Listener {

  private int minY = 0;
  private @Nullable Key worldKey;
  private @Nullable World world;

  @Override
  public void onLoad() {
    this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(
        event -> event.registrar().register(Commands.literal("spawnislandrtp")
            .requires(stack -> stack.getSender().hasPermission("spawnislandrtp.command"))
            .then(Commands.literal("reload")
                .executes(ctx -> {
                  reload();
                  ctx.getSource().getSender().sendRichMessage("<green>Successfully reloaded SpawnIslandRTP.");
                  return Command.SINGLE_SUCCESS;
                })
            )
            .build()
        )
    ));
  }

  @Override
  public void onEnable() {
    this.getServer().getPluginManager().registerEvents(this, this);
    this.reload();
  }

  void reload() {
    this.saveDefaultConfig();
    minY = this.getConfig().getInt("min-y");

    final @Subst("key:value") String worldName = this.getConfig().getString("world");
    if (!Key.parseable(worldName)) {
      this.getSLF4JLogger().warn("World '{}' is in the invalid format! Expected a key.", worldName);
      this.worldKey = null;
      if (this.world != null) {
        this.getSLF4JLogger().warn("Defaulting to the previously declared world '{}'.", world.getName());
      }
      return;
    }

    this.worldKey = Key.key(worldName);
    this.world = null;
  }

  @EventHandler
  void onFallBelowMinY(PlayerMoveEvent event) {
    final World configWorld = this.getWorld();

    if (configWorld == null || configWorld != event.getTo().getWorld()) {
      return;
    }

    if (event.getTo().y() > this.minY) {
      return;
    }

    // This logic is yanked from HelperRTP.tp because I do not want it to send any error messages.
    final Player player = event.getPlayer();

    final boolean ignoreCooldown = true;
    final boolean ignoreDelay = true;

    final RTP_PlayerInfo playerInfo = new RTP_PlayerInfo(!ignoreDelay, true, !ignoreCooldown);
    final World world = HelperRTP.getActualWorld(player, configWorld, null);
    final RTPSetupInformation setup_info = new RTPSetupInformation(world, player, player, true, null, RTP_TYPE.FORCED, null, playerInfo);

    final WorldPlayer pWorld = HelperRTP.getPlayerWorld(setup_info);
    final RTP_ERROR_REQUEST_REASON cantReason = HelperRTP_Check.canRTP(player, player, pWorld, setup_info.getPlayerInfo());

    if (cantReason != null) {
      return;
    }

    BetterRTP.getInstance().getRTP().start(pWorld);
  }

  private @Nullable World getWorld() {
    if (this.world != null) {
      return this.world;
    }
    if (this.worldKey == null) {
      return null;
    }

    final World out = this.getServer().getWorld(this.worldKey);
    if (out == null) {
      this.getSLF4JLogger().warn("Failed to find world with key '{}'.", this.worldKey.asMinimalString());
      return null;
    }

    this.world = out;
    return out;
  }
}