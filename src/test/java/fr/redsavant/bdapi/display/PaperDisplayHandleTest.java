package fr.redsavant.bdapi.display;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaperDisplayHandleTest {

    @Test
    void delegatesIdentityAndBackend() {
        BlockDisplay entity = mock(BlockDisplay.class);
        UUID id = UUID.randomUUID();
        when(entity.getUniqueId()).thenReturn(id);
        when(entity.getEntityId()).thenReturn(123);

        PaperDisplayHandle handle = new PaperDisplayHandle(entity);
        assertEquals(DisplayBackend.PAPER, handle.backend());
        assertEquals(id, handle.uniqueId());
        assertEquals(123, handle.entityId());
    }

    @Test
    void delegatesTransformationAndTeleport() {
        BlockDisplay entity = mock(BlockDisplay.class);
        Transformation tf = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf());
        Location loc = new Location(null, 4, 5, 6);

        PaperDisplayHandle handle = new PaperDisplayHandle(entity);
        handle.transformation(tf);
        handle.teleport(loc);

        verify(entity).setTransformation(tf);
        verify(entity).teleport(loc);
    }

    @Test
    void removeSkippedWhenDead() {
        BlockDisplay entity = mock(BlockDisplay.class);
        when(entity.isDead()).thenReturn(true);

        PaperDisplayHandle handle = new PaperDisplayHandle(entity);
        assertFalse(handle.valid());
        handle.remove();
        verify(entity, never()).remove();
    }

    @Test
    void paperHandleIsGlobalWithNoViewerTracking() {
        BlockDisplay entity = mock(BlockDisplay.class);
        when(entity.isDead()).thenReturn(false);

        PaperDisplayHandle handle = new PaperDisplayHandle(entity);
        UUID viewer = UUID.randomUUID();
        handle.show(viewer);
        handle.hide(viewer);

        assertTrue(handle.global());
        assertTrue(handle.viewers().isEmpty());
        assertTrue(handle.isVisibleTo(viewer));
    }
}
