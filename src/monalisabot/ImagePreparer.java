package monalisabot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class ImagePreparer
{
    BufferedImage image;
    Point[][] basePoint;
    boolean[][] inWave;
    boolean[][] walked;
    int blackPixelsCount;
    boolean[][] isBlack;
    int threshold;

    int[] dx = {-1,-1,-1, 0, 1, 1, 1, 0};
    int[] dy = {-1, 0, 1, 1, 1, 0,-1,-1};
    ImagePreparer(BufferedImage image, int threshold)
    {
        this.image = image;
        this.threshold = threshold;
        blackPixelsCount = 0;
        isBlack = new boolean[image.getWidth()][image.getHeight()];
        inWave = new boolean[image.getWidth()][image.getHeight()];
        walked = new boolean[image.getWidth()][image.getHeight()];
        basePoint = new Point[image.getWidth()][image.getHeight()];
    }

    void rectifyIntersections(int [][] cost, ArrayList<Point> wave)
    {
        //find basePoint of last
        ArrayList<Point> endPoints = new ArrayList<>();
        int x = 0;
        int y = 0;
        boolean found = false;
        while (!wave.isEmpty())
        {
            Point currentPoint = new Point(wave.get(wave.size() - 1));
            x = currentPoint.x;
            y = currentPoint.y;
            for (int k = 0; k < 8; k++)
            {
                int newX = x + dx[k];
                int newY = y + dy[k];
                if (ImageUtil.isInBounds(newX, newY, image) && cost[newX][newY] == cost[x][y] - 1)
                {
                    found = true;
                    if (!endPoints.contains(basePoint[newX][newY]))
                        endPoints.add(basePoint[newX][newY]);
                }
            }
            wave.remove(wave.get(wave.size() - 1));
        }
        if (!found) return;
        int copyX = x;
        x = basePoint[x][y].x;
        y = basePoint[copyX][y].y;
        for (Point p : endPoints)
        {
            ArrayList<Point> linePoints = Bresenham.drawLine(x, y, p.x, p.y);
            for (Point point: linePoints) {
                blackPixelsCount++;
                isBlack[point.x][point.y] = true;
            }
        }
    }

    void findMiddleOfWave(int[][] cost, ArrayList<Point> wave)
    {
        //find the ends
        while (!wave.isEmpty())
        {
            Point currentPoint = new Point(wave.get(wave.size() - 1));
            wave.remove(wave.get(wave.size() - 1));
            if (inWave[currentPoint.x][currentPoint.y]) continue;
            ArrayList<Point> elements = new ArrayList<>();
            boolean set = false;
            int dj = 0;
            int di = 1;
            int currentCost = cost[currentPoint.x][currentPoint.y];
            boolean foundFirstEnd = false;
            while (true)
            {
                int x = currentPoint.x;
                int y = currentPoint.y;
                walked[x][y] = true;
                if (foundFirstEnd)
                {
                    inWave[x][y] = true;
                    elements.add(new Point(x, y));
                }
                boolean found = false;
                for (int k = 1; k < 8; k += 2)
                {
                    int newX = x + dx[k];
                    int newY = y + dy[k];

                    if (dx[k] == -dj && dy[k] == -di && set) continue;
                    if (ImageUtil.isInBounds(newX, newY, image)
                            && cost[newX][newY] == currentCost &&
                            ((!inWave[newX][newY] && foundFirstEnd)
                                    || (!walked[newX][newY] && !foundFirstEnd) ))
                    {
                        set = true;
                        dj = dx[k];
                        di = dy[k];
                        currentPoint.setLocation(newX, newY);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    for (int k = 0; k < 8; k+=2)
                    {
                        int newX = x + dx[k];
                        int newY = y + dy[k];
                        if ((dx[k] == -dj && dy[k] == -di) && set) continue;
                        if (ImageUtil.isInBounds(newX, newY, image)
                                && cost[newX][newY] == currentCost &&
                                ((!inWave[newX][newY] && foundFirstEnd)
                                        || (!walked[newX][newY] && !foundFirstEnd) ))
                        {
                            set = true;
                            dj = dx[k];
                            di = dy[k];
                            currentPoint.setLocation(newX, newY);
                            found = true;
                            break;
                        }
                    }
                if (!found)
                {
                    if (!foundFirstEnd)
                    {
                        foundFirstEnd = true;
                        dj = -dj;
                        di = -di;
                    }
                    else
                        break;
                }
            }
            Point base = new Point(elements.get(elements.size()/2));
            for (Point i : elements)
            {
                basePoint[i.x][i.y] = new Point(base);
                if ((i.x != base.x || i.y != base.y))
                {
                    isBlack[i.x][i.y] = false;
                    blackPixelsCount--;
                }
            }
            if (currentCost != 0)
                rectifyIntersections(cost, elements);
        }
    }

    void makeLinesLessWide()
    {
        boolean[][] a = new boolean[image.getWidth()][image.getHeight()];
        int[][] cost = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
            {
                a[j][i] = isBlack[j][i];
                cost[j][i] = ImageUtil.BIG_INTEGER;
            }
        //attempt reducing width
        //first, a Lee to determine "direction"
        int currentCost;
        LinkedList<Point> queue = new LinkedList<>();
        ArrayList<Point> wave = new ArrayList<>();
        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (!a[j][i]) continue;
                cost[j][i] = 0;
                currentCost = 0;
                a[j][i] = false;
                wave.add(new Point(j, i));
                queue.addLast(new Point(j, i));
                while (!queue.isEmpty())
                {
                    Point top = queue.getFirst();
                    for (int k = 0; k < 8; k++)
                    {
                        int newI = top.y + dy[k];
                        int newJ = top.x + dx[k];
                        if (newI < image.getHeight()
                                && newI >= 0 && newJ >= 0 && newJ < image.getWidth()
                                && a[newJ][newI])
                        {
                            queue.addLast(new Point(newJ, newI));
                            a[newJ][newI] = false;
                            cost[newJ][newI] = cost[top.x][top.y] + 1;
                            if (cost[newJ][newI] > currentCost)
                            {
                                currentCost = cost[newJ][newI];
                                findMiddleOfWave(cost, wave);
                                wave.clear();
                            }
                            wave.add(new Point(newJ, newI));
                        }
                    }
                    queue.removeFirst();
                }
            }
        }
        DebugHelper dh = new DebugHelper(image.getWidth(), image.getHeight());
        dh.writeDebug(cost);
    }

    void makeBounding()
    {
        boolean[][] a = new boolean[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (!isBlack[j][i]) continue;
                for (int k = 0; k < 8; k++)
                {
                    int newX = j + dx[k];
                    int newY = i + dy[k];
                    if (ImageUtil.isInBounds(newX, newY, image) && !isBlack[newX][newY])
                        a[j][i] = true;
                }
            }
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
                isBlack[j][i] = a[j][i];
    }
    void detectBlackPixels()
    {
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++)
            {
                int color = image.getRGB(j, i);
                if ((color & 0xFF000000) != 0xFF000000)
                    continue;
                color = color & 0x00FFFFFF;
                int red = (color & 0xFF0000) >> 16;
                int green = (color & 0xFF00) >> 8;
                int blue = (color & 0xFF);
                int max = Math.max(red, Math.max(blue, green));
                if (max < threshold)
                {
                    isBlack[j][i] = true;
                    blackPixelsCount++;
                }
            }
    }
    void prepareImage()
    {
        detectBlackPixels();
      //  makeBounding();
        makeLinesLessWide();
    }
}
