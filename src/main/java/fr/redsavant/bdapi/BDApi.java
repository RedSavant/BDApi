package fr.redsavant.bdapi;

import org.bukkit.plugin.Plugin;

public final class BDApi {

    private static BDApi instance;

    private final Plugin plugin;

    private BDApi(Plugin plugin) {
        this.plugin = plugin;
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
    public static synchronized void shutdown() {
        if (instance == null) return;

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
}