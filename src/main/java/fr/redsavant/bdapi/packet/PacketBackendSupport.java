package fr.redsavant.bdapi.packet;

public final class PacketBackendSupport {

    private static final String PACKETEVENTS_CLASS = "com.github.retrooper.packetevents.PacketEvents";

    private static volatile PacketDisplaySender cached;

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

    /**
     * Resolves the sender on demand instead of once at startup: PacketEvents may still be loading
     * when the consuming plugin calls {@code BDApi.init}, and caching that miss would disable the
     * backend for the rest of the server lifetime.
     *
     * @return the sender, or {@code null} while PacketEvents is not ready
     */
    public static PacketDisplaySender sender() {
        PacketDisplaySender local = cached;
        if (local != null) {
            return local;
        }
        if (!available()) {
            return null;
        }
        synchronized (PacketBackendSupport.class) {
            if (cached == null) {
                cached = new PacketEventsDisplaySender();
            }
            return cached;
        }
    }

    public static PacketDisplaySender createSender() {
        if (!available()) {
            throw new IllegalStateException(
                    "PacketEvents backend requested but PacketEvents is not installed or initialized.");
        }
        return sender();
    }
}
