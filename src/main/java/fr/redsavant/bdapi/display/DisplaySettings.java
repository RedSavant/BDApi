package fr.redsavant.bdapi.display;

import org.bukkit.entity.Display;

/**
 * Visual options of a display that are not part of its {@link Transform}.
 *
 * <p>Both backends read the same values, so switching backend never changes the meaning of a
 * builder call. Every option but the billboard is optional: a negative value means "not
 * configured" and the backend leaves the vanilla default in place.
 *
 * @param billboard      orientation of the display relative to the viewers
 * @param brightnessBlock block light level applied to the display, {@code -1} to keep the block lighting
 * @param brightnessSky  sky light level applied to the display, {@code -1} to keep the sky lighting
 * @param viewRange      scale factor of the view range, {@code -1} to keep the default
 * @param shadowRadius   radius of the dropped shadow, {@code -1} to keep the default
 * @param shadowStrength strength of the dropped shadow, {@code -1} to keep the default
 */
public record DisplaySettings(
        Display.Billboard billboard,
        int brightnessBlock,
        int brightnessSky,
        float viewRange,
        float shadowRadius,
        float shadowStrength
) {

    /** Default settings: fixed orientation, ambient lighting, no custom shadow. */
    public static final DisplaySettings DEFAULT =
            new DisplaySettings(Display.Billboard.FIXED, -1, -1, -1f, -1f, -1f);

    public static DisplaySettings defaults() {
        return DEFAULT;
    }

    public DisplaySettings withBillboard(Display.Billboard billboard) {
        return new DisplaySettings(billboard, brightnessBlock, brightnessSky, viewRange, shadowRadius, shadowStrength);
    }

    public DisplaySettings withBrightness(int blockLight, int skyLight) {
        return new DisplaySettings(billboard, blockLight, skyLight, viewRange, shadowRadius, shadowStrength);
    }

    public DisplaySettings withViewRange(float viewRange) {
        return new DisplaySettings(billboard, brightnessBlock, brightnessSky, viewRange, shadowRadius, shadowStrength);
    }

    public DisplaySettings withShadow(float radius, float strength) {
        return new DisplaySettings(billboard, brightnessBlock, brightnessSky, viewRange, radius, strength);
    }

    public boolean hasBrightness() {
        return brightnessBlock >= 0 && brightnessSky >= 0;
    }

    public boolean hasViewRange() {
        return viewRange >= 0;
    }

    public boolean hasShadowRadius() {
        return shadowRadius >= 0;
    }

    public boolean hasShadowStrength() {
        return shadowStrength >= 0;
    }
}
