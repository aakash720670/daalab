import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Weighted undirected graph stored as an adjacency list.
 * Each node is a city. Each edge is a road with a distance in km.
 */
public class Graph {
    private final List<String> names = new ArrayList<>();
    private final Map<String, Integer> idByName = new HashMap<>();
    private final List<List<int[]>> adj = new ArrayList<>(); // each entry: {neighbour, weight}
    private int edgeCount = 0;

    /** Adds a city and returns its id. If it already exists, returns the old id. */
    public int addNode(String name) {
        String key = name.trim().toLowerCase();
        Integer existing = idByName.get(key);
        if (existing != null) return existing;
        int id = names.size();
        names.add(name.trim());
        idByName.put(key, id);
        adj.add(new ArrayList<>());
        return id;
    }

    /** Adds a numbered node (used by the random graph generator). */
    public int addNode() {
        return addNode("N" + names.size());
    }

    /** Returns city id, or -1 if the city does not exist. */
    public int getId(String name) {
        Integer id = idByName.get(name.trim().toLowerCase());
        return id == null ? -1 : id;
    }

    /** Adds a two-way road. Dijkstra needs weight >= 0, so negative weights are rejected. */
    public void addEdge(int u, int v, int weight) {
        if (weight < 0) throw new IllegalArgumentException("Dijkstra does not allow negative weights");
        if (u == v) return;
        adj.get(u).add(new int[]{v, weight});
        adj.get(v).add(new int[]{u, weight});
        edgeCount++;
    }

    public int size()                  { return names.size(); }
    public int edgeCount()             { return edgeCount; }
    public String nameOf(int id)       { return names.get(id); }
    public List<int[]> neighbours(int u) { return adj.get(u); }
}
