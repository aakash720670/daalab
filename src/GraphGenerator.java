import java.util.Random;

/** Creates random connected graphs for testing and benchmarking. */
public class GraphGenerator {

    /**
     * Builds a connected graph with n nodes and about m edges (m >= n - 1).
     * First a random spanning tree is built so every node is reachable,
     * then the remaining edges are added between random pairs.
     * Weights are random between 1 and 100.
     */
    public static Graph random(int n, long m, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph();
        for (int i = 0; i < n; i++) g.addNode();

        for (int v = 1; v < n; v++) {
            int u = rnd.nextInt(v);
            g.addEdge(u, v, 1 + rnd.nextInt(100));
        }
        long extra = m - (n - 1);
        for (long i = 0; i < extra; i++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            g.addEdge(u, v, 1 + rnd.nextInt(100));   // self loops are ignored by addEdge
        }
        return g;
    }
}
