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
package com.github.stephengold.sport.physics;

import com.github.stephengold.sport.BaseApplication;
import com.github.stephengold.sport.Constants;
import com.github.stephengold.sport.Filter;
import com.github.stephengold.sport.FlipAxes;
import com.github.stephengold.sport.Geometry;
import com.github.stephengold.sport.Mesh;
import com.github.stephengold.sport.NormalsOption;
import com.github.stephengold.sport.TextureKey;
import com.github.stephengold.sport.UvsOption;
import com.github.stephengold.sport.WrapFunction;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.Box2dShape;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CustomConvexShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.collision.shapes.MultiSphere;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SimplexCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.bullet.util.DebugShapeFactory;
import electrostatic4j.snaploader.LibraryInfo;
import electrostatic4j.snaploader.LoadingCriterion;
import electrostatic4j.snaploader.NativeBinaryLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.PlatformPredicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import jme3utilities.Validate;
import jme3utilities.math.MyVector3f;
import org.joml.Vector4f;

/**
 * An application to visualize 3-D physics.
 *
 * @param <T> the type of PhysicsSpace to simulate
 */
public abstract class BasePhysicsApp<T extends PhysicsSpace>
        extends BaseApplication {
    // *************************************************************************
    // fields

    /**
     * total time simulated (in simulated seconds)
     */
    private static double totalSimulatedTime;
    /**
     * how many times render() has been invoked
     */
    private int renderCount;
    /**
     * timestamp of the previous render() (for {@code renderCount>0}, in
     * nanoseconds)
     */
    private long lastPhysicsUpdate;
    /**
     * accumulated wall-clock time spent in physics simulation, including step
     * listeners (in nanoseconds)
     */
    private static long totalPhysicsNanos;
    /**
     * map shape summaries to auto-generated meshes, for reuse
     */
    final private static Map<ShapeSummary, Mesh> meshCache
            = new WeakHashMap<>(200);
    /**
     * space for physics simulation
     */
    protected T physicsSpace;
    // *************************************************************************
    // constructors

    /**
     * Explicit no-arg constructor to avoid javadoc warnings from JDK 18+.
     */
    protected BasePhysicsApp() {
        // do nothing
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Create the PhysicsSpace during initialization.
     *
     * @return a new object
     */
    protected abstract T createSpace();

    /**
     * Return a Mesh to visualize the summarized shape.
     *
     * @param shape the shape to visualize (not null, unaffected)
     * @param summary a summary of the shape (not null)
     * @return an immutable Mesh (not null)
     */
    static Mesh meshForShape(CollisionShape shape, ShapeSummary summary) {
        Mesh result;

        if (meshCache.containsKey(summary)) {
            result = meshCache.get(summary);

        } else {
            MeshingStrategy strategy = summary.meshingStrategy();
            result = strategy.applyTo(shape);
            result.makeImmutable();
            meshCache.put(summary, result);
        }

        return result;
    }
    // *************************************************************************
    // protected methods

    /**
     * Add physics objects to the PhysicsSpace during initialization.
     */
    abstract protected void populateSpace();

    /**
     * Advance the physics simulation by the specified interval. Invoked during
     * each update. Meant to be overridden.
     *
     * @param intervalSeconds the elapsed (real) time since the previous
     * invocation of {@code updatePhysics} (in seconds, &ge;0)
     */
    protected void updatePhysics(float intervalSeconds) {
        totalSimulatedTime += intervalSeconds;
        physicsSpace.update(intervalSeconds);
    }

    /**
     * Visualize the local axes of the specified collision object.
     *
     * @param pco the object to visualize (or null for world axes)
     * @param axisLength how much of each axis to visualize (in world units,
     * &ge;0)
     * @return an array of new, visible geometries
     */
    protected static Geometry[] visualizeAxes(
            PhysicsCollisionObject pco, float axisLength) {
        Validate.nonNegative(axisLength, "axis length");

        Geometry[] result = new Geometry[MyVector3f.numAxes];
        for (int ai = 0; ai < MyVector3f.numAxes; ++ai) {
            result[ai] = new LocalAxisGeometry(pco, ai, axisLength)
                    .setDepthTest(false);
        }

        return result;
    }

    /**
     * Visualize the shape of the specified collision object.
     *
     * @param pco the collision object to visualize (not null)
     * @return a new, visible Geometry
     */
    protected static Geometry visualizeShape(PhysicsCollisionObject pco) {
        float uvScale = 1f;
        Geometry result = visualizeShape(pco, uvScale);

        return result;
    }

    /**
     * Visualize the shape of the specified collision object.
     *
     * @param pco the collision object to visualize (not null)
     * @param uvScale the UV scale factor to use (default=1)
     * @return a new, visible Geometry
     */
    protected static Geometry visualizeShape(
            PhysicsCollisionObject pco, float uvScale) {
        MeshingStrategy meshingStrategy;
        String programName;
        TextureKey textureKey;

        CollisionShape shape = pco.getCollisionShape();
        if (shape instanceof PlaneCollisionShape) {
            meshingStrategy = new MeshingStrategy(
                    DebugShapeFactory.lowResolution, NormalsOption.Facet,
                    UvsOption.Linear, new Vector4f(uvScale, 0f, 0f, 0f),
                    new Vector4f(0f, 0f, uvScale, 0f)
            );
            programName = "Phong/Distant/Texture";
            boolean mipmaps = true;
            float maxAniso = 16f;
            textureKey = new TextureKey("procedural:///checkerboard?size=128",
                    Filter.Linear, Filter.NearestMipmapLinear,
                    WrapFunction.Repeat, WrapFunction.Repeat, mipmaps,
                    FlipAxes.noFlip, maxAniso);

        } else if (shape instanceof SphereCollisionShape) {
            int positionStrategy = -3;
            meshingStrategy = new MeshingStrategy(
                    positionStrategy, NormalsOption.Sphere, UvsOption.Spherical,
                    new Vector4f(uvScale, 0f, 0f, 0f),
                    new Vector4f(0f, uvScale, 0f, 0f)
            );
            programName = "Phong/Distant/Texture";
            textureKey = new TextureKey(
                    "procedural:///checkerboard?size=2&color0=999999ff",
                    Filter.Nearest, Filter.Nearest);

        } else { // shape isn't a plane or sphere:
            programName = "Phong/Distant/Monochrome";
            textureKey = null;

            if (shape instanceof Box2dShape
                    || shape instanceof BoxCollisionShape
                    || shape instanceof SimplexCollisionShape) {
                meshingStrategy = new MeshingStrategy("low/Facet");

            } else if (shape instanceof CapsuleCollisionShape
                    || shape instanceof CustomConvexShape
                    || shape instanceof HeightfieldCollisionShape
                    || shape instanceof MultiSphere) {
                meshingStrategy = new MeshingStrategy("high/Smooth");

            } else {
                meshingStrategy = new MeshingStrategy("high/Facet");
            }
        }

        Geometry geometry;
        if (pco instanceof PhysicsRigidBody) {
            PhysicsRigidBody body = (PhysicsRigidBody) pco;
            geometry = new RigidBodyShapeGeometry(body, meshingStrategy);

        } else if (pco instanceof PhysicsCharacter) {
            PhysicsCharacter character = (PhysicsCharacter) pco;
            geometry = new CharacterShapeGeometry(character, meshingStrategy);

        } else { // TODO ghost, soft body cases
            throw new IllegalArgumentException(pco.toString());
        }

        geometry.setProgram(programName);
        geometry.setSpecularColor(Constants.GRAY);
        if (textureKey != null) {
            geometry.setTexture(textureKey);
        }

        return geometry;
    }

    /**
     * Visualize the wheels of the specified vehicle.
     *
     * @param vehicle the vehicle to visualize (not null)
     * @return an array of new, visible geometries
     */
    protected static Geometry[] visualizeWheels(PhysicsVehicle vehicle) {
        int numWheels = vehicle.getNumWheels();
        Geometry[] result = new Geometry[numWheels];
        for (int wheelIndex = 0; wheelIndex < numWheels; ++wheelIndex) {
            result[wheelIndex] = new WheelGeometry(vehicle, wheelIndex);
        }

        return result;
    }
    // *************************************************************************
    // BaseApplication methods

    /**
     * Callback invoked after the main update loop terminates.
     */
    @Override
    protected void cleanUp() {
        physicsSpace.destroy();

        // Discard all meshes auto-generated for collision shapes:
        for (Mesh mesh : meshCache.values()) {
            mesh.cleanUp();
        }
        meshCache.clear();
        //physicsThread.stop();
    }

    /**
     * Callback invoked before the main update loop begins. Meant to be
     * overridden.
     */
    @Override
    protected void initialize() {
        LibraryInfo info
                = new LibraryInfo(null, "bulletjme", DirectoryPath.USER_DIR);
        NativeBinaryLoader loader = new NativeBinaryLoader(info);

        NativeDynamicLibrary[] libraries = {
            new NativeDynamicLibrary(
            "native/linux/arm64", PlatformPredicate.LINUX_ARM_64),
            new NativeDynamicLibrary(
            "native/linux/arm32", PlatformPredicate.LINUX_ARM_32),
            new NativeDynamicLibrary(
            "native/linux/x86_64", PlatformPredicate.LINUX_X86_64),
            new NativeDynamicLibrary(
            "native/osx/arm64", PlatformPredicate.MACOS_ARM_64),
            new NativeDynamicLibrary(
            "native/osx/x86_64", PlatformPredicate.MACOS_X86_64),
            new NativeDynamicLibrary(
            "native/windows/x86_64", PlatformPredicate.WIN_X86_64)
        };
        loader.registerNativeLibraries(libraries)
                .initPlatformLibrary()
                .setRetryWithCleanExtraction(true);

        // Load the Libbulletjme native library for this platform.
        try {
            loader.loadLibrary(LoadingCriterion.CLEAN_EXTRACTION);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to load the Libbulletjme native library!");
        }

        this.physicsSpace = createSpace();
        populateSpace();
    }

    /**
     * Callback invoked during each iteration of the main update loop. Meant to
     * be overridden.
     */
    @Override
    protected void render() {
        ++renderCount;

        // Advance the physics, but not during the first render().
        long nanoTime = System.nanoTime();
        if (renderCount > 1) {
            long nanoseconds = nanoTime - lastPhysicsUpdate;
            float seconds = 1e-9f * nanoseconds;
            updatePhysics(seconds);

            long nanoTimeAfter = System.nanoTime();
            long updateNanos = nanoTimeAfter - nanoTime;
            totalPhysicsNanos += updateNanos;
        }
        this.lastPhysicsUpdate = nanoTime;

        cleanUpGeometries();
        super.render();
    }

    /**
     * Return the accumulated wall-clock time spent in physics simulation,
     * including step listeners.
     *
     * @return the total time (in nanoseconds)
     */
    protected static long totalPhysicsNanos() {
        return totalPhysicsNanos;
    }

    /**
     * Return the total time simulated.
     *
     * @return the total time (in simulated seconds)
     */
    protected static double totalSimulatedTime() {
        return totalSimulatedTime;
    }
    // *************************************************************************
    // private methods

    /**
     * Hide any geometries associated with physics objects that are no longer in
     * the PhysicsSpace.
     */
    private void cleanUpGeometries() {
        Collection<Geometry> geometriesToHide
                = new ArrayList<>(); // TODO garbage
        //System.out.println();
        for (Geometry geometry : listVisible()) {
            //System.out.println(
            //       "geometry type=" + geometry.getClass().getSimpleName());
            //System.out.println("  " + geometry.toString());
            if (geometry.wasRemovedFrom(physicsSpace)) {
                geometriesToHide.add(geometry);
            }
        }

        hideAll(geometriesToHide);
    }
}
