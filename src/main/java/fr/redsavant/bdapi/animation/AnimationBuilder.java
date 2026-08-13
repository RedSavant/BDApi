package fr.redsavant.bdapi.animation;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.internal.ActiveAnimation;
import fr.redsavant.bdapi.internal.Animator;
import org.bukkit.Location;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.TimeUnit;

/**
 *   display.animate()
 *       .moveTo(location)
 *       .duration(3, TimeUnit.SECONDS)
 *       .easing(Easing.EASE_OUT)
 *       .play();
 */

public final class AnimationBuilder {

    private final DisplayCrate crate;
    private final Animator animator;

    private Location targetLocation;
    private Vector3f targetScale;
    private Vector3f targetTranslation;
    private Vector3f targetEulerRotation;

    private long durationMillis = 1000L;
    private Easing easing = Easing.LINEAR;
    private Runnable onComplete;

    public AnimationBuilder(DisplayCrate crate, Animator animator) {
        this.crate = crate;
        this.animator = animator;
    }

    public AnimationBuilder moveTo(Location location) {
        this.targetLocation = location;
        return this;
    }

    public AnimationBuilder scaleTo(float x, float y, float z) {
        this.targetScale = new Vector3f(x, y, z);
        return this;
    }

    public AnimationBuilder translateTo(float x, float y, float z) {
        this.targetTranslation = new Vector3f(x, y, z);
        return this;
    }

    public AnimationBuilder rotateTo(float x, float y, float z) {
        this.targetEulerRotation = new Vector3f(x, y, z);
        return this;
    }

    public AnimationBuilder duration(long amount, TimeUnit unit) {
        this.durationMillis = unit.toMillis(amount);
        return this;
    }

    public AnimationBuilder easing(Easing easing) {
        this.easing = easing;
        return this;
    }

    public AnimationBuilder onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public void play() {
        boolean animateLocation = targetLocation != null;
        boolean animateTransform = targetScale != null || targetTranslation != null || targetEulerRotation != null;

        Location startLoc = crate.location().clone();
        Location endLoc = animateLocation ? targetLocation.clone() : startLoc;

        Transformation current = crate.entity().getTransformation();
        Vector3f endScale = targetScale != null ? targetScale : new Vector3f(current.getScale());
        Vector3f endTranslation = targetTranslation != null ? targetTranslation : new Vector3f(current.getTranslation());
        Quaternionf endRotation = targetEulerRotation != null
                ? BlockDisplayBuilder.euleurToQuaternion(targetEulerRotation)
                : new Quaternionf(current.getLeftRotation());

        Transformation endTransform = new Transformation(endTranslation, endRotation, endScale, new Quaternionf());

        ActiveAnimation animation = new ActiveAnimation(
                crate.entity().getUniqueId(),
                crate.entity(),
                startLoc, endLoc, animateLocation,
                current, endTransform, animateTransform,
                durationMillis, easing, onComplete
        );

        animator.submit(animation);
    }
}
