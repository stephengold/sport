# release log for the SPORT Library

## Version 0.9.3 released on TBD

+ Implemented texture compositing:
  + Enhanced 2 fragment shaders to emit alpha channel.
  + Defined the `BlendOp` interface.
  + Added 2 classes:
    + `OverOp`
    + `ReplaceOp`
  + Added 2 methods:
    + `Texture.name()`
    + `TextureKey.textureName()`
  + Publicized the `Utils.setOglCapability()` method.

## Version 0.9.2 released on 22 March 2024

+ Upgraded the Libbulletjme library to v20.2.0
+ Added 4 methods:
  + `InputManager.getGlfwWindowHandle()`
  + `InputManager.glfwCursorX()`
  + `InputManager.glfwCursorY()`
  + `InputProcessor.onCharacter()`

## Version 0.9.1 released on 9 March 2024

Initial release, using code copied from the LbjExamples project.