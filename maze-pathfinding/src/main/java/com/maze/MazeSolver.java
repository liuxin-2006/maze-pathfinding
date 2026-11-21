package com.maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * 在 0/1 迷宫上用 BFS 找最短路（完美迷宫中即为唯一通路）。
 */
public final class MazeSolver {

    private MazeSolver() {
    }

    public static List<int[]> solve(int[][] maze, int sr, int sc, int er, int ec) {
        int h = maze.length;
        int w = maze[0].length;
        if (sr < 0 || sr >= h || sc < 0 || sc >= w || er < 0 || er >= h || ec < 0 || ec >= w) {
            return List.of();
        }
        if (maze[sr][sc] != 0 || maze[er][ec] != 0) {
            return List.of();
        }

        boolean[][] vis = new boolean[h][w];
        int[][] pr = new int[h][w];
        int[][] pc = new int[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                pr[r][c] = -1;
                pc[r][c] = -1;
            }
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        Deque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sr, sc});
        vis[sr][sc] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0];
            int c = cur[1];
            if (r == er && c == ec) {
                return buildPath(pr, pc, sr, sc, er, ec);
            }
            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k];
                int nc = c + dc[k];
                if (nr < 0 || nr >= h || nc < 0 || nc >= w || vis[nr][nc] || maze[nr][nc] != 0) {
                    continue;
                }
                vis[nr][nc] = true;
                pr[nr][nc] = r;
                pc[nr][nc] = c;
                q.add(new int[]{nr, nc});
            }
        }
        return List.of();
    }

    private static List<int[]> buildPath(int[][] pr, int[][] pc, int sr, int sc, int er, int ec) {
        List<int[]> rev = new ArrayList<>();
        int r = er;
        int c = ec;
        while (true) {
            rev.add(new int[]{r, c});
            if (r == sr && c == sc) {
                break;
            }
            int pR = pr[r][c];
            int pC = pc[r][c];
            if (pR < 0) {
                return List.of();
            }
            r = pR;
            c = pC;
        }
        Collections.reverse(rev);
        return rev;
    }
}
