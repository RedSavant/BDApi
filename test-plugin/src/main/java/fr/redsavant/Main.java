package fr.redsavant;

import fr.redsavant.bdapi.BDApi;
import fr.redsavant.bdapi.DisplayCrate;
import fr.redsavant.bdapi.Displays;
import fr.redsavant.bdapi.animation.Easing;
import fr.redsavant.bdapi.group.DisplayGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Main extends JavaPlugin {
    private final List<DisplayCrate> spawned = new ArrayList<>();

    @Override
    public void onEnable() {
        BDApi.init(this);
        getLogger().info("BDApi test plugin enabled. Use /bdtest all");
    }

    @Override
    public void onDisable() {
        BDApi.shutdown(true);
        spawned.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command must be used by a player.");
            return true;
        }

        String test = args.length == 0 ? "all" : args[0].toLowerCase();
        switch (test) {
            case "all" -> runAll(player);
            case "display" -> testDisplay(player);
            case "animation" -> testAnimation(player);
            case "timeline" -> testTimeline(player);
            case "group" -> testGroup(player);
            case "physics" -> testPhysics(player);
            case "effects" -> testEffects(player);
            case "clear" -> clearDisplays(player);
            default -> player.sendMessage("Usage: /bdtest <all|display|animation|timeline|group|physics|effects|clear>");
        }
        return true;
    }

    private void runAll(Player player) {
        clearDisplays(player);
        testDisplay(player);
        testAnimation(player);
        testTimeline(player);
        testGroup(player);
        testPhysics(player);
        testEffects(player);
        player.sendMessage("All BDApi tests started.");
    }

    private void testDisplay(Player player) {
        DisplayCrate crate = displays().create()
                .at(relative(player, -4, 1, 4))
                .block(Material.DIAMOND_BLOCK)
                .brightness(15, 15)
                .shadow(1.5f, 0.8f)
                .viewRange(32f)
                .spawn();
        crate.transform().scale(1.4f).rotate(20, 45, 0).translate(0, 0.5f, 0).apply();
        track(crate);
        player.sendMessage("Display + transform test created.");
    }

    private void testAnimation(Player player) {
        DisplayCrate crate = displays().create()
                .at(relative(player, -2, 1, 4))
                .block(Material.EMERALD_BLOCK)
                .spawn();
        track(crate);
        crate.animate()
                .moveTo(relative(player, -2, 4, 4))
                .rotateTo(0, 360, 0)
                .scaleTo(2, 2, 2)
                .duration(3, TimeUnit.SECONDS)
                .easing(Easing.EASE_IN_OUT)
                .onComplete(() -> player.sendMessage("Animation finished."))
                .play();
    }

    private void testTimeline(Player player) {
        Location start = relative(player, 0, 1, 4);
        DisplayCrate crate = displays().create().at(start).block(Material.GOLD_BLOCK).spawn();
        track(crate);
        crate.timeline()
                .easing(Easing.EASE_OUT)
                .moveTo(start.clone().add(0, 3, 0), 1, TimeUnit.SECONDS)
                .rotate(0, 180, 0, 1, TimeUnit.SECONDS)
                .scale(2, 1, 2, 700, TimeUnit.MILLISECONDS)
                .wait(500, TimeUnit.MILLISECONDS)
                .moveTo(start, 1, TimeUnit.SECONDS)
                .play();
    }

    private void testGroup(Player player) {
        Location origin = relative(player, 3, 1, 4);
        DisplayGroup group = displays().group().at(origin)
                .add(displays().create().block(Material.RED_CONCRETE), 0, 0, 0)
                .add(displays().create().block(Material.BLUE_CONCRETE), 1.5, 0, 0)
                .add(displays().create().block(Material.LIME_CONCRETE), 0.75, 1.5, 0)
                .spawn();
        group.transformAll(transform -> transform.scale(0.8f).rotate(0, 45, 0).apply());
        group.crates().forEach(this::track);
    }

    private void testPhysics(Player player) {
        DisplayCrate crate = displays().create()
                .at(relative(player, 5, 8, 4))
                .block(Material.ANVIL)
                .spawn();
        track(crate);
        crate.physics()
                .gravity()
                .velocity(-0.04, 0, 0)
                .bounce(0.45)
                .drag(0.01)
                .onLand(landed -> player.sendMessage("Physics object landed."))
                .start();
    }

    private void testEffects(Player player) {
        Location target = relative(player, 7, 1, 4);
        BDApi.get().effects().explosion().at(target).power(2).particles(true).spawn();
        DisplayCrate meteor = BDApi.get().effects().meteor()
                .at(target.clone().add(3, 0, 0))
                .fromHeight(15)
                .size(1.5f)
                .block(Material.MAGMA_BLOCK)
                .impact(true)
                .spawn();
        track(meteor);
    }

    private void clearDisplays(Player player) {
        spawned.removeIf(crate -> {
            if (crate.isValid()) crate.remove();
            return true;
        });
        player.sendMessage("Tracked displays removed.");
    }

    private Displays displays() {
        return BDApi.get().displays();
    }

    private void track(DisplayCrate crate) {
        spawned.add(crate);
    }

    private Location relative(Player player, double right, double up, double forward) {
        Location base = player.getLocation();
        org.bukkit.util.Vector direction = base.getDirection().setY(0).normalize();
        org.bukkit.util.Vector rightVector = direction.clone().crossProduct(new org.bukkit.util.Vector(0, 1, 0));
        return base.clone().add(direction.multiply(forward)).add(rightVector.multiply(right)).add(0, up, 0);
    }
}
