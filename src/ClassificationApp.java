import java.util.ArrayList;
import java.util.Arrays;
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
    static Vector sum(Vector[] arr){
        return fjPool.invoke(new SumArray(arr,0,arr.length));
    }

    static CloudData data = new CloudData();
    static CloudData data2 = new CloudData();

    public static void main(String[] args) {
        getData();
        tick();
        prevWindlin();
        float time = tock();
        System.out.println(time);
        tick();
        prevWindFJ();
        time = tock();
        System.out.println(time);
    }

    static void getData(){
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        data.readData(input);
    }

    static void prevWindFJ(){
        Vector wind = sum(data.linAdvection);
        wind.x = (wind.x/data.dim());
        wind.y = (wind.y/data.dim());
        System.out.println(wind.x);
        System.out.println(wind.y);
    }

    static void prevWindlin(){
        Vector wind = new Vector(0,0);
        for (int i = 0; i < data.dimt; i++) {
            for (int j = 0; j < data.dimx; j++) {
                for (int k = 0; k < data.dimy; k++) {
                    wind.toAdd(data.advection[i][j][k]);
                }
            }
        }
        wind.x = (wind.x/data.dim());
        wind.y = (wind.y/data.dim());
        System.out.println(wind.x);
        System.out.println(wind.y);
    }

}
