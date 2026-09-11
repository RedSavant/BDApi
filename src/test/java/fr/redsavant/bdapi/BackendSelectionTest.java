package fr.redsavant.bdapi;

import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketEntityIdAllocator;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender.Type;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BackendSelectionTest {

    private DisplayRegistry registry;
    private RecordingPacketDisplaySender sender;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        registry = new DisplayRegistry();
        sender = new RecordingPacketDisplaySender();
        plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("BackendSelectionTest"));
    }

    private Displays displays(DisplayBackend backend, RecordingPacketDisplaySender packetSender) {
        Animator animator = new Animator(plugin);
        PhysicsEngine physics = new PhysicsEngine(plugin, registry);
        return new Displays(plugin, registry, animator, physics, backend, packetSender, new PacketEntityIdAllocator());
    }

    @Test
    void defaultBackendIsPaper() {
        assertEquals(DisplayBackend.PAPER, BDApiConfig.defaults().defaultBackend());
    }

    @Test
    void explicitPacketBackendSpawnsClientSideDisplay() {
        DisplayCrate crate = displays(DisplayBackend.PAPER, sender).create()
                .backend(DisplayBackend.PACKET_EVENTS)
                .at(new Location(null, 0, 64, 0))
                .spawn();
        assertEquals(DisplayBackend.PACKET_EVENTS, crate.backend());
        assertTrue(crate.bukkitEntity().isEmpty());
    }

    @Test
    void defaultPacketBackendUsedWhenNotOverridden() {
        DisplayCrate crate = displays(DisplayBackend.PACKET_EVENTS, sender).create()
                .at(new Location(null, 0, 64, 0))
                .spawn();
        assertEquals(DisplayBackend.PACKET_EVENTS, crate.backend());
    }

    @Test
    void packetBackendWithoutSenderFails() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                displays(DisplayBackend.PACKET_EVENTS, null).create()
                        .at(new Location(null, 0, 64, 0))
                        .spawn());
        assertTrue(ex.getMessage().contains("PacketEvents backend requested"));
    }

    @Test
    void privateDisplayOnlySentToNamedViewer() {
        Player viewer = mock(Player.class);
        UUID viewerId = UUID.randomUUID();
        when(viewer.getUniqueId()).thenReturn(viewerId);

        DisplayCrate crate = displays(DisplayBackend.PACKET_EVENTS, sender).create()
                .backend(DisplayBackend.PACKET_EVENTS)
                .viewer(viewer)
                .at(new Location(null, 0, 64, 0))
                .spawn();

        assertEquals(1, crate.viewers().size());
        assertEquals(1, sender.countFor(Type.SPAWN, viewerId));
    }

    @Test
    void removeUnregistersAndShutdownCleansUp() {
        Displays displays = displays(DisplayBackend.PACKET_EVENTS, sender);
        DisplayCrate a = displays.create().at(new Location(null, 0, 64, 0)).spawn();
        DisplayCrate b = displays.create().at(new Location(null, 1, 64, 0)).spawn();

        assertEquals(2, registry.all().size());
        a.remove();
        assertFalse(a.isValid());
        assertEquals(1, registry.all().size());

        registry.removeAll();
        assertFalse(b.isValid());
        assertTrue(registry.all().isEmpty());
    }
}
