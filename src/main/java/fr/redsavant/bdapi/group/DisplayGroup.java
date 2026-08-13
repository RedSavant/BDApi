package fr.redsavant.bdapi.group;

import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.builder.BlockDisplayBuilder;
import fr.redsavant.bdapi.internal.Animator;
import fr.redsavant.bdapi.internal.DisplayRegistry;
import fr.redsavant.bdapi.internal.GroupMember;
import fr.redsavant.bdapi.transform.TransformHandle;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class DisplayGroup {

    private final Plugin plugin;
    private final DisplayRegistry registry;
    private final Animator animator;

    private final List<GroupMember> members = new ArrayList<>();
    private Location origin;
    private boolean spawned = false;

    public DisplayGroup(Plugin plugin, DisplayRegistry registry, Animator animator) {
        this.plugin = plugin;
        this.registry = registry;
        this.animator = animator;
    }

    public DisplayGroup at(Location origin) {
        this.origin = origin;
        return this;
    }

    public DisplayGroup add(BlockDisplayBuilder builder) {
        Vector offset = origin != null && builder.location() != null
                ? builder.location().toVector().subtract(origin.toVector())
                : new Vector(0, 0, 0);
        members.add(new GroupMember(builder, offset));
        return this;
    }

    public DisplayGroup add(BlockDisplayBuilder builder, double x, double y, double z) {
        members.add(new GroupMember(builder, new Vector(x, y, z)));
        return this;
    }

    public DisplayGroup add(DisplayCrate crate) {
        Vector offset = origin != null
                ? crate.location().toVector().subtract(origin.toVector())
                : new Vector(0, 0, 0);
        members.add(new GroupMember(crate, offset));
        return this;
    }

    public DisplayGroup spawn() {
        if (spawned) return this;
        for (GroupMember member : members) {
            if (member.crate != null) continue;

            BlockDisplayBuilder builder = member.builder;
            if (origin != null) {
                Location target = origin.clone().add(member.offset);
                builder.at(target);
            }
            member.crate = builder.spawn();
        }
        spawned = true;
        return this;
    }

    public void transformAll(Consumer<TransformHandle> configurator) {
        for (GroupMember member : members) {
            if (member.crate != null) {
                configurator.accept(member.crate.transform());
            }
        }
    }

    public void remove() {
        for (GroupMember member : members) {
            if (member.crate != null) {
                member.crate.remove();
            }
            members.clear();
            spawned = false;
        }
    }

    public List<DisplayCrate> crates() {
        List<DisplayCrate> result = new ArrayList<>();
        for (GroupMember member : members) {
            if (member.crate != null) result.add(member.crate);
        }
        return result;
    }
}
