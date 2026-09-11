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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PacketEventsDisplaySender implements PacketDisplaySender {

    @Override
    public void spawn(UUID viewer, PacketDisplayHandle display) {
        Location location = display.location();
        com.github.retrooper.packetevents.protocol.world.Location peLocation =
                new com.github.retrooper.packetevents.protocol.world.Location(
                        location.getX(), location.getY(), location.getZ(),
                        location.getYaw(), location.getPitch());
        send(viewer, new WrapperPlayServerSpawnEntity(
                display.entityId(), display.uniqueId(), EntityTypes.BLOCK_DISPLAY,
                peLocation, location.getYaw(), 0, new Vector3d()));
    }

    @Override
    public void metadata(UUID viewer, PacketDisplayHandle display) {
        send(viewer, new WrapperPlayServerEntityMetadata(display.entityId(), PacketDisplayMetadataCodec.encode(display)));
    }

    @Override
    public void teleport(UUID viewer, PacketDisplayHandle display) {
        Location location = display.location();
        send(viewer, new WrapperPlayServerEntityTeleport(
                display.entityId(),
                new Vector3d(location.getX(), location.getY(), location.getZ()),
                location.getYaw(), location.getPitch(), true));
    }

    @Override
    public void destroy(UUID viewer, PacketDisplayHandle display) {
        send(viewer, new WrapperPlayServerDestroyEntities(display.entityId()));
    }

    private void send(UUID viewer, PacketWrapper<?> wrapper) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null) {
            return;
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
    }
}
