# Artiste
*Currently only supports Spigot 1.8.3*
Create & share pixel art
          ~
Artiste allows players to 'paint' on an interactive canvas of wool blocks, and share their work with other players - all in-game.

## Features

* **Canvas** - a grid of wool blocks that players can recolour by right-clicking with dyes.

* **Claim Management** - Players that meet a minimum permission requirement may claim the canvas for a configurable period of time.

* **Saving & Buying** - Players may save their work for personal use, and opt to publish artworks to a public registry. Anyone can then purchase artworks for a standardized prices as a map item.

* **Management** - Players must be granted canvas permissions, can have their canvas rights revoked, and must be granted permission to make their artworks available to others. This will help combat the obligatory influx of dick drawings.

* **Optimization** - A fair bit of work was focused on making the plugin run more efficiently - in terms of both memory and disk space. This includes ~
    * Maps are stripped back via reflection to prevent the constant rendering and update checks vanilla maps undergo.
    * Direct NMS interaction is used to reduce the overhead of large numbers of setBlock() calls - by circumventing unnecessary lighting recalculations and chunk updates.
    * Some basic cool-off tasks prevent players spamming memory-intensive commands.
