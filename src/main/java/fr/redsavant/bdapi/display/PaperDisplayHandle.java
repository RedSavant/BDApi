package fr.redsavant.bdapi.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public final class PaperDisplayHandle implements DisplayHandle {

    private final BlockDisplay entity;

    public PaperDisplayHandle(BlockDisplay entity) {
        this.entity = entity;
    }

    public BlockDisplay entity() {
        return entity;
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
    public Transformation transformation() {
        return entity.getTransformation();
    }

    @Override
    public void teleport(Location location) {
        entity.teleport(location);
    }

    @Override
    public void transformation(Transformation transformation) {
        entity.setTransformation(transformation);
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

    @Override
    public boolean isVisibleTo(UUID viewer) {
        return valid();
    }
}
