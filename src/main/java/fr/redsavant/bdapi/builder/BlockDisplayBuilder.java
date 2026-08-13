package fr.redsavant.bdapi.builder;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.Displays;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class BlockDisplayBuilder {

    private final Displays displays;
    private Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;

    private Location location;
    private Material material = Material.STONE; // Default material if not defined in the builder
    private final Vector3f scale = new Vector3f(1f, 1f, 1f); // Default
    private final Vector3f translation = new Vector3f(0f, 0f, 0f); // Default
    private final Vector3f eulerRotation = new Vector3f(0f, 0f, 0f); // Default
    private int brightnessBlock = -1; // Default
    private int brightnessSky = -1; // Default
    private Display.Billboard billboard = Display.Billboard.FIXED; // Default
    private float viewRange = -1f; // Default
    private float shadowRadius = -1f; // Default
    private float shadowStrength = -1f; // Default

    public BlockDisplayBuilder(Displays displays, Plugin plugin, DisplayRegistry registry, Animator animator,
                               PhysicsEngine physicsEngine) {

        this.displays = displays;
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
        this.physicsEngine = physicsEngine;
    }

    /**
     * Location of the block display
     * @param location
     * @return this
     */
    public BlockDisplayBuilder at(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Material of the block display
     * @param material
     * @return this
     */
    public BlockDisplayBuilder block(Material material) {
        if (!material.isBlock()) {
            throw new IllegalArgumentException(material + " is not a bloc.");
        }
        this.material = material;
        return this;
    }

    /**
     * Uniform scale for the block display
     * @param uniform
     * @return this
     */
    public BlockDisplayBuilder scale(float uniform) {
        this.scale.set(uniform, uniform, uniform);
        return this;
    }

    /**
     * Scale of the block display
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public BlockDisplayBuilder scale(float x, float y, float z) {
        this.scale.set(x, y, z);
        return this;
    }

    /**
     * Translation of the block display
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public BlockDisplayBuilder translate(float x, float y, float z) {
        this.translation.set(x, y, z);
        return this;
    }

    /**
     * Rotation in degrees of the block display (pitch=x, yaw=y, roll=z), applied as an Euler rotation ZYX.
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public BlockDisplayBuilder rotate(float x, float y, float z) {
        this.eulerRotation.set(x, y, z);
        return this;
    }

    /**
     * Brightness of the block display
     * @param blockLight
     * @param skyLight
     * @return this
     */
    public BlockDisplayBuilder brightness(int blockLight, int skyLight) {
        this.brightnessBlock = blockLight;
        this.brightnessSky = skyLight;
        return this;
    }

    /**
     * Controls the orientation of the display relative to the player.
     * @param billboard
     * @return this
     */
    public BlockDisplayBuilder billboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    /**
     * View range of the block display
     * @param viewRange
     * @return this
     */
    public BlockDisplayBuilder viewRange(float viewRange) {
        this.viewRange = viewRange;
        return this;
    }

    /**
     * Manage the shadow of the block display
     * @param radius of the shadow
     * @param strength of the shadow
     * @return
     */
    public BlockDisplayBuilder shadow(float radius, float strength) {
        this.shadowRadius = radius;
        this.shadowStrength = strength;
        return this;
    }

    /**
     * Build and spawn the block display
     * @return crate
     */
    public DisplayCrate spawn() {
        if (location == null) {
            throw new IllegalStateException("You need to call .at(location) before .spawn().");
        }
        BlockDisplay entity = location.getWorld().spawn(location, BlockDisplay.class, this::configure);
        DisplayCrate crate = new DisplayCrate(entity, plugin, registry, animator, physicsEngine);
        registry.register(crate);
        return crate;
    }

    /**
     * Alias of spawn
     * @return spawn
     */
    public DisplayCrate build() {
        return spawn();
    }

    /**
     * Set parameters of block display
     * @param entity
     */
    private void configure(BlockDisplay entity) {
        entity.setBlock(material.createBlockData());
        entity.setBillboard(billboard);
        if (brightnessBlock >= 0 && brightnessSky >= 0) {
            entity.setBrightness(new Display.Brightness(brightnessBlock, brightnessSky));
        }
        if (viewRange >= 0) {
            entity.setViewRange(viewRange);
        }
        if (shadowRadius >= 0) {
            entity.setShadowRadius(shadowRadius);
        }
        if (shadowStrength >= 0) {
            entity.setShadowStrength(shadowStrength);
        }
        entity.setTransformation(buildTransformation());
    }

    /**
     * Utility methode to build the transformation of a block display
     * @return Transformation
     */
    private Transformation buildTransformation() {
        Quaternionf rotation = euleurToQuaternion(eulerRotation);
        return new Transformation(
                new Vector3f(translation),
                rotation,
                new Vector3f(scale),
                new Quaternionf()
        );
    }

    /**
     * Utility method to use the Euler convertion
     * @param eulerDegrees
     * @return Quaternionf
     */
    public static Quaternionf euleurToQuaternion(Vector3f eulerDegrees) {
        Quaternionf q = new Quaternionf();

        q.rotateY((float) Math.toRadians(eulerDegrees.y));
        q.rotateX((float) Math.toRadians(eulerDegrees.x));
        q.rotateZ((float) Math.toRadians(eulerDegrees.z));

        return q;
    }

    public Location location() {
        return location;
    }
    public Material material() {
        return material;
    }
}
