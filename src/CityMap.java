/** Built-in sample map: North India cities with approximate road distances in km. */
public class CityMap {

    public static Graph sample() {
        Graph g = new Graph();
        String[] cities = {
            "Delhi", "Panipat", "Karnal", "Ambala", "Chandigarh", "Ludhiana",
            "Amritsar", "Meerut", "Dehradun", "Rohtak", "Agra", "Jaipur", "Lucknow"
        };
        for (String c : cities) g.addNode(c);

        road(g, "Delhi", "Panipat", 90);
        road(g, "Panipat", "Karnal", 40);
        road(g, "Karnal", "Ambala", 80);
        road(g, "Ambala", "Chandigarh", 45);
        road(g, "Ambala", "Ludhiana", 110);
        road(g, "Chandigarh", "Ludhiana", 100);
        road(g, "Ludhiana", "Amritsar", 140);
        road(g, "Delhi", "Chandigarh", 270);     // direct road, longer than the route via Panipat
        road(g, "Delhi", "Meerut", 70);
        road(g, "Meerut", "Dehradun", 190);
        road(g, "Chandigarh", "Dehradun", 170);
        road(g, "Delhi", "Rohtak", 70);
        road(g, "Rohtak", "Panipat", 80);
        road(g, "Delhi", "Agra", 230);
        road(g, "Agra", "Jaipur", 240);
        road(g, "Delhi", "Jaipur", 280);
        road(g, "Agra", "Lucknow", 330);
        road(g, "Meerut", "Lucknow", 460);
        return g;
    }

    /** Approximate map positions {longitude, latitude} used only by the GUI to draw the cities. */
    public static java.util.Map<String, double[]> coords() {
        java.util.Map<String, double[]> m = new java.util.HashMap<>();
        m.put("Delhi", new double[]{77.2, 28.6});
        m.put("Panipat", new double[]{76.97, 29.39});
        m.put("Karnal", new double[]{76.99, 29.69});
        m.put("Ambala", new double[]{76.78, 30.38});
        m.put("Chandigarh", new double[]{76.78, 30.73});
        m.put("Ludhiana", new double[]{75.85, 30.9});
        m.put("Amritsar", new double[]{74.87, 31.63});
        m.put("Meerut", new double[]{77.7, 28.98});
        m.put("Dehradun", new double[]{78.03, 30.32});
        m.put("Rohtak", new double[]{76.6, 28.9});
        m.put("Agra", new double[]{78.0, 27.18});
        m.put("Jaipur", new double[]{75.79, 26.91});
        m.put("Lucknow", new double[]{80.95, 26.85});
        return m;
    }

    private static void road(Graph g, String a, String b, int km) {
        g.addEdge(g.getId(a), g.getId(b), km);
    }
}
