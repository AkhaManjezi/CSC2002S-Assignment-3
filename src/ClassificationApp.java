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
    static Vector sum2(float[] arr){
        return fjPool.invoke(new SumLin(arr,0,arr.length));
    }

    static CloudData data = new CloudData();

    public static void main(String[] args) {
        getData();
//        tick();
        prevWindlin();
        localWindlin();
//        float time = tock();
//        System.out.println(time);
//        tick();
//        prevWindFJ();
//        time = tock();
//        System.out.println(time);
    }

    static void getData(){
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        data.readData(input);
    }

    static void prevWindFJ(){
        Vector wind = sum2(data.linAdvection);
//        wind.x = (wind.x/data.dim());
//        wind.y = (wind.y/data.dim());
//        System.out.println(wind.x);
//        System.out.println(wind.y);
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
        System.out.println(String.format("%s %s %s", data.dimt, data.dimx, data.dimy));
        wind.x = (wind.x/data.dim());
        wind.y = (wind.y/data.dim());
        System.out.println(wind.toString());
//        System.out.println(wind.x);
//        System.out.println(wind.y);
    }

    static void localWindlin(){
        Vector[][][] localWind = new Vector[data.dimt][data.dimx][data.dimy];
//        for (int i = 0; i < data.dimt; i++) {
//            for (int j = 0; j < data.dimx; j++) {
//                for (int k = 0; k < data.dimy; k++) {
//                    int count = 1;
//                    localWind[i][j][k] = new Vector();
//                    localWind[i][j][k].toAdd(data.advection[i][j][k]);
//                    if (j != 0){
//                        localWind[i][j][k].toAdd(data.advection[i][j-1][k]);
//                        count++;
//                    }
//                    if (k != 0){
//                        localWind[i][j][k].toAdd(data.advection[i][j][k-1]);
//                        count++;
//                    }
//                    if (j != 0 && k != 0){
//                        localWind[i][j][k].toAdd(data.advection[i][j-1][k-1]);
//                        count++;
//                    }
//                    if (j != data.dimx-1){
//                        localWind[i][j][k].toAdd(data.advection[i][j+1][k]);
//                        count++;
//                    }
//                    if (k != data.dimy-1){
//                        localWind[i][j][k].toAdd(data.advection[i][j][k+1]);
//                        count++;
//                    }
//                    if (j != data.dimx-1 && k != data.dimy-1){
//                        localWind[i][j][k].toAdd(data.advection[i][j+1][k+1]);
//                        count++;
//                    }
//
//                    if (j != 0 && k != data.dimy-1){
//                        localWind[i][j][k].toAdd(data.advection[i][j-1][k+1]);
//                        count++;
//                    }
//                    if (k != 0 && j != data.dimx-1) {
//                        localWind[i][j][k].toAdd(data.advection[i][j+1][k-1]);
//                        count++;
//                    }
//
//                    localWind[i][j][k].x = localWind[i][j][k].x/count;
//                    localWind[i][j][k].y = localWind[i][j][k].y/count;
//
//                    if (Math.abs(data.convection[i][j][k]) > localWind[i][j][k].mag()){
//                        data.classification[i][j][k] = 0;
//                    }else if(localWind[i][j][k].mag() > 0.2){
//                        data.classification[i][j][k] = 1;
//                    }else{
//                        data.classification[i][j][k] = 2;
//                    }
//                }
//            }
//        }
        for (int i = 0; i < data.dimt; i++) {
            for (int j = 0; j < data.dimx; j++) {
                for (int k = 0; k < data.dimy; k++) {
                    localWind[i][j][k] = new Vector();
                    int neighbours = 0;
                    for (int l = Math.max(0, j-1); l < Math.min(data.dimx, j+2); l++) {
                        for (int m = Math.max(0, k-1); m < Math.min(data.dimy, k+2); m++) {
                            localWind[i][j][k].toAdd(data.advection[i][l][m]);
                            neighbours++;
                        }
                    }
                    localWind[i][j][k].x = localWind[i][j][k].x/neighbours;
                    localWind[i][j][k].y = localWind[i][j][k].y/neighbours;

                    if (Math.abs(data.convection[i][j][k]) > localWind[i][j][k].mag()){
                        data.classification[i][j][k] = 0;
                    }else if(localWind[i][j][k].mag() > 0.2){
                        data.classification[i][j][k] = 1;
                    }else{
                        data.classification[i][j][k] = 2;
                    }
                }
            }
        }


        String output;
        for (int i = 0; i < data.dimt; i++) {
            output = "";
//            String output2 = "";
//            String output3 = "";
            for (int j = 0; j < data.dimx; j++) {
                for (int k = 0; k < data.dimy; k++) {
                    output += data.classification[i][j][k] + " ";
//                    output2 += localWind[i][j][k].mag() + " ";
//                    output3 += data.convection[i][j][k] + " ";
                }
            }
            System.out.println(output);
//            System.out.println(output2);
//            System.out.println(output3);
        }

    }

}
