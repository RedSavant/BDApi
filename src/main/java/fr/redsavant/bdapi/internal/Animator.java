package fr.redsavant.bdapi.internal;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Animator {

    private final Plugin plugin;
    private final Map<UUID, ActiveAnimation> active = new ConcurrentHashMap<>();
    private BukkitTask task;

    public Animator(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        active.clear();
    }

    public void submit(ActiveAnimation animation) {
        active.put(animation.entityId, animation);
    }

    public void cancel(UUID entityId) {
        active.remove(entityId);
    }

    public boolean isAnimating(UUID entityId) {
        return active.containsKey(entityId);
    }

    private void tick() {
        if (active.isEmpty()) return;

        for (ActiveAnimation anim : active.values()) {
            if (anim.entity == null || anim.entity.isDead()) {
                active.remove(anim.entityId);
                continue;
            }

            double rawT = anim.rawProgress();
            double t = anim.easing.apply(rawT);

            if (anim.animateLocation) {
                applyLocation(anim, t);
            }
            if (anim.animateTransform) {
                applyTransform(anim, t);
            }

            if (rawT >= 1.0) {
                active.remove(anim.entityId);
                if (anim.onComplete != null) {
                    anim.onComplete.run();
                }
            }
        }
    }

    private void applyLocation(ActiveAnimation anim, double t) {
        Location from = anim.startLocation;
        Location to = anim.endLocation;
        double x = from.getX() + (to.getX() - from.getX()) * t;
        double y = from.getY() + (to.getY() - from.getY()) * t;
        double z = from.getZ() + (to.getZ() - from.getZ()) * t;
        Location newLoc = new Location(from.getWorld(), x, y, z, anim.entity.getLocation().getYaw(), anim.entity.getLocation().getPitch());
        anim.entity.teleport(newLoc);
    }

    private void applyTransform(ActiveAnimation anim, double t) {
        BlockDisplay entity = anim.entity;
        Transformation from = anim.startTransform;
        Transformation to = anim.endTransform;

        Vector3f translation = MathUtils.lerp(from.getTranslation(), to.getTranslation(), t, new Vector3f());
        Vector3f scale = MathUtils.lerp(from.getScale(), to.getScale(), t, new Vector3f());
        Quaternionf leftRotation = MathUtils.slerp(new Quaternionf(from.getLeftRotation()), new Quaternionf(to.getLeftRotation()), t, new Quaternionf());

        entity.setTransformation(new Transformation(translation, leftRotation, scale, new Quaternionf()));
    }
}
