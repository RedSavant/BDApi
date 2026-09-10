package fr.redsavant.bdapi.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnchorTest {

    private static final float EPS = 1.0e-5f;

    private static Vector3f modelCenter(Transformation t) {
        Vector3f scaled = new Vector3f(t.getScale()).mul(0.5f, 0.5f, 0.5f);
        Vector3f pivoted = new Quaternionf(t.getLeftRotation()).transform(scaled, new Vector3f());
        return new Vector3f(t.getTranslation()).add(pivoted);
    }

    private static void assertVec(float x, float y, float z, Vector3f actual) {
        assertEquals(x, actual.x, EPS);
        assertEquals(y, actual.y, EPS);
        assertEquals(z, actual.z, EPS);
    }

    @Test
    void centerAnchorIsIdentityForScaleOneNoRotation() {
        Transformation t = Anchor.CENTER.toTransformation(
                new Vector3f(0, 0, 0), new Quaternionf(), new Vector3f(1, 1, 1));
        assertVec(0, 0, 0, t.getTranslation());
        assertVec(0.5f, 0.5f, 0.5f, modelCenter(t));
    }

    @Test
    void centerAnchorPinsCenterWhenScaled() {
        Transformation t = Anchor.CENTER.toTransformation(
                new Vector3f(0, 0, 0), new Quaternionf(), new Vector3f(2, 2, 2));
        assertVec(-0.5f, -0.5f, -0.5f, t.getTranslation());
        assertVec(0.5f, 0.5f, 0.5f, modelCenter(t));
    }

    @Test
    void centerAnchorPinsCenterWhenRotated() {
        Quaternionf rot = new Quaternionf().rotateY((float) Math.toRadians(90));
        Transformation t = Anchor.CENTER.toTransformation(new Vector3f(0, 0, 0), rot, new Vector3f(1.5f, 1.5f, 1.5f));
        assertVec(0.5f, 0.5f, 0.5f, modelCenter(t));
    }

    @Test
    void customTranslationShiftsCenter() {
        Transformation t = Anchor.CENTER.toTransformation(
                new Vector3f(0, 0.5f, 0), new Quaternionf(), new Vector3f(1, 1, 1));
        assertVec(0.5f, 1.0f, 0.5f, modelCenter(t));
    }

    @Test
    void cornerAnchorMatchesCenterForBaseCase() {
        Transformation center = Anchor.CENTER.toTransformation(
                new Vector3f(0, 0, 0), new Quaternionf(), new Vector3f(1, 1, 1));
        Transformation corner = Anchor.CORNER.toTransformation(
                new Vector3f(0, 0, 0), new Quaternionf(), new Vector3f(1, 1, 1));
        assertVec(corner.getTranslation().x, corner.getTranslation().y, corner.getTranslation().z, center.getTranslation());
    }

    @Test
    void cornerAnchorAppliesNoCompensation() {
        Transformation t = Anchor.CORNER.toTransformation(
                new Vector3f(0, 0, 0), new Quaternionf(), new Vector3f(2, 2, 2));
        assertVec(0, 0, 0, t.getTranslation());
        assertVec(1, 1, 1, modelCenter(t));
    }
}
