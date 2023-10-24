import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class DiskScheduling {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setTitle("Disk Scheduling");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.setBackground(Color.WHITE);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(0, 10, 10, 10);

            JLabel title = new JLabel("Disk Scheduling");
            title.setFont(new Font("Century Gothic", Font.BOLD, 25));
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 3;
            panel.add(title, c);

            JLabel valueLabel = new JLabel("Value: (1 to 200, space-separated)");
            valueLabel.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            c.gridy = 1;
            c.gridwidth = 1;
            panel.add(valueLabel, c);

            JTextArea valueTextArea = new JTextArea();
            valueTextArea.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            valueTextArea.setRows(3);
            valueTextArea.setColumns(20);
            JScrollPane scrollPane = new JScrollPane(valueTextArea);
            c.gridy = 2;
            panel.add(scrollPane, c);

            JLabel algoLabel = new JLabel("Choose Algorithm:");
            algoLabel.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            c.gridy = 3;
            panel.add(algoLabel, c);

            String[] algorithms = {"FIFO", "SSTF", "SCAN", "CSCAN"};
            JComboBox<String> algoComboBox = new JComboBox<>(algorithms);
            algoComboBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            c.gridy = 4;
            panel.add(algoComboBox, c);

            JLabel directionLabel = new JLabel("Initial direction:");
            directionLabel.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            c.gridy = 5;
            panel.add(directionLabel, c);

            String[] directions = {"LEFT", "RIGHT"};
            JComboBox<String> dirComboBox = new JComboBox<>(directions);
            dirComboBox.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            c.gridy = 6;
            panel.add(dirComboBox, c);

            JLabel positionLabel = new JLabel("Head position:");
            positionLabel.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            c.gridy = 7;
            panel.add(positionLabel, c);

            JTextField positionTextField = new JTextField();
            positionTextField.setFont(new Font("Century Gothic", Font.PLAIN, 16));
            c.gridy = 8;
            panel.add(positionTextField, c);

            JButton visualizeButton = new JButton("Visualize");
            visualizeButton.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            c.gridy = 9;
            panel.add(visualizeButton, c);

            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            visualizeButton.addActionListener(e -> {
                String requestArr = valueTextArea.getText();
                String option1 = (String) algoComboBox.getSelectedItem();
                String option2 = (String) dirComboBox.getSelectedItem();
                int start = Integer.parseInt(positionTextField.getText());
                visualize(requestArr, option1, option2, start);
            });
        });
    }
    public static int TotalSeekTime(ArrayList<Integer> order) {
        int sum = 0;
        for (int i = 1; i < order.size(); i++) {
            sum+= Math.abs(order.get(i - 1) - order.get(i));
        }
        return sum;
    }
    public static ArrayList<Integer> FIFO(ArrayList<Integer> request, int start) {
        ArrayList<Integer> order = new ArrayList<>(request);
        order.add(0, start);
        return order;
    }
    public static ArrayList<Integer> SSTF(ArrayList<Integer> request, int start) {
        int position = start;
        ArrayList<Integer> tempRequest = new ArrayList<>(request);
        ArrayList<Integer> order = new ArrayList<>();
        order.add(position);
        while (!tempRequest.isEmpty()) {
            int diff = 200;
            int newPosition = position;
            for (int i = 0; i < tempRequest.size(); i++) {
                if (diff > Math.abs(tempRequest.get(i) - position)) {
                    diff = Math.abs(tempRequest.get(i) - position);
                    newPosition = tempRequest.get(i);
                }
            }
            position = newPosition;
            order.add(position);
            tempRequest.remove(Integer.valueOf(position));
        }
        return order;
    }
    public static ArrayList<Integer> SCAN(ArrayList<Integer> request, int start, String direction) {
        ArrayList<Integer> tempRequest = new ArrayList<>(request);
        ArrayList<Integer> order = new ArrayList<>();
        tempRequest.add(start);
        Collections.sort(tempRequest);

        int idx = tempRequest.indexOf(start);

        if (direction.equals("RIGHT")) {
            for (int i = idx; i < tempRequest.size(); i++) {
                order.add(tempRequest.get(i));
            }
            for (int i = idx - 1; i >= 0; i--) {
                order.add(tempRequest.get(i));
            }
        } else if (direction.equals("LEFT")) {
            for (int i = idx; i >= 0; i--) {
                order.add(tempRequest.get(i));
            }
            for (int i = idx + 1; i < tempRequest.size(); i++) {
                order.add(tempRequest.get(i));
            }
        }
        return order;
    }
    public static ArrayList<Integer> CSCAN(ArrayList<Integer> request, int start, String direction) {
        ArrayList<Integer> tempRequest = new ArrayList<>(request);
        ArrayList<Integer> order = new ArrayList<>();
        tempRequest.add(start);
        Collections.sort(tempRequest);

        int idx = tempRequest.indexOf(start);

        if (direction.equals("RIGHT")) {
            for (int i = idx; i < tempRequest.size(); i++) {
                order.add(tempRequest.get(i));
            }
            for (int i = 0; i < idx; i++) {
                order.add(tempRequest.get(i));
            }
        } else if (direction.equals("LEFT")) {
            for (int i = idx; i >= 0; i--) {
                order.add(tempRequest.get(i));
            }
            for (int i = tempRequest.size() - 1; i > idx; i--) {
                order.add(tempRequest.get(i));
            }
        }
        return order;
    }
    public static void visualize(String request, String option1, String option2, int start) {
        String[] requestArrStrings = request.split(" ");
        ArrayList<Integer> requestArr = new ArrayList<>();
        for (String s : requestArrStrings) {
            requestArr.add(Integer.parseInt(s));
        }
        final ArrayList<Integer> order;

        if (option1.equals("FIFO")) {
            order = FIFO(requestArr, start);
        } else if (option1.equals("SSTF")) {
            order = SSTF(requestArr, start);
        } else if (option1.equals("SCAN")) {
            order = SCAN(requestArr, start, option2);
        } else if (option1.equals("CSCAN")) {
            order = CSCAN(requestArr, start, option2);
        } else {
            return;
        }

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setTitle(option1);
            frame.getContentPane().add(new LineChart(order));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
class LineChart extends JPanel {
    private static final int WIDTH = 620;
    private static final int HEIGHT = 380;

    private ArrayList<Integer> order;

    public LineChart(ArrayList<Integer> order) {
        this.order = order;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int padding = 30;
        int graphWidth = WIDTH - 2 * padding;
        int graphHeight = HEIGHT - 2 * padding - 20;

        g.drawLine(padding, padding, padding + graphWidth, padding);
        g.drawLine(padding, padding, padding, padding + graphHeight);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        
        int gapX = graphWidth / 40;
        for (int i = 0; i <= 4; i++) {
            int x = padding + i * (graphWidth / 4);
            int labelLength = String.valueOf(i * 50).length();
            if (labelLength < 3) {
                g.drawString(String.valueOf(i * 50), x - (labelLength == 1? 2: 6), padding - 5);
            } else {
                g.drawString(String.valueOf(i * 50), x - (3 + 2 * labelLength), padding - 5);
            }
            if (i < 4) {
                for (int j = 1; j <= 10; j++) {
                    g.drawLine(x + gapX * j, padding, x + gapX * j, padding + 5 * (j % 5 == 0? 2: 1));
                }
            }
        }
        int gapY = graphHeight / (order.size() - 1);
        for (int i = 0; i <= order.size() - 1; i++) {
            int y = padding + i * (graphHeight / (order.size() - 1));
            int labelLength = String.valueOf(i).length();
            g.drawString(String.valueOf(i), padding - (3 + 7 * labelLength), y + 5);
            g.drawLine(padding, y, padding + 10, y);
        }

        g.drawString("Total seek time: " + DiskScheduling.TotalSeekTime(order), padding, padding + graphHeight + 25);

        g.setColor(Color.RED);

        for (int i = 0; i < order.size() - 1; i++) {
            int x1 = padding + order.get(i) * gapX / 5;
            int y1 = padding + i * gapY;
            int x2 = padding + order.get(i + 1) * gapX / 5;
            int y2 = padding + (i + 1) * gapY;

            g.drawLine(x1, y1, x2, y2);
        }

        Graphics2D g2d = (Graphics2D) g;
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
        g2d.setStroke(dashed);
        for (int i = 0; i < order.size() - 1; i++) {
            int x1 = padding + order.get(i + 1) * gapX / 5;
            int y1 = padding;
            int x2 = x1;
            int y2 = padding + (i + 1) * gapY;

            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}
