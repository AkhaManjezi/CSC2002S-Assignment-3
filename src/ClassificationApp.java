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
    static CloudData sum2(CloudData data){
        return fjPool.invoke(new ParallelRun(data,0,data.linAdvection.length));
    }

    static CloudData data = new CloudData();
    static CloudData data2;

    public static void main(String[] args) {
        getData();
        data2 = data;

        tick();
//        sequential();
        float time = tock();
        System.out.println(time);

        System.out.println("");

        tick();
        parallel();
        time = tock();
        System.out.println(time);
    }

    static void getData(){
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        data.readData(input);
    }

    static void parallel(){
        CloudData output = sum2(data2);
        System.out.println(String.format("%s %s %s", output.dimt, output.dimx, output.dimy));
        output.prevWind = output.prevWind.getAverage();
        System.out.println(output.prevWind.toString());
//        String output2 = "";
//        int count3 = 1;
//        for (int i = 0; i < output.linClassification.length; i++) {
//            output2 += output.linClassification[i] + "";
//            if(count3 < output.dimx*data.dimy){
//                output2 += " ";
//            }else{
//                output2 += "\n";
//                count3=0;
//            }
//            count3++;
//        }
//        System.out.println(output2);
    }

    static void prevWindFJ(){
//        Vector wind = sum2(data.linAdvection);
//        wind.x = (wind.x/data.dim());
//        wind.y = (wind.y/data.dim());
//        System.out.println(wind.x);
//        System.out.println(wind.y);
        
    }

    static void sequential(){
        Vector winds = new Vector();
        int classcount = 0;
        int at = 0;
//        int at;
        int left = -3;
        int right = 3;
        int above = -data.dimx*3;
        int bottom = data.dimx*3;
        int x, y, u;
        boolean aboveleft, aboveright, bottomleft,bottomright;
        Vector wind2 = new Vector(0,0,0);
        for (int i = 0; i < data.linAdvection.length; i+=3) {
            at = wind2.n%(data.dimy*data.dimx);
            wind2.toAdd(new Vector(data.linAdvection[i], data.linAdvection[i+1],1));
            x = i;
            y = i + 1;
            u = i + 2;
            aboveleft = false;
            aboveright = false;
            bottomleft = false;
            bottomright = false;

            winds.x += data.linAdvection[x];
            winds.y += data.linAdvection[y];
            winds.n++;

            if(at%data.dimx!=0){
                //add left
                winds.x += data.linAdvection[x+left];
                winds.y += data.linAdvection[y+left];
                winds.n++;
                if(at >= data.dimx){
                    //add above left
                    winds.x += data.linAdvection[x+left+above];
                    winds.y += data.linAdvection[y+left+above];
                    winds.n++;
                    aboveleft = true;
                }
                if((at+data.dimx)<data.dimx*data.dimy){
                    //add bottom left
                    winds.x += data.linAdvection[x+left+bottom];
                    winds.y += data.linAdvection[y+left+bottom];
                    winds.n++;
                    bottomleft = true;
                }
            }
            if(at >= data.dimx){
                //add above
                winds.x += data.linAdvection[x+above];
                winds.y += data.linAdvection[y+above];
                winds.n++;
                if(at%data.dimx!=0 && aboveleft == false) {
                    //add above left
                    winds.x += data.linAdvection[x+above+left];
                    winds.y += data.linAdvection[y+above+left];
                    winds.n++;
                }
                if((at+1)%data.dimx!=0) {
                    //add above right
                    winds.x += data.linAdvection[x+above + right];
                    winds.y += data.linAdvection[y+above + right];
                    winds.n++;
                    aboveright = true;
                }
            }
            if((at+1)%data.dimx!=0){
                //add right
                winds.x += data.linAdvection[x+right];
                winds.y += data.linAdvection[y+right];
                winds.n++;
                if((at+data.dimx)<data.dimx*data.dimy){
                    //add bottom right
                    winds.x += data.linAdvection[x+right+bottom];
                    winds.y += data.linAdvection[y+right+bottom];
                    winds.n++;
                    bottomright=true;
                }
                if(at >= data.dimx && aboveright==false){
                    //add above right
                    winds.x += data.linAdvection[x+above+right];
                    winds.y += data.linAdvection[y+above+right];
                    winds.n++;
                }
            }
            if((at+data.dimx)<data.dimx*data.dimy){
                //add bottom
                winds.x += data.linAdvection[x+bottom];
                winds.y += data.linAdvection[y+bottom];
                winds.n++;
                if(at%data.dimx!=0 && bottomleft==false) {
                    //add bottom left
                    winds.x += data.linAdvection[x+bottom+left];
                    winds.y += data.linAdvection[y+bottom+left];
                    winds.n++;
                }
                if((at+1)%data.dimx!=0 && bottomright==false) {
                    //add bottom right
                    winds.x += data.linAdvection[x+bottom+right];
                    winds.y += data.linAdvection[y+bottom+right];
                    winds.n++;
                }
            }
//            at+=1;
//            if(at == data.dimx*data.dimy){
//                at = 0;
//            }
            
            Vector wind = winds.getAverage();

            if (Math.abs(data.linAdvection[u]) > wind.mag()){
                data.linClassification[classcount++] = 0;
            }else if(wind.mag() > 0.2){
                data.linClassification[classcount++] = 1;
            }else{
                data.linClassification[classcount++] = 2;
            }

            winds.n=0;
            winds.x = 0;
            winds.y = 0;

        }
        System.out.println(String.format("%s %s %s", data.dimt, data.dimx, data.dimy));
        wind2 = wind2.getAverage();
        System.out.println(wind2.toString());
//        String output2 = "";
//        int count3 = 1;
//        for (int i = 0; i < data.linClassification.length; i++) {
//            output2 += data.linClassification[i] + "";
//            if(count3 < data.dimx*data.dimy){
//                output2 += " ";
//            }else{
//                System.out.println(output2);
//                output2 = "";
//                count3=0;
//            }
//            count3++;
//        }
////        System.out.println(output2);
    }
    

    static void prevWindlin(){
//        Vector wind = new Vector(0,0);
//        for (int i = 0; i < data.dimt; i++) {
//            for (int j = 0; j < data.dimx; j++) {
//                for (int k = 0; k < data.dimy; k++) {
//                    wind.toAdd(data.advection[i][j][k]);
//                }
//            }
//        }
//        wind.x = (wind.x/data.dim());
//        wind.y = (wind.y/data.dim());
//        System.out.println(wind.toString());
        Vector wind2 = new Vector(0,0,0);
        for (int i = 0; i < data.linAdvection.length; i+=3) {
            wind2.toAdd(new Vector(data.linAdvection[i], data.linAdvection[i+1],1));
        }
        System.out.println(String.format("%s %s %s", data.dimt, data.dimx, data.dimy));
        wind2 = wind2.getAverage();
        System.out.println(wind2.toString());
        
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
            Vector wind = new Vector(winds[count], winds[count+1], 1);

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
