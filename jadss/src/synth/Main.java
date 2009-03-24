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
		s.setDistance(12);
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B), MAX: "+s.getMaximumDistance()+" m");		
		System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
		System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
		long i,e;
		i = System.currentTimeMillis();
		long dur   = 10000000;
		long start = s.getOffsetMicros();
		s.playFromTo(start,dur);
		e = System.currentTimeMillis();
		System.out.println("Duration: "+(dur-start)/1000.f+" Real duration: "+((e-i))+" us\t Percentage: "+ (((float)(e-i))/(dur-start)*100000)+" %");
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");		
		Thread.sleep(1000);
		
		System.out.println("Bye!");
		s.close();
	}
	
}
