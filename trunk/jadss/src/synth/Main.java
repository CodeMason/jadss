/**
 * 
 */
package synth;

/**
 * @author Marcelino Lopez
 * Main test program
 */
public class Main {
		
	/**
	 * @param args String array where line arguments are placed 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		long i,ini,e,end;
		int times;
		
		Sound s = new Sound("sounds/stillalive.wav");
		s.start();
		s.setDistance(6.02f);
		System.out.println("AudioSize: "+(s.getAudioLength()/1000)+" ms ("+ s.getAudioSize()+" B)  Prec: "+ s.getPrecision()+" s");
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B), MAX: "+s.getMaximumDistance()+" m");		
		System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
		System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
		
		long dur   = 10000;
		ini = System.currentTimeMillis();
		for(times=0;times<1000;times++){
			i = System.currentTimeMillis();
			
			s.playDuringMicros(dur*times, dur);
			e = System.currentTimeMillis();
			System.out.println("Duration: "+(dur)/1000.f+" Real duration: "+((e-i))+" ms\t Percentage: "+ (((float)(e-i))/(dur)*100000)+" %");
			System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");
		}
		end = System.currentTimeMillis();
		System.out.println("Global: "+(dur*times)/1000.f+" Actual: "+(end-ini)+" ms === "+(((float)(end-ini))/(dur*times)*100000)+"%");

		System.out.println("Bye!");
		s.close();
	}
	
}
