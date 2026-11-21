package com.maze;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 迷宫：输入行列、起点终点 → 生成单通路完美迷宫 → BFS 寻路并可视化。
 * 在 IDEA 中打开本目录（含 pom.xml），运行此类即可。
 */
public final class MazeApp extends JFrame {

    private final JTextField tfRows = new JTextField("21", 4);
    private final JTextField tfCols = new JTextField("21", 4);
    private final JTextField tfSr = new JTextField("1", 4);
    private final JTextField tfSc = new JTextField("1", 4);
    private final JTextField tfEr = new JTextField("19", 4);
    private final JTextField tfEc = new JTextField("19", 4);
    private final JTextField tfSeed = new JTextField("", 8);
    private final MazePanel mazePanel = new MazePanel();
    private final JLabel status = new JLabel(" ");

    private MazeApp() {
        super("迷宫 — 单通路生成与寻路");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        addLabeled(top, "行数", tfRows, labelFont);
        addLabeled(top, "列数", tfCols, labelFont);
        addLabeled(top, "起点行", tfSr, labelFont);
        addLabeled(top, "起点列", tfSc, labelFont);
        addLabeled(top, "终点行", tfEr, labelFont);
        addLabeled(top, "终点列", tfEc, labelFont);
        JLabel seedLab = new JLabel("随机种子(可空):");
        seedLab.setFont(labelFont);
        top.add(seedLab);
        tfSeed.setToolTipText("留空则每次不同；填数字可复现同一迷宫");
        top.add(tfSeed);

        JButton go = new JButton("生成迷宫并寻路");
        go.addActionListener(e -> runGenerateAndSolve());
        top.add(go);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel south = new JPanel(new BorderLayout());
        south.add(status, BorderLayout.WEST);
        south.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(mazePanel);
        scroll.getViewport().setBackground(mazePanel.getBackground());

        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private static void addLabeled(JPanel p, String text, JTextField field, Font f) {
        JLabel l = new JLabel(text + ":");
        l.setFont(f);
        p.add(l);
        field.setFont(f);
        p.add(field);
    }

    private void runGenerateAndSolve() {
        int rows;
        int cols;
        int sr;
        int sc;
        int er;
        int ec;
        try {
            rows = Integer.parseInt(tfRows.getText().trim());
            cols = Integer.parseInt(tfCols.getText().trim());
            sr = Integer.parseInt(tfSr.getText().trim());
            sc = Integer.parseInt(tfSc.getText().trim());
            er = Integer.parseInt(tfEr.getText().trim());
            ec = Integer.parseInt(tfEc.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入合法整数。", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        rows = MazeGenerator.normalizeSize(rows);
        cols = MazeGenerator.normalizeSize(cols);
        tfRows.setText(String.valueOf(rows));
        tfCols.setText(String.valueOf(cols));

        Random rng;
        String seedText = tfSeed.getText().trim();
        if (seedText.isEmpty()) {
            rng = new Random();
        } else {
            try {
                rng = new Random(Long.parseLong(seedText));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "随机种子必须是整数。", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int[][] maze = MazeGenerator.generate(rows, cols, rng, sr, sc);
        int[] snap = MazeGenerator.snappedRoomCell(rows, cols, sr, sc);
        int pathSr = sr;
        int pathSc = sc;
        if (maze[sr][sc] != 0) {
            pathSr = snap[0];
            pathSc = snap[1];
        }

        if (maze[er][ec] != 0) {
            JOptionPane.showMessageDialog(this,
                    "终点落在墙上（值为 1）。\n请把终点选在通路上；外圈为墙。\n"
                            + "例如可尝试 (" + (rows - 2) + "," + (cols - 2) + ") 附近，或更换随机种子。",
                    "终点无效", JOptionPane.WARNING_MESSAGE);
            mazePanel.setMazeData(maze, pathSr, pathSc, -1, -1, null);
            status.setText("已生成迷宫，终点在墙上，未寻路。");
            pack();
            return;
        }

        if (maze[pathSr][pathSc] != 0) {
            JOptionPane.showMessageDialog(this, "无法确定有效起点。", "错误", JOptionPane.ERROR_MESSAGE);
            mazePanel.setMazeData(maze, -1, -1, -1, -1, null);
            return;
        }

        List<int[]> path = MazeSolver.solve(maze, pathSr, pathSc, er, ec);
        mazePanel.setMazeData(maze, pathSr, pathSc, er, ec, path);
        if (path.isEmpty()) {
            status.setText("无通路。");
            JOptionPane.showMessageDialog(this, "未找到从起点到终点的通路。", "寻路", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String extra = (pathSr != sr || pathSc != sc)
                    ? String.format(" | 输入起点在墙上，已用对齐格 (%d,%d) 寻路", pathSr, pathSc)
                    : "";
            status.setText(String.format("迷宫 %d×%d | 路径长度: %d 格（含起终点）%s", rows, cols, path.size(), extra));
        }
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeApp f = new MazeApp();
            f.setVisible(true);
        });
    }
}
