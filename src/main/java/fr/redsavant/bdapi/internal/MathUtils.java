package fr.redsavant.bdapi.internal;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Internal math helper methods used for interpolation.
 * Not intended for use outside the animation and physics engines.
 */

public final class MathUtils {

    private MathUtils() {}

    /**
     * Clamps a value between 0 and 1.
     *
     * @param t the input value
     * @return the clamped value, within [0, 1]
     */
    public static double clamp01(double t) {
        return Math.max(0, Math.min(1, t));
    }

    public static Vector3f lerp(Vector3f from, Vector3f to, double t, Vector3f dest) {
        dest.x = (float) (from.x + (to.x - from.x) * t);
        dest.y = (float) (from.y + (to.y - from.y) * t);
        dest.z = (float) (from.z + (to.z - from.z) * t);
        return dest;
    }

    public static Quaternionf slerp(Quaternionf from, Quaternionf to, double t, Quaternionf dest) {
        from.slerp(to, (float) t, dest);
        return dest;
    }
}
