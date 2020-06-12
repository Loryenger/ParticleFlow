import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public abstract class ParticleSystemInitializer {

    public void initParticleSystem(@NotNull ParticleSystem ps) {

        ps.getVisibleParticles().forEach(particle3D -> initParticle(ps.getSketch(), particle3D));
    }

    protected abstract void initParticle(PApplet sketch, Particle3D p);
}

abstract class BufferedInitializer extends ParticleSystemInitializer {
    protected ArrayList<Particle3D> particleBuffer;

    BufferedInitializer() {

        particleBuffer = new ArrayList<>();
    }

    public ArrayList<Particle3D> getParticleBuffer() {

        return particleBuffer;
    }

    public void setParticleBuffer(ArrayList<Particle3D> particleBuffer) {

        this.particleBuffer = particleBuffer;
    }

    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {

        setParticleBuffer(ps.getParticles());
        particleBuffer.forEach(particle3D -> initParticle(ps.getSketch(), particle3D));
    }

    public void initParticleSystem(PApplet sketch, ArrayList<Particle3D> particleBuffer) {

        setParticleBuffer(particleBuffer);
        particleBuffer.forEach(particle3D -> initParticle(sketch, particle3D));
    }

}

class DefaultInitializer extends ParticleSystemInitializer {

    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {

    }

    @Override
    protected void initParticle(PApplet sketch, Particle3D p) {

    }
}

class ImmortalInitializer extends ParticleSystemInitializer {
    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {
        //ps.getParticles().forEach(particle3D -> particle3D.setVisible(true));
        super.initParticleSystem(ps);
    }

    @Override
    public void initParticle(PApplet sketch, @NotNull Particle3D p) {
        //p.setVisible(true);
        p.stop();
        p.setLifeSpan(-1);
    }
}

class CircleInitializer extends ParticleSystemInitializer {
    final float r;
    final float rate;
    final float time;
    final float g;

    CircleInitializer(float r, float rate, float time, float g) {

        this.r = r;
        this.rate = rate;
        this.time = time;
        this.g = g;
    }

    CircleInitializer() {

        this(100, 1.2f, 10, 0.1f);
    }

    @Override
    protected void initParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {

        Random random = new Random(System.identityHashCode(p));
        float theta = (random.nextFloat() + 0.001f) * PApplet.TWO_PI;
        p.setLocation(new PVector(sketch.width / 2.f, sketch.height / 2.f, -500));
        p.setVelocity(new PVector(PApplet.cos(theta) * 5.f, PApplet.sin(theta) * 5.f, 0));
        p.setLifeSpan(-1);
    }
}

class ShooterInitializer extends ParticleSystemInitializer {
    float wC;
    float hC;
    float w;
    float h;
    float initX;
    float initY;

    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {

        wC = ps.getSketch().width / 8.f*3;
        hC = ps.getSketch().height / 8.f * 2.f;
        w = ps.getSketch().width / 4.f;
        h = ps.getSketch().height / 4.f;
        initX = ps.getSketch().width / 2.f;
        initY = ps.getSketch().height;
        ps.getParticles().forEach(particle3D -> initParticle(ps.getSketch(), particle3D));
    }

    @Override
    protected void initParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {

        float time = sketch.random(3, 5.5f) * sketch.frameRate;
        float targetX = wC + sketch.random(1, w);
        float targetY = hC + sketch.random(1, h);
        float targetZ = sketch.random(-100, 0);
        PVector v = new PVector((targetX - initX) / time, (targetY - initY) / time, targetZ / time);
        p.setLocation(new PVector(initX, initY, 0));
        p.setAcceleration(new PVector(0, 0, 0));
        p.setVelocity(v);
        p.setLifeSpan((int) time);
    }
}

class SwarmInitializer extends BufferedInitializer {

    private final float attenuationCoefficient;
    private final float radius;

    SwarmInitializer() {

        attenuationCoefficient = 0.98f;
        radius = 200;
    }

    SwarmInitializer(float ac, float r) {
        attenuationCoefficient = ac;
        radius = r;
    }

    @Override
    protected void initParticle(PApplet sketch, Particle3D p1) {

        PVector acceleration = new PVector(0, 0);
        for (Particle3D p2 : getParticleBuffer()) {
            float distanceX = p2.getLocation().x - p1.getLocation().x;
            float distanceY = p2.getLocation().y - p1.getLocation().y;
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (distance < 1) distance = 1.f;
            float force = (distance - radius) * p2.getMass() / distance;
            acceleration.add(new PVector(force * distanceX, force * distanceY));
        }
        float vx = p1.getVelocity().x * attenuationCoefficient + acceleration.x * p1.getMass();
        float vy = p1.getVelocity().y * attenuationCoefficient + acceleration.y * p1.getMass();
        p1.setVelocity(new PVector(vx, vy, 0));
        p1.setAcceleration(new PVector(0, 0, 0));
    }
}

class PrinterInitializer extends ShooterInitializer {
    ArrayList<PVector> loci;
    ArrayList<Integer> pixel;
    ArrayList<PVector> init;
    PImage image;
    String imageFileName;
    ImageSampler sampler;

    PrinterInitializer(String imageFileName) {

        image = new PImage();
        loci = new ArrayList<>();
        init = new ArrayList<>();
        pixel = new ArrayList<>();
        this.imageFileName = imageFileName;
        sampler = new ImageSampler();
    }

    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {

        initParameters(ps.getSketch(), ps.getNum());
        ps.getParticles().forEach(particle3D -> initParticle(ps.getSketch(), particle3D));
    }


    protected void initParameters(PApplet sketch, int num) {

        image = sketch.loadImage(imageFileName);
        image.resize(sketch.width / 4 * 3, sketch.height / 4 * 3);
        sampler.sampleImage(sketch, image, num);
        loci = sampler.getLoci();
        pixel = sampler.getPixel();
        wC = sketch.width / 4.f / 2.f;
        hC = sketch.height / 4.f / 2.f;
        w = image.width;
        h = image.height;
        initX = sketch.width / 2.f;
        initY = sketch.height / 2.f;
        //init.add(new PVector(sketch.width / 2.f, 50, 0));
        //init.add(new PVector(50, sketch.height / 2.f, 0));
        init.add(new PVector(sketch.width / 2.f, sketch.height - 50, 0));
        //init.add(new PVector(sketch.width - 50, sketch.height / 2.f, 0));
    }

    @Override
    protected void initParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {

        Random random = new Random(System.identityHashCode(p));
        PVector start = init.get(random.nextInt(init.size()));
        PVector target = loci.remove(0);
        float cX = 1 - PApplet.abs((initX - start.x) / initX);
        float cY = 1 - PApplet.abs((initY - start.y) / initY);
        target.x += wC;
        target.y += hC;
        float distance = PApplet.sqrt(cX * PApplet.pow(target.y - start.y, 2) + cY * PApplet.pow(target.x - start.x, 2));
        float maxDistance = PApplet.sqrt(cX * PApplet.pow(hC + h, 2) + cY * PApplet.pow(wC + w, 2));
        float time = (1.f - distance / maxDistance * 0.5f) * 3f * sketch.frameRate;
        PVector acc = new PVector(0, 0, 0);
        acc.x = (target.x - start.x) * 2.f / (time * time);
        acc.y = (target.y - start.y) * 2.f / (time * time);
        acc.z = (target.z) * 2.f / (time * time);

        p.setLocation(start);
        p.setAcceleration(acc);
        p.setLifeSpan((int) time);
        p.setVelocity(new PVector(0,0,0));
        p.setColor_(pixel.remove(0));
    }
}

class HiddenInitializer extends ParticleSystemInitializer{

    @Override
    protected void initParticle(PApplet sketch, Particle3D p) {
        Random random = new Random(System.identityHashCode(p));
        float score = random.nextFloat();
        if(score<0.80) p.setVisible(false);
        else p.setLifeSpan(-1);
    }
}

class ExplodeInitializer extends ParticleSystemInitializer{
    @Override
    protected void initParticle(PApplet sketch, Particle3D p) {
        Random random = new Random(System.nanoTime());
        float rotation = random.nextFloat() * 6.2831855f;
        p.setLifeSpan(-1);
        p.setAcceleration(new PVector(0,0,0));
        p.setVelocity(new PVector((float) Math.cos(rotation),(float)Math.sin(rotation)));
    }
}

class FountainInitializer extends ShooterInitializer{

    @Override
    protected void initParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        super.initParticle(sketch, p);
        p.setAcceleration(new PVector(0,0.03f,0));
        p.setLifeSpan(-1);
    }
}

class PortrayInitializer extends ParticleSystemInitializer{
    private final ParticleMover mover;

    public PortrayInitializer() {
        mover = new ParticleMover();
    }

    @Override
    public void initParticleSystem(@NotNull ParticleSystem ps) {
        super.initParticleSystem(ps);
        //ps.getVisibleParticles().forEach(particle3D -> mover.randomPosition(particle3D, ps.getSketch().width,  ps.getSketch().height));
    }

    @Override
    protected void initParticle(PApplet sketch, Particle3D p) {
        //mover.randomPosition(p, sketch.width, sketch.height);
        p.setVisible(true);
        //p.setRadius(6);
        //p.setLifeSpan(-1);
        //p.setPos(new PVector(0,0,0));
        //p.setVelocity(new PVector(0,0,0));
        //p.setAcceleration(new PVector(0,0,0));
    }
}


