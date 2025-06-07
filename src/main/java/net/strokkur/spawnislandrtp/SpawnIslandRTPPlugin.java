package net.strokkur.spawnislandrtp;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public final class SpawnIslandRTPPlugin extends JavaPlugin implements Listener {

    private int minY = 0;
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
        Bukkit.getPluginManager().registerEvents(this, this);
        reload();
    }

    void reload() {
        this.saveDefaultConfig();
        minY = this.getConfig().getInt("min-y");

        String worldName = this.getConfig().getString("world");
        Optional<World> world = Optional.ofNullable(worldName)
            .map(Bukkit::getWorld);
        if (world.isEmpty()) {
            this.getComponentLogger().warn("Failed to find world for {}. Please define a valid world!", worldName);
        } else {
            this.world = world.get();
        }
    }

    @EventHandler
    void onFallBelowMinY(PlayerMoveEvent event) {
        if (this.world == null) {
            return;
        }

        if (this.world != event.getTo().getWorld()) {
            return;
        }

        if (event.getTo().y() > this.minY) {
            return;
        }

        HelperRTP.tp(event.getPlayer(), event.getPlayer(), this.world, null, RTP_TYPE.FORCED, true, true);
    }
}