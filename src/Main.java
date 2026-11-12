import java.util.List;
import java.util.Scanner;

/** Console menu for the Shortest Route Planner (Dijkstra's Algorithm). */
public class Main {
    private static Graph graph = CityMap.sample();
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("  SHORTEST ROUTE PLANNER USING DIJKSTRA'S ALGORITHM");
        System.out.println("=====================================================");

        while (true) {
            System.out.println("\n1. Show all cities and roads");
            System.out.println("2. Find shortest route between two cities");
            System.out.println("3. Show distances from one city to all others");
            System.out.println("4. Add a city");
            System.out.println("5. Add a road");
            System.out.println("6. Run test cases");
            System.out.println("7. Run benchmark (heap vs array)");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": showMap(); break;
                case "2": findRoute(); break;
                case "3": distancesFrom(); break;
                case "4": addCity(); break;
                case "5": addRoad(); break;
                case "6": TestRunner.runAll(); break;
                case "7": Benchmark.runAll("benchmark_results.csv"); break;
                case "0": System.out.println("Goodbye."); return;
                default:  System.out.println("Invalid choice. Enter a number from the menu.");
            }
        }
    }

    private static void showMap() {
        System.out.println("\nCities: " + graph.size() + "   Roads: " + graph.edgeCount());
        for (int u = 0; u < graph.size(); u++) {
            StringBuilder sb = new StringBuilder(String.format("%-12s -> ", graph.nameOf(u)));
            for (int[] e : graph.neighbours(u)) {
                sb.append(graph.nameOf(e[0])).append("(").append(e[1]).append(" km)  ");
            }
            System.out.println(sb);
        }
    }

    private static int askCity(String prompt) {
        System.out.print(prompt);
        String name = in.nextLine();
        int id = graph.getId(name);
        if (id == -1) System.out.println("City not found: " + name.trim());
        return id;
    }

    private static void findRoute() {
        int from = askCity("From city: ");
        if (from == -1) return;
        int to = askCity("To city: ");
        if (to == -1) return;

        PathResult r = DijkstraHeap.run(graph, from);
        if (!r.isReachable(to)) {
            System.out.println("No route exists between these cities.");
            return;
        }
        printPath(r, to);
        // second opinion from the array version
        PathResult check = DijkstraArray.run(graph, from);
        System.out.println("Cross check with array version: "
                + (check.dist[to] == r.dist[to] ? "same distance" : "MISMATCH"));
    }

    private static void printPath(PathResult r, int to) {
        List<Integer> path = r.pathTo(to);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(graph.nameOf(path.get(i)));
        }
        System.out.println("Shortest route : " + sb);
        System.out.println("Total distance : " + r.dist[to] + " km");
        System.out.println("Cities visited : " + path.size());
    }

    private static void distancesFrom() {
        int from = askCity("From city: ");
        if (from == -1) return;
        PathResult r = DijkstraHeap.run(graph, from);
        System.out.println("\nDistances from " + graph.nameOf(from) + ":");
        for (int v = 0; v < graph.size(); v++) {
            String d = r.isReachable(v) ? r.dist[v] + " km" : "unreachable";
            System.out.printf("  %-12s %s%n", graph.nameOf(v), d);
        }
    }

    private static void addCity() {
        System.out.print("New city name: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        if (graph.getId(name) != -1) { System.out.println("City already exists."); return; }
        graph.addNode(name);
        System.out.println("Added " + name + ". Now add a road, or it will stay unreachable.");
    }

    private static void addRoad() {
        int a = askCity("First city: ");
        if (a == -1) return;
        int b = askCity("Second city: ");
        if (b == -1) return;
        if (a == b) { System.out.println("A road needs two different cities."); return; }
        System.out.print("Distance in km: ");
        try {
            int km = Integer.parseInt(in.nextLine().trim());
            graph.addEdge(a, b, km);
            System.out.println("Road added.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a whole number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
