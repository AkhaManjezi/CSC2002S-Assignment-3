public class Vector {
    float x;
    float y;

    public Vector() {
        x =0;
        y=0;
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void toAdd(Vector toAdd){
        x += toAdd.x;
        y += toAdd.y;
    }

    float mag(){
        return (float)Math.sqrt(x*x + y*y);
    }

    Vector combine(Vector toCombine){
        return new Vector(x+toCombine.x, y+toCombine.y);
    }

    @Override
    public String toString() {
        return String.format("%.6f %.6f",x, y);
    }
}
