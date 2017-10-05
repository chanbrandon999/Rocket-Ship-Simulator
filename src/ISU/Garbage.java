//##########################################GARBAGE CODE#########################################
//    @Override
//    public void keyPressed(KeyEvent e) {
//        System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
//        int key = e.getKeyCode();
//
//        if (key == KeyEvent.VK_LEFT) {
//            System.out.println("Left");
//            rSpeed += -1;
//        }
//
//        if (key == KeyEvent.VK_RIGHT) {
//            System.out.println("Right");
//            rSpeed += 1;
//        }
//
//        if (key == KeyEvent.VK_UP) {
//            System.out.println("Up");
//            dy += -1;
//        }
//
//        if (key == KeyEvent.VK_DOWN) {
//            System.out.println("Down");
//            dy += 1;
//        }
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e
//    ) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//}

//                double pythagsTest = Math.sqrt(Math.pow(xPos - xPos[k], 2) + Math.pow(yPos - yPos[k], 2));    //Calculates distance between two balls. 
/**
 * Trashed drag profile
 */
////        if (dragXc > Math.pow(dragX, 2) dragX * Math.sqrt(dragX * 10) && rSpeed != 0) {
//        if (dragXc > dragX * Math.sqrt(dragX * 10) && rSpeed != 0) {
//            dragX++;
//            dragXc = 0;
////            System.out.println("Moving xPos");
//            xPos += rSpeed;
//
//        } else if (rSpeed != 0) {
//
//            if (dragXc > 30000) {
////                dragXc += dragXc);
//                rSpeed--;
//                if (rSpeed > 0) {
//                    rSpeed--;
//                } else if (rSpeed < 0) {
//                    rSpeed++;
//                } else if (rSpeed == 0) {
//                    dragXc = 1;
//                    dragX = 0;
//                }
//            } else {
//                dragXc += 1000;
//            }
////            System.out.println(dragXc * 1000 + ", " + Math.pow(dragX, 2) * Math.sqrt(dragX * 10) + ", " + dragX);
//
//        }
//
//        if (dragYc > dragY * Math.sqrt(dragY * 10) && dy != 0) {
//            dragY++;
//            dragYc = 0;
////            System.out.println("Moving yPos");
//            yPos += dy;
//
//        } else if (dy != 0) {
//
//            if (dragYc > 30000) {
////                dragXc += dragXc);
//                dy--;
//                if (dy > 0) {
//                    dy--;
//                } else if (dy < 0) {
//                    dy++;
//                } else if (dy == 0) {
//                    dragYc = 1;
//                    dragY = 0;
//                }
//            } else {
//                dragYc += 1000;
//            }
////            System.out.println(dragYc * 1000 + ", " + Math.pow(dragY, 2) * Math.sqrt(dragY * 10) + ", " + dragY);
//
//        }
//#############Linear drag
//        if (dy > 0) {
//            dy += -(drag);
//        } else if (dy < 0) {
//            dy += (drag);
//        }
//#################Drawing a square instead
//        g.setColor(Color.red);
//        Rectangle myShape = new Rectangle(-radius, -radius, radius * 2, radius * 2);
//        g2d.translate(xPos / 1000, yPos / 1000);
//        g2d.rotate(Math.toRadians(aThrottle));
//        g2d.draw();
//        g2d.fill(myShape);
//######################Other garbage tests
////        double locationX = scale * rPic.getWidth() / 2;
////        double locationY = scale * rPic.getHeight() / 2;
//        double locationX = getWidth() / 2;
//        double locationY = getHeight() / 2;
//
//        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(aThrottle), locationY, locationY);
////        tx.scale(scale, scale);
//        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
////        g2d.drawImage(op.filter(rPic, null), getWidth() - rPic.getHeight(), getHeight() - rPic.getHeight(), null);
//        g2d.drawImage(op.filter(rPic, null), getWidth() - rPic.getHeight(), getHeight() /2 - rPic.getHeight(), null);
//###############For square window 
//        if (xPos < 00000) {                                             //Detects if the ball tries to cross the left perimeter
////            rSpeed = Math.abs(rSpeed);                                        //absolute value 
//            xPos = (getWidth() - 00000) * 1000;
//        }
//        if (xPos > (getWidth() - 00000) * 1000) { //Detects if the ball tries to cross the right perimeter
//            //            rSpeed = -Math.abs(rSpeed);
//            xPos = 0;
//        }
//
//        if (yPos < 00000) {                                             //Detects if the ball tries to cross the top perimeter
////            dy = Math.abs(dy);
//            yPos = (getHeight() - 00000) * 1000;
//
//        }
//        if (yPos > (getHeight() - 00000) * 1000) {                               //Detects if the ball tries to cross the bottom perimeter
////            dy = -Math.abs(dy);
//            yPos = 0;
//        }
//#####################Old Controls
//            if (key == KeyEvent.VK_LEFT) {
//                System.out.println("Left");
////                    dragX = 1;
//
//                if (aSpeed > -10) {
//                    aSpeed--;
//
//                }
//
//            }
//
//            if (key == KeyEvent.VK_RIGHT) {
//                System.out.println("Right");
//
//                if (aSpeed < 10) {
//                    aSpeed++;
//                }
//            }
//
//            if (key == KeyEvent.VK_UP) {
//                System.out.println("Up");
////                rSpeed += -1000;
//
//                sThrottle += 0.1;
//                if (sThrottle > 1) {
//                    sThrottle = 1;
//                }
//            }
//
//            if (key == KeyEvent.VK_DOWN) {
//                System.out.println("Down");
////                rSpeed = 000;
//
//                sThrottle -= 0.1;
//                if (sThrottle < 0) {
//                    sThrottle = 0;
//                }
//
//            }
//###########Planet Display
//        pYpos = (long) ((xPos) / 1000 * 1 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) (((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) ((((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight())) / 2) * zScale);
//        g.fillOval(pXpos, pYpos, pScale, pScale);
