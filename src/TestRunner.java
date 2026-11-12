import java.util.Arrays;
import java.util.List;

/** Simple self-checks (no JUnit needed). Run with option 6 in the menu. */
public class TestRunner {
    private static int passed = 0, failed = 0;

    public static void runAll() {
        System.out.println("\n--- Running test cases ---");
        testKnownGraph();
        testSampleMap();
        testUnreachable();
        testSingleNode();
        testZeroWeight();
        testNegativeRejected();
        testHeapMatchesArray();
        System.out.println("\nPassed: " + passed + "   Failed: " + failed);
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
        if (ok) passed++; else failed++;
    }

    private static void testKnownGraph() {
        // classic 5 node example: 0-1(4), 0-2(1), 2-1(2), 1-3(1), 2-3(5), 3-4(3)
        Graph g = new Graph();
        for (int i = 0; i < 5; i++) g.addNode();
        g.addEdge(0, 1, 4); g.addEdge(0, 2, 1); g.addEdge(2, 1, 2);
        g.addEdge(1, 3, 1); g.addEdge(2, 3, 5); g.addEdge(3, 4, 3);
        int[] expected = {0, 3, 1, 4, 7};
        check("Known 5-node graph (heap)",  Arrays.equals(DijkstraHeap.run(g, 0).dist, expected));
        check("Known 5-node graph (array)", Arrays.equals(DijkstraArray.run(g, 0).dist, expected));
        List<Integer> path = DijkstraHeap.run(g, 0).pathTo(4);
        check("Path 0 -> 4 is 0,2,1,3,4", path.equals(Arrays.asList(0, 2, 1, 3, 4)));
    }

    private static void testSampleMap() {
        Graph g = CityMap.sample();
        PathResult r = DijkstraHeap.run(g, g.getId("Delhi"));
        check("Delhi -> Chandigarh = 255 km (via Panipat, Karnal, Ambala)",
                r.dist[g.getId("Chandigarh")] == 255);
        check("Delhi -> Amritsar = 460 km", r.dist[g.getId("Amritsar")] == 460);
    }

    private static void testUnreachable() {
        Graph g = new Graph();
        g.addNode("A"); g.addNode("B"); g.addNode("C");
        g.addEdge(0, 1, 5);
        PathResult r = DijkstraHeap.run(g, 0);
        check("Unreachable node is INF", !r.isReachable(2) && r.pathTo(2).isEmpty());
    }

    private static void testSingleNode() {
        Graph g = new Graph();
        g.addNode("Only");
        check("Single node graph", DijkstraHeap.run(g, 0).dist[0] == 0);
    }

    private static void testZeroWeight() {
        Graph g = new Graph();
        for (int i = 0; i < 3; i++) g.addNode();
        g.addEdge(0, 1, 0); g.addEdge(1, 2, 0);
        check("Zero weight edges", DijkstraHeap.run(g, 0).dist[2] == 0);
    }

    private static void testNegativeRejected() {
        Graph g = new Graph();
        g.addNode(); g.addNode();
        boolean rejected = false;
        try { g.addEdge(0, 1, -3); } catch (IllegalArgumentException e) { rejected = true; }
        check("Negative weight is rejected", rejected);
    }

    private static void testHeapMatchesArray() {
        boolean allMatch = true;
        for (int seed = 1; seed <= 50; seed++) {
            Graph g = GraphGenerator.random(200, 800, seed);
            if (!Arrays.equals(DijkstraHeap.run(g, 0).dist, DijkstraArray.run(g, 0).dist)) allMatch = false;
        }
        check("Heap and array agree on 50 random graphs", allMatch);
    }
}
