package fr.redsavant.bdapi.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum Anchor {

    CENTER(0.5f, 0.5f, 0.5f),
    CORNER(0f, 0f, 0f),
    BOTTOM_CENTER(0.5f, 0f, 0.5f);

    private final Vector3f pivot;

    Anchor(float x, float y, float z) {
        this.pivot = new Vector3f(x, y, z);
    }

    public Vector3f pivot() {
        return new Vector3f(pivot);
    }

    public Transformation toTransformation(Vector3f translation, Quaternionf leftRotation, Vector3f scale) {
        return transformation(pivot, translation, leftRotation, scale);
    }

    public static Transformation transformation(Vector3f pivot, Vector3f translation, Quaternionf leftRotation, Vector3f scale) {
        Vector3f resultTranslation = new Vector3f(translation).add(compensation(pivot, leftRotation, scale));
        return new Transformation(resultTranslation, new Quaternionf(leftRotation), new Vector3f(scale), new Quaternionf());
    }

    public static Vector3f compensation(Vector3f pivot, Quaternionf leftRotation, Vector3f scale) {
        Vector3f scaledPivot = new Vector3f(scale).mul(pivot);
        Vector3f pivoted = new Quaternionf(leftRotation).transform(scaledPivot, new Vector3f());
        return new Vector3f(pivot).sub(pivoted);
    }

    @Deprecated
    public static Vector3f centerCompensation(Quaternionf leftRotation, Vector3f scale) {
        return compensation(CENTER.pivot, leftRotation, scale);
    }
}
