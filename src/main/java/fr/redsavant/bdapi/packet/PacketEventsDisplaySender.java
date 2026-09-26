package fr.redsavant.bdapi.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import fr.redsavant.bdapi.display.PacketDisplayHandle;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketEventsDisplaySender implements PacketDisplaySender {

    private final Map<Material, Integer> blockStateIds = new ConcurrentHashMap<>();

    @Override
    public void spawn(UUID viewer, PacketDisplayHandle display) {
        Location location = display.location();
        com.github.retrooper.packetevents.protocol.world.Location peLocation =
                new com.github.retrooper.packetevents.protocol.world.Location(
                        location.getX(), location.getY(), location.getZ(),
                        location.getYaw(), location.getPitch());
        send(viewer, display, new WrapperPlayServerSpawnEntity(
                display.entityId(), display.uniqueId(), EntityTypes.BLOCK_DISPLAY,
                peLocation, location.getYaw(), 0, new Vector3d()));
    }

    @Override
    public void metadata(UUID viewer, PacketDisplayHandle display) {
        send(viewer, display, new WrapperPlayServerEntityMetadata(display.entityId(),
                PacketDisplayMetadataCodec.encode(display)));
    }

    @Override
    public void teleport(UUID viewer, PacketDisplayHandle display) {
        Location location = display.location();
        send(viewer, display, new WrapperPlayServerEntityTeleport(
                display.entityId(),
                new Vector3d(location.getX(), location.getY(), location.getZ()),
                location.getYaw(), location.getPitch(), true));
    }

    @Override
    public void destroy(UUID viewer, PacketDisplayHandle display) {
        send(viewer, display, new WrapperPlayServerDestroyEntities(display.entityId()));
    }

    @Override
    public int blockStateId(Material material) {
        return blockStateIds.computeIfAbsent(material,
                key -> SpigotConversionUtil.fromBukkitBlockData(key.createBlockData()).getGlobalId());
    }

    /**
     * Sends a packet to a viewer of the display world.
     *
     * <p>No packet of this family carries a dimension, so a viewer standing in another world would
     * render the fake display at the same coordinates in its own world. Such packets are dropped
     * here; the display is re-sent when the viewer comes back, which the listener does on world
     * change and respawn.
     */
    private void send(UUID viewer, PacketDisplayHandle display, PacketWrapper<?> wrapper) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !sameWorld(player.getWorld(), display.location().getWorld())) {
            return;
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
    }

    private static boolean sameWorld(World playerWorld, World displayWorld) {
        return playerWorld == null || displayWorld == null || playerWorld.equals(displayWorld);
    }
}
