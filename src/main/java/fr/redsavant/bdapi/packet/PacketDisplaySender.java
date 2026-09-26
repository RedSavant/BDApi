package fr.redsavant.bdapi.packet;

import fr.redsavant.bdapi.display.PacketDisplayHandle;
import org.bukkit.Material;

import java.util.UUID;

public interface PacketDisplaySender {

    void spawn(UUID viewer, PacketDisplayHandle display);

    void metadata(UUID viewer, PacketDisplayHandle display);

    void teleport(UUID viewer, PacketDisplayHandle display);

    void destroy(UUID viewer, PacketDisplayHandle display);

    /**
     * Resolves the global block state id of a material. Implementations are expected to cache the
     * result: metadata updates run on every animated tick for every viewer.
     *
     * @param material the material of the display
     * @return the protocol block state id
     */
    int blockStateId(Material material);
}
