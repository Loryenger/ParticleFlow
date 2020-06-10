import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.stream.Collectors;

public class ParticleSystem {
    private final PApplet sketch;
    private ArrayList<Particle3D> particles;

    ParticleSystem(PApplet sketch, int num) {
        this.sketch = sketch;
        particles = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < num; i++) {
            int color_ = sketch.color(random.nextFloat() * 255,
                    random.nextFloat() * 255, random.nextFloat() * 255, random.nextFloat() * 255);
            float mass = 0.005f + random.nextFloat() * 0.045f;
            float radius = 5 + mass * 200;
            particles.add(new Particle3D(radius, mass, color_, -1));
        }
    }

    public ArrayList<Particle3D> getVisibleParticles() {
        return particles.stream().filter(Particle3D::isVisible)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Particle3D> getDeadParticles() {

        return particles.stream().filter(Particle3D::isDead)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addParticle(Particle3D p) {

        particles.add(p);
    }

    public boolean removeParticle(Particle3D p) {

        return particles.remove(p);
    }

    public Particle3D removeParticleAt(int index) {

        return particles.remove(index);
    }

    public float[] getLocationArray() {
        float[] locations = new float[particles.size() * 2];
        for (int i = 0; i < particles.size(); i += 2) {
            locations[i] = particles.get(i).getLocation().x;
            locations[i + 1] = particles.get(i).getLocation().y;
        }
        return locations;
    }

    public boolean removeDead() {

        return particles.removeIf(Particle3D::isAlive);
    }

    public PApplet getSketch() {

        return sketch;
    }

    public ArrayList<Particle3D> getParticles() {

        return particles;
    }

    public void setParticles(ArrayList<Particle3D> particles) {
        this.particles = particles;
    }

    public ArrayList<Particle3D> getAliveParticles() {

        return particles.stream().filter(Particle3D::isAlive).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Particle3D> getImmortalParticles() {

        return particles.stream().filter(Particle3D::isImmortal).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Particle3D> getInvisibleParticles() {
        return particles.stream().filter(Particle3D::isInvisible).collect(Collectors.toCollection(ArrayList::new));
    }

    public float[] getUniqueLocationArray(){
        ArrayList<PVector> points = new ArrayList<>();
        for(Particle3D p: particles){
            points.add(p.getLocation());
        }
        Comparator<PVector> xComparator = (o1, o2) -> Float.compare(o1.x, o2.x);
        Comparator<PVector> yComparator = (o1, o2) -> Float.compare(o1.y, o2.y);
        points.sort(xComparator);
        for (int i=0;i<points.size()-1;i++){
            if(xComparator.compare(points.get(i), points.get(i+1))==0){
                if(yComparator.compare(points.get(i), points.get(i+1))==0){
                    points.remove(i+1);
                    i--;
                }
            }
        }
        float[] locations = new float[points.size()*2];
        for(int i=0; i<points.size(); i++){
            locations[2*i] = points.get(i).x;
            locations[2*i+1] = points.get(i).y;
        }
        return locations;
    }

    public int getNum() {

        return particles.size();
    }

    public void move() {

        particles.forEach(Particle3D::move);
    }

    public void move(PVector acc, float speedLimit) {
        particles.forEach(particle3D -> particle3D.move(acc, speedLimit));
    }

    public void stop() {

        particles.forEach(Particle3D::stop);
    }

    public long aliveParticleNumber() {

        return particles.stream().filter(Particle3D::isAlive).count();
    }

    public void transDeadToImmortal() {

        particles.forEach(Particle3D::transDeadToImmortal);
    }
}



