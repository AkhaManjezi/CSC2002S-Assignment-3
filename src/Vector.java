public class Vector {
    float x;
    float y;
    int n;

    public Vector() {
        x =0;
        y=0;
        n=0;
    }

    public Vector(float x, float y, int n) {
        this.x = x;
        this.y = y;
        n = n;
    }

    void toAdd(Vector toAdd){
        x += toAdd.x;
        y += toAdd.y;
        n++;
    }

    float mag(){
        return (float)Math.sqrt(x*x + y*y);
    }

    Vector combine(Vector toCombine){
        Vector created = new Vector(x+toCombine.x, y+toCombine.y, n+toCombine.n);
        return created;
    }

    Vector getAverage(){
        return new Vector(x/n, y/n,1);
    }

    @Override
    public String toString() {
        return String.format("%.6f %.6f",x, y);
    }
}
