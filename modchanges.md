### Additions
- Added the ability to send a trail somewhere else to handle what's left of it so that trails can be made to not just disappear
- Added the ability to automatically have a trail tick
- Added a small utility for rendering text on screen for debug purposes
- Added a method for checking how many positions a trail has
- Added a method for getting a position that a trail has stored

### Changes
- Improved ColorGradient fadeAlpha so that it creates points at the start and end positions so that the fade is always proper
- Made TrailRender throw an exception if you try to use more segments than time