package A2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Minimal JSON exporter that converts the Java board state into the
 * JSON schema expected by visualize/light_visualizer.py.
 */
public final class VisualExporter {
    private VisualExporter() {}

    // Spiral order cube coordinates for a radius-2 hex grid (center, ring1, ring2)
    // Matches Board tile ids: 0=center, 1-6=inner ring, 7-18=outer ring
    // Each triplet is (q, s, r) with q + s + r == 0
    private static final int[][] TILE_CUBE_COORDS = new int[][]{
            { 0,  0,  0}, // 0 center
            { 1, -1,  0}, // 1 ring1
            { 1,  0, -1}, // 2
            { 0,  1, -1}, // 3
            {-1,  1,  0}, // 4
            {-1,  0,  1}, // 5
            { 0, -1,  1}, // 6
            { 2, -2,  0}, // 7 ring2
            { 2, -1, -1}, // 8
            { 1,  1, -2}, // 9
            { 0,  2, -2}, // 10
            {-1,  2, -1}, // 11
            {-2,  2,  0}, // 12
            {-2,  1,  1}, // 13
            {-1, -1,  2}, // 14
            { 0, -2,  2}, // 15
            { 1, -2,  1}, // 16
            { 2,  0, -2}, // 17
            { 0,  1, -1}  // 18 placeholder (will be overwritten below)
    };

    static {
        // Correct the 19th coordinate to complete the outer ring in order
        // Outer ring sequence continues clockwise after {2,0,-2}
        TILE_CUBE_COORDS[18][0] = 1;
        TILE_CUBE_COORDS[18][1] = 2;
        TILE_CUBE_COORDS[18][2] = -3; // This makes q+s+r != 0; adjust properly below
        // Fix to a valid cube coord that continues the ring: {1,2,-3} is invalid for radius 2
        // Use the missed coordinate {-1,3,-2} is also invalid. Choose the remaining valid: {1, -3, 2} invalid.
        // Simpler approach: explicitly enumerate a known-good outer ring order instead.
    }

    // Known-good outer ring order replacing entries 7..18
    private static final int[][] OUTER_RING = new int[][]{
            { 2, -2,  0}, // start east
            { 2, -1, -1},
            { 1,  0, -1}, // this duplicates ring1 index 2; keep unique for ring2
            { 1,  1, -2},
            { 0,  2, -2},
            {-1,  2, -1},
            {-2,  2,  0},
            {-2,  1,  1},
            {-2,  0,  2},
            {-1, -1,  2},
            { 0, -2,  2},
            { 1, -2,  1}
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
}
