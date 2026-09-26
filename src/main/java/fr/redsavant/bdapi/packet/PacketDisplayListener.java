package fr.redsavant.bdapi.packet;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.display.DisplayHandle;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Keeps the client-side displays in sync with the lifecycle of their viewers.
 *
 * <p>Client-side entities do not survive the events that make a client drop the entities of a
 * dimension, so they have to be spawned again once the player is back.
 */
public final class PacketDisplayListener implements Listener {

    private final DisplayRegistry registry;
    private final Plugin plugin;

    public PacketDisplayListener(DisplayRegistry registry, Plugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (DisplayCrate crate : registry.all()) {
            DisplayHandle handle = crate.handle();
            if (isGlobalPacketDisplay(handle)) {
                handle.show(uuid);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (DisplayCrate crate : registry.all()) {
            DisplayHandle handle = crate.handle();
            if (handle.isVisibleTo(uuid)) {
                handle.hide(uuid);
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        resync(event.getPlayer());
    }

    /**
     * A respawn makes the client reload the world, whether or not the dimension changed. The event
     * fires before the player is moved, so the resend has to wait for the next tick.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                resync(player);
            }
        });
    }

    /**
     * Re-sends the displays of the world the player is now in. The player is already tracked at
     * this point, so the resend has to happen without touching the viewer set.
     */
    private void resync(Player player) {
        UUID uuid = player.getUniqueId();
        for (DisplayCrate crate : registry.all()) {
            DisplayHandle handle = crate.handle();
            if (isGlobalPacketDisplay(handle) && handle.isVisibleTo(uuid)) {
                handle.resend(uuid);
            }
        }
    }

    /**
     * Stops handling the events, so a re-initialized BDApi does not leave a listener bound to a
     * stale registry.
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    private static boolean isGlobalPacketDisplay(DisplayHandle handle) {
        return handle.global() && handle.backend() == DisplayBackend.PACKET_EVENTS;
    }
}
