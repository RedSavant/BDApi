package fr.redsavant.bdapi.builder;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.Displays;
import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.display.DisplaySettings;
import fr.redsavant.bdapi.display.PacketDisplayHandle;
import fr.redsavant.bdapi.display.PaperDisplayHandle;
import fr.redsavant.bdapi.display.Transform;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketDisplaySender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

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
    private DisplaySettings settings = DisplaySettings.defaults();
    private DisplayBackend backend;
    private Anchor anchor = Anchor.CENTER;
    private boolean global = false;
    private final Set<UUID> viewers = new LinkedHashSet<>();

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
        this.settings = settings.withBrightness(blockLight, skyLight);
        return this;
    }

    /**
     * Controls the orientation of the display relative to the player.
     * @param billboard
     * @return this
     */
    public BlockDisplayBuilder billboard(Display.Billboard billboard) {
        this.settings = settings.withBillboard(billboard);
        return this;
    }

    /**
     * View range of the block display
     * @param viewRange
     * @return this
     */
    public BlockDisplayBuilder viewRange(float viewRange) {
        this.settings = settings.withViewRange(viewRange);
        return this;
    }

    /**
     * Manage the shadow of the block display
     * @param radius of the shadow
     * @param strength of the shadow
     * @return
     */
    public BlockDisplayBuilder shadow(float radius, float strength) {
        this.settings = settings.withShadow(radius, strength);
        return this;
    }

    public BlockDisplayBuilder backend(DisplayBackend backend) {
        this.backend = backend;
        return this;
    }

    public BlockDisplayBuilder anchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public BlockDisplayBuilder viewer(Player player) {
        this.viewers.add(player.getUniqueId());
        return this;
    }

    public BlockDisplayBuilder viewers(Collection<? extends Player> players) {
        for (Player player : players) {
            this.viewers.add(player.getUniqueId());
        }
        return this;
    }

    public BlockDisplayBuilder global() {
        this.global = true;
        return this;
    }

    public DisplayCrate spawn() {
        if (location == null) {
            throw new IllegalStateException("You need to call .at(location) before .spawn().");
        }
        DisplayBackend resolved = backend != null ? backend : displays.defaultBackend();
        DisplayCrate crate = resolved == DisplayBackend.PACKET_EVENTS ? spawnPacket() : spawnPaper();
        registry.register(crate);
        return crate;
    }

    private DisplayCrate spawnPaper() {
        BlockDisplay entity = location.getWorld().spawn(location, BlockDisplay.class, this::configure);
        return new DisplayCrate(new PaperDisplayHandle(entity, anchor), plugin, registry, animator, physicsEngine);
    }

    private DisplayCrate spawnPacket() {
        PacketDisplaySender sender = displays.packetSender();
        if (sender == null) {
            throw new IllegalStateException(
                    "PacketEvents backend requested but PacketEvents is not installed or initialized. "
                    + "Add 'softdepend: [packetevents]' to your plugin.yml so PacketEvents loads first.");
        }
        PacketDisplayHandle handle = new PacketDisplayHandle(
                UUID.randomUUID(), displays.entityIdAllocator().next(), sender, global,
                location, buildTransform(), material, settings);
        DisplayCrate crate = new DisplayCrate(handle, plugin, registry, animator, physicsEngine);
        if (global) {
            // Viewers of another world are filtered out by the sender: a spawn packet carries no
            // dimension and they would render the display at the same coordinates in their world.
            for (Player online : Bukkit.getOnlinePlayers()) {
                handle.show(online.getUniqueId());
            }
        } else if (viewers.isEmpty()) {
            plugin.getLogger().warning("PACKET_EVENTS display spawned with no viewers and without .global(); "
                    + "it is visible to nobody until show()/addViewer() is called.");
        } else {
            for (UUID viewer : viewers) {
                handle.show(viewer);
            }
        }
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
        entity.setBillboard(settings.billboard());
        if (settings.hasBrightness()) {
            entity.setBrightness(new Display.Brightness(settings.brightnessBlock(), settings.brightnessSky()));
        }
        if (settings.hasViewRange()) {
            entity.setViewRange(settings.viewRange());
        }
        if (settings.hasShadowRadius()) {
            entity.setShadowRadius(settings.shadowRadius());
        }
        if (settings.hasShadowStrength()) {
            entity.setShadowStrength(settings.shadowStrength());
        }
        entity.setTransformation(buildTransform().toTransformation());
    }

    /**
     * Utility methode to build the transformation of a block display
     * @return Transform
     */
    private Transform buildTransform() {
        Quaternionf rotation = euleurToQuaternion(eulerRotation);
        return Transform.of(translation, rotation, scale, anchor);
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
