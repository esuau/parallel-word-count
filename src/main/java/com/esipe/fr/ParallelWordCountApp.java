package com.esipe.fr;

import org.apache.log4j.Logger;

public class ParallelWordCountApp {

    static final Logger logger = Logger.getLogger(ParallelWordCountApp.class);

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            throw new Exception("Pas de fichier et / ou pas de mot à rechercher de fourni");
        }

        Utils.readFile(args);

    }
}
