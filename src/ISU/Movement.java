package ISU;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * The movement class is responsible for the output and display of the
 * components on screen
 *
 * @author Brandon
 */
public class Movement extends JPanel implements ActionListener, MouseWheelListener, MouseListener, MouseMotionListener {

    Timer timer;        //Main timer
    Timer blowUpTimer;       //Timer for blowing up
    Timer mmTimer;      //Timer for minimap

    int accuracy = 1000;                //Percision for the smallest unit of measurment when moving the rocket (in milimeters)
    int delay = 10;                     //Delay in between running the program
    int tDelay = 1000 / delay;          //Delay in number of second divisions

    long pRadius = 1 * 600000, pScale = pRadius * 10000 * 2;    //Sets radius of the planet
    long pXpos = 0, pYpos = 0;              //Sets the virtual planet position
    double backColour;                      //Sets the colour of the background atmosphere

    double[][] stars = new double[50][3];   //Initializes the star array for the positioning for each star 
    boolean isPaused;               //Initializes pausing variable
    boolean blowUp;                 //Initializes variable to blow up   
    int blowUpCount;                //Initializes blowUp variable to keep track of how many explosions are displayed
    boolean showMiniMap;            //Variable to show miniMap
    boolean isMiniMapUp;            //Variable to check if miniMap is displayed
    boolean timeWarpOn;             //Variable to check if time warping is on
    int timeWarpSpeed = 1;          //Sets the time warping speed (should not be less than 0. This value is the number of times the positionUpdate method is called per frame. 

    int[] controlPassThrough = new int[10];     //Passes through the keyboard controls to Calculations for it to do something with them. 
    int[][] mouseDrag = new int[3][3];          //Array of mouse clicking and dragging to determine posisitioning and movements of the miniMap
    int[] pPosMM;                               //Initializes array for the positioning of the miniMap

    Calculations c = new Calculations();
    PauseMenu pm = new PauseMenu();

    Random rand = new Random(); //Random number generator
    BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e4 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e5 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage throttleAngle = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage angles = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage miniShip = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage stable = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage prograde = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage retrograde = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage rIn = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage rOut = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage warp = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage aScale = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage aPointer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage fScale = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    double zScale = 0.3;
    private javax.swing.JTextField altitude;
    private javax.swing.JTextField speedometer;

    /**
     * Initializes the game and game components
     */
    public Movement() {

        try {
            //Gets all the pictures files ready 
            rPic = ImageIO.read(new File("Rocket.png"));
            e1 = ImageIO.read(new File("e1.png"));
            e2 = ImageIO.read(new File("e2.png"));
            e3 = ImageIO.read(new File("e3.png"));
            e4 = ImageIO.read(new File("e4.png"));
            e5 = ImageIO.read(new File("e5.png"));
            arrow = ImageIO.read(new File("Arrow.png"));
            arrow2 = ImageIO.read(new File("Arrow2.png"));
            throttleAngle = ImageIO.read(new File("ThrottleArrow.png"));
            angles = ImageIO.read(new File("Angles.png"));
            miniShip = ImageIO.read(new File("MiniShip.png"));
            stable = ImageIO.read(new File("stable.png"));
            prograde = ImageIO.read(new File("prograde.png"));
            retrograde = ImageIO.read(new File("retrograde.png"));
            rIn = ImageIO.read(new File("rIn.png"));
            rOut = ImageIO.read(new File("rOut.png"));
            warp = ImageIO.read(new File("warp.png"));
            aScale = ImageIO.read(new File("aScale.png"));
            aPointer = ImageIO.read(new File("aPointer.png"));
            fScale = ImageIO.read(new File("fScale.png"));

        } catch (IOException e) {
            System.out.println(e);
        }

        //Adds input listeners
        addKeyListener(new TAdapter());
        setFocusable(true);
        addMouseWheelListener(this);

        //Prepares the altitude text-box
        altitude = new javax.swing.JTextField(20);
        altitude.setEditable(false);
        altitude.setFont(new java.awt.Font("Impact", 1, 18));
        altitude.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        //Prepares the speed text-box
        speedometer = new javax.swing.JTextField(10);
        speedometer.setEditable(false);
        speedometer.setFont(new java.awt.Font("Impact", 1, 11));
        speedometer.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        speedometer.setText("Speed: 0");

        //Initializes star positions
        for (double[] star : stars) {
            star[0] = rand.nextInt(ISU.getScreenSize('x'));
            star[1] = rand.nextInt(ISU.getScreenSize('y'));
            star[2] = (rand.nextInt(4) + 2);
        }

        //Adds pause menu
        pm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pm.addKeyListener(new TAdapter());
        pm.addWindowListener(new WindowAdapter() {
            //Sets window closing action
            @Override
            public void windowClosing(WindowEvent e) {
                isPaused = false;
                timer.start();
            }
        });

        //Starts the show!
        timer = new Timer(10, this);
        timer.start();

    }

    /**
     * Activated by the timer, it initializes the variables and switches to
     * perform different actions based off user input.
     *
     * @param e The action that was performed (timer activation)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        pm.setLocation(getWidth() / 2, getHeight() / 2);

        altitude.setLocation(getWidth() / 2 - 142, 4);
        speedometer.setLocation(getWidth() / 2 - 46, getHeight() - 200 - 15);

        if (showMiniMap == true && isMiniMapUp == false) {          //Before the minimap opens, run these commands
            timer.stop();                                           //Stop running timer
            pPosMM = new int[]{getWidth() / 2, getHeight() / 2};    //Initialize planet position
            mmTimer = new Timer(100, this);
            mmTimer.start();                        //Switch to less-intensive timer
            isMiniMapUp = true;                     //Sets this variable to check later 
            remove(altitude);                       //Removes temporarily
            remove(speedometer);
        } else if (showMiniMap == false && isMiniMapUp == true) { //When closing minimap, start the timer again
            mmTimer.stop();                 //Stops minimap timer
            isMiniMapUp = false;            //Resets variable
            timer.start();                  //Starts normal timer 
            add(altitude);                  //Puts back components
            add(speedometer);
        }

        //Normal/default action to take 
        if (blowUp == false && showMiniMap == false) {

            //Updates position more frequently based off of the time warp factor
            for (int i = 0; i < timeWarpSpeed; i++) {

                //Turns off time warping if in the atmosphere
                c.positionUpdate(controlPassThrough);

                if (c.altitudeToPlanetCenter < c.pAHL) {
                    timeWarpSpeed = 1;
                    timeWarpOn = false;
                    break;
                }
            }
            add(altitude);          //Keeps the text boxes on screen 
            add(speedometer);
        }
        
        if (c.startBlowUp == true) {    //When blowing up 
            remove(altitude);           //Removes temporarily
            remove(speedometer);
            timer.stop();               //Stops normal timer 

            if (blowUp == false) {  //Only if it hasn't blown up yet
                blowUpTimer = new Timer(300, this);  //Starts blow-up timer 
                blowUpTimer.start();
                blowUp = true;
            }
        }
        //Puts updated components on screen 
        repaint();
    }

    /**
     * Paints all the elements onto the screen
     *
     * @param g The graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //Sets planet size scale 
        pScale = (long) (pRadius * 2 * 10000 * zScale);

        //Normal path to take to display objects 
        if (blowUp == false && showMiniMap == false) {
            dispScenery(g);
            AffineTransform at = new AffineTransform();

            //Displays the rocket
            at.translate(getWidth() / 2 - rPic.getWidth() * zScale / 2, getHeight() / 2 - rPic.getHeight() * zScale / 2);  //Don't touch. Puts the rocket at the center
            at.scale(zScale, zScale);
            at.rotate(Math.toRadians(c.angle), rPic.getWidth() / 2, rPic.getHeight() / 2);
            g2d.drawImage(rPic, at, this);

            //Displays dials and meters on screen 
            dispControls(g);

            //Sets altitude and speedometer text 
            altitude.setText("Altitude: " + (int) (c.altitudeToPlanetCenter - pRadius) + " m");
            speedometer.setText((int) ((Math.sqrt(Math.pow(c.rYSpeed, 2) + Math.pow(c.rXSpeed, 2)))) + " m/s");

            //Activates if blowing up 
        } else if (blowUp == true) {

            //Displays background, stars and planet still. No rocket or dials (because it blew up)
            dispScenery(g);
            if (blowUpCount < 15) {
                dispExplosion(g);
                blowUpCount++;
            } else {
                blowUpTimer.stop();
            }

            //Displays miniMap
        } else if (showMiniMap == true) {
            //Mouse listeners for clicking and draging the map around 
            addMouseMotionListener(this);
            addMouseListener(this);
            dispMiniMap(g);
        }

    }

    /**
     * This method displays 2 to 4 explosion animations randomly around the
     * rocket each time it is called
     *
     * @param g
     */
    private void dispExplosion(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //Sets the picture size 
        int exSize = (int) (550 * zScale);

        //Loops a random amount o`f times (from 2-4)
        for (int i = 0; i < rand.nextInt(2) + 2; i++) {

            //Picks random fire picture to display 
            switch (rand.nextInt(5) + 1) {
                case 1:
                    g2d.drawImage(e1, getExpPos('w', exSize), getExpPos('h', exSize), exSize, exSize, this);
                    break;
                case 2:
                    g2d.drawImage(e2, getExpPos('w', exSize), getExpPos('h', exSize), exSize, exSize, this);
                    break;
                case 3:
                    g2d.drawImage(e3, getExpPos('w', exSize), getExpPos('h', exSize), exSize, exSize, this);
                    break;
                case 4:
                    g2d.drawImage(e4, getExpPos('w', exSize), getExpPos('h', exSize), exSize, exSize, this);
                    break;
                case 5:
                    g2d.drawImage(e5, getExpPos('w', exSize), getExpPos('h', exSize), exSize, exSize, this);
                    break;
            }

        }
    }

    /**
     * Returns positions around the center where the rocket would blow up
     *
     * @param x The length or width of the explosion coordinate
     * @return Random coordinates around the center of the screen (where the
     * rocket would be)
     */
    private int getExpPos(char x, int eSz) {
        if (x == 'w') {
            return (int) (-(eSz / 2) + (rand.nextInt(100) - 50) * zScale + getWidth() / 2);
            //Returns random position for explosion in X direction based off the width of the window 
        } else if (x == 'h') {
            return (int) (-(eSz / 2) + (rand.nextInt(100) - 50) * zScale + getHeight() / 2);
            //Returns random position for explosion in Y direction based off the height of the window 
        }
        return 0;   //Only if the char is not set properly

    }

    /**
     * Displays the scenery needed in the rocket program. Displays the
     * atmosphere, the stars and the planet.
     *
     * @param g The graphics object to help display the components
     */
    private void dispScenery(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //Draws the background atmosphere/space
        if (c.altitudeToPlanetCenter > (c.pAHL)) {      //If the rocket is above the atmosphere 
            backColour = 0;
        } else {
            //Draws the atmospheric colour based off of the altitude of the rocket
            backColour = .7 - ((c.altitudeToPlanetCenter - pRadius) / (c.pAHL - pRadius)) * 0.7;
        }
        //Paints the atmosphere
        g2d.setPaint(Color.getHSBColor((float) .55, (float) .71, (float) backColour));  //Last number for darkness 
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //Draws the stars 
        g.setColor(Color.white);
        for (double[] star : stars) {
            if (blowUp == false) {
                star[0] += c.rYSpeed / 1000;
                star[1] -= c.rXSpeed / 1000;
                //Will put stars on the opposite side of the screen if they cross over
                if (star[0] > ISU.totalY) {
                    star[0] -= ISU.totalY;
                } else if (star[0] < 0) {
                    star[0] += ISU.totalY;
                }
                //Will put stars on the opposite side of the screen if they cross over
                if (star[1] > ISU.totalX) {
                    star[1] -= ISU.totalX;
                } else if (star[1] < 0) {
                    star[1] += ISU.totalX;
                }
            }
            //Draws the star
            g.fillOval((int) star[1], (int) star[0], (int) star[2], (int) star[2]);
        }

        //Draws the planet 
        pXpos = (getWidth() - pScale) / 2;      //Sets x positioning (in the middle bottom of screen if visible)
        pYpos = (long) (getHeight() / 2 + ((rPic.getHeight() / 2) + 1 * (c.altitudeToPlanetCenter - pRadius) * accuracy) * zScale);     //Sets Y positioning (depends on rocket altitude and zoom scale)
        g.setColor(Color.green);
        Ellipse2D.Double shape = new Ellipse2D.Double(pXpos, pYpos, pScale, pScale);
        g2d.draw(shape);
        g2d.fill(shape);

    }

    /**
     * Draws the "control panel"
     *
     * @param g The graphics object for the method to draw its own pictures
     */
    private void dispControls(Graphics g) {
        //Sets altitude in altitude box 
        altitude.setText("Altitude: " + (int) c.altitudeToPlanetCenter);

        Graphics2D g2d = (Graphics2D) g;

        //Draws the blue border around middle bottom of screen (really a full circle but that doesn't matter)
        g.setColor(Color.blue);
        g.fillOval((getWidth()) / 2 - 100, getHeight() - 200, 200, 200);

        //Draws the smaller White circle for the angle numbers
        g.setColor(Color.white);
        g.fillOval((getWidth()) / 2 - 100 + 5, getHeight() - 200 + 5, 190, 190);

        //Draws the picture of all the angles for the arrows 
        g.drawImage(angles, getWidth() / 2 - 126, getHeight() - 195, 222, 190, this);

        //Draws throttle meter on left side of angle ball 
        AffineTransform at1 = new AffineTransform();
        at1.translate(getWidth() / 2 - 125, getHeight() - 195);  //Don't touch, draws the spinning arrow
        at1.scale(0.317, 0.31766);
        at1.rotate(Math.toRadians(c.sThrottle * 60), 400, 300); //Don't touch this, this spins the other arrow 
        g2d.drawImage(throttleAngle, at1, this);

        //Draws the SAS heading logos if applicable 
        switch (c.selectHeading) {
            case 1:
                g.drawImage(stable, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 2:
                g.drawImage(prograde, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 3:
                g.drawImage(rIn, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 4:
                g.drawImage(retrograde, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 5:
                g.drawImage(rOut, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
        }

        //Displays time warping triangles in top left of screen. Displays more triangles for faster warp speeds 
        if (timeWarpSpeed >= 1) {
            for (int i = 1; Math.pow(2, i) <= Math.pow(timeWarpSpeed, 2); i++) {
                g.drawImage(warp, (int) (15 + i * 20), 15, 15, 15, this);
            }
        }   

        //Draws heading arrow of where rocket is pointing 
        AffineTransform at = new AffineTransform();
        at.translate(((getWidth() / 2) - 128 + 1), (getHeight() - 256 + 57.5 / 2) * 1);     //Moves arrow to specified spot
        at.rotate(Math.toRadians(c.angle), arrow.getWidth() / 2, arrow.getHeight() / 2);    //Rotates arrow
        g2d.drawImage(arrow, at, this);

        //Draws direction of travel angle 
        AffineTransform at2 = new AffineTransform();
        at2.translate(((getWidth() / 2) - arrow2.getWidth() / 2), (getHeight() - arrow2.getHeight()) + 57.5 / 2);           //Moves arrow to spot
        at2.rotate(Math.toRadians(Calculations.travelAngle(c.rXSpeed, c.rYSpeed)), arrow2.getWidth() / 2, arrow2.getHeight() / 2);     //Rotates arrow based off direction of travel 
        g2d.drawImage(arrow2, at2, this);

        //Displays atmospheric pressure scale under altitude box
        g.drawImage(aScale, (getWidth() - 284) / 2, 0, this);
        if (((c.altitudeToPlanetCenter - pRadius) / (c.pAHL - pRadius)) > 1) {
            g.drawImage(aPointer, (int) ((getWidth() + 284 - 30) / 2 - 284), 61, this);
            //Draws arrow in final resting spot
        } else {
            g.drawImage(aPointer, (int) ((getWidth() + 284 - 30) / 2 - 284 + 284 * ((((100.13 * Math.pow(Math.E, -(c.altitudeToPlanetCenter - c.pRadius) / 5600)) / (100.13))))), 61, this);
            //Moves arrow to indicate relative pressure 
        }

        //Displays fuel gague
        g.drawImage(fScale, (getWidth() - 284), 0, this);
        if (c.rFuel < 0) {
            g.drawImage(aPointer, (int) ((getWidth() - 284 - 15)), 61, this);
            //Draws arrow in final resting spot
        } else {
            g.drawImage(aPointer, (int) ((getWidth()) - 284 * ((c.initFuel - c.rFuel) / c.initFuel) - 15), 61, this);
            //Moves arrow to indicate fuel remaining
        }

    }

    /**
     * Draws the minimap with the position of the rocket in relation to the
     * world
     *
     * @param g The graphics object for it to draw the minimap by itself
     */
    private void dispMiniMap(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());      //Draws background
        g.setColor(Color.green);                        //To draw the planet 
        g.fillOval((int) (- 600 / 2 * zScale * 5 + (pPosMM[0])), (int) (- 600 / 2 * zScale * 5 + pPosMM[1]), (int) (600 * (zScale * 5)), (int) (600 * (zScale * 5)));
        g.drawImage(miniShip, (int) (- 600 / 2 * zScale * 5 + (pPosMM[0]) + 600 * (zScale * 5) / 2 - 15 + c.xPos / 2000000 * (zScale * 5)), (int) (- 600 / 2 * (zScale * 5) + pPosMM[1] + 600 * (zScale * 5) / 2 - 15 - c.yPos / 2000000 * (zScale * 5)), 30, 30, this);
        //Draws the position of the ship relative to the world 
    }

    /**
     * Changes the zoomScale variable to reflect the desired zoom levels.
     *
     * @param e The mousewheel event 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //Changes the zoom level up or down based off wheel rotation 
        if (zScale >= 0.00005 && zScale <= 1) {
            if (e.getWheelRotation() > 0) {
                zScale -= 0.25 * zScale;
            } else if (e.getWheelRotation() < 0) {
                zScale += 0.25 * zScale;
            }
        }

        //If the rotation is out of bounds, put it back in
        if (zScale < 0.00005) {
            zScale = 0.00005;
        } else if (zScale > 1) {
            zScale = 1;
        }
    }

    /**
     * Sets the "anchor" for the mouse clicking and dragging on the miniMap
     *
     * @param e The mouse movements
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseDrag[0][0] = e.getX();
        mouseDrag[0][1] = e.getY();
        mouseDrag[1][0] = e.getX();
        mouseDrag[1][1] = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Gets how far the miniMap planet should move based off the mouse movements
     *
     * @param e The mouse movements
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDrag[0][0] = mouseDrag[1][0];
        mouseDrag[0][1] = mouseDrag[1][1];
        mouseDrag[1][0] = e.getX();
        mouseDrag[1][1] = e.getY();
        mouseDrag[2][0] = mouseDrag[1][0] - mouseDrag[0][0];
        mouseDrag[2][1] = mouseDrag[1][1] - mouseDrag[0][1];
        pPosMM[0] += mouseDrag[2][0];
        pPosMM[1] += mouseDrag[2][1];
    }

    /**
     * Sets the key variables as on and off when they are pressed or released
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W) {     //Releasing key W
                controlPassThrough[0] = 0;
            }

            if (key == KeyEvent.VK_S) {     //Releasing key S
                controlPassThrough[1] = 0;
            }
            if (key == KeyEvent.VK_A) {     //Releasing key A
                controlPassThrough[2] = 0;
            }

            if (key == KeyEvent.VK_D) {     //Releasing key D
                controlPassThrough[3] = 0;
            }

            if (key == KeyEvent.VK_Z) {     //Releasing key Z
                controlPassThrough[0] = 0;
            }

            if (key == KeyEvent.VK_X) {     //Releasing key X
                controlPassThrough[1] = 0;
            }
            if (key == KeyEvent.VK_HOME) {     //Releasing key HOME
                controlPassThrough[7] = 0;
            }

            if (key == KeyEvent.VK_END) {     //Releasing key END
                controlPassThrough[8] = 0;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (timeWarpOn == false) {

                if (key == KeyEvent.VK_W) {     //Pressed key W
                    controlPassThrough[0] = 1;
                    //Increase Throttle
                }

                if (key == KeyEvent.VK_S) {     //Pressed key S
                    controlPassThrough[1] = 1;
                    //Decrease Throttle
                }
                if (key == KeyEvent.VK_A) {     //Pressed key A
                    controlPassThrough[2] = 1;
                    //Turn left
                }

                if (key == KeyEvent.VK_D) {     //Pressed key D
                    controlPassThrough[3] = 1;
                    //Turn right
                }

                if (key == KeyEvent.VK_HOME) {     //Pressed key HOME
                    controlPassThrough[7] = 10;
                    //Set on preset orbit
                }
                if (key == KeyEvent.VK_END) {     //Pressed key END
                    controlPassThrough[8] = 10;
                    //For refueling
                }

                if (key == KeyEvent.VK_Z) {     //Pressed key Z
                    controlPassThrough[0] = -1;
                    //For min Throttle
                }

                if (key == KeyEvent.VK_X) {     //Pressed key X
                    controlPassThrough[1] = 10;
//                    for maxThrottle
                }

                if (key == KeyEvent.VK_1) {      //Pressed key 1 for a Stable Heading
                    if (c.selectHeading != 1) {
                        c.selectHeading = 1;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_2) {     //Pressed key 2 for pointing Prograde
                    if (c.selectHeading != 2) {
                        c.selectHeading = 2;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_3) {     //Pressed key 3 for pointing Right
                    if (c.selectHeading != 3) {
                        c.selectHeading = 3;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_4) {     //Pressed key 4 for pointing Retrograde
                    if (c.selectHeading != 4) {
                        c.selectHeading = 4;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_5) {     //Pressed key 5 for pointing Left
                    if (c.selectHeading != 5) {
                        c.selectHeading = 5;
                    } else {
                        c.selectHeading = 0;
                    }
                }

                if (key == KeyEvent.VK_F3) {     //Pressed key F3
                    c.startBlowUp = true;
                    //For a SuRpRiSe! What would it be????
                }
            }

            if (key == KeyEvent.VK_ESCAPE) {     //Pressed key ESCAPE for toggling pause menu
                if (isPaused == false) {
                    timer.stop();
                    isPaused = true;
                    pm.setVisible(true);
                    pm.addKeyListener(this);
                } else {
                    isPaused = false;
                    timer.start();
                }
            }

            if (key == KeyEvent.VK_M) {     //Pressed key M for toggle minimap menu
                showMiniMap = showMiniMap == false;
            }

            if (key == KeyEvent.VK_PERIOD) {     //Pressed key PERIOD for increasing time warp
                if (c.altitudeToPlanetCenter >= c.pAHL) {
                    timeWarpOn = true;
                    controlPassThrough[0] = 0;  //Resets controls 
                    controlPassThrough[1] = 0;
                    c.sThrottle = 0;
                    if (timeWarpSpeed < 256) {  //Make sure time warping does not go too fast 
                        timeWarpSpeed *= 2;     //Increase time warping s
                        controlPassThrough[0] = 0;
                        c.selectHeading = 1;        //Stops rocket rotation when time-warping to prevent the rocket spinning out of control 
                    } else {
                        timeWarpSpeed = 256;
                    }
                }

            }
            if (key == KeyEvent.VK_COMMA) {     //Pressed key COMMA for decreasing time warp
                if (timeWarpSpeed > 1) {    //Make sure it does not get too low
                    timeWarpSpeed /= 2;     //Decreasing time warping 
                } else {
                    timeWarpSpeed = 1;      //Warp back to normal
                    timeWarpOn = false;     //Turning it off
                }
            }

        }

    }

    //Bunch o garbage useless methods I don't need. 
    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
