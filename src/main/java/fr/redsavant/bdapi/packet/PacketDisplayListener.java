package fr.redsavant.bdapi.packet;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.display.DisplayHandle;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PacketDisplayListener implements Listener {

    private final DisplayRegistry registry;

    public PacketDisplayListener(DisplayRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (DisplayCrate crate : registry.all()) {
            DisplayHandle handle = crate.handle();
            if (handle.global() && handle.backend() == fr.redsavant.bdapi.display.DisplayBackend.PACKET_EVENTS) {
                handle.show(uuid);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        for (DisplayCrate crate : registry.all()) {
            DisplayHandle handle = crate.handle();
            if (handle.isVisibleTo(uuid)) {
                handle.hide(uuid);
            }
        }
    }
}
