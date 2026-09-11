package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.support.DisplayStates;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender.Type;
import org.bukkit.Location;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketDisplayHandleTest {

    private RecordingPacketDisplaySender sender;
    private final UUID viewerA = UUID.randomUUID();
    private final UUID viewerB = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sender = new RecordingPacketDisplaySender();
    }

    private PacketDisplayHandle handle(boolean global) {
        return new PacketDisplayHandle(UUID.randomUUID(), 7, sender, global,
                new Location(null, 1, 2, 3), DisplayStates.simple());
    }

    @Test
    void showRegistersViewerAndSendsSpawnAndMetadata() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        assertTrue(h.isVisibleTo(viewerA));
        assertEquals(1, sender.countFor(Type.SPAWN, viewerA));
        assertEquals(1, sender.countFor(Type.METADATA, viewerA));
    }

    @Test
    void duplicateShowIsIgnored() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        h.show(viewerA);
        assertEquals(1, h.viewers().size());
        assertEquals(1, sender.count(Type.SPAWN));
    }

    @Test
    void hideRemovesViewerAndSendsDestroy() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        h.hide(viewerA);
        assertFalse(h.isVisibleTo(viewerA));
        assertEquals(1, sender.countFor(Type.DESTROY, viewerA));
    }

    @Test
    void teleportOnlySentToViewers() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        h.teleport(new Location(null, 5, 6, 7));
        assertEquals(1, sender.countFor(Type.TELEPORT, viewerA));
        assertEquals(0, sender.countFor(Type.TELEPORT, viewerB));
    }

    @Test
    void unchangedTeleportIsSkipped() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        h.teleport(new Location(null, 1, 2, 3));
        assertEquals(0, sender.count(Type.TELEPORT));
    }

    @Test
    void metadataOnlySentToViewersOnTransformChange() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        long before = sender.countFor(Type.METADATA, viewerA);
        h.transformation(Anchor.CENTER.toTransformation(new Vector3f(), new Quaternionf(), new Vector3f(2, 2, 2)));
        assertEquals(before + 1, sender.countFor(Type.METADATA, viewerA));
        assertEquals(0, sender.countFor(Type.METADATA, viewerB));
    }

    @Test
    void unchangedTransformIsSkipped() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        long before = sender.count(Type.METADATA);
        h.transformation(h.transformation());
        assertEquals(before, sender.count(Type.METADATA));
    }

    @Test
    void removeDestroysForAllViewersAndInvalidates() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        h.show(viewerB);
        h.remove();
        assertFalse(h.valid());
        assertTrue(h.viewers().isEmpty());
        assertEquals(1, sender.countFor(Type.DESTROY, viewerA));
        assertEquals(1, sender.countFor(Type.DESTROY, viewerB));
    }

    @Test
    void globalFlagAndUniqueIdentity() {
        PacketDisplayHandle a = handle(true);
        PacketDisplayHandle b = handle(false);
        assertTrue(a.global());
        assertFalse(b.global());
        assertNotEquals(a.uniqueId(), b.uniqueId());
        assertEquals(DisplayBackend.PACKET_EVENTS, a.backend());
    }
}
