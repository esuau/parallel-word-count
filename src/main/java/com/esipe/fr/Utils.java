package com.esipe.fr;

import mpi.MPI;
import java.io.*;

public class Utils {

    public static void readFile(String[] args) throws Exception {

        File file = new File(args[0]);

        if (!file.exists()) {
            throw new Exception("File doesn't exists");
        }

        String word = args[1];

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            MPI.Init(args);

            int rank = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();

            int destination = 0;
            int source;

            if(rank == 0) {
                int totalOccurs = 0;
                String line = bufferedReader.readLine();

                while (line != null) {
                    line = bufferedReader.readLine();

                    destination = destination == size ? 1 : destination + 1;

                    int lineOccurs = 0;

                    MPI.COMM_WORLD.Send(line, 0, line.length(), MPI.OBJECT,
                            destination, 0);
                    MPI.COMM_WORLD.Recv(lineOccurs, 0, 0, MPI.OBJECT,
                            destination, 0);

                    totalOccurs += lineOccurs;
                }
            } else {
                while(true) {
                    int lineOccurs = 0;
                    String line = "";

                    // To return to original Processor equivalent to destination for Sending back lineOccurs
                    source = 0;

                    MPI.COMM_WORLD.Recv(line, 0, line.length() , MPI.OBJECT,
                            source, 0);

                    for (String wordOfLine : line.split(" ")) {
                        if (wordOfLine.equals(word)) {
                            lineOccurs++;
                        }
                    }

                    MPI.COMM_WORLD.Send(lineOccurs, 0, 0, MPI.OBJECT,
                            source, 0);
                }

            }
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            MPI.Finalize();
        }
    }
}
