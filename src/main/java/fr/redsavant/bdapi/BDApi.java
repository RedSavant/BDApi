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

import java.util.function.Supplier;

public final class BDApi {

    private static BDApi instance;

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;
    private final PhysicsEngine physicsEngine;
    private final Displays displays;
    private final Effects effects;
    private final DisplayBackend defaultBackend;
    private final PacketDisplayListener packetListener;

    private BDApi(Plugin plugin, BDApiConfig config) {
        this.plugin = plugin;
        this.registry = new DisplayRegistry();
        this.animator = new Animator(plugin);
        this.physicsEngine = new PhysicsEngine(plugin, registry);
        this.defaultBackend = config.defaultBackend();
        this.displays = new Displays(plugin, registry, animator, physicsEngine,
                defaultBackend, resolveSender(config), new PacketEntityIdAllocator());
        this.effects = new Effects(displays, plugin);
        this.packetListener = new PacketDisplayListener(registry, plugin);

        this.animator.start();
        this.physicsEngine.start();
        if (PacketBackendSupport.present()) {
            plugin.getServer().getPluginManager().registerEvents(packetListener, plugin);
        }
    }

    private static Supplier<PacketDisplaySender> resolveSender(BDApiConfig config) {
        if (config.packetSender() != null) {
            PacketDisplaySender provided = config.packetSender();
            return () -> provided;
        }
        return PacketBackendSupport::sender;
    }

    public static synchronized BDApi init(Plugin plugin) {
        return init(plugin, BDApiConfig.defaults());
    }

    public static synchronized BDApi init(Plugin plugin, BDApiConfig config) {
        if (instance != null) {
            throw new IllegalStateException("BDApi is already loaded");
        }
        instance = new BDApi(plugin, config);
        return instance;
    }

    public static synchronized void shutdown(boolean removeEntities) {
        if (instance == null) return;
        instance.animator.stop();
        instance.physicsEngine.stop();
        instance.packetListener.unregister();
        // Client-side displays live in the packets that were already sent, so they have to be
        // destroyed even when the server entities are deliberately kept.
        instance.registry.removePacketDisplays();
        if (removeEntities) {
            instance.registry.removeAll();
        }
        instance = null;
    }

    public static BDApi get() {
        if (instance == null) {
            throw new IllegalStateException("BDApi is not initialized. Call BDApi.init(plugin) at the start of your plugin.");
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
