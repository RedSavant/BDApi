package fr.redsavant.bdapi.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public final class PaperDisplayHandle implements DisplayHandle {

    private final BlockDisplay entity;
    private final Anchor anchor;

    public PaperDisplayHandle(BlockDisplay entity, Anchor anchor) {
        this.entity = entity;
        this.anchor = anchor == null ? Anchor.CENTER : anchor;
    }

    public PaperDisplayHandle(BlockDisplay entity) {
        this(entity, Anchor.CENTER);
    }

    public BlockDisplay entity() {
        return entity;
    }

    public Anchor anchor() {
        return anchor;
    }

    @Override
    public DisplayBackend backend() {
        return DisplayBackend.PAPER;
    }

    @Override
    public UUID uniqueId() {
        return entity.getUniqueId();
    }

    @Override
    public int entityId() {
        return entity.getEntityId();
    }

    @Override
    public Location location() {
        return entity.getLocation();
    }

    @Override
    public Transform transform() {
        return Transform.from(entity.getTransformation(), anchor);
    }

    @Override
    public void teleport(Location location) {
        entity.teleport(location);
    }

    @Override
    public void transform(Transform transform) {
        entity.setTransformation(transform.toTransformation());
    }

    @Override
    public void block(Material material) {
        entity.setBlock(material.createBlockData());
    }

    @Override
    public boolean valid() {
        return !entity.isDead();
    }

    @Override
    public void remove() {
        if (!entity.isDead()) {
            entity.remove();
        }
    }

    @Override
    public boolean global() {
        return true;
    }

    @Override
    public Set<UUID> viewers() {
        return Collections.emptySet();
    }

    @Override
    public void show(UUID viewer) {
    }

    @Override
    public void hide(UUID viewer) {
    }

    /**
     * Paper displays are real world entities, the client follows the dimension changes on its own.
     */
    @Override
    public void resend(UUID viewer) {
    }

    @Override
    public boolean isVisibleTo(UUID viewer) {
        return valid();
    }
}
