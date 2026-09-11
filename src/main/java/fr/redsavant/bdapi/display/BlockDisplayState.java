package fr.redsavant.bdapi.display;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public final class BlockDisplayState {

    private BlockData blockData;
    private Transformation transformation;
    private Display.Billboard billboard;
    private Display.Brightness brightness;
    private float viewRange;
    private float shadowRadius;
    private float shadowStrength;

    public BlockDisplayState(BlockData blockData, Transformation transformation, Display.Billboard billboard,
                             Display.Brightness brightness, float viewRange, float shadowRadius, float shadowStrength) {
        this.blockData = blockData.clone();
        this.transformation = transformation;
        this.billboard = billboard;
        this.brightness = brightness;
        this.viewRange = viewRange;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
    }

    public BlockData blockData() {
        return blockData.clone();
    }

    public void blockData(BlockData blockData) {
        this.blockData = blockData.clone();
    }

    public Transformation transformation() {
        return transformation;
    }

    public void transformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public Display.Billboard billboard() {
        return billboard;
    }

    public Display.Brightness brightness() {
        return brightness;
    }

    public boolean hasBrightness() {
        return brightness != null;
    }

    public boolean hasViewRange() {
        return viewRange >= 0f;
    }

    public float viewRange() {
        return viewRange;
    }

    public boolean hasShadowRadius() {
        return shadowRadius >= 0f;
    }

    public float shadowRadius() {
        return shadowRadius;
    }

    public boolean hasShadowStrength() {
        return shadowStrength >= 0f;
    }

    public float shadowStrength() {
        return shadowStrength;
    }
}
