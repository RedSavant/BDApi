package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PacketDisplayHandleTest {

    private RecordingPacketDisplaySender sender;
    private final UUID viewerA = UUID.randomUUID();
    private final UUID viewerB = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sender = new RecordingPacketDisplaySender();
    }

    private PacketDisplayHandle handle(boolean global) {
        return handleAt(global, new Location(null, 1, 2, 3));
    }

    private PacketDisplayHandle handleAt(Location location) {
        return handleAt(true, location);
    }

    private PacketDisplayHandle handleAt(boolean global, Location location) {
        Transform transform = Transform.of(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), Anchor.CENTER);
        return new PacketDisplayHandle(UUID.randomUUID(), 7, sender, global,
                location, transform, Material.STONE, DisplaySettings.defaults());
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
        h.transform(Transform.of(new Vector3f(), new Quaternionf(), new Vector3f(2, 2, 2), Anchor.CENTER));
        assertEquals(before + 1, sender.countFor(Type.METADATA, viewerA));
        assertEquals(0, sender.countFor(Type.METADATA, viewerB));
    }

    @Test
    void unchangedTransformIsSkipped() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);
        long before = sender.count(Type.METADATA);
        h.transform(h.transform());
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

    @Test
    void sameWorldTeleportKeepsTheEntity() {
        World world = mock(World.class);
        PacketDisplayHandle h = handleAt(new Location(world, 1, 2, 3));
        h.show(viewerA);

        h.teleport(new Location(world, 5, 6, 7));

        assertEquals(1, sender.countFor(Type.TELEPORT, viewerA));
        assertEquals(1, sender.countFor(Type.SPAWN, viewerA));
    }

    @Test
    void crossWorldTeleportRespawnsTheEntity() {
        World origin = mock(World.class);
        World target = mock(World.class);
        PacketDisplayHandle h = handleAt(new Location(origin, 1, 2, 3));
        h.show(viewerA);

        h.teleport(new Location(target, 1, 2, 3));

        // An entity teleport carries no dimension, so the fake entity has to be packed again.
        assertEquals(0, sender.countFor(Type.TELEPORT, viewerA));
        assertEquals(2, sender.countFor(Type.SPAWN, viewerA));
        assertEquals(1, sender.countFor(Type.DESTROY, viewerA));
        assertEquals(1, h.viewers().size());
    }

    @Test
    void resendRepacksWithoutTouchingTheViewerSet() {
        PacketDisplayHandle h = handle(false);
        h.show(viewerA);

        h.resend(viewerA);

        assertEquals(2, sender.countFor(Type.SPAWN, viewerA));
        assertEquals(1, sender.countFor(Type.DESTROY, viewerA));
        assertEquals(2, sender.countFor(Type.METADATA, viewerA));
        assertEquals(1, h.viewers().size());
    }

    @Test
    void resendOfAnUntrackedViewerIsIgnored() {
        PacketDisplayHandle h = handle(false);

        h.resend(viewerA);

        assertEquals(0, sender.count(Type.SPAWN));
        assertTrue(h.viewers().isEmpty());
    }

    @Test
    void blockStateIdIsResolvedOncePerMaterial() {
        PacketDisplayHandle h = handle(false);

        int first = h.blockStateId();
        h.transform(Transform.of(new Vector3f(), new Quaternionf(), new Vector3f(2, 2, 2), Anchor.CENTER));

        assertEquals(first, h.blockStateId());
        assertNotEquals(first, new PacketDisplayHandle(UUID.randomUUID(), 8, sender, false,
                new Location(null, 1, 2, 3), Transform.of(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), Anchor.CENTER),
                Material.DIRT, DisplaySettings.defaults()).blockStateId());
    }
}
