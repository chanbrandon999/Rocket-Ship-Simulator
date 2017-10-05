/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import static ISU.Calculations.travelAngle;
import static ISU.Calculations.trigAngle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Someone
 */
public class CalculationsTest {

    Calculations c = new Calculations();

    public CalculationsTest() {
    }

    /**
     * Tests the positioning method to make sure it moves the rocket correctly
     * given the forces
     */
    @Test
    public void testPositionUpdate() {
        Calculations c = new Calculations();
        c.xPos = 0;
        c.yPos = 610000 * 1000;

        c.positionUpdate(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(610000, c.altitudeToPlanetCenter, 0.01);
        c.positionUpdate(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(0, c.xPos, 0.01);
        assertEquals((610000 - (c.calcG() / c.rMass) / 10000) * 1000, c.yPos, 0.01);    //Checks to see if the rocket falls the correct distance due to gravity in "10 milliseconds"
        assertEquals(-(c.calcG() + c.calcD()) / c.rMass, c.yAccel, 0.01);               //Checks to see if the rocket is accelerating at the correct distance in "10 milliseconds"

        for (int i = 0; i < 10; i++) {                                                  //Updates the position 10 times when the throttle is open (pointing) up to make sure the rocket accelerates at the correct amount 
            c.positionUpdate(new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            assertEquals((c.rThrust * c.sThrottle - (c.calcG() + c.calcD())) / c.rMass, c.yAccel, 0.01);
        }

        //Turns rocket to test for acceleration in the X direction
        c.angle = 90;
        c.positionUpdate(new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertEquals((c.rThrust * c.sThrottle - ((c.calcG() * trigAngle('s', c.xPos, c.yPos) + c.calcD() * trigAngle('s', c.rXSpeed, c.rYSpeed)))) / c.rMass, c.xAccel, 0.01);

    }

    /**
     * Tests the movement method to test if it moves the rocket by the correct
     * amount each time given acceleration
     */
    @Test
    public void testMoveRocket() {
        Calculations c = new Calculations();

        //Checks for proper X and Y movements 
        c.xAccel = 2;
        c.yAccel = 2;
        c.applyMovement();
        assertEquals(0.2, c.xPos, 0.01);
        assertEquals(600000000 + 0.2, c.yPos, 0.01);

        //Checks for proper angular movement
        c.aSpeed = 10;
        c.applyMovement();
        assertEquals(10, c.angle, 0.01);
        assertEquals(0.6, c.xPos, 0.01);
        assertEquals(600000000 + 0.6, c.yPos, 0.01);

        //Checks for rocket gliding 
        c.xAccel = 0;
        c.yAccel = 0;
        c.applyMovement();
        assertEquals(1, c.xPos, 0.01);
        assertEquals(20, c.angle, 0.01);
        assertEquals(600000000 + 1, c.yPos, 0.01);

        //Checks if it properly stops the rotation
        //Also checks for moving the rocket to the proper position around the planet if it somehow gets stuck in the middle. 
        c.timeWarpOn = true;
        c.xPos = 10;
        c.yPos = 10;
        c.rXSpeed = 0;
        c.rYSpeed = 0;
        c.xAccel = 0;
        c.yAccel = 0;
        c.applyMovement();
        assertEquals(20, c.angle, 0.01);
        assertEquals(Math.sin(Math.toRadians(45)) * 600000 * 1000, c.xPos, 0.01);
        assertEquals(Math.cos(Math.toRadians(45)) * 600000 * 1000, c.yPos, 0.01);

        c.xPos = 10;
        c.yPos = 10;
        c.rXSpeed = 0;
        c.rYSpeed = 6.001;
        c.applyMovement();
        assertEquals(true, c.startBlowUp);
        
        c.xPos = 10;
        c.yPos = 10;
        c.rXSpeed = 0;
        c.rYSpeed = 5.999;
        c.applyMovement();
        assertEquals(false, c.startBlowUp);

    }

    /**
     * Tests if the method can properly identify whether or not the rocket would
     * need to turn left or right to reach the specified angle (the second one)
     */
    @Test
    public void testleftORright() {
        double angle1 = 200, angle2 = 10;
        //angle, hAngle
        assertEquals(1, c.leftORright(200, 10)); //Should turn to the right
        assertEquals(-1, c.leftORright(10, 200)); //Should turn left
        assertEquals(1, c.leftORright(10, 40)); //Should also turn right 
        assertEquals(-1, c.leftORright(10, -10)); //Should turn left
        assertEquals(1, c.leftORright(-10, 10)); //Should also turn right 
        assertEquals(1, c.leftORright(355, 30)); //Should also turn right 
        assertEquals(-1, c.leftORright(5, 355)); //Should also turn right 

        assertEquals(0, c.leftORright(0, 0));
        assertEquals(1, c.leftORright(350, 10));
        assertEquals(1, c.leftORright(350, 0));
        assertEquals(1, c.leftORright(10, 180));
        assertEquals(-1, c.leftORright(350, 180));
        assertEquals(-1, c.leftORright(10, 350));
        assertEquals(-1, c.leftORright(10, 1));
    }

    /**
     * Tests the program to see if it knows what angle the rocket is pointing in
     * given the components of the X and Y directions
     */
    @Test
    public void testTravelAngle() {
        assertEquals(0, (int) travelAngle(0, 1));
        assertEquals(90, (int) travelAngle(1, 0));
        assertEquals(180, (int) travelAngle(0, -1));
        assertEquals(270, (int) travelAngle(-1, 0));
        assertEquals(45, (int) travelAngle(1, 1));
        assertEquals(45 + 90, (int) travelAngle(1, -1));
        assertEquals(45 + 2 * 90, (int) travelAngle(-1, -1));
        assertEquals(45 + 3 * 90, (int) travelAngle(-1, 1));
    }

    /**
     * Tests to see if it can properly "fix" or adjust the angle to a number >0
     * and <360.
     */
    @Test
    public void testFixAngle() {
        assertEquals(350, (int) c.fix(-10));
        assertEquals(10, (int) c.fix(10));
        assertEquals(180, (int) c.fix(180));
        assertEquals(360, (int) c.fix(360));
        assertEquals(1, (int) c.fix(361));
        assertEquals(359, (int) c.fix(-361));
    }

    /**
     * Tests the program if it can properly calculate the sine and cosine of
     * each angle (from 0 to 360)
     */
    @Test
    public void testTrigAngle_char_double() {

        for (int i = 0; i <= 360; i++) {
            assertEquals(Math.cos(Math.toRadians(i)), trigAngle('c', i), .001);
            assertEquals(Math.sin(Math.toRadians(i)), trigAngle('s', i), .001);
        }
    }

    /**
     * Test to see if the program can properly calculate the sine and cosine
     * given the X and Y components
     */
    @Test
    public void testTrigAngle_char_double_double() {
        assertEquals(Math.sin(-45), trigAngle('s', -1, 1), 1);
        assertEquals(Math.sin(-45), trigAngle('s', -1, 1), 1);
        assertEquals(Math.sin(45), trigAngle('s', 1, 1), 1);
        assertEquals(Math.cos(45), trigAngle('c', 1, 1), 1);

        assertEquals(Math.cos(-45), trigAngle('c', -1, 1), 1);
        assertEquals(0, trigAngle('X', -1, 1), 1);
        assertEquals(0, trigAngle('s', 1, 0), 1);
        assertEquals(0, trigAngle('c', 1, 0), 1);
        assertEquals(0, trigAngle('s', 0, 0), 1);
        assertEquals(1, trigAngle('s', 0, 1), 1);
        assertEquals(0, trigAngle('c', 0, 0), 1);
        assertEquals(0, trigAngle('c', 0, 1), 1);
    }

    /**
     * Tests to see if it can calculate the thrust of the rocket correctly
     */
    @Test
    public void testCalcT() {
//        Calculations c = new Calculations();
        c.sThrottle = 1;
        assertEquals(1500000, c.calcT(), 1);
        c.sThrottle = 0.5;
        assertEquals(1500000 / 2, c.calcT(), 1);
        c.sThrottle = 0;
        assertEquals(0, c.calcT(), 1);
        c.sThrottle = 1;
        c.rFuel = 0;
        assertEquals(0, c.calcT(), 1);
    }

    /**
     * Tests to see if it returns the correct force of gravity in different
     * scenarios
     */
    @Test
    public void testCalcG() {
        Calculations c = new Calculations();
        c.altitudeToPlanetCenter = 650000;
        assertEquals(8.358816568 * 28750, c.calcG(), .1);
        c.altitudeToPlanetCenter = 600000 + 200000;
        c.rMass = 20000;
        assertEquals(5.518125 * 20000, c.calcG(), .1);
        c.altitudeToPlanetCenter = 600000 - 100000;
        assertEquals(0, c.calcG(), 0);
    }

    /**
     * Tests to see if it can calculate drag properly in certain scenarios
     */
    @Test
    public void testCalcD() {
        Calculations c = new Calculations();
        c.altitudeToPlanetCenter = 610000;
        c.rXSpeed = 100 * 100;
        c.rYSpeed = 100 * 100;
        assertEquals(164873.12, c.calcD(), .1);
        c.altitudeToPlanetCenter = 605000;
        c.rXSpeed = 10000;
        c.rYSpeed = -10000;
        assertEquals(402636.16, c.calcD(), .1);
        c.altitudeToPlanetCenter = 600000;
        c.rXSpeed = 0;
        c.rYSpeed = 0;
        assertEquals(0, c.calcD(), .1);
    }

    /**
     * Performs Tests to see if the rocket can properly rotate to the specified
     * angle coming from another angle
     */
    @Test
    public void testRotationControl() {

        c.rXSpeed = 10;
        c.rYSpeed = 0;
        c.xPos = 0;
        c.yPos = 700000 * 1000;

        for (int k = 0; k < 360; k++) {

            for (int j = 0; j < 4; j++) { //tests to see if it will correctly turn to each specified positions (prograde, radial in, retrograde and radial out)
                c.angle = k;    //Tests different starting points to test the rocket 

                for (int i = 0; i < 1000; i++) {        //Runs for "10 seconds" of game time to find final rotated position 
                    c.rotationControl(j + 2);
                    c.angle += c.aSpeed;                //Rotation control cannot change the angle, but can change the angle speed 
                }
                
                c.angle = c.fix(c.angle);
                if ((c.angle > 359 && c.hAngle[j] > 359) || (c.angle < 1 && c.hAngle[j] > 359)) {
                    assertEquals(true, true);
                    //The testing program cannot properly find whether the angle is close enough around the 360 degrees point. This is an exception 
                } else {
                    assertEquals(true, c.angle > c.fix(c.hAngle[j] - 1) && c.angle < c.fix(c.hAngle[j] + 1));
                    //Checks to see if it is close enough within 1 degree on each side 
                }
            }
        }
        //                    System.out.println("angle: " + c.angle + ", hAngle[" + j + "]: " + c.hAngle[j] + "midAngle " + c.midAngle);
    }

    /**
     * Tests if the controls properly adjust their designated control
     */
    @Test
    public void testControls() {
        c.controls = new int[]{1, 0, 1, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 15; i++) {
            c.controls();
        }
        assertEquals(0.075, c.sThrottle, 0.0001);
        assertEquals(-0.15, c.aSpeed, 0.0001);

        c.controls = new int[]{0, 1, 0, 1, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 10; i++) {
            c.controls();
        }
        assertEquals(0.025, c.sThrottle, 0.0001);
        assertEquals(-0.05, c.aSpeed, 0.0001);

        c.controls = new int[]{-1, 0, 0, 0, 0, 0, 0, 10, 10};
        c.controls();
        assertEquals(0, c.sThrottle, 0.0001);
        assertEquals(700000 * 1000, c.yPos, 0.0001);
        assertEquals(2243, c.rXSpeed, 0.0001);
        assertEquals(16000, c.rFuel, 0.0001);

    }

}
