package fr.redsavant.bdapi.timeline;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.animation.Easing;
import fr.redsavant.bdapi.internal.Animator;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *   display.timeline()
 *       .moveTo(pos1, 1, SECONDS)
 *       .rotate(0, 180, 0, 1, SECONDS)
 *       .scale(2, 1, 2, 500, MILLISECONDS)
 *       .wait(1, SECONDS)
 *       .moveTo(pos2, 2, SECONDS)
 *       .play();
 */
public final class Timeline {

    private final DisplayCrate crate;
    private final Animator animator;
    private final Plugin plugin;

    private final List<TimelineStep> steps = new ArrayList<>();
    private Easing defaultEasing = Easing.LINEAR;
    private boolean loop = false;
    private TimelineExecutor executor;

    public Timeline(DisplayCrate crate, Animator animator, Plugin plugin) {
        this.crate = crate;
        this.animator = animator;
        this.plugin = plugin;
    }

    public Timeline easing(Easing easing) {
        this.defaultEasing = easing;
        return this;
    }

    public Timeline moveTo(Location target, long amount, TimeUnit unit) {
        steps.add(new TimelineSteps.MoveStep(crate, target, amount, unit, defaultEasing));
        return this;
    }

    public Timeline rotate(float x, float y, float z, long amount, TimeUnit unit) {
        steps.add(new TimelineSteps.RotateStep(crate, x, y, z, amount, unit, defaultEasing));
        return this;
    }

    public Timeline scale(float x, float y, float z, long amount, TimeUnit unit) {
        steps.add(new TimelineSteps.ScaleStep(crate, x, y, z, amount, unit, defaultEasing));
        return this;
    }

    public Timeline wait(long amount, TimeUnit unit) {
        steps.add(new TimelineSteps.WaitStep(plugin, amount, unit));
        return this;
    }

    public Timeline loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public void play() {
        executor = new TimelineExecutor(steps, loop);
        executor.start();
    }

    public void stop() {
        if (executor != null) {
            executor.cancel();
        }
    }
}
