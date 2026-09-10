package fr.redsavant.bdapi;

import fr.redsavant.bdapi.animation.Easing;
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
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimationBackendTest {

    @Test
    void animationDrivesPacketBackend() throws InterruptedException {
        Plugin plugin = mock(Plugin.class);
        DisplayRegistry registry = new DisplayRegistry();
        Animator animator = new Animator(plugin);
        PhysicsEngine physics = new PhysicsEngine(plugin, registry);
        RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
        Displays displays = new Displays(plugin, registry, animator, physics,
                DisplayBackend.PACKET_EVENTS, sender, new PacketEntityIdAllocator());

        Player viewer = mock(Player.class);
        UUID viewerId = UUID.randomUUID();
        when(viewer.getUniqueId()).thenReturn(viewerId);

        DisplayCrate crate = displays.create()
                .viewer(viewer)
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
}
