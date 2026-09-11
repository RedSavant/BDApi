package fr.redsavant.bdapi.packet;

import org.bukkit.entity.Display;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketDisplayMetadataCodecTest {

    @Test
    void billboardModesMapToProtocolBytes() {
        assertEquals((byte) 0, PacketDisplayMetadataCodec.billboardId(Display.Billboard.FIXED));
        assertEquals((byte) 1, PacketDisplayMetadataCodec.billboardId(Display.Billboard.VERTICAL));
        assertEquals((byte) 2, PacketDisplayMetadataCodec.billboardId(Display.Billboard.HORIZONTAL));
        assertEquals((byte) 3, PacketDisplayMetadataCodec.billboardId(Display.Billboard.CENTER));
    }

    @Test
    void brightnessDefaultZeroPacksToZero() {
        assertEquals(0, PacketDisplayMetadataCodec.packBrightness(new Display.Brightness(0, 0)));
    }

    @Test
    void brightnessFullPacksBlockAndSky() {
        int packed = PacketDisplayMetadataCodec.packBrightness(new Display.Brightness(15, 15));
        assertEquals((15 << 4) | (15 << 20), packed);
    }

    @Test
    void brightnessDistinctBlockAndSkyValues() {
        int packed = PacketDisplayMetadataCodec.packBrightness(new Display.Brightness(7, 3));
        assertEquals((7 << 4) | (3 << 20), packed);
        assertEquals(7, (packed >> 4) & 0xF);
        assertEquals(3, (packed >> 20) & 0xF);
    }
}
