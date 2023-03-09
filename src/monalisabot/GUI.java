package monalisabot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

class ImagePreview extends JPanel {
    BufferedImage image = null;
    ImagePreview()
    {
        setBackground(Color.WHITE);
        setMinimumSize(new Dimension(300, 300));
        setSize(300, 300);
        setPreferredSize(new Dimension(300, 300));
        setMaximumSize(new Dimension(3000, 3000));
    }
    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        super.setPreferredSize(new Dimension(width, height));
        super.setMinimumSize(new Dimension(width, height));
    }
    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        if (image != null)
            graphics.drawImage(image, 0, 0, null);
    }
    public void setImage(boolean[][] isBlack)
    {
        int width = isBlack[0].length;
        int height = isBlack.length;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (isBlack[i][j])
                    g.drawRect(i, j, 0, 0);
    }
}

public class GUI extends JFrame {
    static final String DEFAULT_FILE_TEXT = "Pick a file";
    static final String DEFAULT_COORDINATES_TEXT = "Pick coordinates";
    static final String DEFAULT_SEE_COORDINATES_TEXT = "See coordinates on screen";
    JButton fileButton;
    JSlider thresholdSlider;
    JTextField thresholdShow;
    JButton coordinatesButton;
    JButton seeCoordinatesButton;
    JTextArea errorArea;
    JFileChooser jFileChooser;
    ImagePreview imagePreview;
    File initialFile;
    Point topLeftMost = null;
    Point bottomRightMost = null;
    ImagePreparer imagePreparer = null;
    GUI()
    {
        super("Mona Lisa Bot");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inside = new JPanel();
        inside.setLayout(new BoxLayout(inside, BoxLayout.X_AXIS));
        getContentPane().add(inside);


        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setMaximumSize(new Dimension(300, 300));
        jFileChooser = new JFileChooser();
        fileButton = new JButton(DEFAULT_FILE_TEXT);
        fileButton.addActionListener((e) -> {filePopUp();});

        JPanel thresholdPane = new JPanel();
        JLabel jl = new JLabel("Threshold:");
        thresholdSlider = new JSlider(0, 255);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.addChangeListener((e)-> precalculateImage());
        thresholdShow = new JTextField(3);
        thresholdShow.addActionListener((e) -> changeThresholdFromField());
        thresholdPane.add(jl);
        thresholdPane.add(thresholdSlider);
        thresholdPane.add(thresholdShow);

        coordinatesButton = new JButton(DEFAULT_COORDINATES_TEXT);
        seeCoordinatesButton = new JButton(DEFAULT_SEE_COORDINATES_TEXT);

        JButton drawButton = new JButton("Start the bot!");
        drawButton.addActionListener((e) -> startBot());
        errorArea = new JTextArea();
        errorArea.setEnabled(false);
        errorArea.setDisabledTextColor(Color.RED);
        errorArea.setRows(3);

        buttons.add(fileButton);
        buttons.add(thresholdPane);
        buttons.add(coordinatesButton);
        buttons.add(seeCoordinatesButton);
        buttons.add(drawButton);
        buttons.add(errorArea);

        inside.add(buttons);
        imagePreview = new ImagePreview();
        inside.add(imagePreview);
        super.pack();
        super.setLocationRelativeTo(null);
    }
    void filePopUp()
    {
        int ok = jFileChooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) {
            initialFile = jFileChooser.getSelectedFile();
            fileButton.setText("Chosen file: " + initialFile.getName());
            precalculateImage();
        }
    }
    void precalculateImage()
    {
        int threshold = thresholdSlider.getValue();
        thresholdShow.setText(threshold + "");

        if (initialFile != null)
        {
            try
            {
                BufferedImage image = ImageIO.read(initialFile);
                if (image == null)
                {
                    throw new Exception("Use a valid image!");
                }
                if (image.getWidth() > 800 || image.getHeight() > 800)
                {
                    throw new Exception("Please use a smaller image (no bigger than 800)");
                }
                imagePreparer = new ImagePreparer(ImageIO.read(initialFile), threshold);
                imagePreparer.prepareImage();
                imagePreview.setSize(image.getWidth(), image.getHeight());
                imagePreview.setImage(imagePreparer.isBlack);
                pack();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errorArea.setText(e.getMessage());
            }
        }
    }
    void changeThresholdFromField()
    {
        try
        {
            int threshold = Integer.parseInt(thresholdShow.getText());
            thresholdSlider.setValue(threshold);
        } catch (Exception e) {
            e.printStackTrace();
            errorArea.setText(e.getMessage());
        }
    }
    void startBot()
    {
        try
        {
            Drawer drawer = new Drawer(imagePreparer.image,
                                        imagePreparer.isBlack,
                                        200,
                                        200,
                                        imagePreparer.blackPixelsCount);
            Thread th = new Thread(drawer);
            th.start();
        } catch (Exception e) {
            errorArea.setText(e.getMessage());
            e.printStackTrace();
        }
    }
}
