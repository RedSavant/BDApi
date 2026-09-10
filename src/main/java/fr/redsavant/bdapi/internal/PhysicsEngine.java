package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.display.DisplayHandle;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PhysicsEngine {

    private static final double TERMINAL_VELOCITY = -3.0;
    private static final double GROUND_EPSILON = 0.05;

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Map<UUID, PhysicsState> active = new ConcurrentHashMap<>();
    private BukkitTask task;

    public PhysicsEngine(Plugin plugin, DisplayRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
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

    public void submit(PhysicsState state) {
        active.put(state.crate.uniqueId(), state);
    }

    public void cancel(UUID entityId) {
        active.remove(entityId);
    }

    public void tick() {
        if (active.isEmpty()) return;

        for (PhysicsState state : active.values()) {
            DisplayCrate crate = state.crate;
            DisplayHandle handle = crate.handle();

            if (!handle.valid()) {
                active.remove(handle.uniqueId());
                continue;
            }

            if (state.gravity != 0) {
                double newY = Math.max(state.velocity.getY() - state.gravity, TERMINAL_VELOCITY);
                state.velocity.setY(newY);
            }
            if (state.drag > 0) {
                state.velocity.multiply(1.0 - state.drag);
            }

            Location current = handle.location();
            Location next = current.clone().add(state.velocity);

            Block ground = groundBelow(next);
            if (ground != null) {
                Location landed = next.clone();
                landed.setY(ground.getY() + 1.0);

                double reboundVelocity = Math.abs(state.velocity.getY()) * state.bounce;
                if (state.bounce > 0 && reboundVelocity > GROUND_EPSILON) {
                    handle.teleport(landed);
                    state.velocity.setY(reboundVelocity);
                } else {
                    handle.teleport(landed);
                    state.velocity.setY(0);
                    state.grounded = true;
                    active.remove(handle.uniqueId());
                    if (state.onLand != null) {
                        state.onLand.accept(crate);
                    }
                }
            } else {
                handle.teleport(next);
            }
        }
    }

    private Block groundBelow(Location loc) {
        Block block = loc.clone().subtract(0, 0.1, 0).getBlock();
        return block.getType().isSolid() ? block : null;
    }
}
