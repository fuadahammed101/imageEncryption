import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

public class ImageEncryptionApp {

    private static SecretKey generateSecretKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        return new SecretKeySpec(key, "AES");
    }

    private static byte[] encryptImage(byte[] imageBytes, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(imageBytes);
    }

    private static byte[] decryptImage(byte[] encryptedBytes, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedBytes);
    }

    private static void saveImage(byte[] imageBytes, String filename) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(imageBytes);
        fos.close();
    }

    private static byte[] loadImageBytes(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        byte[] imageBytes = fis.readAllBytes();
        fis.close();
        return imageBytes;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Encryption App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JLabel label = new JLabel("Enter Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton encryptButton = new JButton("Encrypt Image");
        JButton decryptButton = new JButton("Decrypt Image");

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(passwordField);
        panel.add(encryptButton);
        panel.add(decryptButton);
        frame.add(panel, BorderLayout.CENTER);

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String password = new String(passwordField.getPassword());
                    SecretKey key = generateSecretKey(password);

                    // Load image
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        byte[] imageBytes = loadImageBytes(file.getAbsolutePath());

                        // Encrypt and save
                        byte[] encryptedBytes = encryptImage(imageBytes, key);

                        // Save encrypted image with the same name but with .enc extension
                        String outputPath = file.getAbsolutePath().replaceFirst("[.][^.]+$", "") + ".enc";
                        saveImage(encryptedBytes, outputPath);
                        JOptionPane.showMessageDialog(null, "Image encrypted and saved as " + outputPath);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });


        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String password = new String(passwordField.getPassword());
                    SecretKey key = generateSecretKey(password);

                    // Load encrypted image
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        byte[] encryptedBytes = loadImageBytes(file.getAbsolutePath());

                        // Decrypt and save
                        byte[] decryptedBytes = decryptImage(encryptedBytes, key);

                        // Save decrypted image with the same name but with .jpg extension
                        String outputPath = file.getAbsolutePath().replaceFirst("[.][^.]+$", "") + ".jpg";
                        saveImage(decryptedBytes, outputPath);
                        JOptionPane.showMessageDialog(null, "Image decrypted and saved as " + outputPath);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });


        frame.setVisible(true);
    }
}
