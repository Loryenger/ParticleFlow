import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.Random;

public abstract class ParticleSystemMover {
    protected final ParticleMover mover;
    ParticleSystemMover(){
        mover = new ParticleMover();
    }
    public void move(@NotNull ParticleSystem ps){
        ps.getVisibleParticles().forEach(particle3D -> moveParticle(ps.getSketch(), particle3D));
    }
    protected abstract void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p);
    public void init(ParticleSystem ps) {
    }
}

class DefaultMover extends ParticleSystemMover {
    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        mover.defaultMove(p);
    }
}

class FountainMover extends ParticleSystemMover {

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        p.move(new PVector(0, 0, 0), 50.f);
        mover.rebound(p, mover.checkEdge(sketch.width, sketch.height, p));
    }
}

class PerlinMover extends ParticleSystemMover {
    private final NoiseGenerator noiseGenerator;
    PerlinMover(){
        noiseGenerator = new NoiseGenerator();
    }
    @Override
    public void move(@NotNull ParticleSystem ps) {
        noiseGenerator.setSketch(ps.getSketch());
        super.move(ps);
    }

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        mover.perlinMove(noiseGenerator, p);
        if(mover.checkEdge(sketch.width, sketch.height,p)!=-1)
        mover.randomPosition(p, sketch.width, sketch.height);
    }
}

class ExplodeMover extends ParticleSystemMover {

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        mover.explode(p);
        mover.randomPosition(p, sketch.width, sketch.height);
    }
}

class DynamicSwarmMover extends ParticleSystemMover {
    protected SwarmInitializer swarmInitializer;

    DynamicSwarmMover() {

        swarmInitializer = new SwarmInitializer();
    }

    @Override
    public void move(@NotNull ParticleSystem ps) {

        ps.transDeadToImmortal();
        swarmInitializer.initParticleSystem(ps.getSketch(), ps.getImmortalParticles());
        super.move(ps);
    }

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        p.move();
    }
}

class PrinterMover extends ParticleSystemMover {
    private final long interval;
    private int num;
    private final int step;
    private long time;

    PrinterMover() {

        time = System.currentTimeMillis();
        num = 0;
        interval = 300L;
        step = 200;
    }

    PrinterMover(int step, long interval) {
        this.time = System.currentTimeMillis();
        this.interval = interval;
        this.num = 0;
        this.step = step;
    }

    @Override
    public void move(@NotNull ParticleSystem ps) {

        if (System.currentTimeMillis() - time > interval) {
            time = System.currentTimeMillis();
            num += step;
            if (num > ps.getNum())
                num = ps.getNum();
        }
        ps.getParticles().stream().limit(num).filter(Particle3D::isAlive).forEach(Particle3D::move);
    }

    @Override
    public void init(ParticleSystem ps) {
        time = System.currentTimeMillis();
        num = 0;
    }

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {

    }
}

class FixedSwarmMover extends DynamicSwarmMover {
    long time;

    FixedSwarmMover() {
        swarmInitializer = new SwarmInitializer(0.99f, 200);
        time = System.currentTimeMillis();
    }

    @Override
    public void move(@NotNull ParticleSystem ps) {

        swarmInitializer.initParticleSystem(ps.getSketch(), ps.getVisibleParticles());
        ps.getVisibleParticles().forEach(Particle3D::move);
        if (System.currentTimeMillis() - time < 1000L)
            ps.getVisibleParticles().forEach(particle3D -> particle3D.getLocation().x += ps.getSketch().width / 100.f);
    }

    @Override
    public void init(ParticleSystem ps) {
        time = System.currentTimeMillis();
    }
}

class PortrayMover extends PrinterMover {
    protected final String file;
    protected final ImageSampler sampler;
    protected final int locX;
    protected final int locY;
    protected final boolean[] status;
    protected final NoiseGenerator noiseGenerator;
    PortrayMover(String file, ParticleSystem ps) {
        status = new boolean[ps.getNum()];
        for (int i=0; i<ps.getNum(); i++)
            status[i] = false;
        this.file = file;
        sampler = new ImageSampler();
        PImage img = ps.getSketch().loadImage(file);
        int width = ps.getSketch().width/2;
        int height = img.height * width / img.width;
        img.resize(width, height);
        //img.resize(img.width * ps.getSketch().height / img.height, ps.getSketch().height);
        locY = (ps.getSketch().height - img.height) / 2;
        locX = (ps.getSketch().width - img.width) / 2;
        sampler.sampleImage(ps.getSketch(), img, ps.getNum());
        noiseGenerator = new NoiseGenerator(ps.getSketch());
        ps.getVisibleParticles().forEach(particle3D -> setTarget(particle3D, ps.getSketch()));

    }

    public String getFile() {
        return file;
    }

    @Override
    public void move(@NotNull ParticleSystem ps) {
        ps.getVisibleParticles().forEach(particle3D -> moveParticle(particle3D, ps));
        ps.transDeadToImmortal();
        super.move(ps);
    }

    protected void moveParticle(Particle3D p, ParticleSystem ps) {
        if (p.isImmortal() && !status[ps.getParticles().indexOf(p)]){
            status[ps.getParticles().indexOf(p)] = true;
            Random random = new Random(System.nanoTime());
            p.setVisible(random.nextFloat()>0.80);
        }
        if(status[ps.getParticles().indexOf(p)]){
            mover.perlinMove(noiseGenerator, p, 0.00001f);
            if(!sampler.contains(locX, locY, p.getLocation()))
                p.setLocation(sampler.getRandomPoint(locX, locY));
        }
        if(p.isAlive())
            mover.defaultMove(p);
    }

    @Override
    protected void moveParticle(@NotNull PApplet sketch, @NotNull Particle3D p) {
        if(!sampler.contains(locX, locY, p.getLocation()))
            p.setLocation(sampler.getRandomPoint(locX, locY));
        else mover.perlinMove(noiseGenerator, p, 0.00001f);
    }

    private void setTarget(@NotNull Particle3D p, PApplet sketch){
        PVector target = sampler.getRandomPoint(locX, locY);
        Random random = new Random(System.nanoTime());
        float frame = (10+random.nextInt(10))*sketch.frameRate;
        float distanceX = target.x - p.getLocation().x;
        float distanceY = target.y - p.getLocation().y;
        float accX = distanceX*2/(frame*frame);
        float accY = 0;
        float vX = 0;
        float vY = distanceY / frame;
        p.setLifeSpan((int)frame);
        p.setAcceleration(new PVector(accX, accY));
        p.setVelocity(new PVector(vX, vY));
    }

    @Override
    public void init(ParticleSystem ps) {
        super.init(ps);
        ps.getVisibleParticles().forEach(particle3D -> setTarget(particle3D, ps.getSketch()));

    }
}