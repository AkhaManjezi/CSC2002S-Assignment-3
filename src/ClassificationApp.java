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
//        Vector[][][] localWind = new Vector[data.dimt][data.dimx][data.dimy];
//        for (int i = 0; i < data.dimt; i++) {
//            for (int j = 0; j < data.dimx; j++) {
//                for (int k = 0; k < data.dimy; k++) {
//                    localWind[i][j][k] = new Vector();
//                    int neighbours = 0;
//                    for (int l = Math.max(0, j-1); l < Math.min(data.dimx, j+2); l++) {
//                        for (int m = Math.max(0, k-1); m < Math.min(data.dimy, k+2); m++) {
//                            localWind[i][j][k].toAdd(data.advection[i][l][m]);
//                            neighbours++;
//                        }
//                    }
//                    localWind[i][j][k].x = localWind[i][j][k].x/neighbours;
//                    localWind[i][j][k].y = localWind[i][j][k].y/neighbours;
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
//
//        String output;
//        for (int i = 0; i < data.dimt; i++) {
//            output = "";
//            for (int j = 0; j < data.dimx; j++) {
//                for (int k = 0; k < data.dimy; k++) {
//                    output += data.classification[i][j][k] + " ";
//                }
//            }
//            System.out.println(output);
//        }

        //Linear ARRAY TEST

//        float[] winds = new float[data.dim()*2];
        float[] winds = new float[2];
        int count = 0;
        int classcount = 0;
        int at = 0;
        int left = -3;
        int right = 3;
        int above = -data.dimx*3;
        int bottom = data.dimx*3;
        int neighbours = 0;
        for (int i = 0; i < data.linAdvection.length; i+=3) {
            int x = i;
            int y = i + 1;
            int u = i + 2;

            boolean aboveleft = false;
            boolean aboveright = false;
            boolean bottomleft = false;
            boolean bottomright = false;

            System.out.println(at);

            winds[count] += data.linAdvection[x];
            winds[count+1] += data.linAdvection[y];
            neighbours++;

            if(at%data.dimx!=0){
                //add left
                System.out.println("add left");
                winds[count] += data.linAdvection[x+left];
                winds[count+1] += data.linAdvection[y+left];
                neighbours++;
                if(at >= data.dimx){
                    //add above left
                    System.out.println("add above left");
                    winds[count] += data.linAdvection[x+left+above];
                    winds[count+1] += data.linAdvection[y+left+above];
                    neighbours++;
                    aboveleft = true;
                }
                if((at+data.dimx)<data.dimx*data.dimy){
                    //add bottom left
                    System.out.println("add bottom left");
                    winds[count] += data.linAdvection[x+left+bottom];
                    winds[count+1] += data.linAdvection[y+left+bottom];
                    neighbours++;
                    bottomleft = true;
                }
            }
            if(at >= data.dimx){
                //add above
                System.out.println("add above");
                winds[count] += data.linAdvection[x+above];
                winds[count+1] += data.linAdvection[y+above];
                neighbours++;
                if(at%data.dimx!=0 && aboveleft == false) {
                    //add above left
                    System.out.println("add above left");
                    winds[count] += data.linAdvection[x+above+left];
                    winds[count+1] += data.linAdvection[y+above+left];
                    neighbours++;
                }
                if((at+1)%data.dimx!=0) {
                    //add above right
                    System.out.println("add above right");
                    winds[count] += data.linAdvection[x+above + right];
                    winds[count+1] += data.linAdvection[y+above + right];
                    neighbours++;
                    aboveright = true;
                }
            }
            if((at+1)%data.dimx!=0){
                //add right
                System.out.println("add right");
                winds[count] += data.linAdvection[x+right];
                winds[count+1] += data.linAdvection[y+right];
                neighbours++;
                if((at+data.dimx)<data.dimx*data.dimy){
                    //add bottom right
                    System.out.println("add bottom right");
                    winds[count] += data.linAdvection[x+right+bottom];
                    winds[count+1] += data.linAdvection[y+right+bottom];
                    neighbours++;
                    bottomright=true;
                }
                if(at >= data.dimx && aboveright==false){
                    //add above right
                    System.out.println("add above right");
                    winds[count] += data.linAdvection[x+above+right];
                    winds[count+1] += data.linAdvection[y+above+right];
                    neighbours++;
                }
            }
            if((at+data.dimx)<data.dimx*data.dimy){
                //add bottom
                System.out.println("add bottom");
                winds[count] += data.linAdvection[x+bottom];
                winds[count+1] += data.linAdvection[y+bottom];
                neighbours++;
                if(at%data.dimx!=0 && bottomleft==false) {
                    //add bottom left
                    System.out.println("add bottom left");
                    winds[count] += data.linAdvection[x+bottom+left];
                    winds[count+1] += data.linAdvection[y+bottom+left];
                    neighbours++;
                }
                if((at+1)%data.dimx!=0 && bottomright==false) {
                    //add bottom right
                    System.out.println("add bottom right");
                    winds[count] += data.linAdvection[x+bottom+right];
                    winds[count+1] += data.linAdvection[y+bottom+right];
                    neighbours++;
                }
            }
            at+=1;
            if(at == data.dimx*data.dimy){
                at = 0;
            }

            winds[count] = winds[count]/neighbours;
            winds[count+1] = winds[count+1]/neighbours;
            Vector wind = new Vector(winds[count], winds[count+1]);

            if (Math.abs(data.linAdvection[u]) > wind.mag()){
                data.linClassification[classcount++] = 0;
            }else if(wind.mag() > 0.2){
                data.linClassification[classcount++] = 1;
            }else{
                data.linClassification[classcount++] = 2;
            }

            neighbours=0;
            winds[0] = 0;
            winds[1] = 0;

        }

        String output2 = "";
        int count3 = 1;
        for (int i = 0; i < data.linClassification.length; i++) {
            output2 += data.linClassification[i] + "";
            if(count3 < data.dimx*data.dimy){
                output2 += " ";
            }else{
                output2 += "\n";
                count3=0;
            }
            count3++;
        }
        System.out.println(output2);




    }

}
