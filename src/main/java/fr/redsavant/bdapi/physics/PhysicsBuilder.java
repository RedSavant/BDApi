package fr.redsavant.bdapi.physics;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.internal.PhysicsState;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 *   displays.create()
 *       .at(location.clone().add(0, 50, 0))
 *       .block(Material.CHEST)
 *       .physics()
 *           .gravity()
 *           .bounce(0.4)
 *           .onLand(crate -> effects.explosion(crate.location()))
 *           .start();
 */
public final class PhysicsBuilder {

    private static final double DEFAULT_GRAVITY = 0.08; // ~ approx block/tick², proche de la gravité vanilla

    private final DisplayCrate crate;
    private final PhysicsEngine engine;

    private Vector velocity = new Vector(0, 0, 0);
    private double gravity = 0;
    private double bounce = 0;
    private double drag = 0.02;
    private Consumer<DisplayCrate> onLand;

    public PhysicsBuilder(DisplayCrate crate, PhysicsEngine engine) {
        this.crate = crate;
        this.engine = engine;
    }

    public PhysicsBuilder gravity() {
        this.gravity = DEFAULT_GRAVITY;
        return this;
    }

    public PhysicsBuilder gravity(double strength) {
        this.gravity = strength;
        return this;
    }

    public PhysicsBuilder bounce(double factor) {
        this.bounce = factor;
        return this;
    }

    public PhysicsBuilder drag(double drag) {
        this.drag = drag;
        return this;
    }

    public PhysicsBuilder velocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    public PhysicsBuilder velocity(double x, double y, double z) {
        this.velocity = new Vector(x, y, z);
        return this;
    }

    public PhysicsBuilder onLand(Consumer<DisplayCrate> onLand) {
        this.onLand = onLand;
        return this;
    }

    public void start() {
        PhysicsState state = new PhysicsState(crate, velocity, gravity, bounce, drag, onLand);
        engine.submit(state);
    }
}
