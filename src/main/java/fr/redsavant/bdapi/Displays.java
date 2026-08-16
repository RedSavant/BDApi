package fr.redsavant.bdapi;

import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.group.DisplayGroup;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import org.bukkit.plugin.Plugin;

/**
 * Endpoint to create displays
 */

public final class Displays {

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;

    public Displays(Plugin plugin, DisplayRegistry registry, Animator animator, PhysicsEngine physicsEngine) {
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
        this.physicsEngine = physicsEngine;
    }

    public BlockDisplayBuilder create() {
        return new BlockDisplayBuilder(this, plugin, registry, animator, physicsEngine);
    }

    public DisplayGroup group() {
        return new DisplayGroup(plugin, registry, animator);
    }

    public Plugin plugin() {
        return plugin;
    }
    public DisplayRegistry registry() {
        return registry;
    }
    public Animator animator() { return animator; }
}
