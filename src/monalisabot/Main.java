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
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

}
