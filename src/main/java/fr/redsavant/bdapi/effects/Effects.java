package fr.redsavant.bdapi.effects;

import fr.redsavant.bdapi.Displays;
import org.bukkit.plugin.Plugin;

public final class Effects {

    private final Displays displays;
    private final Plugin plugin;

    public Effects(Displays displays, Plugin plugin) {
        this.displays = displays;
        this.plugin = plugin;
    }

}
