import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

public class DiskInfoUI extends JPanel {
    private final Map<String, Speedometer> speedometerMap;

    public DiskInfoUI() {
        setLayout(new GridLayout(2, 2)); 

        // Disk harflerini sıralı şekilde tanımla
        speedometerMap = new HashMap<>();
        speedometerMap.put("C", new Speedometer(0, 100, "C:"));
        speedometerMap.put("D", new Speedometer(0, 100, "D:"));
        speedometerMap.put("E", new Speedometer(0, 100, "E:"));
        speedometerMap.put("F", new Speedometer(0, 100, "F:"));

        for (Speedometer speedometer : speedometerMap.values()) {
            add(speedometer);
        }

        JButton button = new JButton("Run PowerShell Script");
        button.addActionListener(e -> runPowerShellScript());

        add(button, BorderLayout.SOUTH);
    }

    private void runPowerShellScript() {
        String scriptPath = Paths.get("src", "GetDiskInfo.ps1").toAbsolutePath().toString();
        String userHome = System.getProperty("user.home");
        Path desktopPath = Paths.get(userHome, "Desktop", "output.csv").toAbsolutePath();

        FileTime lastModifiedTime = Files.exists(desktopPath) ? getFileTime(desktopPath) : null;

        String[] cmd = {
            "cmd.exe", "/c", "powershell.exe", "-Command",
            "Start-Process powershell.exe -ArgumentList '-NoProfile -ExecutionPolicy Bypass -File \"" + scriptPath + "\"' -Verb RunAs -WindowStyle Hidden"
        };

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
            while (true) {
                if (Files.exists(desktopPath)) {
                    FileTime newModifiedTime = getFileTime(desktopPath);
                    if (lastModifiedTime == null || !newModifiedTime.equals(lastModifiedTime)) {
                        break;
                    }
                }
                Thread.sleep(1000);
            }

            System.out.println("File found and updated: " + desktopPath + "\n");

            try (BufferedReader fileReader = new BufferedReader(new FileReader(desktopPath.toFile()))) {
                String line;
                boolean isFirstLine = true;
                while ((line = fileReader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    line = line.replaceAll("\"", "");
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            String diskName = parts[0].trim();
                            int temperature = Integer.parseInt(parts[1].trim());
                            Speedometer speedometer = speedometerMap.get(diskName);
                            if (speedometer != null) {
                                speedometer.setValue(temperature);
                            } else {
                                System.out.println("Unknown disk name: " + diskName);
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Error parsing line: " + line);
                            ex.printStackTrace();
                        }
                    } else {
                        System.out.println("Invalid CSV line format: " + line);
                    }
                }
            }

        } catch (IOException | InterruptedException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private FileTime getFileTime(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class Speedometer extends JPanel {
        private int value;
        private final int minValue;
        private final int maxValue;
        private final String diskLabel;
        private Color needleColor;

        public Speedometer(int minValue, int maxValue, String diskLabel) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.value = minValue;
            this.diskLabel = diskLabel;
            setPreferredSize(new Dimension(200, 200));
        }

        public void setValue(int value) {
            this.value = Math.max(minValue, Math.min(maxValue, value));
            updateNeedleColor();
            repaint();
        }

        private void updateNeedleColor() {
            if (value <= 30) {
                needleColor = Color.GREEN;
            } else if (value <= 55) {
                needleColor = Color.ORANGE;
            } else {
                needleColor = Color.RED;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int radius = Math.min(width, height) / 2 - 20;
            int centerX = width / 2;
            int centerY = height / 2;
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(10));
            g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, 180);

            double angle = 180 * ((double)(value - minValue) / (maxValue - minValue));
            double radians = Math.toRadians(180 - angle);
            int pointerLength = radius - 10;
            int x = centerX + (int) (pointerLength * Math.cos(radians));
            int y = centerY - (int) (pointerLength * Math.sin(radians));

            g2d.setColor(needleColor);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(centerX, centerY, x, y);
            g2d.setColor(Color.WHITE);
            g2d.drawString(diskLabel, centerX - g2d.getFontMetrics().stringWidth(diskLabel) / 2, centerY - radius - 10);

            g2d.drawString("Value: " + value, centerX - 30, centerY + 10);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Disk Info");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new DiskInfoUI());
            frame.setVisible(true);
        });
    }
}
