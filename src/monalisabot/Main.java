package monalisabot;

import javax.swing.*;

public class Main
{
    private static void createAndShowGUI() {
        GUI gui = new GUI();
        gui.setVisible(true);
    }
    public static void main(String[] args)
    {
        //https://stackoverflow.com/questions/63144081/is-there-a-way-to-disable-native-dpi-scaling-in-swing-via-code-or-command-line
        System.setProperty("sun.java2d.uiScale", "1");
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

}
