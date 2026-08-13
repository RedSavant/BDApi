package fr.redsavant.bdapi;

import fr.redsavant.bdapi.internal.DisplayRegistry;
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

    public DisplayCrate(BlockDisplay entity, Plugin plugin, DisplayRegistry registry) {
        this.entity = entity;
        this.plugin = plugin;
        this.registry = registry;
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
