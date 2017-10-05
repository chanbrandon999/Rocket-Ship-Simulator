package ISU;

import java.util.Random;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * This class is responsible for handling the physics of the rocket. Handles the
 * controls, calculates the next position/angle and all the forces acting on the
 * rocket.
 *
 * @author Brandon
 */
public class Calculations {

    int accuracy = 1000;                //Percision for the smallest unit of measurment when moving the rocket (in milimeters)
    int delay = 10;                     //Delay in between running the program
    int tDelay = 1000 / delay;          //Delay in number of second divisions
    long pRadius = 1 * 600000;                    //The raduis of the planet in Meters
    public double xPos = 0;                        //The position of the rocket in the X direction (in milimeters)
    public double yPos = pRadius * accuracy;       //The position of the rocket in the Y direction (in milimeters)
    public double aSpeed, angle;                                //Initializing the angular throttles (in degrees per 10 milliseconds)
    public double rXSpeed = 0, rYSpeed = 0, sThrottle = 0;      //Setting the rocket speeds in the X and Y directions (meters per second) && Initializing rocket throttle (0 to 1)
    long rThrust = 1500 * 1000 * 1;                             //Setting the maximum thrust of the rocket (in Newtons)
    double pGravity = 9.81;                                     //Setting the strength of gravity at the surface (in m/s^2)
    double pAHL = 670000;                                       //Setting the atmospheric height limit where the atmosphere is no longer simulated (in meters)
    double[][] stars = new double[50][3];                       //Initializes the star array for each of the star positions (in virtual screen position)
    public double altitudeToPlanetCenter;                       //Initializes the altitude of the rocket (in meters)
    double rFuel = 16000, initFuel = rFuel;                     //Sets the amount of fuel the rocket has (in kilograms)
    double rMass = 12.75 * 1000 + rFuel;                        //Sets the mass of the rocket (kilograms)

    double xForce, yForce;                                      //Sets the X and Y forces that are applied to the rocket (in Newtons)
    double xAccel, yAccel;                                      //Initializes acceleration variables (in Meters per Second)
    int[] controls = new int[10];                               //Initializes the array which is passed from the Movement class. Identifies if keys were pressed or not with numbers. 
    int selectHeading;                                          //Typed with a key, it identifies which direction the user wants the rocket to point in 

    boolean startBlowUp;                            //Specifies whether or not the rocket hits the "planet" with enough force to blow up 
    boolean timeWarpOn;                             //Specifies if time warping is on or not 

    boolean resetAngle;                             //For the rotationControl method, identifies whether or not the method's variables need to be reset
    double hAngle[] = new double[4];                //The angle of each main direction relative to the direction of travel 
    double midAngle;                                //The angle at which the rocket should start slowing down to point in the specified direction
    boolean slowSpin = true;                        //Whether or not it should start slowing down or not
    double[] previousASpeed = new double[2];        //The previous angular speed the rocket was traveling at to determine if it switches direction
    boolean firstTurn = true;                       //Whether or not the rocket has started pointing in the right direction or not

    Random rand = new Random();                     //Random number generator for the stars
    OutputWindow flightStats = new OutputWindow();          //An output window in which it would display flight data (forces, acceleration, angles, etc.)
    boolean dispFlightStats = false;                         //Whether or not the window should be shown (default == off)

    /**
     * Initializes certain variables if needed
     */
    public Calculations() {

        for (int i = 0; i < stars.length; i++) {            //Creates random assortment of stars in the background
            stars[i][0] = rand.nextDouble() * ISU.getScreenSize('x');   //Sets x screen position
            stars[i][1] = rand.nextDouble() * ISU.getScreenSize('y');   //Sets y screen position
            stars[i][2] = (rand.nextInt(4) + 2);            //sets star size
        }

        if (dispFlightStats == true) {
            flightStats.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            flightStats.setVisible(true);
            flightStats.setResizable(true);
            flightStats.setSize(600, 300);
            flightStats.setLocation(1250, 50);
        }

        if (false) {        //Sets the rocket in pre-set orbit at beginning of program
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
            rXSpeed = 2243 * 1;
            rYSpeed = 0;
        }

        if (false) {        //Sets the rocket in crash course with ground (testing crashes if needed (not really)
            yPos = (pRadius + 300) * accuracy;
            angle = 180;
            rYSpeed = -1000;
        }

    }

    /**
     * Updates the position of the rocket based off the forces acting on the
     * rocket
     *
     * @param controlsPassThrough An array of values with the positioning and
     * User Input. Typically a 1 or a 0 for if the key has been pressed or not.
     */
    public void positionUpdate(int[] controlsPassThrough) {

        if (timeWarpOn == true) { //Skips calculating thrust and drag (rocket would not be accelerating or be in the atmosphere)
            xForce = (-calcG() * trigAngle('s', xPos, yPos));       //Accelerates the rocket towards the planet in X plane
            yForce = (-calcG() * trigAngle('c', xPos, yPos));       //Accelerates the rocket towards the planet in Y plane
        } else {
            controls = controlsPassThrough;                         //Gets the controls from Movement to get rotations and thrust
            controls();                                             //Performs actions with given controls
            xForce = (calcT() * trigAngle('s', fix(angle + 0 * travelAngle(xPos, yPos)))) - (calcG() * trigAngle('s', xPos, yPos) + calcD() * trigAngle('s', rXSpeed, rYSpeed));
            yForce = (calcT() * trigAngle('c', fix(angle + 0 * travelAngle(xPos, yPos)))) - (calcG() * trigAngle('c', xPos, yPos) + calcD() * trigAngle('c', rXSpeed, rYSpeed));
            //Adds components of the X forces and Y forces together. Includes Thrust, Gravity and Drag. 
            //See link for force vectors: http://www.physicstutorials.org/home/vectors
        }

        xAccel = (xForce / rMass);
        yAccel = (yForce / rMass);
        //Calculates acceleration based off of force components according to Newton's law: F = mass * acceleration

        //Calculates the altitude from the center of the planet 
        //Moves the rocket given acceleration
        applyMovement();

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2)));

        //For displaying the flight statistics 
        if (dispFlightStats == true) {
            flightStats.toOutputWindow(calcT(), calcG(), trigAngle('s', xPos, yPos), xForce, yForce, xAccel, yAccel, altitudeToPlanetCenter, aSpeed, xPos * accuracy, yPos * accuracy, angle, trigAngle('c', angle), rXSpeed, rYSpeed, calcD() * trigAngle('c', rXSpeed, rYSpeed), calcD() * trigAngle('s', rXSpeed, rYSpeed));
        }

    }

    /**
     * Moves the rocket given the acceleration of the rocket in X and Y
     * directions.
     */
    public void applyMovement() {

        rXSpeed += xAccel / tDelay;     //Increases the speed of the rocket by the amount of acceleration experienced by the rocket in 10 millisecond 
        rYSpeed += yAccel / tDelay;

        xPos += rXSpeed * delay;        //Moves the rocket given the current speed of the rocket. 
        yPos += rYSpeed * delay;        //The position updates each 10 milliseconds by the speed each millisecond 

        if ((Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2))) < pRadius) {   //Checks if the rocket goes underneath the ground (don't change to altitudeToPlanetCenter-will break test cases)

            if ((Math.sqrt(Math.pow(rXSpeed, 2) + Math.pow(rYSpeed, 2))) > 6) {     //Checks if it hits it hard enough to blow up. Crash tolerance is 6 m/s
                startBlowUp = true;                                                                 
                
//Start rocket blowing up sequence in Movement
            }

            //Sets the initial crash angles to prevent inaccuracies between calculations
            double crashAngleX = trigAngle('s', xPos, yPos);
            double crashAngleY = trigAngle('c', xPos, yPos);

            //Moves the rocket to edge of the planet if it tries to go in the ground 
            xPos = pRadius * crashAngleX * accuracy;
            yPos = pRadius * crashAngleY * accuracy;
        }

        //Prevents rocket rotation during time warping 
        if (timeWarpOn == false) {
            rotationControl(selectHeading);     //Rotates the rocket to specified heading 
            angle += aSpeed;                    //Adds the angular speed of the rocket to the angle (in degrees)
            angle = fix(angle);                 //Fixes angle if it gets negative or above 360 degrees. 
        }
    }

    /**
     * Finds the Sine or Cosine value of two specified "sides." Used when no
     * angle is known, but the X and Y coordinates of something is known
     * (speeds, positioning, etc.)
     *
     * @param sinORcos Specifies which math equation to use. 's' for Sine, 'c'
     * for Cosine
     * @param xDir The xPosition of the rocket to find the angle
     * @param yDir The yPosition of the rocket to find the angle
     * @return Returns the cosine or sine value of the angle to the center of
     * the planet
     */
    public static double trigAngle(char sinORcos, double xDir, double yDir) {

        //Main part of this method would return error 
        if (xDir == 0 && sinORcos == 's') {
            return 0;
        } else if (xDir == 0 && sinORcos == 'c') {
            return 1;
        }

        //Calculates the sin or cos values based off of the adjacent/opposite side of a triangle. 
        if (sinORcos == 's') {
            if (Math.signum(xDir) == -1) {      //Checks if negative. Would need to keep it negative later. 
                return -Math.abs(Math.sin((Math.atan(xDir / yDir))));
            } else {
                return Math.abs(Math.sin((Math.atan(xDir / yDir))));
            }
        } else if (sinORcos == 'c') {
            if (Math.signum(yDir) == -1) {      //Checks if negative. Would need to keep it negative later. 
                return -Math.abs(Math.cos((Math.atan(xDir / yDir))));
            } else {
                return Math.abs(Math.cos((Math.atan(xDir / yDir))));
            }
        }
        return 0;
    }

    /**
     * Finds the Sine or Cosine value of the specified angle. When only the
     * angle is available, use this method.
     *
     * @param sinORcos Specifies which math equation to use. 's' for Sine, 'c'
     * for Cosine
     * @param angle The angle of the rocket to find the trigonometric value for
     * @return The Sine or Cosine value of a specified angle
     */
    public static double trigAngle(char sinORcos, double angle) {

        if ((sinORcos == 's')) {
            return Math.sin(Math.toRadians(angle));
        } else if (sinORcos == 'c') {
            return Math.cos(Math.toRadians(angle));
        }
        return 0;
    }

    /**
     * Calculates the force of gravity given a certain altitude from planet
     *
     * @return The force of gravity in Newtons
     */
    public double calcG() {

        //quits if the rocket is underneath the planet
        if (altitudeToPlanetCenter <= pRadius) {
            return 0;
        } else {
            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pRadius, 2));
            //Returns force of gravity following the law of diminishing gravity
            //See link for equations: https://www.mansfieldct.org/Schools/MMS/staff/hand/lawsgravaltitude.htm
        }
    }

    /**
     * Calculates the force of drag that is acting upon the rocket.
     *
     * @return Returns the force of drag acting on the rocket
     */
    public double calcD() {
        if (altitudeToPlanetCenter < pAHL) {
            double atmPressure = 100.13 * Math.pow(Math.E, -(altitudeToPlanetCenter - pRadius) / 5600); //Calculates atmospheric pressure in kPa.
            return 0.5 * atmPressure * Math.pow((Math.sqrt(Math.pow(rXSpeed / 100, 2) + Math.pow(rYSpeed / 100, 2))), 2) * 0.2 * 4.91;
            //Calculates atmospheric drag based off the speed, atmospheric pressure and rocket drag coefficients
            //See link for details and equations: http://wiki.kerbalspaceprogram.com/wiki/Atmosphere
        } else {
            return 0;
        }
    }

    /**
     * Calculates the thrust of the rocket given the max thrust and the current
     * throttle setting. The rocket must not be out of fuel to be able to go.
     * The total thrust would be limited by the throttle as a percentage of the
     * thrust.
     *
     * @return Force of thrust in Newtons.
     */
    public double calcT() {
        if (rFuel > 0) {
            if (sThrottle > 0) {
                rFuel -= 492.74 / 1000 * sThrottle;
                //Subtracts an amount of fuel per 10 milliseconds according to engine efficiency and throttle setting
                rMass = 12.75 * 1000 + rFuel;
                //Subtracts fuel mass from rocket
                return sThrottle * rThrust;
            }
        }
        return 0;
    }

    /**
     * Rotates the ship to a specified position
     *
     * @param choice The specified direction the user wants the rocket to point
     * to
     */
    public void rotationControl(int choice) {

        //Quits if if the rocket is not moving (no heading to go to)
        if ((Math.sqrt(Math.pow(rYSpeed, 2) + Math.pow(rXSpeed, 2))) == 0 && choice >= 1) {
            return;
        }

        //designates each heading relative to direction of travel 
        hAngle[0] = fix(travelAngle(rXSpeed, rYSpeed));
        hAngle[1] = fix(hAngle[0] + 90);
        hAngle[2] = fix(hAngle[1] + 90);
        hAngle[3] = fix(hAngle[2] + 90);

        //Should only run once once there is a heading selection made 
        if (choice != 0 && resetAngle == true) {
            resetAngle = false;
            slowSpin = false;
            firstTurn = true;
        }

        if (choice == 1) { //Holds a stable heading 
            if (aSpeed > 0.01) {
                aSpeed -= 0.01;
            } else if (aSpeed < -0.01) {
                aSpeed += 0.01;
            } else {
                aSpeed = 0;
            }
        } else if (2 <= choice && choice <= 5) {
            rotateTo(choice - 2);
        } else if (resetAngle != true) {
            resetAngle = true;
        }
    }

    /**
     * Rotates towards specified location 0, 1, 2 or 3. These locations are
     * north/east/south/west of the direction of movement.
     *
     * @param SASpos The selection of location to rotate towards
     */
    private void rotateTo(int SASpos) {

        //Finds out if the rocket switches spinning directions or is opposite to the desired heading 
        //Should only get a new mid-point when the rocket switches directions towards the selected heading, if it is the first time running, or if it is completely opposite and needs a fresh start
        if (((Math.signum(previousASpeed[0]) * Math.signum(previousASpeed[1]) == -1)) || ((angle) > fix(hAngle[SASpos] + 178) && (angle) < fix(hAngle[SASpos] - 178)) || firstTurn == true) {

            //Finds out the mid-angle between the two angles 
            //The mid-angle is the point at which the ship should start slowing down 
            if ((angle) > hAngle[SASpos]) {
                if ((angle) - hAngle[SASpos] > 180) {
                    midAngle = fix((((angle) - 360) + hAngle[SASpos]) / 2);
                } else {
                    midAngle = fix((((angle)) + hAngle[SASpos]) / 2);
                }
            } else {
                if (hAngle[SASpos] - fix(angle) > 180) {
                    midAngle = fix(((hAngle[SASpos] - 360) + fix(angle)) / 2);
                } else {
                    midAngle = fix(((hAngle[SASpos]) + fix(angle)) / 2);
                }
            }

            //For it to remember to slow down after passing the midAngle
            slowSpin = true;
            firstTurn = false;
        }

        //This part slows down the ship if it is going too fast 
        if (aSpeed > 1.5) {
            aSpeed -= 0.01;
        } else if (aSpeed < -1.5) {
            aSpeed += 0.01;
        } else if (leftORright((angle), midAngle) == 1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == -1) {
            aSpeed += 0.01;
            //Completed for when the ship rotation needs to slow down when travelling to the left 
        } else if (leftORright((angle), midAngle) == -1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == 1) {
            aSpeed -= 0.01;
            //Completed for when the ship rotation needs to slow down when travelling to the right 
        } else if (leftORright((angle), hAngle[SASpos]) == -1) {
            aSpeed -= 0.01;
            //Completed for when the ship rotation needs to rotate to the left 
        } else if (leftORright((angle), hAngle[SASpos]) == 1) {
            aSpeed += 0.01;
            //Completed for when the ship rotation needs to rotate to the right
        } else {
            //Resorts to slowing/stopping the rotation if all else does not meet any conditions
            if (aSpeed > hAngle[SASpos]) {
                aSpeed -= 0.01;
            } else if (aSpeed < hAngle[SASpos]) {
                aSpeed += 0.01;
            } else {
                aSpeed = 0;
            }
        }

        //Sets current rotation to find out later if the ship changes speeds 
        previousASpeed[0] = previousASpeed[1];
        previousASpeed[1] = aSpeed;
    }

    /**
     * Changes negative angles to positive angles, and reduces angles higher
     * than 360.
     *
     * @param angle The angle to see if it needs fixing
     * @return The fixed angle from 0 to 360
     */
    public double fix(double angle) {
        if (Math.signum(angle) == -1) {
            while (angle < 0) {
                angle += 360;
            }
            return angle;
        }
        if (angle > 360) {
            return angle % 360;
        }
        return angle;
    }

    /**
     * Returns which direction the rocket should turn for the shortest distance
     * to angle2
     *
     * @param angle The angle the rocket is pointing at
     * @param angle2 The desired pointed angle
     * @return The direction the rocket should turn
     */
    public int leftORright(double angle, double angle2) {
        //Sets as difference 1
        double angleL = fix((angle - angle2) % 360);
        //Sets as difference 2
        double angleR = fix((360 - angleL) % 360);
//        System.out.println("angleL: " + angleL + ", other: " + other);
        if (angleL < angleR) {
            return -1;
        } else if (angleL > angleR) {
            return 1;
        }
        return 0;
    }

    /**
     * Figures out what direction the rocket is traveling in given the X and Y
     * speeds 
     *
     * @param x The X component of the direction
     * @param y The Y component of the direction
     * @return The angle of the direction of travel
     */
    public static double travelAngle(double x, double y) {
        if (Math.signum(x) == 1 && Math.signum(y) == 1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 0);
        } else if (Math.signum(x) == 1 && Math.signum(y) == -1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 180);
        } else if (Math.signum(x) == -1 && Math.signum(y) == -1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 180);
        } else if (Math.signum(x) == -1 && Math.signum(y) == 1) {
            return (360 - (Math.abs(Math.toDegrees(Math.atan(x / y)))));
//##############################################################################    //Below are special cases that would throw divide by zero errors (or not)
        } else if (Math.signum(x) == 0 && Math.signum(y) == 1) {
            return 0;
        } else if (Math.signum(x) == 1 && Math.signum(y) == 0) {
            return 90;
        } else if (Math.signum(x) == 0 && Math.signum(y) == -1) {
            return 180;
        } else if (Math.signum(x) == -1 && Math.signum(y) == 0) {
            return 270;
        }
        return 0;
    }

    /**
     * Takes the passThrough array to process the user input from the Movement
     * class
     */
    public void controls() {

        if (controls[0] == 1) {             //W key - Increases Throttle slowly
            sThrottle += 0.005;
            if (sThrottle > 1) {
                sThrottle = 1;
            }
        } else if (controls[0] == -1) {     //The 'Z' key - Completely cuts throttle
            sThrottle = 0;
        }

        if (controls[1] == 1) {             //S key - Decreases throttle slowly
            sThrottle -= 0.005;
            if (sThrottle < 0) {
                sThrottle = 0;
            }
        } else if (controls[1] == 10) {     //The 'X' key - Sets to max throttle
            sThrottle = 1;
        }

        if (controls[2] == 1) {             //A key - Turns rocket to the left 
            if (aSpeed > -7) {
                aSpeed -= 0.01;
            }
        }

        if (controls[3] == 1) {             //D key - Turns rocket to the right 
            if (aSpeed < 7) {
                aSpeed += 0.01;
            }
        }

        if (controls[7] == 10) {            //Home key - Puts rocket in preset orbit 
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
            rXSpeed = 2243 * 1;
            rYSpeed = 0;
        }

        if (controls[8] == 10) {            //End key - fills fuel to 100%
            rFuel = 16000;
        }

    }
}
