package monalisabot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class Drawer implements Runnable
{
    BufferedImage image;
    boolean[][] isBlack;
    int[] dx = {-1,-1,-1, 0, 1, 1, 1, 0};
    int[] dy = {-1, 0, 1, 1, 1, 0,-1,-1};
    int countLines;
    int offsetX;
    int offsetY;
    int countBlackPixels;
    Robot bot;

    Drawer (BufferedImage image, boolean[][] isBlack, int offsetX, int offsetY, int blackPixelsCount)
    {
        this.image = image;
        this.isBlack = isBlack;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.countBlackPixels = blackPixelsCount;
        countLines = 0;
    }
    void findAndDrawLine()
    {
        //make Lee
        boolean[][] a = new boolean[image.getWidth()][image.getHeight()];
        int[][] cost = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
            {
                a[j][i] = isBlack[j][i];
                if(a[j][i])
                    cost[j][i] = 0;
                else
                    cost[j][i] = ImageUtil.BIG_INTEGER;
            }
        LinkedList<Point> queue = new LinkedList<>();
        Point origin = new Point(0, 0);
        boolean found = false;
        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (!a[j][i]) continue;
                found = true;
                a[j][i] = false;
                origin = new Point(j, i);
                queue.addLast(new Point(origin));
                while (!queue.isEmpty())
                {
                    Point top = queue.getFirst();
                    for (int k = 0; k < 8; k++)
                    {
                        int newI = top.y + dy[k];
                        int newJ = top.x + dx[k];
                        if (ImageUtil.isInBounds(newJ, newI, image)
                                && a[newJ][newI])
                        {
                            queue.addLast(new Point(newJ, newI));
                            a[newJ][newI] = false;
                            cost[newJ][newI] = cost[top.x][top.y] + 1;
                        }
                    }
                    queue.removeFirst();
                }
                break;
            }
            if (found) break;
        }
        //get farthest point
        int max = 0;
        Point posMax = new Point(origin.x, origin.y);
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (!isBlack[j][i]) continue;
                if (cost[j][i] > max && cost[j][i] != ImageUtil.BIG_INTEGER)
                {
                    max = cost[j][i];
                    posMax = new Point(j, i);
                }
            }
        //Now get the route from the farthest point to the beginning
        Point current = posMax;
        isBlack[posMax.x][posMax.y] = false;
        countBlackPixels--;
        ArrayList<Point> line = new ArrayList<>();
        while (current.x != origin.x || current.y != origin.y)
        {
            for (int k = 0; k < 8; k++)
            {
                int newI = current.y + dy[k];
                int newJ = current.x + dx[k];
                if (ImageUtil.isInBounds(newJ, newI, image)
                        && cost[newJ][newI] + 1 == cost[current.x][current.y] && isBlack[newJ][newI])
                {
                    current = new Point(newJ, newI);
                    line.add(current);
                    isBlack[newJ][newI] = false;
                    countBlackPixels--;
                    break;
                }
            }
        }
        // draw it
        if (line.size() >= 3)
        {
            //   bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            try
            {
                Thread.sleep(75);//100 also works
            }
            catch (Exception e)
            {
                System.out.println("haha I have insomnia");
            }
            boolean first = true;
            for (Point p : line)
            {
                if (first)
                {
                    bot.mouseMove(p.x + offsetX, p.y + offsetY);
                    first = false;
                    try
                    {
                        Thread.sleep(75);
                    }
                    catch (Exception e)
                    {
                        System.out.println("haha I have insomnia");
                    }
                  //  bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                }
            //    System.out.println(p.x + offsetX + " " + (p.y + offsetY));
           //     bot.mouseMove(p.x + offsetX, p.y + offsetY);
            }
          //  bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            try
            {
                Thread.sleep(75);
            }
            catch (Exception e)
            {
                System.out.println("haha I have insomnia");
            }
            countLines++;
        }
    }
    public void run()
    {
        try
        {
            bot = new Robot();
            while (countBlackPixels != 0)
                findAndDrawLine();
            System.out.println(countLines);
        }
        catch (AWTException awt)
        {
            System.out.println("Couldn't create bot!");
        }
    }
}
