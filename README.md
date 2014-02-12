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
   
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_projects.PNG?raw=true">
<br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_run1.PNG?raw=true">
<br>
   
   - Run *LaunchPad.java* as a Java application. This class provides a remote to start services on Munin peers. 
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_run2.png?raw=true">


   - This opens a textbox for naming the Munin space. Note that all devices within the peer-to-peer network should use the same id.
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_name_space.png?raw=true">

   - Select a space configuration. We currently support two window, dual monitor, and 3x2 display wall settings.   
<br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_space_configuration.png?raw=true">

   - This opens up a launch pad with the proper space configuration settings. 
<br><br>
<img src="https://github.com/karthikbadam/Munin/blob/master/instructions/munin_launchpad.png?raw=true">
