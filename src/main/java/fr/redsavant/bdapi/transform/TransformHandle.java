package fr.redsavant.bdapi.transform;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.display.Anchor;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TransformHandle {

    private final DisplayCrate crate;
    private final Vector3f scale;
    private final Vector3f translation = new Vector3f(0f, 0f, 0f);
    private final Vector3f eulerRotation = new Vector3f(0f, 0f, 0f);
    private Anchor anchor = Anchor.CENTER;

    public TransformHandle(DisplayCrate crate) {
        this.crate = crate;
        Transformation current = crate.handle().transformation();
        this.scale = new Vector3f(current.getScale());
    }

    public TransformHandle scale(float uniform) {
        this.scale.set(uniform, uniform, uniform);
        return this;
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
        this.eulerRotation.set(x, y, z);
        return this;
    }

    public TransformHandle anchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public void apply() {
        Quaternionf rotation = BlockDisplayBuilder.euleurToQuaternion(eulerRotation);
        crate.handle().transformation(anchor.toTransformation(translation, rotation, scale));
    }
}
