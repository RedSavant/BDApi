package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DisplayRegistry {
    private final Map<UUID, DisplayCrate> crates = new ConcurrentHashMap<>();

    public void register(DisplayCrate crate) {
        crates.put(crate.entity().getUniqueId(), crate);
    }

    public void unregister(UUID entityId) {
        crates.remove(entityId);
    }

    public DisplayCrate get(UUID entityId) {
        return crates.get(entityId);
    }

    public Collection<DisplayCrate> all() {
        return crates.values();
    }

    public void removeAll() {
        for (DisplayCrate crate : crates.values()) {
            if (crate.entity() != null && !crate.entity().isDead()) {
                crate.entity().remove();
            }
        }
        crates.clear();
    }
}
