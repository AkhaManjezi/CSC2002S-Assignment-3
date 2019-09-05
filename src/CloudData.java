import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CloudData {

    int dimx, dimy, dimt; // data dimensions
    float[] linAdvection;
    int[] linClassification;

    // overall number of elements in the timeline grids
    int dim() {
        return dimt * dimx * dimy;
    }

    // convert linear position into 3D location in simulation grid
    void locate(int pos, int[] ind) {
        ind[0] = (int) pos / (dimx * dimy); // t
        ind[1] = (pos % (dimx * dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
    }

    // read cloud simulation data from file
    void readData(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName), "UTF-8");

            // input grid dimensions and simulation duration in timesteps
            dimt = sc.nextInt();
            dimx = sc.nextInt();
            dimy = sc.nextInt();

            // initialize and load advection (wind direction and strength) and convection
            linAdvection = new float[dim() * 3];
            int count = 0;
            for (int t = 0; t < dimt; t++)
                for (int x = 0; x < dimx; x++)
                    for (int y = 0; y < dimy; y++) {
                        linAdvection[count++] = sc.nextFloat();
                        linAdvection[count++] = sc.nextFloat();
                        linAdvection[count++] = sc.nextFloat();
                    }
            linClassification = new int[dim()];
            sc.close();
        } catch (IOException e) {
            System.out.println("Unable to open input file " + fileName);
            e.printStackTrace();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Malformed input file " + fileName);
            e.printStackTrace();
        }
    }

    // write classification output to file
    void writeData(String fileName, Vector wind) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
            printWriter.printf("%f %f\n", wind.x, wind.y);

            String output2 = "";
            int count3 = 1;
            for (int i = 0; i < linClassification.length; i++) {
                output2 += linClassification[i] + "";
                if (count3 < dimx * dimy) {
                    output2 += " ";
                } else {
                    printWriter.println(output2);
                    output2 = "";
                    count3 = 0;
                }
                count3++;
            }

            printWriter.close();
        } catch (IOException e) {
            System.out.println("Unable to open output file " + fileName);
            e.printStackTrace();
        }
    }
}
