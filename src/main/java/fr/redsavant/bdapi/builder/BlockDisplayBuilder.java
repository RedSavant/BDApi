package fr.redsavant.bdapi.builder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.plugin.Plugin;
import org.joml.Vector3f;

public final class BlockDisplayBuilder {

    private Plugin plugin;

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

    public BlockDisplayBuilder(Plugin plugin) {
        this.plugin = plugin;
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
}
