package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.animation.Easing;
import fr.redsavant.bdapi.display.DisplayHandle;
import org.bukkit.Location;
import org.bukkit.util.Transformation;

import java.util.UUID;

public final class ActiveAnimation {

    public final UUID entityId;
    public final DisplayHandle handle;

    public final Location startLocation;
    public final Location endLocation;
    public final boolean animateLocation;

    public final Transformation startTransform;
    public final Transformation endTransform;
    public final boolean animateTransform;

    public final long startTimeMillis;
    public final long durationMillis;
    public final Easing easing;
    public final Runnable onComplete;

    public boolean finished = false;

    public ActiveAnimation(UUID entityId, DisplayHandle handle, Location startLocation, Location endLocation, boolean animateLocation,
                           Transformation startTransform, Transformation endTransform, boolean animateTransform,
                           long durationMillis, Easing easing, Runnable onComplete) {
        this.entityId = entityId;
        this.handle = handle;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.animateLocation = animateLocation;
        this.startTransform = startTransform;
        this.endTransform = endTransform;
        this.animateTransform = animateTransform;
        this.startTimeMillis = System.currentTimeMillis();
        this.durationMillis = Math.max(durationMillis, 1L);
        this.easing = easing;
        this.onComplete = onComplete;
    }

    public double rawProgress() {
        long elapsed = System.currentTimeMillis() - startTimeMillis;
        return MathUtils.clamp01((double) elapsed / (double) durationMillis);
    }
}
