package monalisabot;

import java.awt.*;
import java.util.*;


// In order to prevent the mouseListener from not working properly
// (unable to stop when meeting an already existent line),
// missing pixels are filled out with pixels in the shape of a line
// with  Bresenham's algorithm.
class Bresenham {

    public static Point switchToOctant3From(int octant, int x, int y)
    {
        Point p;
        switch (octant)
        {
            case 1:
                p = new Point(-y, x);
                break;
            case 2:
                p = new Point(x, -y);
                break;
            case 3:
                p = new Point(x, y);
                break;
            case 4:
                p = new Point(y, x);
                break;
            case 5:
                p = new Point(y,-x);
                break;
            case 6:
                p = new Point(-x,y);
                break;
            case 7:
                p = new Point(-x,-y);
                break;

            default:
                p = new Point(-y,-x);
        }
        return p;
    }

    public static Point switchFromOctant3To(int octant, int x, int y)
    {
        Point p;
        switch (octant)
        {
            case 1:
                p = new Point(y, -x);
                break;
            case 2:
                p = new Point(x, -y);
                break;
            case 3:
                p = new Point(x, y);
                break;
            case 4:
                p = new Point(y, x);
                break;
            case 5:
                p = new Point(-y,x);
                break;
            case 6:
                p = new Point(-x,y);
                break;
            case 7:
                p = new Point(-x,-y);
                break;

            default:
                p = new Point(-y,-x);
        }
        return p;
    }

    public static ArrayList<Point> drawLine(int origX, int origY, int beginX, int beginY)
    {
        int octant;
        int xFinal = beginX - origX;
        int yFinal = beginY - origY;
        ArrayList<Point> result = new ArrayList<>();
        // choose octant
        {
            if (xFinal >= 0 && yFinal <= 0 && xFinal <= -yFinal)
                octant = 1;
            else if (xFinal >= 0 && yFinal <= 0 && xFinal >= -yFinal)
                octant = 2;
            else if (xFinal >= 0 && yFinal >= 0 && xFinal >= yFinal)
                octant = 3;
            else if (xFinal >= 0 && yFinal >= 0 && xFinal <= yFinal)
                octant = 4;
            else if (xFinal <= 0 && yFinal >= 0 && -xFinal <= yFinal)
                octant = 5;
            else if (xFinal <= 0 && yFinal >= 0 && -xFinal >= yFinal)
                octant = 6;
            else if (xFinal <= 0 && yFinal <= 0 && -xFinal >= -yFinal)
                octant = 7;
            else
                octant = 8;
        }
        Point p = switchToOctant3From(octant, xFinal, yFinal);
        int currentX = 0;
        int currentY = 0;
        xFinal = p.x;
        yFinal = p.y;
        try
        {
            while (currentX != xFinal && currentY != yFinal)
            {
                currentX++;
                int d = yFinal * currentX / xFinal - currentY;
                if (d != 0) currentY++;
                int thisX = switchFromOctant3To(octant, currentX, currentY).x + origX;
                int thisY = switchFromOctant3To(octant, currentX, currentY).y + origY;
                result.add(new Point(thisX, thisY));
            }
        }
        catch (Exception i) // if division is by 0
        {
            while (currentY != yFinal)
            {
                currentY++;
                int thisX = switchFromOctant3To(octant, currentX, currentY).x + origX;
                int thisY = switchFromOctant3To(octant, currentX, currentY).y + origY;
                result.add(new Point(thisX, thisY));
            }
        }
        return result;
    }
}