package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;

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
}
