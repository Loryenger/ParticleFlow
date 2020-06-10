import org.jetbrains.annotations.NotNull;
import processing.core.PImage;
import processing.core.PVector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ColorPicker {
    PImage img;
    Integer[] colorTable;

    ColorPicker(PImage img) {
        init(img);
    }

    private void init(PImage img) {
        this.img = img;
        Set<Integer> colorSet = new HashSet<>();
        for (int i = 0; i < this.img.pixels.length; i++)
            colorSet.add(this.img.pixels[i]);
        colorTable = new Integer[colorSet.size()];
        colorTable = colorSet.toArray(new Integer[0]);
    }

    public int getRandomColor() {
        Random random = new Random(System.nanoTime());
        return colorTable[random.nextInt(colorTable.length)];
    }

    public int getColor(int index){
        if(index<0||index>=colorTable.length)
            index=0;
        return colorTable[index];
    }

    public int getColor(int x, int y, @NotNull PVector pos){
        int X = (int)pos.x-x;
        int Y = (int)pos.y-y;
        if(X<0||Y<0||X>=img.width||Y>=img.height)
            return getRandomColor();
        else return img.pixels[Y*img.width+X];
    }
}
