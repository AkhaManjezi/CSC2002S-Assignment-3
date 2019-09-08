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
    static OutputObject sum2(CloudData data){
        return fjPool.invoke(new ParallelRun(data,0,data.linAdvection.length));
    }

    static CloudData data = new CloudData();
    static OutputObject output;
    static Vector wind2;
    static String outputFile;

    public static void main(String[] args) {
        getData();
        parallel();
//        sequential();
        output.data.writeData("parallel.txt", output.wind);
    }

    static void getData(){
        Scanner scan = new Scanner(System.in);
        String inputFile = scan.next();
        outputFile = scan.next();
        if(!inputFile.endsWith(".txt")){
            inputFile += ".txt";
        }
        if(!outputFile.endsWith(".txt")){
            outputFile += ".txt";
        }
        data.readData(inputFile);
    }

    static void parallel(){
        output = sum2(data);
        output.wind = output.wind.getAverage();
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
        wind2 = new Vector(0,0,0);
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
        wind2 = wind2.getAverage();

    }



}
