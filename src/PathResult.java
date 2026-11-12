import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Output of a shortest path run from one source: distances and parent links. */
public class PathResult {
    public static final int INF = Integer.MAX_VALUE;

    public final int[] dist;    // dist[v] = shortest distance from source to v (INF if unreachable)
    public final int[] parent;  // parent[v] = previous node on the shortest path (-1 for source)

    public PathResult(int[] dist, int[] parent) {
        this.dist = dist;
        this.parent = parent;
    }

    public boolean isReachable(int target) {
        return dist[target] != INF;
    }

    /** Rebuilds the route from source to target by walking the parent links backwards. */
    public List<Integer> pathTo(int target) {
        List<Integer> path = new ArrayList<>();
        if (!isReachable(target)) return path;
        for (int v = target; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }
}
