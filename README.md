# SpawnIslandRTP

This is a very simple plugin with makes it so that when a Player falls below a certain y-height, it randomly
teleports the player. That's it. There isn't more to it.

This plugin depends on **BetterRTP**, so make sure to install it when using this plugin.

The current default config looks like this:
```yml
# This incredibly tiny config declares the y value from which the player gets RTPed from.
# It also describes which world should listen to, printing an error if the world does not exist

# The y coordinate from which to teleport
min-y: 0

# The world to listen for
world: world
```
