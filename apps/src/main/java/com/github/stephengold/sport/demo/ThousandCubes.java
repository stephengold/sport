/*
 Copyright (c) 2022-2025 Stephen Gold and Yanis Boudiaf

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.stephengold.sport.demo;

import com.github.stephengold.sport.Constants;
import com.github.stephengold.sport.Geometry;
import com.github.stephengold.sport.Mesh;
import com.github.stephengold.sport.Utils;
import com.github.stephengold.sport.input.InputProcessor;
import com.github.stephengold.sport.input.RotateMode;
import com.github.stephengold.sport.mesh.CrosshairsMesh;
import com.github.stephengold.sport.mesh.LoopMesh;
import com.github.stephengold.sport.physics.BasePhysicsApp;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

/**
 * A 3-D physics demo that drops 1000 cubes onto a horizontal surface and
 * launches balls at them.
 */
public class ThousandCubes extends BasePhysicsApp<PhysicsSpace> {
    // *************************************************************************
    // fields

    /**
     * shape for stacked boxes
     */
    private static BoxCollisionShape boxShape;
    /**
     * shape for bodies launched when the E key is pressed
     */
    private static CollisionShape launchShape;
    /**
     * simulation speed (simulated seconds per wall-clock second)
     */
    private static float physicsSpeed = 1f;
    /**
     * cross geometry for the crosshairs
     */
    private static Geometry cross;
    /**
     * loop geometry for the crosshairs
     */
    private static Geometry loop;
    /**
     * generate random colors for boxes
     */
    final private static Random random = new Random();
    /**
     * temporary storage for location vectors
     */
    final private static Vector3f tmpLocation = new Vector3f();
    // *************************************************************************
    // constructors

    /**
     * Instantiate the ThousandCubes application.
     * <p>
     * This no-arg constructor was made explicit to avoid javadoc warnings from
     * JDK 18+.
     */
    public ThousandCubes() {
        // do nothing
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Main entry point for the ThousandCubes application.
     *
     * @param arguments array of command-line arguments (not null)
     */
    public static void main(String[] arguments) {
        Logger.getLogger("").setLevel(Level.WARNING);
        ThousandCubes app = new ThousandCubes();
        app.start();
    }
    // *************************************************************************
    // BasePhysicsApp methods

    /**
     * Create the PhysicsSpace. Invoked once during initialization.
     *
     * @return a new object
     */
    @Override
    public PhysicsSpace createSpace() {
        // because this app might be used to evaluate performance:
        if (Utils.areAssertionsEnabled()) {
            System.out.println("Warning:  Assertions are enabled.");
        }
        if (NativeLibrary.isDebug()) {
            System.out.println("Warning:  using a Debug native library");
        }
        if (NativeLibrary.isDoublePrecision()) {
            System.out.println("Warning:  using a Dp native library");
        }

        PhysicsSpace result
                = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        return result;
    }

    /**
     * Initialize the application.
     */
    @Override
    protected void initialize() {
        super.initialize();

        addCrosshairs();
        configureCamera();
        configureInput();
        setBackgroundColor(Constants.SKY_BLUE);
    }

    /**
     * Populate the PhysicsSpace. Invoked once during initialization.
     */
    @Override
    protected void populateSpace() {
        boxShape = new BoxCollisionShape(0.5f);
        launchShape = new SphereCollisionShape(0.5f);

        CollisionShape planeShape
                = new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y, 0f));
        PhysicsRigidBody floor
                = new PhysicsRigidBody(planeShape, PhysicsBody.massForStatic);
        physicsSpace.addCollisionObject(floor);
        visualizeShape(floor, 0.05f);

        // Create 1000 boxes, arranged in a cube:
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    addBox(2f * i, 2f * j, 2f * k - 2.5f);
                }
            }
        }
    }

    /**
     * Callback invoked during each iteration of the main update loop.
     */
    @Override
    protected void render() {
        updateScales();
        super.render();
    }

    /**
     * Advance the physics simulation by the specified amount. Invoked during
     * each update.
     *
     * @param wallClockSeconds the elapsed wall-clock time since the previous
     * invocation of {@code updatePhysics} (in seconds, &ge;0)
     */
    @Override
    protected void updatePhysics(float wallClockSeconds) {
        float simulateSeconds = physicsSpeed * wallClockSeconds;
        physicsSpace.update(simulateSeconds);
    }
    // *************************************************************************
    // private methods

    /**
     * Add a dynamic box to the space, at the specified coordinates.
     *
     * @param x the desired X coordinate (in physics space)
     * @param y the desired Y coordinate (in physics space)
     * @param z the desired Z coordinate (in physics space)
     */
    private void addBox(float x, float y, float z) {
        // Create and add a box:
        float mass = 10f;
        PhysicsRigidBody box = new PhysicsRigidBody(boxShape, mass);
        physicsSpace.addCollisionObject(box);

        box.setAngularDamping(0.1f);
        box.setLinearDamping(0.3f);
        tmpLocation.set(x, y, z);
        box.setPhysicsLocation(tmpLocation);

        // Visualize the box in a random color:
        float red = FastMath.pow(random.nextFloat(), 2.2f);
        float green = FastMath.pow(random.nextFloat(), 2.2f);
        float blue = FastMath.pow(random.nextFloat(), 2.2f);
        visualizeShape(box).setColor(new Vector4f(red, green, blue, 1f));
    }

    /**
     * Create geometries to render yellow crosshairs at the center of the
     * window.
     */
    private static void addCrosshairs() {
        float crossWidth = 0.1f;
        Mesh crossMesh = new CrosshairsMesh(crossWidth, crossWidth);
        cross = new Geometry(crossMesh)
                .setColor(Constants.YELLOW)
                .setProgram("Unshaded/Clipspace/Monochrome");

        int numLines = 32;
        float loopRadius = 0.3f * crossWidth;
        Mesh loopMesh = new LoopMesh(numLines, loopRadius, loopRadius);
        loop = new Geometry(loopMesh)
                .setColor(Constants.YELLOW)
                .setProgram("Unshaded/Clipspace/Monochrome");
    }

    /**
     * Configure the Camera and CIP during startup.
     */
    private static void configureCamera() {
        getCameraInputProcessor().setRotationMode(RotateMode.Immediate);
        cam.setLocation(60f, 15f, 28f)
                .setAzimuth(-2.7f)
                .setUpAngle(-0.25f);
    }

    /**
     * Configure keyboard input during startup.
     */
    private void configureInput() {
        getInputManager().add(new InputProcessor() {
            @Override
            public void onKeyboard(int keyId, boolean isPressed) {
                switch (keyId) {
                    case GLFW.GLFW_KEY_E:
                        if (isPressed) {
                            launchRedBall();
                        }
                        return;

                    case GLFW.GLFW_KEY_PAUSE:
                    case GLFW.GLFW_KEY_PERIOD:
                        if (isPressed) {
                            togglePause();
                        }
                        return;

                    default:
                }
                super.onKeyboard(keyId, isPressed);
            }
        });
    }

    /**
     * Launch a single red ball from the camera location.
     */
    private void launchRedBall() {
        float mass = 10f;
        PhysicsRigidBody missile = new PhysicsRigidBody(launchShape, mass);
        physicsSpace.addCollisionObject(missile);

        float radius = launchShape.maxRadius();
        missile.setCcdMotionThreshold(radius);
        missile.setCcdSweptSphereRadius(radius);

        float speed = 100f;
        Vector3f velocity = cam.getDirection().mult(speed);
        missile.setLinearVelocity(velocity);

        missile.setAngularDamping(0.1f);
        missile.setLinearDamping(0.3f);
        missile.setPhysicsLocation(cam.getLocation());

        // Visualize the ball in red:
        visualizeShape(missile).setColor(Constants.RED);
    }

    /**
     * Toggle the physics simulation: paused/running.
     */
    private static void togglePause() {
        float pausedSpeed = 1e-9f;
        physicsSpeed = (physicsSpeed <= pausedSpeed) ? 1f : pausedSpeed;
    }

    /**
     * Scale the crosshair geometries so they will render as an equal-armed
     * cross and a circle, regardless of the window's aspect ratio.
     */
    private static void updateScales() {
        float aspectRatio = aspectRatio();
        float yScale = Math.min(1f, aspectRatio);
        float xScale = yScale / aspectRatio;
        Vector3fc newScale = new org.joml.Vector3f(xScale, yScale, 1f);

        cross.setScale(newScale);
        loop.setScale(newScale);
    }
}
