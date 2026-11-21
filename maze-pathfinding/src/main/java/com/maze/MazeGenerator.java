package com.maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 递归回溯在网格上生成「完美迷宫」：通路无环，任意两格之间恰有一条通路。
 * 0 = 通路，1 = 墙（与题目一致）。
 */
public final class MazeGenerator {

    private MazeGenerator() {
    }

    /**
     * @param rows    迷宫总行数（含外圈墙），建议为奇数且 &gt;= 5
     * @param cols    总列数
     * @param rng     随机源（可固定种子便于复现）
     * @param startR  用户期望的起点行（会钳制到内区并尽量对齐到「房间」奇数格再开始挖洞）
     * @param startC  用户期望的起点列
     * @return 二维数组，1 墙 0 路
     */
    public static int[][] generate(int rows, int cols, Random rng, int startR, int startC) {
        int[][] g = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g[r][c] = 1;
            }
        }
        int[] root = snappedRoomCell(rows, cols, startR, startC);
        dfsCarve(g, root[0], root[1], rng);
        sealBorder(g);
        return g;
    }

    private static void sealBorder(int[][] g) {
        int h = g.length;
        int w = g[0].length;
        for (int c = 0; c < w; c++) {
            g[0][c] = 1;
            g[h - 1][c] = 1;
        }
        for (int r = 0; r < h; r++) {
            g[r][0] = 1;
            g[r][w - 1] = 1;
        }
    }

    private static void dfsCarve(int[][] g, int r, int c, Random rng) {
        g[r][c] = 0;
        List<int[]> dirs = new ArrayList<>();
        dirs.add(new int[]{0, 2});
        dirs.add(new int[]{0, -2});
        dirs.add(new int[]{2, 0});
        dirs.add(new int[]{-2, 0});
        Collections.shuffle(dirs, rng);

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr > 0 && nr < g.length - 1 && nc > 0 && nc < g[0].length - 1 && g[nr][nc] == 1) {
                g[r + d[0] / 2][c + d[1] / 2] = 0;
                dfsCarve(g, nr, nc, rng);
            }
        }
    }

    /** 将尺寸规范为奇数且不小于 5，便于从 (1,1) 开始挖洞。 */
    public static int normalizeSize(int n) {
        if (n < 5) {
            return 5;
        }
        return (n % 2 == 0) ? n + 1 : n;
    }

    /** 与挖洞算法一致：把任意坐标钳到内区奇数「房间」格。 */
    public static int[] snappedRoomCell(int rows, int cols, int r, int c) {
        return new int[]{clampOddInner(r, rows), clampOddInner(c, cols)};
    }

    /** 内区 [1, size-2] 内的奇数坐标，作为递归回溯的「房间」格。 */
    private static int clampOddInner(int v, int size) {
        int max = size - 2;
        if (max < 1) {
            return 1;
        }
        int x = Math.max(1, Math.min(max, v));
        if (x % 2 == 0) {
            x = (x + 1 <= max) ? x + 1 : x - 1;
        }
        if (x < 1) {
            x = 1;
        }
        return x;
    }
}
