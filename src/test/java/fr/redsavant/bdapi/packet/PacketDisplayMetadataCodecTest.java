package fr.redsavant.bdapi.packet;

import fr.redsavant.bdapi.display.DisplaySettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketDisplayMetadataCodecTest {

    @Test
    void brightnessUsesVanillaPackedLightLayout() {
        DisplaySettings settings = DisplaySettings.defaults().withBrightness(15, 15);

        assertEquals(
                (15 << 4) | (15 << 20),
                PacketDisplayMetadataCodec.packBrightness(settings)
        );
    }

    @Test
    void displayMetadataIndicesMatchVanillaLayout() {
        assertEquals(16, PacketDisplayMetadataCodec.brightnessMetadataIndex());
        assertEquals(23, PacketDisplayMetadataCodec.blockStateMetadataIndex());
    }
}
