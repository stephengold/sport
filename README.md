<img height="150" src="https://i.imgur.com/YEPFEcx.png" alt="Libbulletjme Project logo">

[The SPORT Project][project] implements
an [OpenGL]-based graphics engine
for [the Libbulletjme 3-D physics library][libbulletjme].

It contains 2 sub-projects:

1. lib: the SPORT graphics engine (a single JVM runtime library)
2. apps: demos and non-automated test software

Complete source code (in [Java]) is provided under
[a 3-clause BSD license][license].


<a name="toc"></a>

## Contents of this document

+ [About SPORT](#about)
+ [How to add SPORT to an existing project](#add)
+ [How to build and run SPORT from source](#build)
+ [Conventions](#conventions)
+ [What's missing](#todo)
+ [History](#history)
+ [Acknowledgments](#acks)


<a name="about"></a>

## About SPORT

SPORT is a Simple Physics-ORienTed graphics engine written in Java 1.8.
In addition to [Libbulletjme],
it uses [LWJGL], [Assimp], [GLFW], [JOML], and [OpenGL].
It has been tested on Windows, Linux, and macOS.

[Jump to the table of contents](#toc)


<a name="add"></a>

## How to add SPORT to an existing project

SPORT comes pre-built as a single library that depends on [Libbulletjme].
However, the Libbulletjme dependency is intentionally omitted from SPORT's POM
so developers can specify *which* Libbulletjme library should be used.

For projects built using [Maven] or [Gradle], it is
*not* sufficient to specify the
dependency on the SPORT Library.
You must also explicitly specify the Libbulletjme dependency.

### Gradle-built projects

Add to the project’s "build.gradle" file:

    repositories {
        mavenCentral()
    }
    dependencies {
        implementation 'com.github.stephengold:Libbulletjme:20.1.0'
        implementation 'com.github.stephengold:sport:0.9.1'
    }

For some older versions of Gradle,
it's necessary to replace `implementation` with `compile`.

### Maven-built projects

Add to the project’s "pom.xml" file:

    <repositories>
      <repository>
        <id>mvnrepository</id>
        <url>https://repo1.maven.org/maven2/</url>
      </repository>
    </repositories>

    <dependency>
      <groupId>com.github.stephengold</groupId>
      <artifactId>Libbulletjme</artifactId>
      <version>20.1.0</version>
    </dependency>

    <dependency>
      <groupId>com.github.stephengold</groupId>
      <artifactId>sport</artifactId>
      <version>0.9.1</version>
    </dependency>

### Coding a SPORT application

Every SPORT application should extend the `BasePhysicsApp` class,
which provides hooks for:

+ initializing the application,
+ creating and configuring the application's physics space,
+ populating the space with physics objects, and
+ updating the space before each frame is rendered.

The graphics engine maintains an internal list of rendered objects,
called *geometries*.
Instantiating a geometry automatically adds it to the list.

+ To visualize world (physics-space) coordinate axes,
  instantiate one or more `LocalAxisGeometry` objects.

By default, physics objects are not visualized.

+ To visualize the shape of a `PhysicsCollisionObject`,
  invoke the `visualizeShape()` method on the object.
+ To visualize the local coordinate axes of a `PhysicsCollisionObject`,
  invoke the `visualizeAxes()` method on the object.
+ To visualize a `Constraint`,
  instantiate one or more `ConstraintGeometry` objects.
+ To visualize the wheels of a `PhysicsVehicle`,
  invoke the `visualizeWheels()` method on the vehicle.
+ To visualize the bounding box of a `PhysicsCollisionObject`,
  instantiate an `AabbGeometry` for the object.
+ To visualize a `Constraint`,
  instantiate a `ConstraintGeometry` for each end.
+ To visualize the faces of a `PhysicsSoftBody`,
  instantiate a `FacesGeometry` for the body.
+ To visualize the links of a `PhysicsSoftBody`,
  instantiate a `LinksGeometry` for the body.
+ To visualize the pins of a `PhysicsSoftBody`,
  instantiate a `PinsGeometry` for the body.
+ To visualize the wind acting on a `PhysicsSoftBody`,
  instantiate a `WindVelocityGeometry` for the body.

[Jump to the table of contents](#toc)


<a name="build"></a>

## How to build and run SPORT from source

### Initial build

1. Install a [Java Development Kit (JDK)][adoptium],
   if you don't already have one.
2. Point the `JAVA_HOME` environment variable to your JDK installation:
   (In other words, set it to the path of a directory/folder
   containing a "bin" that contains a Java executable.
   That path might look something like
   "C:\Program Files\Eclipse Adoptium\jdk-17.0.3.7-hotspot"
   or "/usr/lib/jvm/java-17-openjdk-amd64/" or
   "/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home" .)
  + using Bash or Zsh: `export JAVA_HOME="` *path to installation* `"`
  + using [Fish]: `set -g JAVA_HOME "` *path to installation* `"`
  + using Windows Command Prompt: `set JAVA_HOME="` *path to installation* `"`
  + using PowerShell: `$env:JAVA_HOME = '` *path to installation* `'`
3. Download and extract the SPORT source code from GitHub:
  + using [Git]:
    + `git clone https://github.com/stephengold/sport.git`
    + `cd sport`
4. Run the [Gradle] wrapper:
  + using Bash or Fish or PowerShell or Zsh: `./gradlew build`
  + using Windows Command Prompt: `.\gradlew build`

After a successful build,
Maven artifacts will be found in "lib/build/libs".

You can install the artifacts to your local Maven repository:
+ using Bash or Fish or PowerShell or Zsh: `./gradlew install`
+ using Windows Command Prompt: `.\gradlew install`

### Demos

Seven demo applications are included:
+ ConveyorDemo
+ NewtonsCradle
+ Pachinko
+ SplitDemo
+ TestGearJoint
+ ThousandCubes
+ Windlass

Documentation for the demo apps is at
https://stephengold.github.io/Libbulletjme/lbj-en/English/demos.html

### Chooser

A Swing-based chooser application is included.
However, it doesn't work on macOS yet.

To run the chooser:
+ using Bash or Fish or PowerShell or Zsh: `./gradlew AppChooser`
+ using Windows Command Prompt: `.\gradlew AppChooser`

### Cleanup

You can restore the project to a pristine state:
+ using Bash or Fish or PowerShell or Zsh: `./gradlew clean`
+ using Windows Command Prompt: `.\gradlew clean`

Note:  these commands will delete any downloaded native libraries.

[Jump to the table of contents](#toc)


<a name="conventions"></a>

## Conventions

Package names begin with `com.github.stephengold` or `jme3utilities.minie`.

The source code and pre-built libraries are compatible with JDK 8.

Rotation signs, polygon windings, and 3-D coordinate axes
are right-handed/counter-clockwise unless otherwise noted.

Angles are quantified in *radians* unless otherwise noted.

The world coordinate system is assumed to be Z-forward, Y-up.

[Jump to the table of contents](#toc)


<a name="todo"></a>

## What's missing

This project is incomplete.
Future enhancements might include:

+ graphics and physics on separate threads
+ graphical user interface
+ automated tests
+ shadow rendering
+ physically-based rendering
+ more performance statistics
+ sound effects
+ skeletal animation
+ run on mobile platforms (Android and/or iOS)

[Jump to the table of contents](#toc)


<a name="history"></a>

## History

From March 2022 to March 2024, SPORT was a sub-project of
[the LbjExamples Project][lbjexamples].

Since March 2024, SPORT has been a separate project, hosted at
[GitHub][project].

[Jump to the table of contents](#toc)


<a name="acks"></a>

## Acknowledgments

The ThousandCubes demo app and most of the original graphics code
were authored by Yanis Boudiaf.

The IcosphereMesh class derives from source code
published by James Khan in May 2017.

The ConveyorDemo app derives from source code
contributed by "qwq" in March 2022.

This project has made use of the following libraries and software tools:

  + the [Checkstyle] tool
  + the [Firefox] web browser
  + the [Git] revision-control system and GitK commit viewer
  + the [GitKraken] client
  + the [GLFW] library
  + the [Gradle] build tool
  + the [Java] compiler, standard doclet, and runtime environment
  + [the Java OpenGL Math Library][joml]
  + [the Lightweight Java Gaming Library][lwjgl]
  + the [Linux Mint][mint] operating system
  + the [Markdown] document-conversion tool
  + the [Meld] visual merge tool
  + the [NetBeans] integrated development environment
  + [the Open Asset Import (Assimp) Library][assimp]
  + the [OpenGL] API
  + Microsoft Windows

I am grateful to [GitHub] and [Imgur]
for providing free hosting for this project
and many other open-source projects.

I'm also grateful to my dear Holly, for keeping me sane.

If I've misattributed anything or left anyone out, please let me know, so I can
correct the situation: sgold@sonic.net

[Jump to the table of contents](#toc)


[adoptium]: https://adoptium.net/releases.html "Adoptium Project"
[assimp]: https://www.assimp.org/ "The Open Asset Importer Library"
[checkstyle]: https://checkstyle.org "Checkstyle"
[firefox]: https://www.mozilla.org/en-US/firefox "Firefox"
[fish]: https://fishshell.com/ "Fish command-line shell"
[git]: https://git-scm.com "Git"
[github]: https://github.com "GitHub"
[gitkraken]: https://www.gitkraken.com "GitKraken client"
[glfw]: https://www.glfw.org "GLFW Library"
[gradle]: https://gradle.org "Gradle Project"
[imgur]: https://imgur.com/ "Imgur"
[java]: https://en.wikipedia.org/wiki/Java_(programming_language) "Java programming language"
[joml]: https://joml-ci.github.io/JOML "Java OpenGL Math Library"
[lbjexamples]: https://github.com/stephengold/LbjExamples "LbjExamples Project"
[libbulletjme]: https://stephengold.github.io/Libbulletjme/lbj-en/English/overview.html "Libbulletjme Project"
[license]: https://github.com/stephengold/sport/blob/master/LICENSE "SPORT license"
[lwjgl]: https://www.lwjgl.org "Lightweight Java Game Library"
[markdown]: https://daringfireball.net/projects/markdown "Markdown Project"
[maven]: https://maven.apache.org "Maven Project"
[meld]: https://meldmerge.org "Meld merge tool"
[mint]: https://linuxmint.com "Linux Mint Project"
[netbeans]: https://netbeans.org "NetBeans Project"
[opengl]: https://www.khronos.org/opengl "OpenGL API"
[project]: https://github.com/stephengold/sport "SPORT Project"
