package fr.redsavant.bdapi.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Backend independent transform of a display.
 *
 * <p>Values are kept in their logical form: {@link #translation()} is the offset asked by the user,
 * before the {@link Anchor} compensation is applied. Interpolating two logical transforms and
 * compensating once on the result keeps the visual centre pinned for the whole animation, which
 * does not hold when already compensated values are interpolated.
 */
public final class Transform {

    private final Vector3f translation;
    private final Quaternionf leftRotation;
    private final Vector3f scale;
    private final Anchor anchor;

    private Transform(Vector3f translation, Quaternionf leftRotation, Vector3f scale, Anchor anchor) {
        this.translation = new Vector3f(translation);
        this.leftRotation = new Quaternionf(leftRotation);
        this.scale = new Vector3f(scale);
        this.anchor = anchor == null ? Anchor.CENTER : anchor;
    }

    public static Transform of(Vector3f translation, Quaternionf leftRotation, Vector3f scale, Anchor anchor) {
        return new Transform(translation, leftRotation, scale, anchor);
    }

    /**
     * Reads back a transformation that was produced by {@link #toTransformation()}, recovering the
     * logical translation of the given anchor.
     *
     * @param transformation a compensated transformation
     * @param anchor         the anchor the transformation was built with
     */
    public static Transform from(Transformation transformation, Anchor anchor) {
        Anchor effective = anchor == null ? Anchor.CENTER : anchor;
        Vector3f scale = new Vector3f(transformation.getScale());
        Quaternionf leftRotation = new Quaternionf(transformation.getLeftRotation());
        Vector3f translation = new Vector3f(transformation.getTranslation());
        if (effective == Anchor.CENTER) {
            translation.sub(Anchor.centerCompensation(leftRotation, scale));
        }
        return new Transform(translation, leftRotation, scale, effective);
    }

    public Vector3f translation() {
        return new Vector3f(translation);
    }

    public Quaternionf leftRotation() {
        return new Quaternionf(leftRotation);
    }

    public Vector3f scale() {
        return new Vector3f(scale);
    }

    public Anchor anchor() {
        return anchor;
    }

    public Transform withTranslation(Vector3f value) {
        return new Transform(value, leftRotation, scale, anchor);
    }

    public Transform withLeftRotation(Quaternionf value) {
        return new Transform(translation, value, scale, anchor);
    }

    public Transform withScale(Vector3f value) {
        return new Transform(translation, leftRotation, value, anchor);
    }

    public Transform withAnchor(Anchor value) {
        return new Transform(translation, leftRotation, scale, value);
    }

    /**
     * Applies the anchor compensation for the current scale and rotation.
     *
     * @return the transformation to hand over to a backend
     */
    public Transformation toTransformation() {
        return anchor.toTransformation(translation, leftRotation, scale);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Transform transform)) return false;
        return translation.equals(transform.translation)
                && leftRotation.equals(transform.leftRotation)
                && scale.equals(transform.scale)
                && anchor == transform.anchor;
    }

    @Override
    public int hashCode() {
        int result = translation.hashCode();
        result = 31 * result + leftRotation.hashCode();
        result = 31 * result + scale.hashCode();
        result = 31 * result + anchor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Transform{translation=" + translation
                + ", leftRotation=" + leftRotation
                + ", scale=" + scale
                + ", anchor=" + anchor + '}';
    }
}
