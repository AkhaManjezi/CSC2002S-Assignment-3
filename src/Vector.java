public class Vector {
    float x;
    float y;

    public Vector() {
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void toAdd(Vector toAdd){
        x += toAdd.x;
        y += toAdd.y;
    }

    Vector combine(Vector toCombine){
        return new Vector(x+toCombine.x, y+toCombine.y);
    }

    @Override
    public String toString() {
        return String.format("[%f,%f]",x, y);
    }
}
