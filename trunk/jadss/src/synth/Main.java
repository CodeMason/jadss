/**
 * 
 */
package synth;

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
		s.setChunkSize(8192);
		s.setDistance(5f);
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B), MAX: "+s.getMaximumDistance()+" m");		
		System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
		System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
		long i,e;
		i = System.nanoTime();
		long dur= 5000000;
		s.playFromTo(0,dur);
		e = System.nanoTime();
		System.out.println("Real duration: "+((e-i)/1000)+" us\t Percentage: "+ (((e-i)/1000.f)/dur*100)+" %");
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");		

		s.close();

	}
	
}
