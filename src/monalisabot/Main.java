package monalisabot;

import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;

public class Main implements Runnable
{
    BufferedImage theBeautifulMonaLisa;
    Robot bot;
    boolean[][] is;
    boolean[][] inWave;
    boolean[][] walked;
    Point[][] basePoint;
    int countt;
    int offsetX = 200;
    int offsetY = 200;
    int countLines = 0;
    BufferedImage img;

    int[] dx = {-1,-1,-1, 0, 1, 1, 1, 0};
    int[] dy = {-1, 0, 1, 1, 1, 0,-1,-1};
    void getChars()
    {
        boolean[][] a = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
        int[][] cost = new int[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
            {
                a[j][i] = is[j][i];
                if(a[j][i])
                    cost[j][i] = 0;
                else
                    cost[j][i] = 1000000;
            }
        //make Lee
        LinkedList<Point> queue = new LinkedList<>();
        Point origin = new Point(0, 0);
        boolean found = false;
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
        {
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
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
                        if (isInBounds(newJ, newI)
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
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
            {
                if (!is[j][i]) continue;
                if (cost[j][i] > max && cost[j][i] != 1000000)
                {
                    max = cost[j][i];
                    posMax = new Point(j, i);
                }
            }
        //draw the route from the farthest point to the beginning
        Point current = posMax;
        is[posMax.x][posMax.y] = false;
        countt--;
        ArrayList<Point> line = new ArrayList<>();
        while (current.x != origin.x || current.y != origin.y)
        {
            for (int k = 0; k < 8; k++)
            {
                int newI = current.y + dy[k];
                int newJ = current.x + dx[k];
                if (isInBounds(newJ, newI)
                        && cost[newJ][newI] + 1 == cost[current.x][current.y] && is[newJ][newI])
                {
                    current = new Point(newJ, newI);
                    line.add(current);
                    is[newJ][newI] = false;
                    countt--;
                    break;
                }
            }
        }
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
                    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                }
                System.out.println(p.x + offsetX + " " + (p.y + offsetY));
                bot.mouseMove(p.x + offsetX, p.y + offsetY);
            }
            bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
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

    boolean isInBounds(int x, int y)
    {
        return y < theBeautifulMonaLisa.getHeight()
                && y >= 0 && x >=0 && x < theBeautifulMonaLisa.getWidth();
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
                if (isInBounds(newX, newY) && cost[newX][newY] == cost[x][y] - 1)
                {
                    found = true;
                    if(!endPoints.contains(basePoint[newX][newY]))
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
                countt++;
                is[point.x][point.y] = true;
            }
        }
    }

    void findMiddleOfWave(int[][] cost, ArrayList<Point> wave)
    {
        //find the ends
        while(!wave.isEmpty())
        {
            Point currentPoint = new Point(wave.get(wave.size() - 1));
            wave.remove(wave.get(wave.size() - 1));
            if(inWave[currentPoint.x][currentPoint.y]) continue;
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
                    if (isInBounds(newX, newY)
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
                        if (isInBounds(newX, newY)
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
                    is[i.x][i.y] = false;
                    countt--;
                }
            }
            if (currentCost != 0)
                rectifyIntersections(cost, elements);
        }
    }


    void makeLinesLessWide()
    {
        boolean[][] a = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
        int[][] cost = new int[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
            {
                a[j][i] = is[j][i];
                cost[j][i] = 1000000;
            }
        //attempt reducing width
        //first, a Lee to determine "direction"
        int currentCost;
        LinkedList<Point> queue = new LinkedList<>();
        ArrayList<Point> wave = new ArrayList<>();
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
        {
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
            {
                if(!a[j][i]) continue;
                cost[j][i] = 0;
                currentCost = 0;
                a[j][i] = false;
                wave.add(new Point(j, i));
                queue.addLast(new Point(j, i));
                while (!queue.isEmpty())
                {
                    Point top = queue.getFirst();
                    for (int k = 0; k < 8; k+=1)
                    {
                        int newI = top.y + dy[k];
                        int newJ = top.x + dx[k];
                        if (newI < theBeautifulMonaLisa.getHeight()
                                && newI >= 0 && newJ >= 0 && newJ < theBeautifulMonaLisa.getWidth()
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
        DebugHelper dh = new DebugHelper(theBeautifulMonaLisa.getWidth(), theBeautifulMonaLisa.getHeight());
        dh.writeDebug(cost);
    }

    void makeBounding()
    {
        boolean[][] a = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
            {
                if (!is[j][i]) continue;
                for (int k = 0; k < 8; k++)
                {
                    int newX = j + dx[k];
                    int newY = i + dy[k];
                    if (isInBounds(newX, newY) && !is[newX][newY])
                        a[j][i] = true;
                }
            }
        for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
            for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
                is[j][i] = a[j][i];
    }

    public void run()
    {
        try
        {
            bot = new Robot();
            countt = 0;
            theBeautifulMonaLisa = ImageIO.read(getClass().getClassLoader().getResourceAsStream("monalisa.png"));
            is = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
            inWave = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
            walked = new boolean[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
            basePoint = new Point[theBeautifulMonaLisa.getWidth()][theBeautifulMonaLisa.getHeight()];
            for (int i = 0; i < theBeautifulMonaLisa.getHeight(); i++)
                for (int j = 0; j < theBeautifulMonaLisa.getWidth(); j++)
                {
                    int color = theBeautifulMonaLisa.getRGB(j, i);
                    if ((color & 0xFF000000) != 0xFF000000)
                        continue;
                    color = color & 0x00FFFFFF;
                    int red = (color & 0xFF0000)>>16;
                    int green = (color & 0xFF00)>>8;
                    int blue = (color & 0xFF);
                    int max = (red > green) ? red : green;
                    max = (max > blue) ? max : blue;
                    if(max < 0x00000022) //usually 55 sometimes AA
                    {
                        is[j][i] = true;
                        countt++;
                    }
                }
            //  makeBounding();
            makeLinesLessWide();
            while (countt!=0)
                getChars();
            System.out.println(countLines);
            try
            {
                ImageIO.write(img, "png", new File("output.png"));
            }
            catch (Exception e) {}
        }
        catch (IOException ioe)
        {
            System.out.println("Couldn't create Mona Lisa!");
        }
        catch (AWTException awt)
        {
            System.out.println("Couldn't create bot!");
        }
    }

    public static void main(String[] args)
    {
        Main mlb = new Main();
        Thread th = new Thread(mlb);
        th.start();
    }

}
