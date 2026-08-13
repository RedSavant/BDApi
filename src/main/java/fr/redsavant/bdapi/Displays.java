package fr.redsavant.bdapi;

import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import org.bukkit.plugin.Plugin;

/**
 * Endpoint to create displays
 */

public final class Displays {

    private final Plugin plugin;
    private final DisplayRegistry registry;

    public Displays(Plugin plugin, DisplayRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public BlockDisplayBuilder create() {
        return new BlockDisplayBuilder(this, plugin, registry);
    }

    public Plugin plugin() {
        return plugin;
    }
    public DisplayRegistry registry() {
        return registry;
    }
}
