package fr.redsavant.bdapi.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Transformation;

import java.util.Set;
import java.util.UUID;

public interface DisplayHandle {

    DisplayBackend backend();

    UUID uniqueId();

    int entityId();

    Location location();

    Transformation transformation();

    BlockData blockData();

    void teleport(Location location);

    void transformation(Transformation transformation);

    void block(BlockData blockData);

    @Deprecated
    default void block(Material material) {
        block(material.createBlockData());
    }

    boolean valid();

    void remove();

    boolean global();

    Set<UUID> viewers();

    void show(UUID viewer);

    void hide(UUID viewer);

    boolean isVisibleTo(UUID viewer);
}
