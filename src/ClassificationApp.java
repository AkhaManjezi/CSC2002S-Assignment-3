import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ClassificationApp {
    static long startTime = 0;

    private static void tick(){
        startTime = System.currentTimeMillis();
    }
    private static float tock(){
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }
    static final ForkJoinPool fjPool = new ForkJoinPool();
    static float sum(float[] arr){
        return fjPool.invoke(new SumArray(arr,0,arr.length));
    }

    static CloudData data = new CloudData();
    static CloudData data2 = new CloudData();

    public static void main(String[] args) {
        getData();
        prevWindlin();
    }

    static void getData(){
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        data.readData(input);
        data2.readData(input);
    }

    static void prevWindlin(){
        Vector wind = new Vector();
        float totalx = 0;
        float totaly = 0;
        for (int i = 0; i < data.dimt; i++) {
            for (int j = 0; j < data.dimx; j++) {
                for (int k = 0; k < data.dimy; k++) {
                    totalx += data.advection[i][j][k].x;
                    totaly += data.advection[i][j][k].y;
                }
            }
        }
        wind.x = (totalx/data.dimt)/(data.dimx+data.dimy);
        wind.y = (totaly/data.dimt)/(data.dimx+data.dimy);
    }

}
