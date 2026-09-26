package fr.redsavant.bdapi;

import fr.redsavant.bdapi.internal.PhysicsEngine;
import fr.redsavant.bdapi.internal.PhysicsState;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicsStateTest {

    private PhysicsState state(double gravity, double drag, Vector velocity) {
        return new PhysicsState(null, velocity, gravity, 0.0, drag, null);
    }

    @Test
    void gravityReducesVerticalVelocity() {
        PhysicsState s = state(0.08, 0.0, new Vector(0, 0, 0));
        PhysicsEngine.applyForces(s);
        assertEquals(-0.08, s.velocity.getY(), 1.0e-9);
    }

    @Test
    void gravityIsClampedToTerminalVelocity() {
        PhysicsState s = state(1.0, 0.0, new Vector(0, -2.9, 0));
        PhysicsEngine.applyForces(s);
        assertTrue(s.velocity.getY() >= -3.0);
    }

    @Test
    void dragScalesVelocity() {
        PhysicsState s = state(0.0, 0.1, new Vector(1.0, 0, 0));
        PhysicsEngine.applyForces(s);
        assertEquals(0.9, s.velocity.getX(), 1.0e-9);
    }
}
