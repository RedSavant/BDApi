package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.packet.PacketDisplaySender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    private Transform transform;
    private DisplaySettings settings;
    private Material material;
    private Integer blockStateId;
    private boolean removed;

    public PacketDisplayHandle(UUID uuid, int entityId, PacketDisplaySender sender, boolean global,
                               Location location, Transform transform, Material material,
                               DisplaySettings settings) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.sender = sender;
        this.global = global;
        this.location = location.clone();
        this.transform = transform;
        this.material = material;
        this.settings = settings == null ? DisplaySettings.defaults() : settings;
    }

    public Material material() {
        return material;
    }

    public DisplaySettings settings() {
        return settings;
    }

    /**
     * @return the block state id sent to the clients, resolved once per material change
     */
    public int blockStateId() {
        Integer cached = blockStateId;
        if (cached != null) {
            return cached;
        }
        cached = sender.blockStateId(material);
        blockStateId = cached;
        return cached;
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
    public Transform transform() {
        return transform;
    }

    public Transformation transformation() {
        return transform.toTransformation();
    }

    @Override
    public void teleport(Location target) {
        if (removed || target.equals(location)) {
            return;
        }
        World previous = location.getWorld();
        World next = target.getWorld();
        this.location = target.clone();

        if (sameWorld(previous, next)) {
            for (UUID viewer : viewers) {
                sender.teleport(viewer, this);
            }
            return;
        }
        // An entity teleport packet carries coordinates but no dimension: the client would keep the
        // fake entity in its previous world, so it has to be destroyed and spawned again.
        respawnForViewers();
    }

    @Override
    public void transform(Transform target) {
        if (removed || target.equals(transform)) {
            return;
        }
        this.transform = target;
        for (UUID viewer : viewers) {
            sender.metadata(viewer, this);
        }
    }

    @Override
    public void block(Material target) {
        if (removed || target == material) {
            return;
        }
        this.material = target;
        this.blockStateId = null;
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
    public void resend(UUID viewer) {
        if (removed || !viewers.contains(viewer)) {
            return;
        }
        sender.destroy(viewer, this);
        sender.spawn(viewer, this);
        sender.metadata(viewer, this);
    }

    @Override
    public boolean isVisibleTo(UUID viewer) {
        return viewers.contains(viewer);
    }

    private void respawnForViewers() {
        for (UUID viewer : viewers) {
            sender.destroy(viewer, this);
            sender.spawn(viewer, this);
            sender.metadata(viewer, this);
        }
    }

    private static boolean sameWorld(World first, World second) {
        return first == null || second == null || first.equals(second);
    }
}
