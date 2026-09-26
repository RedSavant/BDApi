package fr.redsavant.bdapi.transform;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.Displays;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.display.Transform;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketEntityIdAllocator;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransformHandleTest {

    private static final float EPS = 1.0e-5f;

    private Displays displays;

    @BeforeEach
    void setUp() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("TransformHandleTest"));
        DisplayRegistry registry = new DisplayRegistry();
        RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
        displays = new Displays(plugin, registry, new Animator(plugin), new PhysicsEngine(plugin, registry),
                DisplayBackend.PACKET_EVENTS, () -> sender, new PacketEntityIdAllocator());
    }

    @Test
    void changingOnlyTheScaleKeepsTranslationRotationAndAnchor() {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CORNER)
                .translate(1f, 2f, 3f)
                .rotate(10f, 20f, 30f)
                .scale(2f)
                .at(new Location(null, 0, 64, 0))
                .spawn();

        crate.transform().scale(3f).apply();

        Transform result = crate.handle().transform();
        assertEquals(Anchor.CORNER, result.anchor());
        assertVec(1f, 2f, 3f, result.translation());
        assertVec(3f, 3f, 3f, result.scale());
        assertEquals(euler(10f, 20f, 30f), result.leftRotation());
    }

    @Test
    void applyWithoutAnyChangeKeepsTheDisplayInPlace() {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CENTER)
                .translate(0.5f, 0f, 0f)
                .scale(2f)
                .at(new Location(null, 0, 64, 0))
                .spawn();

        Transform before = crate.handle().transform();
        crate.transform().apply();

        assertEquals(before, crate.handle().transform());
        // The compensation is rebuilt from the logical values, so the round trip is exact.
        assertEquals(before.toTransformation(), crate.handle().transform().toTransformation());
    }

    @Test
    void switchingAnchorRecomputesTheCompensation() {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CENTER)
                .scale(2f)
                .at(new Location(null, 0, 64, 0))
                .spawn();

        crate.transform().anchor(Anchor.CORNER).apply();

        Transform result = crate.handle().transform();
        assertEquals(Anchor.CORNER, result.anchor());
        assertVec(0f, 0f, 0f, result.translation());
    }

    private static Quaternionf euler(float x, float y, float z) {
        return BlockDisplayBuilder.euleurToQuaternion(new Vector3f(x, y, z));
    }

    private static void assertVec(float x, float y, float z, Vector3f actual) {
        assertEquals(x, actual.x, EPS, "x");
        assertEquals(y, actual.y, EPS, "y");
        assertEquals(z, actual.z, EPS, "z");
    }
}
