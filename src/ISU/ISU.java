/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * The main class that prepares the window and initializes the separate classes.
 * @author Brandon
 */
public class ISU {

    static JFrame frame;
   
    static int setX, setY;
    static int totalX, totalY;

    /**
     * Creates and displays the Movement class Window
     *
     * @param args the command line arguments
     */
    private static void createAndShowGUI() {

        frame = new JFrame("Rocket Simulation");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setX = gd.getDisplayMode().getWidth() - 400;
        setY = gd.getDisplayMode().getHeight() - 100;
        if (setX < 1000) {
            setX = 1000;
        } 
        if (setY < 650) {
            setY = 650;
        } 
        
        totalX = gd.getDisplayMode().getWidth();
        totalY = gd.getDisplayMode().getHeight();

//        int setX = 700, setY = 700; //Flexible window dimensions
        Movement mvmnt = new Movement();
        frame.add(mvmnt);
        frame.setSize(setX, setY);
        frame.setVisible(true);

    }

    public static void restartGame() {
//        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.setVisible(false);
        frame.dispose();

        createAndShowGUI();

//        frame = new JFrame("Rocket Simulation");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        int setX = gd.getDisplayMode().getWidth() - 700;
//        int setY = gd.getDisplayMode().getHeight() - 100;
//
//        Movement mvmnt = new Movement();
//        frame.add(mvmnt);
//        frame.setSize(setX, setY);
//        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }

    public static int getScreenSize(char xORy) {

        if (xORy == 'x') {
            return totalX;
        } else if (xORy == 'y') {
            return totalY;
        }
        return 0;
    }

}
