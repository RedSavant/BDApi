package fr.redsavant.bdapi;

import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.group.DisplayGroup;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketDisplaySender;
import fr.redsavant.bdapi.packet.PacketEntityIdAllocator;
import org.bukkit.plugin.Plugin;

public final class Displays {

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;
    private final DisplayBackend defaultBackend;
    private final PacketDisplaySender packetSender;
    private final PacketEntityIdAllocator entityIdAllocator;

    public Displays(Plugin plugin, DisplayRegistry registry, Animator animator, PhysicsEngine physicsEngine,
                    DisplayBackend defaultBackend, PacketDisplaySender packetSender,
                    PacketEntityIdAllocator entityIdAllocator) {
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
        this.physicsEngine = physicsEngine;
        this.defaultBackend = defaultBackend;
        this.packetSender = packetSender;
        this.entityIdAllocator = entityIdAllocator;
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

    public Animator animator() {
        return animator;
    }

    public DisplayBackend defaultBackend() {
        return defaultBackend;
    }

    public PacketDisplaySender packetSender() {
        return packetSender;
    }

    public PacketEntityIdAllocator entityIdAllocator() {
        return entityIdAllocator;
    }
}
