package fr.upec.esipe;

import mpi.MPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParallelWordCount {

    public static void main(String[] args) throws Exception {
        String[] appArgs = MPI.Init(args);

        if (!(appArgs.length > 1)) {
            throw new Exception("No input file or search word provided");
        }

        int rank = MPI.COMM_WORLD.Rank();

        if (rank == 0) {
            File file = new File(appArgs[0]);
            readFile(file);
        } else {
            countWord(appArgs[1], rank);
        }

        MPI.Finalize();
    }

    private static void readFile(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("The file does not exists");
        }

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            int destination = 0;
            int totalOccurs = 0;

            int size = MPI.COMM_WORLD.Size();

            while ((line = bufferedReader.readLine()) != null) {

                destination = destination == size - 1 ? 1 : destination + 1;
                int[] lineOccurs = {0};
                String[] strings = {line};

                System.out.println("Sending line: " + line + ", destination: " + destination);

                MPI.COMM_WORLD.Send(strings, 0, 1, MPI.OBJECT, destination, 0);
                MPI.COMM_WORLD.Recv(lineOccurs, 0, 1, MPI.INT, destination, 0);

                totalOccurs += lineOccurs[0];
            }

            System.out.println("Result: " + totalOccurs);

            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(new String[]{"terminated"}, 0, 1, MPI.OBJECT, i, 0);
            }

        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    private static void countWord(String searchWord, int rank) {
        while (true) {
            int occurs = 0;
            String[] lineToReceive = {""};

            MPI.COMM_WORLD.Recv(lineToReceive, 0, 1, MPI.OBJECT, 0, 0);

            if (lineToReceive[0].equals("terminated")) {
                break;
            }

            String[] words = lineToReceive[0].split(" ");
            for (String word : words) {
                if (word.toLowerCase().equals(searchWord.toLowerCase())) {
                    occurs++;
                }
            }

            System.out.println("Received line: " + lineToReceive[0] + ", rank: " + rank + ", occurs: " + occurs);

            int[] occursArr = {occurs};
            MPI.COMM_WORLD.Send(occursArr, 0, 1, MPI.INT, 0, 0);
        }
    }

}
