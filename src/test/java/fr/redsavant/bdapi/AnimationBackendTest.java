package fr.redsavant.bdapi;

import fr.redsavant.bdapi.animation.Easing;
import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.display.Transform;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.packet.PacketEntityIdAllocator;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender.Type;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimationBackendTest {

    private static final float EPS = 1.0e-5f;

    private final DisplayRegistry registry = new DisplayRegistry();
    private final Animator animator = new Animator(mock(Plugin.class));
    private final RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
    private final Displays displays = new Displays(mock(Plugin.class), registry, animator,
            new PhysicsEngine(mock(Plugin.class), registry), DisplayBackend.PACKET_EVENTS,
            () -> sender, new PacketEntityIdAllocator());

    @Test
    void animationDrivesPacketBackend() throws InterruptedException {
        UUID viewerId = UUID.randomUUID();

        DisplayCrate crate = displays.create()
                .viewer(viewer(viewerId))
                .at(new Location(null, 0, 64, 0))
                .spawn();

        AtomicBoolean done = new AtomicBoolean(false);
        crate.animate()
                .moveTo(new Location(null, 0, 74, 0))
                .scaleTo(2, 2, 2)
                .duration(1, TimeUnit.MILLISECONDS)
                .easing(Easing.LINEAR)
                .onComplete(() -> done.set(true))
                .play();

        assertTrue(animator.isAnimating(crate.uniqueId()));
        Thread.sleep(5);
        animator.tick();

        assertTrue(done.get());
        assertFalse(animator.isAnimating(crate.uniqueId()));
        assertTrue(sender.countFor(Type.TELEPORT, viewerId) >= 1);
        assertTrue(sender.countFor(Type.METADATA, viewerId) >= 2);
    }

    @Test
    void centerAnchorStaysPinnedWhileScaling() throws InterruptedException {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CENTER)
                .scale(1f)
                .viewer(viewer(UUID.randomUUID()))
                .at(new Location(null, 0, 64, 0))
                .spawn();

        assertModelCenter(crate, 0.5f, 0.5f, 0.5f);

        crate.animate()
                .scaleTo(2f, 2f, 2f)
                .duration(1, TimeUnit.MILLISECONDS)
                .easing(Easing.LINEAR)
                .play();
        Thread.sleep(5);
        animator.tick();

        assertEquals(2f, crate.handle().transform().scale().x, EPS);
        // The anchor compensation is recomputed on the last frame instead of being baked into the
        // target, so the visual centre does not drift while the scale grows.
        assertModelCenter(crate, 0.5f, 0.5f, 0.5f);
    }

    @Test
    void centerAnchorStaysPinnedWhileRotating() throws InterruptedException {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CENTER)
                .rotate(0f, 90f, 0f)
                .viewer(viewer(UUID.randomUUID()))
                .at(new Location(null, 0, 64, 0))
                .spawn();

        assertModelCenter(crate, 0.5f, 0.5f, 0.5f);

        crate.animate()
                .rotateTo(0f, 180f, 0f)
                .duration(1, TimeUnit.MILLISECONDS)
                .easing(Easing.LINEAR)
                .play();
        Thread.sleep(5);
        animator.tick();

        assertModelCenter(crate, 0.5f, 0.5f, 0.5f);
    }

    @Test
    void cornerAnchorIsNotCompensatedWhileAnimating() throws InterruptedException {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CORNER)
                .scale(1f)
                .viewer(viewer(UUID.randomUUID()))
                .at(new Location(null, 0, 64, 0))
                .spawn();

        crate.animate()
                .scaleTo(2f, 2f, 2f)
                .duration(1, TimeUnit.MILLISECONDS)
                .easing(Easing.LINEAR)
                .play();
        Thread.sleep(5);
        animator.tick();

        Transformation result = crate.handle().transform().toTransformation();
        assertEquals(0f, result.getTranslation().x, EPS);
        assertModelCenter(crate, 1f, 1f, 1f);
    }

    @Test
    void animatedTransformKeepsTheDisplayAnchor() throws InterruptedException {
        DisplayCrate crate = displays.create()
                .anchor(Anchor.CORNER)
                .viewer(viewer(UUID.randomUUID()))
                .at(new Location(null, 0, 64, 0))
                .spawn();

        crate.animate()
                .translateTo(4f, 0f, 0f)
                .duration(1, TimeUnit.MILLISECONDS)
                .easing(Easing.LINEAR)
                .play();
        Thread.sleep(5);
        animator.tick();

        assertEquals(Anchor.CORNER, crate.handle().transform().anchor());
    }

    private void assertModelCenter(DisplayCrate crate, float x, float y, float z) {
        Transformation transformation = crate.handle().transform().toTransformation();
        Vector3f half = new Vector3f(transformation.getScale()).mul(0.5f);
        Vector3f rotated = new Quaternionf(transformation.getLeftRotation()).transform(half, new Vector3f());
        Vector3f center = new Vector3f(transformation.getTranslation()).add(rotated);
        assertEquals(x, center.x, EPS, "model center x");
        assertEquals(y, center.y, EPS, "model center y");
        assertEquals(z, center.z, EPS, "model center z");
    }

    private Player viewer(UUID id) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }
}
