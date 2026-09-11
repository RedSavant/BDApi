package fr.redsavant.bdapi.support;

import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.BlockDisplayState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DisplayStates {

    private DisplayStates() {
    }

    public static BlockData mockBlockData(String key) {
        BlockData data = mock(BlockData.class);
        lenient().when(data.clone()).thenReturn(data);
        lenient().when(data.getAsString()).thenReturn(key);
        lenient().when(data.matches(data)).thenReturn(true);
        return data;
    }

    public static Transformation identity() {
        return Anchor.CENTER.toTransformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1));
    }

    public static BlockDisplayState simple() {
        return new BlockDisplayState(mockBlockData("stone"), identity(), Display.Billboard.FIXED, null, -1f, -1f, -1f);
    }

    public static BlockDisplayState full(BlockData blockData) {
        return new BlockDisplayState(blockData, identity(), Display.Billboard.CENTER,
                new Display.Brightness(15, 15), 64f, 1f, 0.5f);
    }
}
