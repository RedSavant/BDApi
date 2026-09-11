package fr.redsavant.bdapi.display;

import fr.redsavant.bdapi.support.DisplayStates;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender.Type;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlockDataStateTest {

    private final UUID viewerA = UUID.randomUUID();
    private final UUID viewerB = UUID.randomUUID();

    private PacketDisplayHandle handle(RecordingPacketDisplaySender sender, BlockData data) {
        BlockDisplayState state = new BlockDisplayState(data, DisplayStates.identity(),
                org.bukkit.entity.Display.Billboard.FIXED, null, -1f, -1f, -1f);
        return new PacketDisplayHandle(UUID.randomUUID(), 5, sender, false, new Location(null, 0, 64, 0), state);
    }

    @Test
    void handlePreservesBlockData() {
        BlockData north = DisplayStates.mockBlockData("oak_stairs[facing=north]");
        PacketDisplayHandle h = handle(new RecordingPacketDisplaySender(), north);
        assertEquals("oak_stairs[facing=north]", h.blockData().getAsString());
    }

    @Test
    void changingBlockDataSendsMetadataWithoutRespawn() {
        RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
        BlockData north = DisplayStates.mockBlockData("oak_stairs[facing=north]");
        PacketDisplayHandle h = handle(sender, north);
        h.show(viewerA);

        long spawnsBefore = sender.count(Type.SPAWN);
        long metaBefore = sender.count(Type.METADATA);

        BlockData east = mock(BlockData.class);
        lenient().when(east.clone()).thenReturn(east);
        lenient().when(east.getAsString()).thenReturn("oak_stairs[facing=east]");
        when(east.matches(north)).thenReturn(false);
        h.block(east);

        assertEquals(spawnsBefore, sender.count(Type.SPAWN));
        assertEquals(metaBefore + 1, sender.count(Type.METADATA));
        assertEquals("oak_stairs[facing=east]", h.blockData().getAsString());
    }

    @Test
    void newViewerReceivesSpawnAndMetadata() {
        RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
        PacketDisplayHandle h = handle(sender, DisplayStates.mockBlockData("stone"));
        h.show(viewerA);
        h.show(viewerB);

        assertEquals(1, sender.countFor(Type.SPAWN, viewerB));
        assertEquals(1, sender.countFor(Type.METADATA, viewerB));
    }

    @Test
    void nonViewerReceivesNoUpdate() {
        RecordingPacketDisplaySender sender = new RecordingPacketDisplaySender();
        BlockData north = DisplayStates.mockBlockData("oak_stairs[facing=north]");
        PacketDisplayHandle h = handle(sender, north);
        h.show(viewerA);

        BlockData east = mock(BlockData.class);
        lenient().when(east.clone()).thenReturn(east);
        when(east.matches(north)).thenReturn(false);
        h.block(east);

        assertEquals(0, sender.countFor(Type.METADATA, viewerB));
    }
}
