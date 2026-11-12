import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/** Swing GUI: shows the map, lets the user pick two cities, and highlights the shortest route. */
public class RouteGui extends JFrame {
    private final Graph graph = CityMap.sample();
    private final Map<String, double[]> coords = CityMap.coords();

    private final JComboBox<String> fromBox = new JComboBox<>();
    private final JComboBox<String> toBox = new JComboBox<>();
    private final JLabel resultLabel = new JLabel("Choose two cities and click 'Find Route'.");
    private final JTextArea distArea = new JTextArea(18, 22);
    private final MapPanel mapPanel = new MapPanel();

    private List<Integer> route = null;
    private int fromId = -1, toId = -1;

    public RouteGui() {
        super("Shortest Route Planner - Dijkstra's Algorithm");
        for (int i = 0; i < graph.size(); i++) {
            fromBox.addItem(graph.nameOf(i));
            toBox.addItem(graph.nameOf(i));
        }
        fromBox.setSelectedItem("Delhi");
        toBox.setSelectedItem("Amritsar");

        JButton findBtn = new JButton("Find Route");
        findBtn.addActionListener(e -> findRoute());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.add(new JLabel("From:")); top.add(fromBox);
        top.add(new JLabel("To:"));   top.add(toBox);
        top.add(findBtn);

        distArea.setEditable(false);
        distArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(distArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Distances from source"));

        resultLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 14f));

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.EAST);
        add(resultLabel, BorderLayout.SOUTH);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        findRoute();   // show a first route when the window opens
    }

    /** Runs Dijkstra (heap version) and updates the map and text. */
    void findRoute() {
        fromId = graph.getId((String) fromBox.getSelectedItem());
        toId = graph.getId((String) toBox.getSelectedItem());
        PathResult r = DijkstraHeap.run(graph, fromId);

        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < graph.size(); v++) {
            sb.append(String.format("%-12s %s%n", graph.nameOf(v),
                    r.isReachable(v) ? r.dist[v] + " km" : "unreachable"));
        }
        distArea.setText(sb.toString());

        if (!r.isReachable(toId)) {
            route = null;
            resultLabel.setText("No route exists between these cities.");
        } else {
            route = r.pathTo(toId);
            StringBuilder p = new StringBuilder();
            for (int i = 0; i < route.size(); i++) {
                if (i > 0) p.append(" -> ");
                p.append(graph.nameOf(route.get(i)));
            }
            resultLabel.setText(p + "   |   " + r.dist[toId] + " km");
        }
        mapPanel.repaint();
    }

    /** Draws roads, cities and the highlighted route. */
    private class MapPanel extends JPanel {
        private Point pos(int id, int w, int h) {
            double[] c = coords.get(graph.nameOf(id));
            int margin = 70;
            double x = (c[0] - 74.4) / (81.4 - 74.4);
            double y = (32.1 - c[1]) / (32.1 - 26.5);
            return new Point(margin + (int) (x * (w - 2 * margin)), margin / 2 + (int) (y * (h - margin - 20)));
        }

        private boolean onRoute(int a, int b) {
            if (route == null) return false;
            for (int i = 0; i + 1 < route.size(); i++) {
                int x = route.get(i), y = route.get(i + 1);
                if ((x == a && y == b) || (x == b && y == a)) return true;
            }
            return false;
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));

            // roads
            for (int u = 0; u < graph.size(); u++) {
                for (int[] e : graph.neighbours(u)) {
                    int v = e[0];
                    if (v < u) continue;   // draw each road once
                    Point a = pos(u, w, h), b = pos(v, w, h);
                    boolean hot = onRoute(u, v);
                    g.setColor(hot ? new Color(214, 39, 40) : new Color(180, 180, 180));
                    g.setStroke(new BasicStroke(hot ? 4f : 1.5f));
                    g.drawLine(a.x, a.y, b.x, b.y);
                    g.setColor(hot ? new Color(160, 20, 20) : Color.GRAY);
                    g.drawString(String.valueOf(e[1]), (a.x + b.x) / 2 + 3, (a.y + b.y) / 2 - 3);
                }
            }
            // cities
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            for (int i = 0; i < graph.size(); i++) {
                Point p = pos(i, w, h);
                boolean inRoute = route != null && route.contains(i);
                Color fill = i == fromId ? new Color(44, 160, 44)
                           : i == toId ? new Color(31, 119, 180)
                           : inRoute ? new Color(255, 160, 60) : new Color(90, 90, 90);
                g.setColor(fill);
                g.fillOval(p.x - 8, p.y - 8, 16, 16);
                g.setColor(Color.BLACK);
                g.drawString(graph.nameOf(i), p.x + 11, p.y - 9);
            }
            // legend
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(44, 160, 44));  g.fillOval(12, h - 62, 10, 10);
            g.setColor(Color.BLACK);             g.drawString("Start", 28, h - 53);
            g.setColor(new Color(31, 119, 180)); g.fillOval(12, h - 44, 10, 10);
            g.setColor(Color.BLACK);             g.drawString("Destination", 28, h - 35);
            g.setColor(new Color(214, 39, 40));  g.fillRect(12, h - 26, 10, 4);
            g.setColor(Color.BLACK);             g.drawString("Shortest route  (numbers on roads = km)", 28, h - 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RouteGui().setVisible(true));
    }
}
