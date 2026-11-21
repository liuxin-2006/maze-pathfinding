package com.maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

/**
 * 绘制迷宫：墙、空地、起点/终点、寻路结果。
 */
public class MazePanel extends JPanel {

    private int[][] maze;
    private List<int[]> path;
    private int startR = -1;
    private int startC = -1;
    private int endR = -1;
    private int endC = -1;

    private static final int CELL = 14;
    private static final int PAD = 8;

    public MazePanel() {
        setBackground(new Color(40, 44, 52));
    }

    public void setMazeData(int[][] maze, int sr, int sc, int er, int ec, List<int[]> path) {
        this.maze = maze;
        this.startR = sr;
        this.startC = sc;
        this.endR = er;
        this.endC = ec;
        this.path = path;
        if (maze != null && maze.length > 0) {
            int w = maze[0].length * CELL + PAD * 2;
            int h = maze.length * CELL + PAD * 2;
            setPreferredSize(new Dimension(w, h));
            revalidate();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (maze == null || maze.length == 0) {
            g2.setColor(new Color(180, 190, 200));
            g2.drawString("请点击「生成迷宫并寻路」", PAD, PAD + 20);
            g2.dispose();
            return;
        }

        int rows = maze.length;
        int cols = maze[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = PAD + c * CELL;
                int y = PAD + r * CELL;
                if (maze[r][c] == 1) {
                    g2.setColor(new Color(30, 32, 40));
                    g2.fillRect(x, y, CELL, CELL);
                    g2.setColor(new Color(55, 58, 70));
                    g2.drawRect(x, y, CELL - 1, CELL - 1);
                } else {
                    g2.setColor(new Color(245, 245, 248));
                    g2.fillRect(x, y, CELL, CELL);
                }
            }
        }

        if (path != null && path.size() >= 2) {
            g2.setStroke(new BasicStroke(Math.max(3f, CELL * 0.35f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(230, 126, 34));
            for (int i = 0; i < path.size() - 1; i++) {
                int[] a = path.get(i);
                int[] b = path.get(i + 1);
                int x1 = PAD + a[1] * CELL + CELL / 2;
                int y1 = PAD + a[0] * CELL + CELL / 2;
                int x2 = PAD + b[1] * CELL + CELL / 2;
                int y2 = PAD + b[0] * CELL + CELL / 2;
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        if (startR >= 0 && startC >= 0 && startR < rows && startC < cols) {
            drawMarker(g2, startR, startC, new Color(46, 204, 113));
        }
        if (endR >= 0 && endC >= 0 && endR < rows && endC < cols) {
            drawMarker(g2, endR, endC, new Color(231, 76, 60));
        }

        g2.dispose();
    }

    private void drawMarker(Graphics2D g2, int r, int c, Color fill) {
        int x = PAD + c * CELL + CELL / 2;
        int y = PAD + r * CELL + CELL / 2;
        int rad = Math.max(4, CELL / 2 - 2);
        g2.setColor(fill);
        g2.fillOval(x - rad, y - rad, rad * 2, rad * 2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x - rad, y - rad, rad * 2, rad * 2);
    }
}
