package monalisabot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DebugHelper
{
    BufferedImage img;
    int width;
    int height;
    Graphics2D g2;

    DebugHelper(int width, int height)
    {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;
    }

    void addPixelColour(int x, int y, int cost)
    {
        if (cost == ImageUtil.BIG_INTEGER)
            return;
        cost = cost % 765;
        int red = Math.max(255 - cost, 0) + Math.min(Math.max(cost - 511, 0), 255);
        int green = 255 - Math.min(Math.abs(255 - cost), 255);
        int blue = Math.max(255 - Math.min(Math.abs(511 - cost), 255), 0);
        red = (red % 16) * 16;
        green = (green % 16) * 16;
        blue = (blue % 16) * 16;
        g2.setColor(new Color(red, green, blue));
        g2.fillRect(x, y, 1, 1);
    }

    void writeDebug(int[][] cost)
    {
        g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[i].length; j++) {
                    addPixelColour(i, j, cost[i][j]);
            }
        }
        try
        {
            ImageIO.write(img, "png", new File("output.png"));
        }
        catch (Exception e) {}
    }
}
