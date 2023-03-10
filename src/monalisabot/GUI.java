package monalisabot;

import java.awt.*;
import java.awt.event.*;
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
        if (image != null) {
            graphics.drawImage(image, 0, 0, null);
        }
    }
    public void setImage(boolean[][] isBlack)
    {
        int height = isBlack[0].length;
        int width = isBlack.length;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (isBlack[i][j])
                    g.drawRect(i, j, 0, 0);
    }
}
class PreviewFrame extends JFrame implements MouseListener, MouseMotionListener {
    ImagePreview imagePreview;
    BufferedImage image;
    Point initialPoint;
    PreviewFrame(BufferedImage image, boolean[][] isBlack)
    {
        super("Put this window where you want it to be drawn");
        super.setUndecorated(true);
        super.setOpacity(0.3f);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setAlwaysOnTop(true);
        this.image = image;
        imagePreview = new ImagePreview();
        imagePreview.setSize(image.getWidth(), image.getHeight());
        imagePreview.setImage(isBlack);
        add(imagePreview);
        addMouseMotionListener(this);
        addMouseListener(this);
        pack();
    }
    public void set(BufferedImage image, boolean[][] isBlack)
    {
        this.image = image;
        imagePreview.setSize(image.getWidth(), image.getHeight());
        imagePreview.setImage(isBlack);
        pack();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getXOnScreen() - initialPoint.x;
        int y = e.getYOnScreen() - initialPoint.y;
        super.setBounds(x, y, image.getWidth(), image.getHeight());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialPoint = new Point(e.getX(), e.getY());
    }

    //unused
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
public class GUI extends JFrame {
    static final String DEFAULT_FILE_TEXT = "Pick a file";
    JButton fileButton;
    JSlider thresholdSlider;
    JTextField thresholdShow;
    JTextArea errorArea;
    JFileChooser jFileChooser;
    boolean setFolder = false;
    File initialFile;
    PreviewFrame locationPreview = null;
    ImagePreparer imagePreparer = null;
    GUI()
    {
        super("Mona Lisa Bot");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setMaximumSize(new Dimension(300, 300));
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

        JButton drawButton = new JButton("Start the bot!");
        drawButton.addActionListener((e) -> startBot());
        errorArea = new JTextArea();
        errorArea.setEnabled(false);
        errorArea.setDisabledTextColor(Color.RED);
        errorArea.setRows(3);

        getContentPane().add(fileButton);
        getContentPane().add(thresholdPane);
        getContentPane().add(drawButton);
        getContentPane().add(errorArea);
        pack();
        super.setLocationRelativeTo(null);
    }
    void filePopUp()
    {
        if (!setFolder) {
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            setFolder = true;
        }
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
                if (image.getWidth() > 1300 || image.getHeight() > 1300)
                {
                    throw new Exception("Please use a smaller image (no bigger than 800)");
                }
                imagePreparer = new ImagePreparer(ImageIO.read(initialFile), threshold);
                imagePreparer.prepareImage();
                if (locationPreview != null) {
                    locationPreview.set(image, imagePreparer.isBlack);
                } else {
                    locationPreview = new PreviewFrame(image, imagePreparer.isBlack);
                    locationPreview.setVisible(true);
                }

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
            int offsetX = locationPreview.getBounds().x;
            int offsetY = locationPreview.getBounds().y;
            boolean[][] isBlackCopy = new boolean[imagePreparer.isBlack.length][imagePreparer.isBlack[0].length];
            for (int i = 0; i < imagePreparer.isBlack.length; i++)
                for (int j = 0; j < imagePreparer.isBlack[0].length; j++)
                    isBlackCopy[i][j] = imagePreparer.isBlack[i][j];
            Drawer drawer = new Drawer(imagePreparer.image,
                                        isBlackCopy,
                                        offsetX,
                                        offsetY,
                                        imagePreparer.blackPixelsCount);
            Thread drawerThread = new Thread(drawer);
            Thread keyboardThread = new Thread(new StopFromKeyboard(drawer));
            drawerThread.start();
            keyboardThread.start();
            locationPreview.setVisible(false);
            drawerThread.join();
            locationPreview.setVisible(true);
        } catch (Exception e) {
            errorArea.setText(e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void pack()
    {
        super.pack();
        if (locationPreview != null)
            locationPreview.repaint();
    }
}
