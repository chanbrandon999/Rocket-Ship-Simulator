Brandon's ISU
 • This program simulates piloting a rocket into space. The user pilots a spacecraft into space using keyboard controls to control the thrust and rotation of the rocket. This simulation attempts to simulate the realistic physics that would act upon the rocket during flight including thrust, drag and gravity. The rocket would be placed upon a planet in which they player can get the rocket to achieve orbit. 

Features:
 • The rocket has a thrust value of 1500000 N, a default mass of 28750 kg, and can make it point to four different headings based around the direction of travel, called Stability Assist System (or SAS). There is 16000 kg of fuel and is used up at a rate of 492.74 kg / second.
 • The planet is similar to Earth. It has a radius of 600000m, surface gravity of 9.81 m/s^2, and the atmosphere starts at 100.13, diminishing exponentially as the altitude increases, stopping at 70000m.
 • The gravity should always be pointing to the center of the planet. The force of gravity diminishes as the rocket travels further away from the planet.
 • The drag should always be opposite to the center of motion (unless in a vacuum). The drag also decreases as the altitude of the rocket increases. Drag will be zero above 70000m.
 • The thrust points to wherever the player wishes. It will be limited to the throttle setting the user chooses. 

GUI features:
 • There is a navigation circle at the bottom of the screen with two arrows to show the current heading and the direction of travel. A throttle arrow shows the current throttle setting, and in the middle of the ball, a symbol should appear if one of the SAS settings are applied. There are speed and altitude boxes where the speed and altitude are displayed, as well as meters to show the remaining fuel and the atmospheric pressure where the rocket is at. Time warping triangles will show up in the top left to show the relative speed of the time warping. A minimap will also be available to show the position of the rocket relative to the planet. 
 • The planet will be shown at the bottom of the screen if the rocket is close enough to see it. It will travel further away as the rocket goes higher. The atmosphere will start out as a sky blue, but will get darker as the rocket travels further into space. Eventually the colour will be black. Stars will also be shown to pass through the sky and move around as the rocket moves around. 

Ship Controls:
W for throttle up
S for throttle down
A for turning left 
D for turning right 
Z for minimum throttle (0)
X for maximum throttle 

SAS controls:
1 for keeping a stable heading 
2 for pointing prograde (the direction of travel)
3 for pointing to radial in (to the right of the direction of travel) 
4 for pointing to retrograde (opposite of the direction of travel)
5 for pointing to radial in (to the left of the direction of travel) 

Extra controls:
ESC for pause menu
. for increasing time warping 
, for decreasing time warping
M for minimap/zoomed out map 
HOME for a pre-set orbit
END for maximum fuel 
