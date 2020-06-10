import com.badlogic.gdx.utils.IntArray;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class DelaunayTest extends PApplet {
    DelaunayTriangulator triangulator;
    ParticleSystem ps;
    ParticleMover mover;
    ParticleVisualizer visualizer;
    ColorPicker picker;
    IntArray indices;
    ImageSampler sampler;
    PImage img, bg;
    float[] buffer;
    NoiseGenerator ng;
    int locX, locY;

    public static void main(String[] passedArgs) {

        String[] appletArgs = new String[]{"DelaunayTest"};
        PApplet.main(appletArgs);
    }

    @Override
    public void settings() {

        size(1200, 600);
        pixelDensity(2);
    }

    @Override
    public void setup() {
        frameRate(60.0f);
        triangulator = new DelaunayTriangulator();
        ps = new ParticleSystem(this, 10000);
        mover = new ParticleMover();
        sampler = new ImageSampler();
        visualizer = new ParticleVisualizer();
        ng = new NoiseGenerator(this);
        bg = loadImage("/Users/loryenger/Downloads/7.png");
        bg.resize(width, height);
        picker = new ColorPicker(bg);
        img = loadImage("/Users/loryenger/Downloads/2.png");
        img.resize(img.width * height / img.height, height);
        locX = (width - img.width) / 2;
        locY = 0;
        sampler.sampleImage(this, img, ps.getNum());
        ps.getParticles().forEach(particle3D -> mover.randomPosition(particle3D, width, height));
        strokeWeight(0.1f);
        noFill();
    }

    @Override
    public void draw() {
        background(0);
        buffer = ps.getUniqueLocationArray();
        indices = triangulator.computeTriangles(buffer, true);
        for (int i = 0; i < indices.size; i += 3) {
            if (buffer[indices.get(i)] * buffer[indices.get(i) + 1]
                    * buffer[indices.get(i + 1)] * buffer[indices.get(i + 1) + 1]
                    * buffer[indices.get(i + 2)] * buffer[indices.get(i + 2) + 1]
                    == 0) continue;
            if (triangulator.tooLong(buffer[indices.get(i)], buffer[indices.get(i) + 1],
                    buffer[indices.get(i + 1)], buffer[indices.get(i + 1) + 1],
                    buffer[indices.get(i + 2)], buffer[indices.get(i + 2) + 1], 40.f)) continue;
            if (sampler.visible(img, locX, locY, new PVector(buffer[indices.get(i)], buffer[indices.get(i) + 1]),
                    new PVector(buffer[indices.get(i + 1)], buffer[indices.get(i + 1) + 1]),
                    new PVector(buffer[indices.get(i + 2)], buffer[indices.get(i + 2) + 1]),2)) {
                fill(picker.getColor(0,0, new PVector(buffer[indices.get(i)], buffer[indices.get(i) + 1])));
            } else {
                stroke(0xEB,0xDD,0x5C);
                noFill();
            }
            //stroke(picker.getRandomColor());
            triangle(buffer[indices.get(i)], buffer[indices.get(i) + 1],
                    buffer[indices.get(i + 1)], buffer[indices.get(i + 1) + 1],
                    buffer[indices.get(i + 2)], buffer[indices.get(i + 2) + 1]);
        }
    }
}

