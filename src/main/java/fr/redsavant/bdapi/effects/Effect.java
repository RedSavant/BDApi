package fr.redsavant.bdapi.effects;

/**
 * Common marker for effect builders (MeteorEffect, ExplosionEffect, etc.)
 * Each effect has its own terminal method (spawn(), play(), etc.) because the
 * parameters vary too much from one effect to another to allow for any greater unification
 */

public interface Effect {
}
