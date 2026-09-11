package fr.redsavant.bdapi.packet;

public final class PacketBackendSupport {

    private static final String PACKETEVENTS_CLASS = "com.github.retrooper.packetevents.PacketEvents";

    private PacketBackendSupport() {
    }

    public static boolean present() {
        try {
            Class.forName(PACKETEVENTS_CLASS);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static boolean available() {
        return present() && PacketEventsProbe.initialized();
    }

    public static PacketDisplaySender createSender() {
        if (!available()) {
            throw new IllegalStateException(
                    "PacketEvents backend requested but PacketEvents is not installed or initialized.");
        }
        return new PacketEventsDisplaySender();
    }
}
