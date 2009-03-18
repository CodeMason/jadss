package synth;


import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Marce
 * Sound class. It can be controlled in data bytes or time and offers a simple compensation of distance (in gain and delay).
 */
public class Sound{

	/** Sample data array */
	private byte data[];
	
	/**Size of array bytes for sampling*/
	private int playSampleCount = 262144;
	
	/**Default sampling rate. It'll change for file's default if used*/
	private float defaultRate = 44100;

	/**Size of each chunk that will be written in line
	 * If it's too small, sound will play with artifacts due to the little pauses between writings (empty buffer).
	 * If it's too big, the precision will decrease*/
	private int chunkSize = 4096;
	
	
	/**Sound offset in bytes used for both sounds, files and 'sampled'*/
	private int offset = 0;
	
	/**Default value for bitsPerSample*/
	private int bitsPerSample=8;
	
	/**Default value for reference Distance*/
	private float refDistance = .1f;

	/**Default value for distance*/
	private float distance = refDistance;
	
	
	/**Default value for reference gain, (the lower it is, the further distance we can simulate)*/
	private float refGain = -35.4174823761f;
	/**Default value for gain on the distance*/
	private float gain=refGain;

	/**Amount of bytes that will be played in a second*/
	private long bytesSec;
	
	/** Integer that shows the size of an audio frame, needed to prevent exceptions on write*/
	private int frameSize;	
	
	/*SYSTEM PARAMETERS*/
	/**Audio Input Stream that loads the file*/
	private AudioInputStream audioInputStream = null;
	/**Audio Format variable (contains sound format information)*/
	private AudioFormat format;
	
	/**Variable with all the info contained in the audio data line*/
	private DataLine.Info info;
	
	/**Line we will use to write on the sound card*/
	private SourceDataLine line;
	
	/**Control for the line Master gain*/
	private FloatControl gainControl;

	private int audioSize;


	
	/**
	 * Default constructor, generates a Sin wave sound sampled directly
	 */
	public Sound(){
		data = new byte[playSampleCount];
		audioSize = playSampleCount;
		format = new AudioFormat(defaultRate, bitsPerSample, 1, true, true);
	    info = new DataLine.Info(SourceDataLine.class, format);
	    
	    //Calculate time factor
	    // 
	    bytesSec = (long) defaultRate*bitsPerSample/8;
	    frameSize = 1;
	    
 	    double x;
	    for(int i=0;i<playSampleCount;i++){
	    	x= Math.sin(i/(4*Math.PI));
	    	data[i]=(byte) (((x+1)*256/2-128));
	    }
	    
	    try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, playSampleCount);
			gainControl = (FloatControl) line.getControl( FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(refGain);

	    } catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}
	    

	}
	

	/**
	 * Constructor with parameters we will use in case we need to play a sound file
	 * @param filename String that contains the path to the file we will use
	 */
	public Sound(String filename){

		File f = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
			audioSize = audioInputStream.available();
			format = audioInputStream.getFormat();
			frameSize = audioInputStream.getFormat().getFrameSize();

			bitsPerSample = format.getSampleSizeInBits();
			defaultRate = format.getSampleRate();

			//Calculate time factor
		    bytesSec = (long) (defaultRate*bitsPerSample/8);
		    
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		data = new byte[audioSize];
		int read=0;
		try {
			read = audioInputStream.read(data,0,audioSize);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		if(read!=audioSize){
			System.err.println("The file hasn't been read correctly");
			System.exit(1);
		}
		
		info = new DataLine.Info(SourceDataLine.class, format);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, playSampleCount);
			gainControl = (FloatControl) line.getControl( FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(refGain);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			audioInputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

	
	//GETTERS AND SETTERS	
	public int getPlaySampleCount() {
		return playSampleCount;
	}

	public void setPlaySampleCount(int playSampleCount) {
		this.playSampleCount = playSampleCount;
	}

	public float getDefaultRate() {
		return defaultRate;
	}
	
	public int getBitsPerSample(){
		return bitsPerSample;
	}

	public void setDefaultRate(float defaultRate) {
		this.defaultRate = defaultRate;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public long getBytesSec() {
		return bytesSec;
	}

	public float getRefGain() {
		return refGain;
	}

	public float getRefDistance() {
		return refDistance;
	}

	public void setRefDistance(float refDistance){
		this.refDistance = refDistance;
		this.setDistance(distance);
	}
	
	public void setRefGain(float refGain) {
		this.refGain = refGain;
		gainControl.setValue(this.refGain);
		//Update the gain and delay
		this.setDistance(distance);
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public float getDistance() {
		return distance;
	}
	
	

	public void setDistance(float meters){
		//VOLi = VOLRef - 19.93 log10(di / max(di = 1,..., 4))
		//TDi = [max(di=1,..,4) - di] / Vs
		
		//If distance is bigger than we can allow, we need to adjust it 
		// (maybe in future we should make refGain change dynamically along the distance) 
		if(meters<getMaximumDistance()){
			distance = meters;
			gain = (float)(19.93f*Math.log10(distance/refDistance));
			gain+=refGain;
			gainControl.setValue(gain);
		}
		else {
			System.err.println("Distance input is higher than maximum distance: Adjusting to maximum distance");
			distance = getMaximumDistance();
			gain = refGain+(float)(19.93f*Math.log10(distance));
			gainControl.setValue(gain);
		}
		
		float f =(getOffsetMicros()+(distance-refDistance)/343*1000000);
		setOffsetMicros((long)f);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		if(offset>=audioSize){
			System.err.println("Warning: Offset Bigger than audio size");
			offset=0;
		}
		else this.offset = offset;
	}
	
	public long getOffsetMicros(){
		float f= ((1000000.f*offset)/bytesSec);
		return (long) f;
	}

	public void setOffsetMicros(long off){
		float f = (off*bytesSec)/1000000.f;
		offset = (int) f;
	}
	
	public float getPrecision(){
		float result = ((float)chunkSize)/bytesSec;
		
		return result;
	}
	
	public float getMaximumDistance() {
		float max = gainControl.getMaximum();
		
		return (float) (refDistance*Math.pow( 10, ((max-refGain)/19.93)));
	}
	
	
	public void start(){
		line.start();
	}
	

	/**
	 * Play indefinitely a sound 
	 */
	public void loop(){
		if(offset%frameSize!=0){
			int pOffset = offset;
			offset+=offset%frameSize;
			System.err.println("Warning, the initial offset needs to be rounded to match an integral frame size. Old value: "+pOffset+" New value: "+ offset);
		}

		while(true){
			if(offset >= audioSize ){
				offset = 0;
			}
			offset+= line.write(data, offset,((offset+chunkSize)>=audioSize)?(audioSize-offset):chunkSize);	
		}
	}

	
	/**
	 * Play a sound during a certain amount of time. Uses play(start,end)
	 * @param uStart starting time expressed in microseconds
	 * @param uEnd end time expressed in microseconds
	 */
	public void playFromTo(long uStart, long uEnd){
		float f = (uStart*bytesSec)/1000000.f;
		int start = (int) f;
		f = (uEnd*bytesSec)/1000000.f;
		int end = (int) f;
		if(end%frameSize!=0){
			int pEnd = end;
			end-=end%frameSize;
			System.err.println("Warning, the end time needs to be rounded to match an integral frame size. Old value: "+pEnd+" New value: "+ end);
		}

		offset+=start;
		
		System.out.println("t0: ("+uStart+" , "+offset+")     te: ("+uEnd+" , "+end+") in (us, B)");
		this.play(start,end);
	}
	
	/**
	 * Play a sound between the bytes indicated.
	 * @param start
	 * @param end
	 */
	public void play(int start, int end){		
		if(offset%frameSize!=0){
			int pOffset = offset;
			offset+=offset%frameSize;
			System.err.println("Warning, the initial offset needs to be rounded to match an integral frame size. Old value: "+pOffset+" New value: "+ offset);
		}


		//playOffset will provide a offset counter
		int playOffset = offset;
		int toWrite = chunkSize;
		int written = 0;

		//We need to write just until we reach end-chunkSize, so we could write the difference later
		while(playOffset<(end-chunkSize)){
			//If we're about to get over the audio size, we reset the actual offset
			if(offset >= audioSize ){
				offset = 0;
				toWrite = chunkSize;
			}
			//If we're on the end of the file, we should write just the last chunk of sound
			else if((offset+chunkSize)>=audioSize){
				toWrite = (audioSize-offset);
			}

			//Writing on the file
			written = line.write(data, offset,toWrite);
			offset+=written;
			playOffset+=written;
		}
		toWrite = end-playOffset;
		if((toWrite%frameSize)!=0){
			System.err.println("Approximation needed in writing: ("+toWrite+") is not an integral number for frameSize: "+frameSize);
			toWrite-=(toWrite%frameSize);
		}
		//We need to write the last chunk (smaller than chunkSize)
		written = line.write(data, offset,toWrite);
		playOffset += written;
		offset=playOffset;
		line.drain();
	}
	
	
	/**
	 * Play a complete sound, from the beginning to the end. 
	 */
	public void play(){
		if(offset%frameSize!=0){
			int pOffset = offset;
			offset+=offset%frameSize;
			System.err.println("Warning, the initial offset needs to be rounded to match an integral frame size. Old value: "+pOffset+" New value: "+ offset);
		}

		while(offset<(audioSize-chunkSize)){
				line.write(data, offset, chunkSize);
				offset+=chunkSize;
		}
		offset += line.write(data, offset, audioSize-offset);
		line.drain();
	}

	/**
	 * Close the sound system
	 */
	public void close() {		
		line.close();
	}	
}
