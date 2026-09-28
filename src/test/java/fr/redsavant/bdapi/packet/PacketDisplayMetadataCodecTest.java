package fr.redsavant.bdapi.packet;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.DisplaySettings;
import fr.redsavant.bdapi.display.PacketDisplayHandle;
import fr.redsavant.bdapi.display.Transform;
import fr.redsavant.bdapi.support.RecordingPacketDisplaySender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PacketDisplayMetadataCodecTest {

    @Test
    void brightnessUsesIntSerializerAndVanillaPackedLightLayout() {
        DisplaySettings settings = DisplaySettings.defaults().withBrightness(15, 15);
        Transform transform = Transform.of(
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                Anchor.CENTER
        );

        PacketDisplayHandle display = new PacketDisplayHandle(
                UUID.randomUUID(),
                42,
                new RecordingPacketDisplaySender(),
                true,
                new Location(null, 0, 64, 0),
                transform,
                Material.STONE,
                settings
        );

        List<EntityData<?>> metadata = PacketDisplayMetadataCodec.encode(display);
        EntityData<?> brightness = metadata.stream()
                .filter(data -> data.getIndex() == 16)
                .findFirst()
                .orElseThrow();

        assertSame(EntityDataTypes.INT, brightness.getType());
        assertEquals((15 << 4) | (15 << 20), brightness.getValue());
    }

    @Test
    void blockStateMetadataRemainsAtVanillaBlockDisplayIndex() {
        Transform transform = Transform.of(
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                Anchor.CENTER
        );

        PacketDisplayHandle display = new PacketDisplayHandle(
                UUID.randomUUID(),
                43,
                new RecordingPacketDisplaySender(),
                true,
                new Location(null, 0, 64, 0),
                transform,
                Material.STONE,
                DisplaySettings.defaults()
        );

        EntityData<?> blockState = PacketDisplayMetadataCodec.encode(display).stream()
                .filter(data -> data.getIndex() == 23)
                .findFirst()
                .orElseThrow();

        assertSame(EntityDataTypes.BLOCK_STATE, blockState.getType());
        assertEquals(display.blockStateId(), blockState.getValue());
    }
}
