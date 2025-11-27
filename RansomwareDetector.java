import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RansomwareDetector {
    private static final String[] RANSOMWARE_EXTENSIONS = {
        ".crypto", ".wannacry", ".locky", ".cryptolocker", ".petya", ".badrabbit",
        ".notpetya", ".nopetya", ".ryuk", ".djvu", ".phobos", ".dharma", ".cont",
        ".nephilim", ".avaddon", ".makop", ".ransomexx", ".egregor", ".hellokitty"
    };

    private JFrame frame;
    private JTextField directoryField;
    private JButton scanButton;
    private JButton stopButton;
    private JLabel resultLabel;
    private JPanel recoveryIsolationPanel;
    private JButton recoveryButton;
    private JButton isolationButton;
    private List<String> suspiciousFiles = new ArrayList<>();
    private AtomicBoolean stopScanFlag = new AtomicBoolean(false);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RansomwareDetector().createGUI());
    }

    private void createGUI() {
        frame = new JFrame("Ransomware Detection and Isolation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Ransomware Detection and Isolation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Directory selection panel
        JPanel directoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        directoryPanel.setBackground(Color.WHITE);
        
        JLabel directoryLabel = new JLabel("Select Directory:");
        directoryLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        directoryPanel.add(directoryLabel);

        directoryField = new JTextField(35);
        directoryPanel.add(directoryField);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseDirectory());
        directoryPanel.add(browseButton);

        centerPanel.add(directoryPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        scanButton = new JButton("Start Scan");
        scanButton.setBackground(Color.GREEN);
        scanButton.setForeground(Color.WHITE);
        scanButton.addActionListener(e -> startScan());
        buttonPanel.add(scanButton);

        stopButton = new JButton("Stop Scan");
        stopButton.setBackground(Color.RED);
        stopButton.setForeground(Color.WHITE);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopScan());
        buttonPanel.add(stopButton);

        centerPanel.add(buttonPanel);

        // Result label
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        resultLabel.setForeground(Color.BLACK);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(resultLabel);

        // Recovery and Isolation panel
        recoveryIsolationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        recoveryIsolationPanel.setBackground(Color.WHITE);

        recoveryButton = new JButton("Recover Files");
        recoveryButton.setBackground(Color.YELLOW);
        recoveryButton.setForeground(Color.BLACK);
        recoveryButton.addActionListener(e -> startRecovery());
        recoveryIsolationPanel.add(recoveryButton);

        isolationButton = new JButton("Isolate Files");
        isolationButton.setBackground(Color.YELLOW);
        isolationButton.setForeground(Color.BLACK);
        isolationButton.addActionListener(e -> startIsolation());
        recoveryIsolationPanel.add(isolationButton);

        recoveryIsolationPanel.setVisible(false);
        centerPanel.add(recoveryIsolationPanel);

        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void browseDirectory() {
        FileDialog dialog = new FileDialog(frame, "Select Directory to Scan", FileDialog.LOAD);
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        dialog.setDirectory(System.getProperty("user.home"));
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        
        String dirPath = dialog.getDirectory();
        String fileName = dialog.getFile();
        
        if (dirPath != null && fileName != null) {
            File selected = new File(dirPath, fileName);
            if (selected.isDirectory()) {
                directoryField.setText(selected.getAbsolutePath());
            } else {
                directoryField.setText(selected.getParent());
            }
        }
    }

    private void startScan() {
        stopScanFlag.set(false);
        String directory = directoryField.getText();

        if (directory.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a directory to scan.",
                "Directory Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scanButton.setEnabled(false);
        stopButton.setEnabled(true);
        resultLabel.setText("Scanning in progress...");
        resultLabel.setForeground(Color.BLUE);

        new Thread(() -> {
            suspiciousFiles = detectFiles(new File(directory));
            SwingUtilities.invokeLater(this::displayResults);
        }).start();
    }

    private void stopScan() {
        stopScanFlag.set(true);
        scanButton.setEnabled(true);
        stopButton.setEnabled(false);
        resultLabel.setText("Scan stopped.");
        resultLabel.setForeground(Color.RED);
    }

    private List<String> detectFiles(File directory) {
        List<String> ransomwareFiles = new ArrayList<>();
        
        try {
            Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    if (stopScanFlag.get()) {
                        return;
                    }
                    
                    String fileName = path.getFileName().toString().toLowerCase();
                    for (String ext : RANSOMWARE_EXTENSIONS) {
                        if (fileName.endsWith(ext.toLowerCase())) {
                            ransomwareFiles.add(path.toAbsolutePath().toString());
                            break;
                        }
                    }
                });
        } catch (IOException e) {
            System.err.println("Error scanning directory: " + e.getMessage());
        }
        
        return ransomwareFiles;
    }

    private void displayResults() {
        if (!suspiciousFiles.isEmpty()) {
            StringBuilder resultText = new StringBuilder("Ransomware-affected files:\n");
            for (String file : suspiciousFiles) {
                resultText.append(file).append("\n");
            }
            resultLabel.setText("<html><body style='width: 600px'>" + 
                resultText.toString().replace("\n", "<br>") + "</body></html>");
            resultLabel.setForeground(Color.RED);
            recoveryIsolationPanel.setVisible(true);
        } else {
            resultLabel.setText("No ransomware-affected files detected.");
            resultLabel.setForeground(Color.GREEN);
            recoveryIsolationPanel.setVisible(false);
        }

        scanButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private boolean recoverFile(String filePath) {
        try {
            String suffix = ".crypto";
            String originalPath;
            
            if (filePath.toLowerCase().endsWith(suffix)) {
                originalPath = filePath.substring(0, filePath.length() - suffix.length());
            } else {
                originalPath = filePath + ".recovered";
            }

            File sourceFile = new File(filePath);
            if (!sourceFile.exists() || !sourceFile.isFile()) {
                return false;
            }

            Files.copy(sourceFile.toPath(), Paths.get(originalPath), 
                      StandardCopyOption.REPLACE_EXISTING);
            Files.delete(sourceFile.toPath());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to recover file '" + filePath + "': " + e.getMessage());
            return false;
        }
    }

    private void startRecovery() {
        if (suspiciousFiles.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No files to recover.",
                "No Suspicious Files", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new Thread(() -> {
            int successful = 0;
            int failed = 0;
            
            for (String file : suspiciousFiles) {
                if (recoverFile(file)) {
                    successful++;
                } else {
                    failed++;
                }
            }
            
            int finalSuccessful = successful;
            int finalFailed = failed;
            SwingUtilities.invokeLater(() -> {
                resultLabel.setText("Recovery completed: " + finalSuccessful + 
                                  " recovered, " + finalFailed + " failed.");
                resultLabel.setForeground(Color.GREEN);
            });
        }).start();
    }

    private void startIsolation() {
        if (suspiciousFiles.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No files to isolate.",
                "No Suspicious Files", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new Thread(() -> {
            FileDialog dialog = new FileDialog(frame, "Select Isolation Directory", FileDialog.LOAD);
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            dialog.setDirectory(System.getProperty("user.home"));
            dialog.setVisible(true);
            System.setProperty("apple.awt.fileDialogForDirectories", "false");
            
            String dirPath = dialog.getDirectory();
            String fileName = dialog.getFile();
            
            if (dirPath != null && fileName != null) {
                File selected = new File(dirPath, fileName);
                File isolationDir = selected.isDirectory() ? selected : selected.getParentFile();
                
                for (String file : suspiciousFiles) {
                    try {
                        File sourceFile = new File(file);
                        File destFile = new File(isolationDir, sourceFile.getName());
                        Files.move(sourceFile.toPath(), destFile.toPath(), 
                                 StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        System.err.println("Could not isolate file '" + file + "': " + e.getMessage());
                    }
                }
                
                SwingUtilities.invokeLater(() -> {
                    resultLabel.setText("Files successfully isolated.");
                    resultLabel.setForeground(Color.BLUE);
                });
            }
        }).start();
    }
}
