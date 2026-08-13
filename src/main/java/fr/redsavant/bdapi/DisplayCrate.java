package fr.redsavant.bdapi;

import fr.redsavant.bdapi.animation.AnimationBuilder;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.physics.PhysicsBuilder;
import fr.redsavant.bdapi.transform.TransformHandle;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.Plugin;

/**
 * Represents a BlockDisplay spawn
 */

public final class DisplayCrate {
    private final BlockDisplay entity;
    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;

    public DisplayCrate(BlockDisplay entity, Plugin plugin, DisplayRegistry registry, Animator animator, PhysicsEngine physicsEngine) {
        this.entity = entity;
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
        this.physicsEngine = physicsEngine;
    }

    public BlockDisplay entity() {
        return entity;
    }

    public Location location() {
        return entity.getLocation();
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

    public void remove() {
        registry.unregister(entity.getUniqueId());
        if (!entity.isDead()) {
            entity.remove();
        }
    }

    public boolean isValid() {
        return entity != null && !entity.isDead();
    }
}
