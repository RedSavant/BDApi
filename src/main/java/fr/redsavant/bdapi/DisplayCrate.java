package fr.redsavant.bdapi;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.Plugin;

/**
 * Represents a BlockDisplay spawn
 */

public final class DisplayCrate {
    private final BlockDisplay entity;
    private final Plugin plugin;

    public DisplayCrate(BlockDisplay entity, Plugin plugin) {
        this.entity = entity;
        this.plugin = plugin;
    }

    public BlockDisplay entity() {
        return entity;
    }

    public Location location() {
        return entity.getLocation();
    }

    public void remove() {
        if (!entity.isDead()) {
            entity.remove();
        }
    }

    public boolean isValid() {
        return entity != null && !entity.isDead();
    }
}
