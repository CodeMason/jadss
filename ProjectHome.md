[![](http://jadss.googlecode.com/svn/trunk/jadss/imgs/logo.png)](http://jadss.googlecode.com)
## Java Advanced Distributed Sound System ##

This project, my PFE (Projet de Fin d'Études) or equivalently my Final Year Project, consists in a Distributed Sound System developed in Java.

This means a distributed system which will play sounds in four different PCs in a room as if it was only one, giving the impression of just a sound source instead of four.

See project [Wiki summary](http://code.google.com/p/jadss/wiki/Summary) for more information.

![http://jadss.googlecode.com/svn/trunk/jadss/imgs/diagram1.png](http://jadss.googlecode.com/svn/trunk/jadss/imgs/diagram1.png)


---



&lt;wiki:gadget url="http://www.ohloh.net/p/jadss/widgets/project\_factoids.xml" width="340" height="170" border="0" /&gt;


---


The problematic explained

  * Is volume control enough in order to achieve the results?
    * If not:
      * Controlling the sound delay based on distance.
        * Is a network solution fast enough?
        * Is Java (as a high-level language for this solution) fast enough, (should we treat this app as real-time?)
  * Which kind of sounds should it play?
    * MIDI ---> Easiest but works in a different way.
    * WAV / AU? ----> Seems easy enough, but they are 'heavy' file formats.
    * MP3?
  * Positioning
    * Micro + Delays?
    * IR? ---> maybe Wiimote + LEDS?
    * RFID?
    * Webcam + detection software.