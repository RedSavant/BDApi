package fr.redsavant.bdapi;

import fr.redsavant.bdapi.internal.DisplayRegistry;
import org.bukkit.plugin.Plugin;

public final class BDApi {

    private static BDApi instance;

    private final Plugin plugin;
    private final DisplayRegistry registry;

    private BDApi(Plugin plugin) {
        this.plugin = plugin;
        this.registry = new DisplayRegistry();
    }

    /**
     * This method initalize the BDApi.
     * @param plugin
     * @return instance
     */
    public static synchronized BDApi init(Plugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("DBApi is already loaded");
        }
        instance = new BDApi(plugin);
        return instance;
    }

    /**
     * Method to shut down the BDApi.
     */
    public static synchronized void shutdown(boolean removeEntities) {
        if (instance == null) return;
        if (removeEntities) {
            instance.registry.removeAll();
        }
        instance = null;
    }

    /**
     * Get the api instance.
     * @return instance
     */
    public static BDApi get() {
        if (instance == null) {
            throw new IllegalStateException("BDApi is not initalized. Pls init it whit DisplayAPI.init(plugin) at the start of your plugin.");
        }
        return instance;
    }

    public Plugin plugin() {
        return plugin;
    }
}