/**
 * 
 */
package synth;

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
			Sound s = new Sound("sounds/alert.wav");
			s.start();
			s.setRefGain(0);
			//s.setDistance(4.5f);
			System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");
			s.setOffsetMicros(200*1000);
			System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");
			
			s.play();

			System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
			System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
			System.out.println("Precision: "+ s.getPrecision() * 1000 +" ms");
			
			//System.out.println("Pause!");
			//Thread.sleep(500);
			//s.play();
			s.close();
			
	
		System.out.println("Bye!!");
	}
	
}
