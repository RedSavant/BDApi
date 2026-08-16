package fr.redsavant.bdapi.transform;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TransformHandle {

    private final DisplayCrate crate;
    private final Vector3f scale;
    private final Vector3f translation;
    private final Vector3f eulerRotation = new Vector3f(0f, 0f, 0f);

    public TransformHandle(DisplayCrate crate) {
        this.crate = crate;
        Transformation current = crate.entity().getTransformation();
        this.scale = new Vector3f(current.getScale());
        this.translation = new Vector3f(current.getTranslation());
    }

    /**
     * Uniform scale for the entity
     * @param uniform
     * @return this
     */
    public TransformHandle scale(float uniform) {
        this.scale.set(uniform, uniform, uniform);
        return this;
    }

    /**
     * Scale of the entity
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public TransformHandle scale(float x, float y, float z) {
        this.scale.set(x, y, z);
        return this;
    }

    /**
     * Translation of the entity
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public TransformHandle translate(float x, float y, float z) {
        this.translation.set(x, y, z);
        return this;
    }

    /**
     * Rotation in degrees of the entity (pitch=x, yaw=y, roll=z), applied as an Euler rotation ZYX.
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public TransformHandle rotate(float x, float y, float z) {
        this.eulerRotation.set(x, y, z);
        return this;
    }

    /**
     * Apply the transformation to the display
     */
    public void apply() {
        Quaternionf rotation = BlockDisplayBuilder.euleurToQuaternion(eulerRotation);
        Transformation transformation = new Transformation(
                new Vector3f(translation),
                rotation,
                new Vector3f(scale),
                new Quaternionf()
        );
        crate.entity().setTransformation(transformation);
    }
}
