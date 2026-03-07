package A2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Minimal JSON exporter that converts the Java board state into the
 * JSON schema expected by visualize/light_visualizer.py.
 *
 * R2.2/R2.3: Maintain external JSON state and integrate with the Python visualizer.
 */
public final class VisualExporter {
    private VisualExporter() {}

    // Spiral order cube coordinates for a radius-2 hex grid (center, ring1, ring2)
    // Matches Board tile ids: 0=center, 1-6=inner ring, 7-18=outer ring
    // Each triplet is (q, s, r) with q + s + r == 0
    private static final int[][] TILE_CUBE_COORDS = new int[][]{
            { 0,  0,  0}, // 0 center
            { 1, -1,  0}, // 1 ring1 (east)
            { 1,  0, -1}, // 2
            { 0,  1, -1}, // 3
            {-1,  1,  0}, // 4
            {-1,  0,  1}, // 5
            { 0, -1,  1}, // 6
            // ring2 clockwise starting east
            { 2, -2,  0}, // 7
            { 2, -1, -1}, // 8
            { 1,  1, -2}, // 9
            { 0,  2, -2}, // 10
            {-1,  2, -1}, // 11
            {-2,  2,  0}, // 12
            {-2,  1,  1}, // 13
            {-2,  0,  2}, // 14
            {-1, -1,  2}, // 15
            { 0, -2,  2}, // 16
            { 1, -2,  1}, // 17
            { 2,  0, -2}  // 18
    };

    private static String toVisualizerResource(ResourceType r) {
        if (r == null) return "DESERT";
        return switch (r) {
            case LUMBER -> "WOOD";
            case GRAIN -> "WHEAT";
            case WOOL -> "SHEEP";
            case BRICK -> "BRICK";
            case ORE -> "ORE";
        };
    }

    private static String toVisualizerBuilding(BuildingType b) {
        if (b == null) return null;
        return switch (b) {
            case SETTLEMENT -> "SETTLEMENT";
            case CITY -> "CITY";
            case ROAD -> "ROAD"; // Only used on edges
        };
    }

    private static String toVisualizerColor(Player p, Player[] players) {
        // Deterministic mapping by seating order
        int idx = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i] == p) { idx = i; break; }
        }
        return switch (idx) {
            case 0 -> "RED";
            case 1 -> "BLUE";
            case 2 -> "ORANGE";
            case 3 -> "WHITE";
            default -> "RED"; // fallback
        };
    }

    public static void export(Board board, Player[] players, boolean renderPng) {
        // Resolve visualize directory under project root
        String root = System.getProperty("user.dir");
        File visualizeDir = new File(root, "visualize");
        File outDir = visualizeDir;
        File scraped = new File(visualizeDir, "scraped_boards");
        if (!scraped.exists()) scraped.mkdirs();

        File baseMap = new File(outDir, "base_map.json");
        File state = new File(outDir, "state.json");

        try {
            writeBaseMap(board, baseMap);
            writeState(board, players, state);
        } catch (IOException e) {
            System.err.println("[Visualizer] Failed to write JSON: " + e.getMessage());
            return;
        }

        if (renderPng) {
            runPythonRender(visualizeDir, baseMap.getName(), state.getName());
        }
    }

    private static void writeBaseMap(Board board, File file) throws IOException {
        List<HexTile> tiles = board.getTiles();
        StringBuilder sb = new StringBuilder(1024);
        sb.append("{\n  \"tiles\": [\n");
        for (int i = 0; i < Math.min(tiles.size(), 19); i++) {
            HexTile t = tiles.get(i);
            int[] c = TILE_CUBE_COORDS[i];
            String res = toVisualizerResource(t.getResource());
            Integer num = t.getTokenNumber();
            if (i > 0) sb.append(",\n");
            sb.append("    {")
              .append("\"q\": ").append(c[0]).append(", ")
              .append("\"s\": ").append(c[1]).append(", ")
              .append("\"r\": ").append(c[2]).append(", ")
              .append("\"resource\": \"").append(res).append("\"");
            if (!Objects.equals(res, "DESERT") && num != null) {
                sb.append(", \"number\": ").append(num);
            }
            sb.append("}");
        }
        sb.append("\n  ]\n}");

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            w.write(sb.toString());
        }
    }

    private static void writeState(Board board, Player[] players, File file) throws IOException {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("{\n  \"buildings\": [\n");

        boolean first = true;
        // Nodes -> settlements/cities
        for (Node n : board.getNodes()) {
            if (n.getOwner() != null && n.getBuilding() != null) {
                if (!first) sb.append(",\n");
                first = false;
                sb.append("    {")
                  .append("\"node\": ").append(n.getId()).append(", ")
                  .append("\"owner\": \"").append(toVisualizerColor(n.getOwner(), players)).append("\",")
                  .append(" \"type\": \"").append(toVisualizerBuilding(n.getBuilding())).append("\"")
                  .append("}");
            }
        }

        sb.append("\n  ],\n  \"roads\": [\n");

        first = true;
        for (Edge e : board.getEdges()) {
            if (e.getOwner() != null && e.getBuilding() == BuildingType.ROAD && e.getConnectedNodes().size() == 2) {
                if (!first) sb.append(",\n");
                first = false;
                int a = e.getConnectedNodes().get(0).getId();
                int b = e.getConnectedNodes().get(1).getId();
                sb.append("    {")
                  .append("\"a\": ").append(a).append(", ")
                  .append("\"b\": ").append(b).append(", ")
                  .append("\"owner\": \"").append(toVisualizerColor(e.getOwner(), players)).append("\"")
                  .append("}");
            }
        }

        sb.append("\n  ]\n}");

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            w.write(sb.toString());
        }
    }

    private static void runPythonRender(File visualizeDir, String baseMapName, String stateName) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    "light_visualizer.py",
                    baseMapName,
                    stateName
            );
            pb.directory(visualizeDir);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            // Best-effort: do not block indefinitely; wait a short time
            p.waitFor();
        } catch (Exception ex) {
            System.err.println("[Visualizer] Python render skipped: " + ex.getMessage());
        }
    }
}
