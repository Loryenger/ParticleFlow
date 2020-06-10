import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

public class ParticleVisualizer {

    public void visual(@NotNull PApplet sketch, @NotNull Particle3D p) {
        if (p.isVisible()) {
            sketch.pushMatrix();
            sketch.translate(p.getLocation().x, p.getLocation().y, p.getLocation().z);
            sketch.noStroke();
            sketch.fill(p.getColor_());
            sketch.sphere(p.getRadius());
            sketch.popMatrix();
        }
    }

    public void visual2D(@NotNull PApplet sketch, @NotNull Particle3D p) {
        if (p.isVisible()) {
            sketch.noStroke();
            sketch.fill(p.getColor_());
            sketch.ellipse(p.getLocation().x, p.getLocation().y, p.getRadius(), p.getRadius());
        }
    }

    public void printProperties(PApplet sketch, @NotNull Particle3D p) {

        PApplet.print("Pos=[", p.getLocation().x, ',', p.getLocation().y, ',', p.getLocation().z, "],");
        PApplet.print("Acceleration=[", p.getAcceleration().x, ',', p.getAcceleration().y, ',', p.getAcceleration().z, "],");
        PApplet.print("Velocity=[", p.getVelocity().x, ',', p.getVelocity().y, ',', p.getVelocity().z, "],");
        PApplet.print("Radius=", p.getRadius(), ", ", "Color=", p.getColor_(), ", ", "LifeSpan=", p.getLifeSpan());
        PApplet.println();
    }
}
