import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// Node class for Huffman tree
class Node implements Comparable<Node> {
    byte character;
    int frequency;
    Node left, right;

    public Node(byte character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }
}

// HuffmanCoding class to handle encoding and decoding
class HuffmanCoding {
    private Map<Byte, String> huffmanCodes = new HashMap<>();
    private Node root;

    public void buildTree(byte[] data) {
        Map<Byte, Integer> frequencyTable = calculateFrequencies(data);
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencyTable.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node((byte) 0, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        root = pq.poll();
        generateHuffmanCodes(root, "");
    }

    private Map<Byte, Integer> calculateFrequencies(byte[] data) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : data) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }
        return frequencies;
    }

    private void generateHuffmanCodes(Node node, String code) {
        if (node == null) return;
        if (node.character != 0) {
            huffmanCodes.put(node.character, code);
        }
        generateHuffmanCodes(node.left, code + "0");
        generateHuffmanCodes(node.right, code + "1");
    }

    public byte[] encode(byte[] data) {
        StringBuilder encodedText = new StringBuilder();
        for (byte b : data) {
            encodedText.append(huffmanCodes.get(b));
        }
        return convertToByteArray(encodedText.toString());
    }

    private byte[] convertToByteArray(String encodedText) {
        int length = (encodedText.length() + 7) / 8;
        byte[] byteArray = new byte[length];
        for (int i = 0; i < encodedText.length(); i++) {
            if (encodedText.charAt(i) == '1') {
                byteArray[i / 8] |= 1 << (7 - i % 8);
            }
        }
        return byteArray;
    }

    public byte[] decode(byte[] encodedData) {
        StringBuilder encodedText = new StringBuilder();
        for (int i = 0; i < encodedData.length * 8; i++) {
            encodedText.append((encodedData[i / 8] & (1 << (7 - i % 8))) != 0 ? '1' : '0');
        }

        ByteArrayOutputStream decodedData = new ByteArrayOutputStream();
        Node current = root;
        for (char bit : encodedText.toString().toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current.character != 0) {
                decodedData.write(current.character);
                current = root;
            }
        }
        return decodedData.toByteArray();
    }
}

// FileUtils class for file operations
class FileUtils {
    public static byte[] readBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
    }

    public static void writeBinaryFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }

    public static String getCompressedFileName(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return originalFileName.substring(0, dotIndex) + " compressed" + originalFileName.substring(dotIndex);
        } else {
            return originalFileName + " compressed";
        }
    }

    public static String getDecompressedFileName(String compressedFileName) {
        int compressedIndex = compressedFileName.lastIndexOf(" compressed");
        if (compressedIndex != -1) {
            compressedFileName = compressedFileName.substring(0, compressedIndex);
        }
        return compressedFileName + " decompressed";
    }
}

// FileCompressorGUI class to create GUI
public class FileCompressorGUI {
    private JFrame frame;
    private JFileChooser fileChooser;
    private HuffmanCoding huffmanCoding;
    private byte[] fileContent;
    private String originalFileName;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public FileCompressorGUI() {
        huffmanCoding = new HuffmanCoding();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("File Compressor using Huffman Coding");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton loadFileButton = new JButton("Load File");
        JButton compressFileButton = new JButton("Compress File");
        JButton decompressFileButton = new JButton("Decompress File");

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    originalFileName = selectedFile.getAbsolutePath();
                    try {
                        fileContent = FileUtils.readBinaryFile(selectedFile.getAbsolutePath());
                        JOptionPane.showMessageDialog(frame, "File loaded successfully.");
                        updateStatus("File loaded: " + selectedFile.getName());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error loading file: " + ex.getMessage());
                        updateStatus("Error loading file: " + ex.getMessage());
                    }
                }
            }
        });

        compressFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileContent != null) {
                    new CompressWorker().execute();
                } else {
                    JOptionPane.showMessageDialog(frame, "No file loaded to compress.");
                    updateStatus("No file loaded to compress.");
                }
            }
        });

        decompressFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    new DecompressWorker(selectedFile).execute();
                }
            }
        });

        buttonPanel.add(loadFileButton);
        buttonPanel.add(compressFileButton);
        buttonPanel.add(decompressFileButton);

        fileChooser = new JFileChooser();

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        statusLabel = new JLabel("Status: Ready");

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);
        frame.add(statusLabel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    private String getOriginalFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    // SwingWorker for compression
    class CompressWorker extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() throws Exception {
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);
            updateStatus("Compressing " + originalFileName);

            byte[] fileContent = FileUtils.readBinaryFile(originalFileName);
            long originalFileSize = fileContent.length;
            huffmanCoding.buildTree(fileContent);
            byte[] encodedData = huffmanCoding.encode(fileContent);
            String compressedFilePath = FileUtils.getCompressedFileName(originalFileName);
            FileUtils.writeBinaryFile(compressedFilePath, encodedData);
            long compressedFileSize = encodedData.length;

            updateStatus(String.format("File compressed. Original size: %d bytes, Compressed size: %d bytes", originalFileSize, compressedFileSize));

            // Calculate compression percentage
            double compressionPercentage = ((double)(originalFileSize - compressedFileSize) / originalFileSize) * 100;
            JOptionPane.showMessageDialog(frame, String.format("Compressed file saved as %s\nCompressed file by %.2f%%", compressedFilePath, compressionPercentage));

            return null;
        }

        @Override
        protected void done() {
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            updateStatus("Compression complete");
        }
    }

    // SwingWorker for decompression
    class DecompressWorker extends SwingWorker<Void, Integer> {
        private File file;

        public DecompressWorker(File file) {
            this.file = file;
        }

        @Override
        protected Void doInBackground() throws Exception {
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);
            updateStatus("Decompressing " + file.getName());

            byte[] compressedContent = FileUtils.readBinaryFile(file.getAbsolutePath());
            long compressedFileSize = compressedContent.length;
            byte[] decompressedContent = huffmanCoding.decode(compressedContent);
            long decompressedFileSize = decompressedContent.length;
            String decompressedFilePath = FileUtils.getDecompressedFileName(file.getAbsolutePath()) + getOriginalFileExtension(originalFileName);
            FileUtils.writeBinaryFile(decompressedFilePath, decompressedContent);

            updateStatus(String.format("File decompressed. Compressed size: %d bytes, Decompressed size: %d bytes", compressedFileSize, decompressedFileSize));
            JOptionPane.showMessageDialog(frame, String.format("Decompressed file saved as %s\nOriginal file size: %d bytes\nCompressed file size: %d bytes", decompressedFilePath, decompressedFileSize, compressedFileSize));
            return null;
        }

        @Override
        protected void done() {
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            updateStatus("Decompression complete");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileCompressorGUI());
    }
}
