### Additions
- Moved interface for dynamically modifying item attributes from DnE to Databank
- Added a system for special conditions
- Added support for armatures in databank models
- Added utilities for stretched blits and nine slices
- Added a dialogue system
- Added a small utility for conversion between Rotation and Direction to DatabankUtils

### Changes
- Modified the databank model format, if you are a developer, please run the python script in the databank github to update your models
- Changed BasicMegablockCores to take in a BlockState in the getRotation method

### Fixes
- Fixed an issue where inflation on models would be inverted on y axis
- Fixed Megastructure logging error saying it was music controllers
- Fixed BasicMegablockCores not using the rotated shape when checking if it can place [PLEASE TEST]
- Fixed it being difficult to change the placement block state for BasicMegablockCores
- Fixed an issue where the BasicMegablockRouter would not properly set the "core" variable