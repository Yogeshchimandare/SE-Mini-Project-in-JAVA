// SmartTextEditor.java
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SmartTextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private javax.swing.Timer autoSaveTimer;
    private Set<String> dictionary;

    public SmartTextEditor() {
        setTitle("Smart Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        createMenuBar();
        initFileChooser();
        loadDictionary();
        startAutoSave();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");

        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontItem = new JMenuItem("Change Font");
        JMenuItem fontSizeItem = new JMenuItem("Change Font Size");
        fontItem.addActionListener(e -> changeFont());
        fontSizeItem.addActionListener(e -> changeFontSize());
        formatMenu.add(fontItem);
        formatMenu.add(fontSizeItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);

        setJMenuBar(menuBar);
    }

    private void initFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
    }

    private void openFile() {
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file.");
            }
        }
    }

    private void saveFile() {
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }

    private void loadDictionary() {
        dictionary = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionary.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("Dictionary not found. Spell check disabled.");
        }
    }

    private void startAutoSave() {
        autoSaveTimer = new javax.swing.Timer(30000, e -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("autosave.txt"))) {
                textArea.write(writer);
                System.out.println("Auto-saved at " + new Date());
            } catch (IOException ex) {
                System.out.println("Auto-save failed.");
            }
        });
        autoSaveTimer.start();
    }

    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Choose Font:", "Font Selector",
                JOptionPane.PLAIN_MESSAGE, null, fonts, textArea.getFont().getFamily());

        if (selectedFont != null) {
            int size = textArea.getFont().getSize();
            textArea.setFont(new Font(selectedFont, Font.PLAIN, size));
        }
    }

    private void changeFontSize() {
        String input = JOptionPane.showInputDialog(this, "Enter Font Size:", textArea.getFont().getSize());
        try {
            int newSize = Integer.parseInt(input);
            String fontName = textArea.getFont().getFamily();
            textArea.setFont(new Font(fontName, Font.PLAIN, newSize));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid font size.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartTextEditor::new);
    }
}
