package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.support.DisplayStates;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BackendParityTest {

    private BlockDisplayState config() {
        return DisplayStates.full(DisplayStates.mockBlockData("oak_stairs[facing=east]"));
    }

    @Test
    void paperAppliesEveryConfiguredProperty() {
        BlockDisplayState state = config();
        BlockDisplay entity = mock(BlockDisplay.class);

        PaperDisplayHandle.applyTo(entity, state);

        verify(entity).setBlock(state.blockData());
        verify(entity).setBillboard(Display.Billboard.CENTER);
        verify(entity).setBrightness(new Display.Brightness(15, 15));
        verify(entity).setViewRange(64f);
        verify(entity).setShadowRadius(1f);
        verify(entity).setShadowStrength(0.5f);
        verify(entity).setTransformation(state.transformation());
    }

    @Test
    void packetRetainsEveryConfiguredProperty() {
        BlockDisplayState state = config();
        PacketDisplayHandle handle = new PacketDisplayHandle(
                UUID.randomUUID(), 1, new RecordingPacketDisplaySender(), false,
                new Location(null, 0, 64, 0), state);

        BlockDisplayState retained = handle.state();
        assertEquals(Display.Billboard.CENTER, retained.billboard());
        assertEquals(new Display.Brightness(15, 15), retained.brightness());
        assertEquals(64f, retained.viewRange());
        assertEquals(1f, retained.shadowRadius());
        assertEquals(0.5f, retained.shadowStrength());
        assertSame(state.transformation(), handle.transformation());
    }
}
