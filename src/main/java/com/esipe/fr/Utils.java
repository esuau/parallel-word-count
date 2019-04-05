package com.esipe.fr;

import mpi.MPI;

import java.io.*;

class Utils {

    static void readFile(String[] args) throws Exception {

        File file = new File(args[4]);

        if (!file.exists()) {
            throw new Exception("File doesn't exists");
        }

        String word = args[5];

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            MPI.Init(args);

            int rank = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();

            int destination = 0;
            int source;

            String line = bufferedReader.readLine();
            System.out.println(rank);

            if (rank == 0) {
                int totalOccurs = 0;

                while ((line = bufferedReader.readLine()) != null) {

                    destination = destination == size ? 1 : destination + 1;

                    int lineOccurs = 0;

                    MPI.COMM_WORLD.Send(line, 0, line.length(), MPI.OBJECT, destination, 0);
                    MPI.COMM_WORLD.Recv(lineOccurs, 0, 0, MPI.OBJECT, destination, 0);

                    totalOccurs += lineOccurs;
                }

                System.out.println(totalOccurs);
            } else {
                while (true) {
                    int lineOccurs = 0;
                    String lineToReceive = "";

                    // To return to original Processor equivalent to destination for Sending back lineOccurs
                    source = 0;

                    MPI.COMM_WORLD.Recv(lineToReceive, 0, lineToReceive.length(), MPI.OBJECT,
                            source, 0);

                    for (String wordOfLine : lineToReceive.split(" ")) {
                        if (wordOfLine.equals(word)) {
                            lineOccurs++;
                        }
                    }

                    MPI.COMM_WORLD.Send(lineOccurs, 0, 0, MPI.OBJECT, source, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MPI.Finalize();
        }
    }
}
