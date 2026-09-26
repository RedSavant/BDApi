package fr.redsavant.bdapi.packet;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import fr.redsavant.bdapi.display.DisplaySettings;
import fr.redsavant.bdapi.display.PacketDisplayHandle;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes the vanilla display metadata of a client-side block display.
 *
 * <p>PacketEvents 2.9 exposes no dedicated {@code EntityDataTypes} entry for the display specific
 * fields, so the standard byte/int/float types are reused with the indices of the vanilla display
 * entity. {@code EntityDataTypes} only describes how a value is serialized, the index carried by
 * {@link EntityData} selects the field, which is exactly how the vanilla entity is laid out.
 */
final class PacketDisplayMetadataCodec {

    private static final int INTERPOLATION_DELAY = 8;
    private static final int TRANSFORMATION_INTERPOLATION_DURATION = 9;
    private static final int POSITION_INTERPOLATION_DURATION = 10;
    private static final int TRANSLATION = 11;
    private static final int SCALE = 12;
    private static final int LEFT_ROTATION = 13;
    private static final int RIGHT_ROTATION = 14;
    private static final int BILLBOARD = 15;
    private static final int BRIGHTNESS_OVERRIDE = 16;
    private static final int VIEW_RANGE = 17;
    private static final int SHADOW_RADIUS = 18;
    private static final int SHADOW_STRENGTH = 19;
    private static final int BLOCK_STATE = 23;

    private static final int INTERPOLATION_TICKS = 2;

    private PacketDisplayMetadataCodec() {
    }

    static List<EntityData<?>> encode(PacketDisplayHandle display) {
        Transformation transformation = display.transformation();
        org.joml.Vector3f translation = transformation.getTranslation();
        org.joml.Vector3f scale = transformation.getScale();
        Quaternionf left = transformation.getLeftRotation();
        Quaternionf right = transformation.getRightRotation();

        List<EntityData<?>> data = new ArrayList<>();
        data.add(new EntityData<>(INTERPOLATION_DELAY, EntityDataTypes.INT, 0));
        data.add(new EntityData<>(TRANSFORMATION_INTERPOLATION_DURATION, EntityDataTypes.INT, INTERPOLATION_TICKS));
        data.add(new EntityData<>(POSITION_INTERPOLATION_DURATION, EntityDataTypes.INT, INTERPOLATION_TICKS));
        data.add(new EntityData<>(TRANSLATION, EntityDataTypes.VECTOR3F, new Vector3f(translation.x, translation.y, translation.z)));
        data.add(new EntityData<>(SCALE, EntityDataTypes.VECTOR3F, new Vector3f(scale.x, scale.y, scale.z)));
        data.add(new EntityData<>(LEFT_ROTATION, EntityDataTypes.QUATERNION, new Quaternion4f(left.x, left.y, left.z, left.w)));
        data.add(new EntityData<>(RIGHT_ROTATION, EntityDataTypes.QUATERNION, new Quaternion4f(right.x, right.y, right.z, right.w)));
        encodeSettings(data, display.settings());
        data.add(new EntityData<>(BLOCK_STATE, EntityDataTypes.BLOCK_STATE, display.blockStateId()));
        return data;
    }

    private static void encodeSettings(List<EntityData<?>> data, DisplaySettings settings) {
        data.add(new EntityData<>(BILLBOARD, EntityDataTypes.BYTE, billboardId(settings.billboard())));
        if (settings.hasBrightness()) {
            data.add(new EntityData<>(BRIGHTNESS_OVERRIDE, EntityDataTypes.BYTE, packBrightness(settings)));
        }
        if (settings.hasViewRange()) {
            data.add(new EntityData<>(VIEW_RANGE, EntityDataTypes.FLOAT, settings.viewRange()));
        }
        if (settings.hasShadowRadius()) {
            data.add(new EntityData<>(SHADOW_RADIUS, EntityDataTypes.FLOAT, settings.shadowRadius()));
        }
        if (settings.hasShadowStrength()) {
            data.add(new EntityData<>(SHADOW_STRENGTH, EntityDataTypes.FLOAT, settings.shadowStrength()));
        }
    }

    private static byte billboardId(Display.Billboard billboard) {
        return switch (billboard) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
        };
    }

    private static byte packBrightness(DisplaySettings settings) {
        return (byte) ((settings.brightnessBlock() << 4) | settings.brightnessSky());
    }
}
