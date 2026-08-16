package fr.redsavant.bdapi.internal;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import org.bukkit.util.Vector;

/**
 * Represent a member of a DisplayGroup: either a builder that has not yet been spawned
 * (relative position known, entity not yet created), or a crate that has already been spawned.
 */

public final class GroupMember {

    public final BlockDisplayBuilder builder; // Can be null if already spawn
    public DisplayCrate crate;
    public final Vector offset;


    // Method Overloading
    /**
     * @param builder
     * @param offset
     */
    public GroupMember(BlockDisplayBuilder builder, Vector offset) {
        this.builder = builder;
        this.crate = null;
        this.offset = offset;
    }

    /**
     * @param crate
     * @param offset
     */
    public GroupMember(DisplayCrate crate, Vector offset) {
        this.builder = null;
        this.crate = crate;
        this.offset = offset;
    }
}
