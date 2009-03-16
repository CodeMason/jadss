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
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Sound s = new Sound("sounds/stillalive.wav");
		s.start();
		s.setChunkSize(512);
		s.setDistance(10f);
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B), MAX: "+s.getMaximumDistance()+" m");		
		System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
		System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
		//s.playFromTo(0, 2000000);
		s.play();
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");		

		s.close();

	}
	
}
