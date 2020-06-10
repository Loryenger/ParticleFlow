import org.lwjgl.Sys;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class ImageSampler {
    private final ArrayList<Integer> selectedIndices;
    private final ArrayList<Integer> pixel;
    private final ArrayList<PVector> loci;
    private final ArrayList<Integer> indices;
    private final ArrayList<PVector> points;
    private int[][] validRange;
    private int[][] invalidRange;
    private int height;
    private int width;

    ImageSampler() {
        indices = new ArrayList<>();
        selectedIndices = new ArrayList<>();
        pixel = new ArrayList<>();
        loci = new ArrayList<>();
        points = new ArrayList<>();
        width = 0;
        height = 0;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ArrayList<PVector> getPoints() {
        return points;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public ArrayList<Integer> getSelectedIndices() {
        return selectedIndices;
    }

    public ArrayList<Integer> getPixel() {
        return pixel;
    }

    public ArrayList<PVector> getLoci() {
        return loci;
    }

    public void sampleImage(PApplet sketch, PImage img, int num) {
        this.width = img.width;
        this.height = img.height;
        this.validRange = new int[height][2];
        this.invalidRange = new int[height][2];
        for (int i = 0; i < img.height; i++) {
            for (int j = 0; j < img.width; j++) {
                int loc = i * img.width + j;
                float a = sketch.alpha(img.pixels[loc]);
                if (a > 200) {
                    indices.add(loc);
                    points.add(new PVector(j, i));
                }
            }
            for (int j = 0; j < img.width; j++) {
                int loc = i * img.width + j;
                float a = sketch.alpha(img.pixels[loc]);
                if (a > 200) {
                    validRange[i][0] = j;
                    break;
                }
            }
            for (int j = img.width - 1; j > 0; j--) {
                int loc = i * img.width + j;
                float a = sketch.alpha(img.pixels[loc]);
                if (a > 200) {
                    validRange[i][1] = j;
                    break;
                }
            }
            for (int j = validRange[i][0]; j < validRange[i][1]; j++) {
                int loc = i * img.width + j;
                float a = sketch.alpha(img.pixels[loc]);
                if (a < 20) {
                    invalidRange[i][0] = j;
                    break;
                }
            }
            for (int j = validRange[i][1]; j > validRange[i][0]; j--) {
                int loc = i * img.width + j;
                float a = sketch.alpha(img.pixels[loc]);
                if (a < 20) {
                    invalidRange[i][1] = j;
                    break;
                }
            }

        }
        for (int i = 0; i < num; i++) {
            selectedIndices.add(indices.get((int) (sketch.random(0, indices.size()))));
            pixel.add(img.pixels[selectedIndices.get(i)]);
            loci.add(new PVector(selectedIndices.get(i) % img.width, selectedIndices.get(i) / img.width));
        }
    }

    public boolean contains(int x, int y, PVector point, PImage img){
        img.filter(PImage.GRAY);
        int X = (int) point.x - x;
        int Y = (int) point.y - y;
        if (X >= width || Y >= height)
            return false;
        if (X < 0 || Y < 0)
            return false;
       // System.out.println(img.pixels[Y * img.width + X]);
        return img.pixels[Y * img.width + X] != -1;
    }

    public boolean contains(int x, int y, PVector point) {
        if ((point.x - x >= width) && (point.y - y >= height))
            return false;
        if (validRange.length <= (int) point.y - y || (int) point.y - y < 0)
            return false;
        return (validRange[(int) point.y - y][0] < point.x - x && validRange[(int) point.y - y][1] > point.x - x) &&
                (invalidRange[(int) point.y - y][0] > point.x - x || invalidRange[(int) point.y - y][1] < point.x - x);
    }

    public PVector getRandomPoint() {
        Random random = new Random(System.nanoTime());
        return points.get(random.nextInt(points.size()));
    }

    public PVector getRandomPoint(int x, int y) {
        PVector p = getRandomPoint();
        return new PVector(p.x + x, p.y + y);
    }
    public boolean visible(PImage img, int x, int y, PVector p1, PVector p2, PVector p3, int level){
        int i = 0;
        if(contains(x, y, p1, img))i++;
        if(contains(x, y, p2, img))i++;
        if(contains(x, y, p3, img))i++;
        return i>=level;
    }

}
