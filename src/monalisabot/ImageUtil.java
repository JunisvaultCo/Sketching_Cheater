package monalisabot;

import java.awt.image.BufferedImage;

public class ImageUtil {
    public static int BIG_INTEGER = 1000000;
    public static boolean isInBounds(int x, int y, BufferedImage bi)
    {
        return y < bi.getHeight() && y >= 0 && x >= 0 && x < bi.getWidth();
    }
}
