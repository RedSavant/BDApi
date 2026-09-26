package fr.redsavant.bdapi.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;

final class PacketEventsProbe {

    private PacketEventsProbe() {
    }

    static boolean initialized() {
        try {
            PacketEventsAPI<?> api = PacketEvents.getAPI();
            return api != null && api.isLoaded();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
