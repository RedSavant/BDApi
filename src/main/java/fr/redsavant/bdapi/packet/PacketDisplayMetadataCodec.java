package fr.redsavant.bdapi.packet;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import fr.redsavant.bdapi.display.PacketDisplayHandle;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;

final class PacketDisplayMetadataCodec {

    private static final int INTERPOLATION_DELAY = 8;
    private static final int TRANSFORMATION_INTERPOLATION_DURATION = 9;
    private static final int POSITION_INTERPOLATION_DURATION = 10;
    private static final int TRANSLATION = 11;
    private static final int SCALE = 12;
    private static final int LEFT_ROTATION = 13;
    private static final int RIGHT_ROTATION = 14;
    private static final int BLOCK_STATE = 23;

    private static final int INTERPOLATION_TICKS = 2;

    private PacketDisplayMetadataCodec() {
    }

    static List<EntityData<?>> encode(PacketDisplayHandle display) {
        Transformation transformation = display.transformation();
        org.joml.Vector3f translation = transformation.getTranslation();
        org.joml.Vector3f scale = transformation.getScale();
        org.joml.Quaternionf left = transformation.getLeftRotation();
        org.joml.Quaternionf right = transformation.getRightRotation();

        List<EntityData<?>> data = new ArrayList<>();
        data.add(new EntityData<>(INTERPOLATION_DELAY, EntityDataTypes.INT, 0));
        data.add(new EntityData<>(TRANSFORMATION_INTERPOLATION_DURATION, EntityDataTypes.INT, INTERPOLATION_TICKS));
        data.add(new EntityData<>(POSITION_INTERPOLATION_DURATION, EntityDataTypes.INT, INTERPOLATION_TICKS));
        data.add(new EntityData<>(TRANSLATION, EntityDataTypes.VECTOR3F, new Vector3f(translation.x, translation.y, translation.z)));
        data.add(new EntityData<>(SCALE, EntityDataTypes.VECTOR3F, new Vector3f(scale.x, scale.y, scale.z)));
        data.add(new EntityData<>(LEFT_ROTATION, EntityDataTypes.QUATERNION, new Quaternion4f(left.x, left.y, left.z, left.w)));
        data.add(new EntityData<>(RIGHT_ROTATION, EntityDataTypes.QUATERNION, new Quaternion4f(right.x, right.y, right.z, right.w)));
        data.add(new EntityData<>(BLOCK_STATE, EntityDataTypes.BLOCK_STATE, blockStateId(display)));
        return data;
    }

    private static int blockStateId(PacketDisplayHandle display) {
        return SpigotConversionUtil.fromBukkitBlockData(display.material().createBlockData()).getGlobalId();
    }
}
