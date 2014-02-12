Munin
=====

A peer-to-peer software framework for ubiquitous visual analytics. 

### To build:

Prerequisites (both Windows and Unix):

    * Java JDK 6 or higher
    * Eclipse
    
    
How to download:

    * git clone https://github.com/karthikbadam/Munin.git


How to compile and run:
   
   - Import *Munin* and *MuninDemos* projects into eclipse using **File > Import > Existing Projects into Workspace**.
<br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_projects.PNG?raw=true">
<br>

   - Run *LaunchPad.java* as a Java application. This class provides a remote to start services on Munin peers. 
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_run2.png?raw=true">


   - This opens a textbox for naming the Munin space. Note that all devices within the peer-to-peer network should use the same id.
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_name_space.png?raw=true">

   - Select a space configuration. We have provided samples for two window, dual monitor, and 3x2 display wall settings in the *configs/* folder.   
<br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_space_configuration.png?raw=true">

   - This opens up a launch pad with the proper space configuration settings. 
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_launchpad.png?raw=true">

   - The menu on the left shows the list of services (some of which are part of Munin while others are from Munin Demos). These include a display service, rendering service, ImageStitch (splits an image across peers based on space configuration), ZoomPanner, Scribble, MultiDim (for building multi-dimensional data visualizations) and more!. Staring the display service, the rendering service, and then a user developed service is the logical sequence for executing the examples. The **surfaces** menu provides choices for the display configuration of the devices (peers).

Resources for Newcomers
---
  - [The Wiki](https://github.com/karthikbadam/munin/wiki)
  - [How to run a Munin service](https://github.com/karthikbadam/Munin/wiki/How-to-run-a-service)   
