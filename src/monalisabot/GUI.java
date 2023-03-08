package monalisabot;

import java.awt.*;
import java.io.File;
import javax.swing.*;

class ImagePreview extends JPanel {
    ImagePreview() {
        setBackground(Color.WHITE);
    }
}

public class GUI extends JFrame {
    static final String DEFAULT_FILE_TEXT = "Pick a file";
    static final String DEFAULT_THRESHOLD_TEXT = "Pick threshold";
    static final String DEFAULT_COORDINATES_TEXT = "Pick coordinates";
    JButton fileButton;
    JSlider thresholdSlider;
    JButton coordinatesButton;
    JButton seeCoordinatesButton;
    JFileChooser jFileChooser;
    ImagePreview imagePreview;
    File initialFile;
    Point topLeftMost = null;
    Point bottomRightMost = null;
    GUI()
    {
        super("Mona Lisa Bot");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setSize(600, 400);
        super.setLocationRelativeTo(null);

        JPanel inside = new JPanel();
        inside.setLayout(new BorderLayout());
        getContentPane().add(inside);


        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        jFileChooser = new JFileChooser();
        fileButton = new JButton(DEFAULT_FILE_TEXT);
        fileButton.addActionListener((e) -> {filePopUp();});

        JPanel thresholdPane = new JPanel();
        JLabel jl = new JLabel("Threshold:");
        thresholdSlider = new JSlider(0, 255);
        thresholdSlider.setPaintLabels(true);
        thresholdPane.add(jl);
        thresholdPane.add(thresholdSlider);
        coordinatesButton = new JButton(DEFAULT_COORDINATES_TEXT);
        seeCoordinatesButton = new JButton("See coordinates on screen");
        buttons.add(fileButton);
        buttons.add(thresholdPane);
        buttons.add(coordinatesButton);
        buttons.add(seeCoordinatesButton);
        inside.add(buttons, BorderLayout.WEST);
        imagePreview = new ImagePreview();
        inside.add(imagePreview, BorderLayout.CENTER);
    }
    void filePopUp()
    {
        int ok = jFileChooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            initialFile = jFileChooser.getSelectedFile();
            fileButton.setText("Chosen file: " + initialFile.getName());
        }
    }
}
