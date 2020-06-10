import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

import java.util.Random;

public class ParticleMover {
    public void defaultMove(@NotNull Particle3D p) {

        p.move();
    }

    public void rebound(@NotNull Particle3D p, int type) {

        if (type == 0)
            p.reverseVelocityX();
        if (type == 1)
            p.reverseVelocityY();
        if (type == 2)
            p.reverseVelocityZ();
    }

    public void moveTo(@NotNull Particle3D p, @NotNull PVector target, int frame){
        p.setLifeSpan(frame);
        float distanceX =  target.x - p.getLocation().x;
        float distanceY = target.y - p.getLocation().y;
        float accX = distanceX * 2 / (frame * frame);
        float accY = distanceY * 2 / (frame * frame);
        p.setAcceleration(new PVector(accX, accY));
        p.setVelocity(new PVector(0,0));
    }

    public void randomPosition(@NotNull Particle3D p, float xRange, float yRange) {

        Random random = new Random(System.identityHashCode(p));
        p.setLocation(new PVector(random.nextFloat() * xRange, random.nextFloat() * yRange, random.nextFloat() * 10.f));
    }

    public void brownianMove(@NotNull Particle3D p) {

        Random random = new Random(System.identityHashCode(p));
        p.setVelocity(new PVector(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        defaultMove(p);
    }
    public void perlinMove(@NotNull NoiseGenerator ng, @NotNull Particle3D p){
        perlinMove(ng, p, 1.f);
    }
    public void perlinMove(@NotNull NoiseGenerator ng, @NotNull Particle3D p, float speedlimit) {

        float noise = ng.generateNoise(p.getLocation().x, p.getLocation().y);
        float rotation = noise * 6.2831855f;
        PVector acc = new PVector((float) Math.cos(rotation), (float) Math.sin(rotation));
        p.move(acc, speedlimit);
    }

    public void explode(@NotNull Particle3D p) {

        Random random = new Random(System.identityHashCode(p));
        float r = random.nextFloat() * 6.2831855f;
        PVector acc = new PVector((float) Math.cos(r) + 0.01f,
                (float) Math.sin(r) + 0.01f);
        p.move(acc, 1.f);
    }

    public int checkEdge(float width, float height, @NotNull Particle3D p) {

        if (p.getLocation().x <= 0 || p.getLocation().x >= width)
            return 0;
        if (p.getLocation().y <= 0 || p.getLocation().y >= height)
            return 1;
        if (p.getLocation().z <= -300 || p.getLocation().z >= 300)
            return 2;
        return -1;
    }

    public boolean checkBoundary(float width, float height, @NotNull Particle3D p) {

        return p.getLocation().x < 0 || p.getLocation().x > width || p.getLocation().y < 0 || p.getLocation().y > height;
    }

}
