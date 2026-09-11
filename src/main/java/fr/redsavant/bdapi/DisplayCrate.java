package fr.redsavant.bdapi;

import fr.redsavant.bdapi.animation.AnimationBuilder;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.display.DisplayHandle;
import fr.redsavant.bdapi.display.PaperDisplayHandle;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.physics.PhysicsBuilder;
import fr.redsavant.bdapi.timeline.Timeline;
import fr.redsavant.bdapi.transform.TransformHandle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class DisplayCrate {

    private final DisplayHandle handle;
    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;

    public DisplayCrate(DisplayHandle handle, Plugin plugin, DisplayRegistry registry, Animator animator, PhysicsEngine physicsEngine) {
        this.handle = handle;
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
        this.physicsEngine = physicsEngine;
    }

    public DisplayHandle handle() {
        return handle;
    }

    public UUID uniqueId() {
        return handle.uniqueId();
    }

    public int entityId() {
        return handle.entityId();
    }

    public DisplayBackend backend() {
        return handle.backend();
    }

    public Location location() {
        return handle.location();
    }

    public BlockData blockData() {
        return handle.blockData();
    }

    public void block(BlockData blockData) {
        handle.block(blockData);
    }

    public void block(Material material) {
        handle.block(material.createBlockData());
    }

    public Optional<BlockDisplay> bukkitEntity() {
        if (handle instanceof PaperDisplayHandle paper) {
            return Optional.of(paper.entity());
        }
        return Optional.empty();
    }

    @Deprecated
    public BlockDisplay entity() {
        return bukkitEntity().orElse(null);
    }

    public TransformHandle transform() {
        return new TransformHandle(this);
    }

    public AnimationBuilder animate() {
        return new AnimationBuilder(this, animator);
    }

    public PhysicsBuilder physics() {
        return new PhysicsBuilder(this, physicsEngine);
    }

    public Timeline timeline() {
        return new Timeline(this, animator, plugin);
    }

    public void show(Player player) {
        handle.show(player.getUniqueId());
    }

    public void hide(Player player) {
        handle.hide(player.getUniqueId());
    }

    public void addViewer(Player player) {
        handle.show(player.getUniqueId());
    }

    public void removeViewer(Player player) {
        handle.hide(player.getUniqueId());
    }

    public boolean isVisibleTo(Player player) {
        return handle.isVisibleTo(player.getUniqueId());
    }

    public Set<UUID> viewers() {
        return handle.viewers();
    }

    public void remove() {
        registry.unregister(handle.uniqueId());
        handle.remove();
    }

    public boolean isValid() {
        return handle.valid();
    }
}
