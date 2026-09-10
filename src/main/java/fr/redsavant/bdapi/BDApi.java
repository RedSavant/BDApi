package fr.redsavant.bdapi;

import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.effects.Effects;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketBackendSupport;
import fr.redsavant.bdapi.packet.PacketDisplayListener;
import fr.redsavant.bdapi.packet.PacketDisplaySender;
import fr.redsavant.bdapi.packet.PacketEntityIdAllocator;
import org.bukkit.plugin.Plugin;

public final class BDApi {

    private static BDApi instance;

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;
    private final Displays displays;
    private final Effects effects;
    private final DisplayBackend defaultBackend;
    private final PacketDisplaySender packetSender;

    private BDApi(Plugin plugin, BDApiConfig config) {
        this.plugin = plugin;
        this.registry = new DisplayRegistry();
        this.animator = new Animator(plugin);
        this.physicsEngine = new PhysicsEngine(plugin, registry);
        this.defaultBackend = config.defaultBackend();
        this.packetSender = resolveSender(config);
        this.displays = new Displays(plugin, registry, animator, physicsEngine,
                defaultBackend, packetSender, new PacketEntityIdAllocator());
        this.effects = new Effects(displays, plugin);

        this.animator.start();
        this.physicsEngine.start();
        if (packetSender != null) {
            plugin.getServer().getPluginManager().registerEvents(new PacketDisplayListener(registry), plugin);
        }
    }

    private static PacketDisplaySender resolveSender(BDApiConfig config) {
        if (config.packetSender() != null) {
            return config.packetSender();
        }
        if (PacketBackendSupport.available()) {
            return PacketBackendSupport.createSender();
        }
        return null;
    }

    public static synchronized BDApi init(Plugin plugin) {
        return init(plugin, BDApiConfig.defaults());
    }

    public static synchronized BDApi init(Plugin plugin, BDApiConfig config) {
        if (instance != null) {
            throw new IllegalStateException("DBApi is already loaded");
        }
        instance = new BDApi(plugin, config);
        return instance;
    }

    public static synchronized void shutdown(boolean removeEntities) {
        if (instance == null) return;
        instance.animator.stop();
        instance.physicsEngine.stop();
        if (removeEntities) {
            instance.registry.removeAll();
        }
        instance = null;
    }

    public static BDApi get() {
        if (instance == null) {
            throw new IllegalStateException("BDApi is not initalized. Pls init it whit DisplayAPI.init(plugin) at the start of your plugin.");
        }
        return instance;
    }

    public Displays displays() {
        return displays;
    }

    public Effects effects() {
        return effects;
    }

    public Plugin plugin() {
        return plugin;
    }

    public DisplayBackend defaultBackend() {
        return defaultBackend;
    }
}
