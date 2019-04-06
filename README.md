# Parallel Word Count

The purpose of this project is to experiment parallel processing with the [MPJ Express](http://mpj-express.org/) library.
This program reads a text file and counts the number of occurrences in the text for a given word. 

Here, we use the first core to read the file and distribute each line to the other cores. Each of these cores count the 
number of occurrences of the given word in the line they receive. Then, they return their result to the main core that
combines them, printing the final result.

## Requirements

### JDK

Download and install the last version of [Java SE Development Kit 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

### MPJ Express

Download the MPJ Express software on [SourceForge](https://sourceforge.net/projects/mpjexpress/files/releases/).

Add `MPJ_HOME` to your environment variables:

```bash
export MPJ_HOME=/path/to/mpj
```

Update your `PATH` variable:

```bash
export PATH=$MPJ_HOME/bin:$PATH
```

For more information, read the official [user guide](http://mpj-express.org/guides.html) for your operating system.

## Compile

Compile with `javac`:

```bash
javac -d bin -cp .:$MPJ_HOME/lib/mpj.jar src/fr/upec/esipe/ParallelWordCount.java
```

## Run

Run with `mpjrun`: 

```bash
cd bin/
mpjrun -np <number of cores> fr.upec.esipe.ParallelWordCount <path/to/textfile> <word to count> 
```

_Note that the number of cores has to be greater or equal to two._