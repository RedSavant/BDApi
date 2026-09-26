package fr.redsavant.bdapi.transform;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.display.Anchor;
import fr.redsavant.bdapi.display.Transform;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TransformHandle {

    private final DisplayCrate crate;
    private final Vector3f translation;
    private final Quaternionf leftRotation;
    private final Vector3f scale;
    private Anchor anchor;

    public TransformHandle(DisplayCrate crate) {
        this.crate = crate;
        // Start from the live transform so that the fields left untouched are preserved, anchor
        // included: rebuilding from scratch would silently move the display.
        Transform current = crate.handle().transform();
        this.translation = current.translation();
        this.leftRotation = current.leftRotation();
        this.scale = current.scale();
        this.anchor = current.anchor();
    }

    public TransformHandle scale(float uniform) {
        return scale(uniform, uniform, uniform);
    }

    public TransformHandle scale(float x, float y, float z) {
        this.scale.set(x, y, z);
        return this;
    }

    public TransformHandle translate(float x, float y, float z) {
        this.translation.set(x, y, z);
        return this;
    }

    public TransformHandle rotate(float x, float y, float z) {
        this.leftRotation.set(BlockDisplayBuilder.euleurToQuaternion(new Vector3f(x, y, z)));
        return this;
    }

    public TransformHandle anchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public void apply() {
        crate.handle().transform(Transform.of(translation, leftRotation, scale, anchor));
    }
}
