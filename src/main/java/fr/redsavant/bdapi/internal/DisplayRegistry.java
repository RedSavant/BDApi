package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.display.DisplayBackend;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DisplayRegistry {
    private final Map<UUID, DisplayCrate> crates = new ConcurrentHashMap<>();

    public void register(DisplayCrate crate) {
        crates.put(crate.uniqueId(), crate);
    }

    public void unregister(UUID uniqueId) {
        crates.remove(uniqueId);
    }

    public DisplayCrate get(UUID uniqueId) {
        return crates.get(uniqueId);
    }

    public Collection<DisplayCrate> all() {
        return crates.values();
    }

    public void removeAll() {
        for (DisplayCrate crate : crates.values()) {
            crate.handle().remove();
        }
        crates.clear();
    }

    /**
     * Drops the client-side displays and unregisters them.
     *
     * <p>Those displays only exist in the packets already sent, so they have to be destroyed even
     * when the caller intends to keep the server side entities around.
     *
     * @return how many displays have been removed
     */
    public int removePacketDisplays() {
        int removed = 0;
        for (DisplayCrate crate : crates.values()) {
            if (crate.handle().backend() != DisplayBackend.PACKET_EVENTS) {
                continue;
            }
            crate.handle().remove();
            unregister(crate.uniqueId());
            removed++;
        }
        return removed;
    }
}
