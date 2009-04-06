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
		test1();
	}
	
	public static void test1(){
		long i,ini,e,end;
		int times;
		
		Sound s = new Sound("sounds/stillalive.wav");
		s.start();
		s.setDistance(6.032f);
		
		
		System.out.println("AudioSize: "+(s.getAudioLength()/1000)+" ms ("+ s.getAudioSize()+" B)  Prec: "+ s.getPrecision()+" s");
		System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B), MAX: "+s.getMaximumDistance()+" m");		
		System.out.println("Play at: "+s.getDistance()+ " m (" + s.getGain()+ " dB)");
		System.out.println("Bytes Per second: "+s.getBytesSec()+" B/s ,  bits per sample: "+s.getBitsPerSample()+" bps , chunksize: "+s.getChunkSize()+" B");
		
		for(int j=32;j<=512;j*=2){
			s.setChunkSize(16*j);
			int dur   = 10000;//s.getChunkSize();
			ini = System.currentTimeMillis();
			for(times=0;times<400;times++){
				i = System.currentTimeMillis();
				
				s.playDuringMicros(dur*times, dur);
				//s.playDuring(dur*times, dur);
				e = System.currentTimeMillis();
	//			System.out.println("Duration: "+(dur)/1000.f+" ms Real duration: "+((e-i))+" ms\t Percentage: "+ (((float)(e-i))/(dur)*100000)+" %");
	//			System.out.println("Offset: "+s.getOffsetMicros()+" us ("+s.getOffset()+" B)");
			}
			end = System.currentTimeMillis();/*" Global: "+(dur*times)/1000.f+" ms Actual: "+(end-ini)+*/
			System.out.println(s.getChunkSize()+"\t"+(((float)(end-ini))/(dur*times)*100000));
		}
		

		System.out.println("Bye!");
		s.close();
	}
}
