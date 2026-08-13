package fr.redsavant.bdapi.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

/**
 *   effects.explosion()
 *       .at(location)
 *       .power(2f)
 *       .particles(true)
 *       .spawn();
 */
public final class ExplosionEffect implements Effect {

    private final Plugin plugin;

    private Location location;
    private float power = 1f;
    private boolean particles = true;

    public ExplosionEffect(Plugin plugin) {
        this.plugin = plugin;
    }

    public ExplosionEffect at(Location location) {
        this.location = location;
        return this;
    }

    public ExplosionEffect power(float power) {
        this.power = power;
        return this;
    }

    public ExplosionEffect particles(boolean particles) {
        this.particles = particles;
        return this;
    }

    public void spawn() {
        if (location == null) {
            throw new IllegalStateException("Il faut appeler .at(location) avant .spawn().");
        }
        if (particles) {
            location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
            location.getWorld().spawnParticle(Particle.FLAME, location, (int) (20 * power), power / 2, power / 2, power / 2, 0.05);
        }
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, Math.min(power, 4f), 1f);
    }
}
