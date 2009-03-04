/**
 * 
 */
package synth;



import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import scene.Scene;

/**
 * @author Marcelino Lopez
 *
 */
public class Main {
		
	/**
	 * @param args String array where line arguments are placed 
	 * @throws MidiUnavailableException 
	 * @throws InvalidMidiDataException 
	 * @throws InterruptedException 
	 * @throws LineUnavailableException 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Hi");
			Sound s = new Sound("sounds/mgs alert.wav");
			s.start();
			s.play();
			s.close();
	
		System.out.println("Bye!!");
	}
	
}
