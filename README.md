# Databank
## Overview
Databank is a library mod. It contains random, mostly rendering related libraries and utilities, as well as data-driven multiblocks, block hiding, and music. These last three can be leveraged by modpacks if they so wish.

## Feature Highlights
Notable features may include...
### Databank Models
Databank allows for dynamically-animated block and entity models without nearly as much interfacing with vanilla animation code as doing it yourself. It is not an entirely "new" model type, but rather a layer on top of the existing vanilla system. A Blockbench plugin is provided to generate Databank models. As a bonus, such models are resource packable.
### Hidden Blocks
Data-driven hiding of blocks in the world as other blocks until certain conditions are met. A few built-in conditions for hiding are available - advancements and logical operators (AND, OR, NOT). More conditions can be easily added in your mod.
### Post Processing Shaders
Databank adds its own loader for post processing shaders.
### Multiblocks
Databank multiblocks are defined in json similarly to recipes, composed of a pattern and a key. They support tag- and blockstate matches.
### Music System
Data-driven music playing, mostly tailored to boss themes and similarly looping music.
