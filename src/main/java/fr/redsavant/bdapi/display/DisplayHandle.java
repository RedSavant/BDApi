package fr.redsavant.bdapi.display;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;
import java.util.UUID;

public interface DisplayHandle {

    DisplayBackend backend();

    UUID uniqueId();

    int entityId();

    Location location();

    /**
     * @return the current logical transform, anchor compensation not yet applied
     */
    Transform transform();

    void teleport(Location location);

    void transform(Transform transform);

    void block(Material material);

    boolean valid();

    void remove();

    boolean global();

    Set<UUID> viewers();

    void show(UUID viewer);

    void hide(UUID viewer);

    /**
     * Re-sends the display to a viewer that is already tracked, without touching the viewer set.
     * Required after a client side event that drops the entities of a dimension, such as a world
     * change or a respawn.
     *
     * @param viewer the viewer to re-send the display to
     */
    void resend(UUID viewer);

    boolean isVisibleTo(UUID viewer);
}
