package fr.redsavant.bdapi.effects;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.Displays;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

/**
 *   effects.meteor()
 *       .at(location)
 *       .fromHeight(150)
 *       .size(5)
 *       .impact(true)
 *       .spawn();
 */

public final class MeteorEffect implements Effect {

    private final Displays displays;
    private final Plugin plugin;

    private Location target;
    private double fromHeight = 100;
    private float size = 2f;
    private Material material = Material.MAGMA_BLOCK;
    private boolean impact = true;

    public MeteorEffect(Displays displays, Plugin plugin) {
        this.displays = displays;
        this.plugin = plugin;
    }

    public MeteorEffect at(Location target) {
        this.target = target;
        return this;
    }

    public MeteorEffect fromHeight(double height) {
        this.fromHeight = height;
        return this;
    }

    public MeteorEffect size(float size) {
        this.size = size;
        return this;
    }

    public MeteorEffect block(Material material) {
        this.material = material;
        return this;
    }

    public MeteorEffect impact(boolean impact) {
        this.impact = impact;
        return this;
    }

    public DisplayCrate spawn() {
        if (target == null) {
            throw new IllegalStateException("You need to call .at(Location) before use spawn");
        }

        Location spawnPoint = target.clone().add(0, fromHeight, 0);

        DisplayCrate crate = displays.create()
                .at(spawnPoint)
                .block(material)
                .scale(size)
                .spawn();

        crate.physics()
                .gravity(0.15)
                .bounce(0)
                .onLand(landed -> {
                    if (impact) {
                        triggerImpact(landed.location());
                    }
                    landed.remove();
                })
                .start();

        return crate;
    }

    private void triggerImpact(Location loc) {
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, (int) size * 15, size / 2, size / 4, size / 2);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
    }
}
