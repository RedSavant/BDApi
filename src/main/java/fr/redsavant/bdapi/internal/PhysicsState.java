package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public final class PhysicsState {

    public final DisplayCrate crate;
    public Vector velocity;
    public double gravity;
    public double bounce;
    public double drag;
    public boolean grounded = false;
    public Consumer<DisplayCrate> onLand;

    public PhysicsState(DisplayCrate crate, Vector velocity, double gravity, double bounce, double drag, Consumer<DisplayCrate> onLand) {
        this.crate = crate;
        this.velocity = velocity;
        this.gravity = gravity;
        this.bounce = bounce;
        this.drag = drag;
        this.onLand = onLand;
    }
}
