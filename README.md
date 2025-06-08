# Databank
## Overview
Databank is a library mod. It contains random, mostly rendering related libraries and utilities, as well as data-driven multiblocks, block hiding, and music. These last three can be leveraged by modpacks if they so wish.

## Feature Highlights
Notable features may include...
### Databank Models
Databank allows for dynamically-animated block, item and entity models which support meshes. A Blockbench plugin is provided to generate Databank models. As a bonus, such models are resource packable.
### Hidden
Data-driven hiding of blocks and items in the world as other blocks until certain conditions are met. A few built-in conditions for hiding are available - advancements and logical operators (AND, OR, NOT). More conditions and more hidden types can be easily added in your mod.
### Post Processing Shaders
Databank adds its own loader for post processing shaders.
### Multiblocks
Databank multiblocks are defined in json similarly to recipes, composed of a pattern and a key. They support tag- and blockstate matches.
### Music System
Data-driven music playing, mostly tailored to boss themes and similarly looping music.
### World Guis
A special system for rendering using a GuiGraphics and accepting per-pixel clicks in-world
### Megablocks
Simple creation of blocks which take up multiple blocks of space
### Megastructures
A special feature which allows you to save builds as json and spawn them in-world using code without any size limit
