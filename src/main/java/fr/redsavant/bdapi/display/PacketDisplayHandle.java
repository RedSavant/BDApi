package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.packet.PacketDisplaySender;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Transformation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class PacketDisplayHandle implements DisplayHandle {

    private final UUID uuid;
    private final int entityId;
    private final PacketDisplaySender sender;
    private final boolean global;
    private final Set<UUID> viewers = new LinkedHashSet<>();

    private Location location;
    private final BlockDisplayState state;
    private boolean removed;

    public PacketDisplayHandle(UUID uuid, int entityId, PacketDisplaySender sender, boolean global,
                               Location location, BlockDisplayState state) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.sender = sender;
        this.global = global;
        this.location = location.clone();
        this.state = state;
    }

    public BlockDisplayState state() {
        return state;
    }

    @Override
    public DisplayBackend backend() {
        return DisplayBackend.PACKET_EVENTS;
    }

    @Override
    public UUID uniqueId() {
        return uuid;
    }

    @Override
    public int entityId() {
        return entityId;
    }

    @Override
    public Location location() {
        return location.clone();
    }

    @Override
    public Transformation transformation() {
        return state.transformation();
    }

    @Override
    public BlockData blockData() {
        return state.blockData();
    }

    @Override
    public void teleport(Location target) {
        if (removed || target.equals(location)) {
            return;
        }
        this.location = target.clone();
        for (UUID viewer : viewers) {
            sender.teleport(viewer, this);
        }
    }

    @Override
    public void transformation(Transformation target) {
        if (removed || target.equals(state.transformation())) {
            return;
        }
        state.transformation(target);
        for (UUID viewer : viewers) {
            sender.metadata(viewer, this);
        }
    }

    @Override
    public void block(BlockData target) {
        if (removed || target.matches(state.blockData())) {
            return;
        }
        state.blockData(target);
        for (UUID viewer : viewers) {
            sender.metadata(viewer, this);
        }
    }

    @Override
    public boolean valid() {
        return !removed;
    }

    @Override
    public void remove() {
        if (removed) {
            return;
        }
        for (UUID viewer : viewers) {
            sender.destroy(viewer, this);
        }
        viewers.clear();
        removed = true;
    }

    @Override
    public boolean global() {
        return global;
    }

    @Override
    public Set<UUID> viewers() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(viewers));
    }

    @Override
    public void show(UUID viewer) {
        if (removed || !viewers.add(viewer)) {
            return;
        }
        sender.spawn(viewer, this);
        sender.metadata(viewer, this);
    }

    @Override
    public void hide(UUID viewer) {
        if (!viewers.remove(viewer)) {
            return;
        }
        sender.destroy(viewer, this);
    }

    @Override
    public boolean isVisibleTo(UUID viewer) {
        return viewers.contains(viewer);
    }
}
