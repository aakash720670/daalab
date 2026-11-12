import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/** Times DijkstraHeap and DijkstraArray on growing graphs and saves results to a CSV file. */
public class Benchmark {
    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_RUNS = 5;

    public static void runAll(String csvFile) {
        int[] sparseSizes = {500, 1000, 2000, 5000, 10000, 20000};
        int[] denseSizes  = {250, 500, 1000, 1500};

        try (PrintWriter out = new PrintWriter(new FileWriter(csvFile))) {
            out.println("type,V,E,heap_ms,array_ms,results_match");

            System.out.println("\n--- Sparse graphs (E = 4 x V) ---");
            printHeader();
            for (int n : sparseSizes) {
                measure("sparse", GraphGenerator.random(n, 4L * n, 42), out);
            }

            System.out.println("\n--- Dense graphs (E = V*V/4) ---");
            printHeader();
            for (int n : denseSizes) {
                measure("dense", GraphGenerator.random(n, (long) n * n / 4, 42), out);
            }
        } catch (IOException e) {
            System.out.println("Could not write CSV: " + e.getMessage());
        }
        System.out.println("\nResults saved to " + csvFile);
    }

    private static void printHeader() {
        System.out.printf("%8s %10s %12s %12s %8s%n", "V", "E", "Heap (ms)", "Array (ms)", "Match");
    }

    private static void measure(String type, Graph g, PrintWriter out) {
        double heapMs = averageMs(true, g);
        double arrMs = averageMs(false, g);
        boolean match = Arrays.equals(DijkstraHeap.run(g, 0).dist, DijkstraArray.run(g, 0).dist);

        System.out.printf("%8d %10d %12.3f %12.3f %8s%n",
                g.size(), g.edgeCount(), heapMs, arrMs, match ? "YES" : "NO");
        out.printf("%s,%d,%d,%.3f,%.3f,%b%n", type, g.size(), g.edgeCount(), heapMs, arrMs, match);
    }

    private static double averageMs(boolean useHeap, Graph g) {
        for (int i = 0; i < WARMUP_RUNS; i++) run(useHeap, g);   // let the JVM warm up
        long total = 0;
        for (int i = 0; i < TIMED_RUNS; i++) {
            long start = System.nanoTime();
            run(useHeap, g);
            total += System.nanoTime() - start;
        }
        return total / (double) TIMED_RUNS / 1_000_000.0;
    }

    private static PathResult run(boolean useHeap, Graph g) {
        return useHeap ? DijkstraHeap.run(g, 0) : DijkstraArray.run(g, 0);
    }
}
