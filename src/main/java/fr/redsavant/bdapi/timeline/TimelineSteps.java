package fr.redsavant.bdapi.timeline;


import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.animation.Easing;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

/**
 * Combines the TimelineStep implementations to avoid having four tiny files.
 */
final class TimelineSteps {

    private TimelineSteps() {}

    static final class MoveStep implements TimelineStep {
        private final DisplayCrate crate;
        private final Location target;
        private final long durationMillis;
        private final Easing easing;

        MoveStep(DisplayCrate crate, Location target, long amount, TimeUnit unit, Easing easing) {
            this.crate = crate;
            this.target = target;
            this.durationMillis = unit.toMillis(amount);
            this.easing = easing;
        }

        @Override
        public void execute(Runnable onDone) {
            crate.animate()
                    .moveTo(target)
                    .duration(durationMillis, TimeUnit.MILLISECONDS)
                    .easing(easing)
                    .onComplete(onDone)
                    .play();
        }
    }

    static final class RotateStep implements TimelineStep {
        private final DisplayCrate crate;
        private final float x, y, z;
        private final long durationMillis;
        private final Easing easing;

        RotateStep(DisplayCrate crate, float x, float y, float z, long amount, TimeUnit unit, Easing easing) {
            this.crate = crate;
            this.x = x;
            this.y = y;
            this.z = z;
            this.durationMillis = unit.toMillis(amount);
            this.easing = easing;
        }

        @Override
        public void execute(Runnable onDone) {
            crate.animate()
                    .rotateTo(x, y, z)
                    .duration(durationMillis, TimeUnit.MILLISECONDS)
                    .easing(easing)
                    .onComplete(onDone)
                    .play();
        }
    }

    static final class ScaleStep implements TimelineStep {
        private final DisplayCrate crate;
        private final float x, y, z;
        private final long durationMillis;
        private final Easing easing;

        ScaleStep(DisplayCrate crate, float x, float y, float z, long amount, TimeUnit unit, Easing easing) {
            this.crate = crate;
            this.x = x;
            this.y = y;
            this.z = z;
            this.durationMillis = unit.toMillis(amount);
            this.easing = easing;
        }

        @Override
        public void execute(Runnable onDone) {
            crate.animate()
                    .scaleTo(x, y, z)
                    .duration(durationMillis, TimeUnit.MILLISECONDS)
                    .easing(easing)
                    .onComplete(onDone)
                    .play();
        }
    }

    static final class WaitStep implements TimelineStep {
        private final Plugin plugin;
        private final long durationTicks;

        WaitStep(Plugin plugin, long amount, TimeUnit unit) {
            this.plugin = plugin;
            this.durationTicks = Math.max(1L, unit.toMillis(amount) / 50L);
        }

        @Override
        public void execute(Runnable onDone) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    onDone.run();
                }
            }.runTaskLater(plugin, durationTicks);
        }
    }
}
