import processing.core.PApplet;

class NoiseGenerator {

    float xScale, yScale, zScale;
    PApplet sketch;

    NoiseGenerator(PApplet sketch) {

        this.sketch = sketch;
        setDefaultParams();
    }

    NoiseGenerator(PApplet sketch, float x, float y, float z) {

        this.sketch = sketch;
        updateParams(x, y, z);
    }

    NoiseGenerator() {
        setDefaultParams();
    }

    public void setSketch(PApplet sketch) {
        this.sketch = sketch;
    }

    void updateParams(float x, float y, float z) {

        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    void setDefaultParams() {

        updateParams(0.002f, 0.002f, 0.0001f);
    }

    float generateNoise(float x, float y) {

        return sketch.noise(x * this.xScale, y * this.yScale, sketch.millis() * this.zScale);
    }
}
