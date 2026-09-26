package fr.redsavant.bdapi.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum Anchor {

    CENTER,
    CORNER;

    private static final Vector3f MODEL_CENTER = new Vector3f(0.5f, 0.5f, 0.5f);

    public Transformation toTransformation(Vector3f translation, Quaternionf leftRotation, Vector3f scale) {
        Vector3f resultTranslation = new Vector3f(translation);
        if (this == CENTER) {
            resultTranslation.add(centerCompensation(leftRotation, scale));
        }
        return new Transformation(resultTranslation, new Quaternionf(leftRotation), new Vector3f(scale), new Quaternionf());
    }

    public static Vector3f centerCompensation(Quaternionf leftRotation, Vector3f scale) {
        Vector3f scaledCenter = new Vector3f(scale).mul(MODEL_CENTER);
        Vector3f pivoted = new Quaternionf(leftRotation).transform(scaledCenter, new Vector3f());
        return new Vector3f(MODEL_CENTER).sub(pivoted);
    }
}
