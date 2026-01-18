# release log for the SPORT Library

## Version 0.9.8 released on 18 January 2026

+ Updated the LWJGL libraries to v3.4.0 .
+ Added chaining to 3 `CameraInputProcessor` setters.
+ Added a `Geometry.setColor()` method that accepts scalars.
+ Added argument validation to `Geometry.setScale()` .

## Version 0.9.7 released on 13 Sepetmber 2025

+ De-publicized 5 `BasePhysicsApp` methods.
+ Bugfix:  performance degraded by `glGetError()`
+ Bugfix:  logic error in `Camera.clipToWorld()`
+ Added methods:
  + `BaseApplication.initialWindowTitle()`
  + `BasePhysicsApp.totalPhysicsNanos()`
  + `BasePhysicsApp.totalSimulatedTime()`
  + `Camera.setLocation(float, float, float)`
  + `Utils.areAssertionsEnabled()`
  + `Utils.setBackgroudColor(float, float, float, float)`
  + `Utils.setLightColor(Vector4fc)`
+ Enhanced the "KEY_C" handler to also print projection properties.
+ Enhanced `BasePhysicsApp.cleanUp()` to hide all geometries.
+ Altered `BaseApplication.start()` so it never returns.
+ Altered `BaseApplication.cleanUpBase()` to preserve the input manager.
+ Overrode the `Geometry.toString()` method.
+ Updated the Libbulletjme library to v22.0.3 .

## Version 0.9.6 released on 17 March 2025

+ Removed the `SoftMesh` class.
+ Augmented the diagnostic when `glfwCreateWindow()` fails.
+ Began using clean extraction to load native libraries.
+ Updated the jSnapLoader library to v1.1.1-stable.

## Version 0.9.5 released on 27 January 2025

+ Enabled depth write at the start of every frame.
+ Added accessors for the size of the MSAA coverage mask.
+ Added the "high2" meshing strategy.
+ Updated the Libbulletjme library to v22.0.1 and began using jSnapLoader.
+ Updated the LWJGL library to v3.3.6 .
+ Updated the JOML library to v1.10.8 .

## Version 0.9.4 released on 3 April 2024

+ Bugfix: the `OverOp` class specifies the wrong package
+ Changed `BaseApplication.blendTexture()` so it doesn't flip UVs.

## Version 0.9.3 released on 23 March 2024

Implemented texture compositing:
+ Enhanced 2 fragment shaders to emit alpha channel.
+ Defined the `BlendOp` interface.
+ Added 2 public classes:
  + `OverOp`
  + `ReplaceOp`
+ Added 2 public methods:
  + `BaseApplication.blendTexture()`
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