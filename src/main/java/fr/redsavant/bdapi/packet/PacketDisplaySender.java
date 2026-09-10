package fr.redsavant.bdapi.packet;

import fr.redsavant.bdapi.display.PacketDisplayHandle;

import java.util.UUID;

public interface PacketDisplaySender {

    void spawn(UUID viewer, PacketDisplayHandle display);

    void metadata(UUID viewer, PacketDisplayHandle display);

    void teleport(UUID viewer, PacketDisplayHandle display);

    void destroy(UUID viewer, PacketDisplayHandle display);
}
