import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ForkJoinPool;

public class ClassificationAppTest {
    static long startTime = 0;

    private static void tick() {
        startTime = System.currentTimeMillis();
    }

    private static float tock() {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    static final ForkJoinPool fjPool = new ForkJoinPool();

    static OutputObject sum2(CloudData data, int c) {
        return fjPool.invoke(new ParallelRunTest(data, 0, data.linAdvection.length, c));
    }

    static CloudData data = new CloudData();
    static CloudData data2;
    static OutputObject output;
    static Vector wind2;

    /**
     * main method of program to automatically test values with certain sequential cutoffs
     * @param args
     */
    public static void main(String[] args) {
        float time;

        //Replace with string array of textfiles of choice
        String[] files = new String[]{"10x512x512.txt", "20x512x512.txt", "20x1024x1024.txt", "15x512x512.txt", "40x512x512.txt"};

        //2D array containing sequential cutoffs to be tested for each textfile
        int[][] cutoff = new int[][]{
                {0, 10000, 100000, 110000, 120000, 130000, 140000, 150000, 160000, 170000, 180000, 190000, 200000, 210000, 220000, 230000, 240000, 242000, 244000, 245000, 246000, 248000, 250000, 291000, 500000, 874000, 1000000, 1500000, 4000000, 10000000},
                {0, 10000, 100000, 250000, 300000, 350000, 400000, 450000, 463000, 475000, 488000, 490000, 492500, 495000, 497500, 500000, 583000, 1000000, 1500000, 1750000, 7500000, 10000000, 10000000, 12500000, 15000000},
                {0, 10000, 100000, 250000, 500000, 1000000, 1500000, 1750000, 1800000, 1900000, 1925000, 1950000, 1955000, 1960000, 1962500, 1965000, 1967500, 1970000, 1975000, 2000000, 2250000, 2330000, 2500000, 5000000, 7500000, 10000000, 12500000, 15000000, 16990000, 17500000, 20000000, 30000000, 45000000},
                {0, 10000, 100000, 250000, 437000, 500000, 983000, 1000000, 1310000, 1500000, 1970000},
                {0, 10000, 100000, 250000, 500000, 1000000, 1170000, 1500000, 2620000, 3500000, 5240000}
        };

        //Loops through files and cutoffs to perform tests
        for (int j = 0; j < files.length; j++) {
            getData(files[j]);
            data2 = data;
            System.out.println(files[j] + " got");
            try {
                FileWriter fileWriter = new FileWriter("out" + files[j], true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for (int c : cutoff[j]) {
                    printWriter.print(c + ",");

                    for (int i = 0; i < 13; i++) {

                        if (c == 0) {
                            tick();
                            sequential();
                            time = tock();
                        } else {
                            tick();
                            parallel(c);
                            time = tock();
                        }

                        printWriter.printf("%f,", time);
                        printWriter.flush();
                        System.gc();
                    }
                    printWriter.print("\n");
                }
                printWriter.close();
            } catch (IOException e) {
                System.out.println("Unable to open output file " + files[j] + ".txt");
                e.printStackTrace();
            }
        }
    }

    static void getData(String args) {
        String input = args;
        data.readData(input);
    }

    static void parallel(int c) {
        output = sum2(data2, c);
        output.wind = output.wind.getAverage();
    }


    static void sequential() {
        Vector winds = new Vector();
        int classcount = 0;
        int at = 0;
        int left = -3;
        int right = 3;
        int above = -data.dimx * 3;
        int bottom = data.dimx * 3;
        int x, y, u;
        boolean aboveleft, aboveright, bottomleft, bottomright;
        wind2 = new Vector(0, 0, 0);
        for (int i = 0; i < data.linAdvection.length; i += 3) {
            at = wind2.n % (data.dimy * data.dimx);
            wind2.toAdd(new Vector(data.linAdvection[i], data.linAdvection[i + 1], 1));
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

            if (at % data.dimx != 0) {
                //add left
                winds.x += data.linAdvection[x + left];
                winds.y += data.linAdvection[y + left];
                winds.n++;
                if (at >= data.dimx) {
                    //add above left
                    winds.x += data.linAdvection[x + left + above];
                    winds.y += data.linAdvection[y + left + above];
                    winds.n++;
                    aboveleft = true;
                }
                if ((at + data.dimx) < data.dimx * data.dimy) {
                    //add bottom left
                    winds.x += data.linAdvection[x + left + bottom];
                    winds.y += data.linAdvection[y + left + bottom];
                    winds.n++;
                    bottomleft = true;
                }
            }
            if (at >= data.dimx) {
                //add above
                winds.x += data.linAdvection[x + above];
                winds.y += data.linAdvection[y + above];
                winds.n++;
                if (at % data.dimx != 0 && aboveleft == false) {
                    //add above left
                    winds.x += data.linAdvection[x + above + left];
                    winds.y += data.linAdvection[y + above + left];
                    winds.n++;
                }
                if ((at + 1) % data.dimx != 0) {
                    //add above right
                    winds.x += data.linAdvection[x + above + right];
                    winds.y += data.linAdvection[y + above + right];
                    winds.n++;
                    aboveright = true;
                }
            }
            if ((at + 1) % data.dimx != 0) {
                //add right
                winds.x += data.linAdvection[x + right];
                winds.y += data.linAdvection[y + right];
                winds.n++;
                if ((at + data.dimx) < data.dimx * data.dimy) {
                    //add bottom right
                    winds.x += data.linAdvection[x + right + bottom];
                    winds.y += data.linAdvection[y + right + bottom];
                    winds.n++;
                    bottomright = true;
                }
                if (at >= data.dimx && aboveright == false) {
                    //add above right
                    winds.x += data.linAdvection[x + above + right];
                    winds.y += data.linAdvection[y + above + right];
                    winds.n++;
                }
            }
            if ((at + data.dimx) < data.dimx * data.dimy) {
                //add bottom
                winds.x += data.linAdvection[x + bottom];
                winds.y += data.linAdvection[y + bottom];
                winds.n++;
                if (at % data.dimx != 0 && bottomleft == false) {
                    //add bottom left
                    winds.x += data.linAdvection[x + bottom + left];
                    winds.y += data.linAdvection[y + bottom + left];
                    winds.n++;
                }
                if ((at + 1) % data.dimx != 0 && bottomright == false) {
                    //add bottom right
                    winds.x += data.linAdvection[x + bottom + right];
                    winds.y += data.linAdvection[y + bottom + right];
                    winds.n++;
                }
            }
//            at+=1;
//            if(at == data.dimx*data.dimy){
//                at = 0;
//            }

            Vector wind = winds.getAverage();

            if (Math.abs(data.linAdvection[u]) > wind.mag()) {
                data.linClassification[classcount++] = 0;
            } else if (wind.mag() > 0.2) {
                data.linClassification[classcount++] = 1;
            } else {
                data.linClassification[classcount++] = 2;
            }

            winds.n = 0;
            winds.x = 0;
            winds.y = 0;

        }
//        System.out.println(String.format("%s %s %s", data.dimt, data.dimx, data.dimy));
        wind2 = wind2.getAverage();
//        System.out.println(wind2.toString());
//        data.writeData("sequential.txt",wind2);
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
    }


}
