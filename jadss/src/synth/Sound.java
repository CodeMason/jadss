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
	/** Default value for ChunkSize	 */
	public static int DEFAULT_CHUNKSIZE = 4096;
	/** Default value for lineSize	 */
	public static int DEFAULT_LINESIZE = 4096;

	/** Sample data array */
	private byte data[];

	/**Size of each chunk that will be written in line
	 *    If it's too small, sound will play with artifacts due to the pauses between writings (buffer underrun).
	 *    If it's too big, the precision will decrease*/
	private int chunkSize = DEFAULT_CHUNKSIZE;
	
	/**Size of array bytes for sampling*/
	private int lineSize = chunkSize;
	
	/**Sound offset in bytes used for both sounds, files and 'sampled'*/
	private int offset = 0;
	
	/**Default sampling rate. It'll change for file's default if used*/
	private float defaultRate = 44100;
	
	/**Default value for bitsPerSample*/
	private int bitsPerSample=8;

	/**Amount of bytes that will be played in a second*/
	private long bytesSec;
	
	/** Integer that shows the size of an audio frame, needed to prevent exceptions on write*/
	private int frameSize;	
	
	/** Size of audio file (or sampled audio size, if it's not a file)*/
	private int audioSize;
	
	
	/**Default value for reference Distance*/
	private float refDistance = 0.1f;
	
	/**Default value for distance*/
	private float distance = refDistance;
	
	/**Default value for reference gain, (the lower it is, the further distance we can simulate)*/
	private float refGain = -35.417482376099f;
	/**Default value for gain on the distance*/
	private float gain=refGain;


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


	/**
	 * Default constructor, generates a Sin wave sound created mathematically.
	 */
	public Sound(){
		data = new byte[(int)(defaultRate)];
		audioSize = data.length;
		format = new AudioFormat(defaultRate, bitsPerSample, 1, true, true);
	    info = new DataLine.Info(SourceDataLine.class, format);
	    
	    //Calculate time factor
	    // 
	    bytesSec = (long) defaultRate*bitsPerSample/8;
	    frameSize = 1;
	    
 	    double x;
	    for(int i=0;i<audioSize;i++){
	    	x= Math.sin(i/(4*Math.PI));
	    	data[i]=(byte) (x*128);
	    }
	    
	    try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, lineSize);
			gainControl = (FloatControl) line.getControl( FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(refGain);

	    } catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	/**
	 * Constructor with parameters we will use in case we need to play a sound file.
	 * @param filename String that contains the path to the file we will use.
	 */	
	public Sound(String filename){
		this(filename,DEFAULT_CHUNKSIZE,DEFAULT_LINESIZE);
	}
	
	/**
	 * Constructor with parameters we will use in case we need to play a sound file.
	 * @param filename String that contains the path to the file we will use.
	 * @param cSize int containing the chunk buffer size desired.
	 * @param lSize int containing the line size desired.
	 */
	public Sound(String filename, int cSize, int lSize){
		this.chunkSize = cSize;
		this.lineSize = lSize;
		File f = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
			audioSize = audioInputStream.available();
			format = audioInputStream.getFormat();
			//System.err.println(format.toString());
			frameSize = format.getFrameSize();

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
			line.open(format, lineSize);
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
	
	/**
	 * @return Size of the audio sample in bytes.
	 */
	public int getAudioSize() {
		return audioSize;
	}
	
	/**
	 * @return Length of audio expressed in microseconds.
	 */
	public long getAudioLength(){
		return (long) ((1000000.f*audioSize)/bytesSec);
	}

	/**
	 * @return Size of line buffer.
	 */
	public int getLineSize() {
		return lineSize;
	}


	/**
	 * @return Default audio rate (size in bytes of sampled file).
	 */
	public float getDefaultRate() {
		return defaultRate;
	}
	
	/**
	 * @return Number of bits in a sample.
	 */
	public int getBitsPerSample(){
		return bitsPerSample;
	}


	/**
	 * @return Chunk buffer size. 
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * @param chunkSize new size of chunk buffer. 
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * @return Bytes Per Second.
	 */
	public long getBytesPerSec() {
		return bytesSec;
	}

	/**
	 * @return Reference Audio Gain.
	 */
	public float getRefGain() {
		return refGain;
	}

	/**
	 * @return Reference Audio Distance.
	 */
	public float getRefDistance() {
		return refDistance;
	}

	/**
	 * @param refDistance Float containing the new reference distance. Calls setDistance with actual distance.
	 */
	public void setRefDistance(float refDistance){
		this.refDistance = refDistance;
		this.setDistance(distance);
	}
	
	/**
	 * @param refGain Float containing the new reference gain. Calls setDistance with actual distance.
	 */
	public void setRefGain(float refGain) {
		this.refGain = refGain;
		gainControl.setValue(this.refGain);
		//Update the gain and delay
		this.setDistance(distance);
	}	
	
	/**
	 * @return Calculated gain in the actual distance.
	 */
	public float getGain() {
		return gain;
	}

	/**
	 * @param gain Float containing the new value for the actual gain.
	 */
	public void setGain(float gain) {
		this.gain = gain;
		gainControl.setValue(gain);
	}

	
	/**
	 * @return Actual distance from sound source.
	 */
	public float getDistance() {
		return distance;
	}
	
	

	/**
	 * Set the distance (from the sound source) to be simulated.
	 * @param meters New distance expressed in meters.
	 */
	public void setDistance(float meters){
		//VOLi = VOLRef - 19.93 log10(di / max(di = 1,..., 4))
		//TDi = [max(di=1,..,4) - di] / Vs
		
		//If distance is bigger than we can allow, we need to adjust it 
		// (maybe in future we should make refGain change dynamically along the distance) 
		if(Math.abs(meters-distance)>0.001){
			if(meters<getMaximumDistance()){
				distance = meters;
				gain = (float)(19.93f*Math.log10(distance/refDistance));
				gain+=refGain;
				gainControl.setValue(gain);
			}
			else {
				System.err.println("Distance input is higher than maximum distance: Adjusting to maximum");
				distance = getMaximumDistance();
				gain = gainControl.getMaximum();
				gainControl.setValue(gain);
			}
			float f =(getOffsetMicros()+(distance-refDistance)/343*1000000);
			setOffsetMicros((long)f);
		}
	}

	/**
	 * @return Position within the audio, expressed in bytes.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Establish the new initial playing position offset.
	 * @param off new offset.
	 */
	public void setOffset(int off) {
		if(this.offset!=off){
			this.offset = off;
			if(off>=audioSize){
				this.offset=off%audioSize;
				System.err.println("Warning: Offset Bigger than audio size:( "+ off+" % "+audioSize+" = "+this.offset+" )");
			}
			if(off%frameSize!=0){
				this.offset = this.offset +(this.offset%frameSize);
				System.err.println("Warning, offset needs to match an integral frame size. Old value: "+off+" New value: "+ this.offset);
			}
		}
	}
	
	/**
	 * @return offset from starting position in microseconds.
	 */
	public long getOffsetMicros(){
		float f= ((1000000.f*offset)/bytesSec);
		return (long) f;
	}

	/**
	 * Sets the new audio offset position. 
	 * @param off New offset expressed in microseconds.
	 */
	public void setOffsetMicros(long off){
		float offsetBytes = (off*bytesSec)/1000000.f;
		setOffset((int) offsetBytes);
	}
	
	
	
	/**
	 * Gets the precision the system offers for changing distance
	 * @return Precision expressed in microseconds.
	 */
	public float getPrecision(){
		float result = (1000000f*chunkSize)/bytesSec;
		
		return result;
	}
	
	/**
	 * Gets the maximum allowed distance for the current reference gain.
	 * @return distance expressed in meters.
	 */
	public float getMaximumDistance() {
		float max = gainControl.getMaximum();
		
		return (float) (refDistance*Math.pow( 10, ((max-refGain)/19.93)));
	}
	
	
	/**
	 * Start and open the soundcard line.
	 */
	public void start(){
		line.start();
	}
	

	/**
	 * Play indefinitely a sound. This method never ends, since it enters in a infinite loop.
	 */
	public void loop(){
		while(true){
			if(offset >= audioSize ){
				offset = 0;
			}
			offset+= line.write(data, offset,((offset+chunkSize)>=audioSize)?(audioSize-offset):chunkSize);	
		}
	}

	
	/**
	 * Play a sound between two time positions. Calls play(start,end).
	 * @param uStart starting time expressed in microseconds.
	 * @param uEnd end time expressed in microseconds.
	 */
	public void playFromTo(long uStart, long uEnd){
		float f = (uStart*bytesSec)/1000000.f;
		int start = (int) f;
		f = (uEnd*bytesSec)/1000000.f;
		int end = (int) f;
		if(end%frameSize!=0){
			int pEnd = end;
			end-=end%frameSize;
			System.err.println("Warning, the end offset time needs to match an integral frame size. Old value: "+pEnd+" New value: "+ end);
		}
		
		this.play(start,end);
	}
	
	
	/**
	 * Play a sound during a certain amount of time. Calls playDuring(start,length).
	 * @param uStart starting position time expressed in microseconds.
	 * @param uLength duration expressed in microseconds
	 */
	public void playDuringMicros(long uStart, long uLength){
		float f = (uStart*bytesSec)/1000000.f;
		int start = (int) f;
		f = (uLength*bytesSec)/1000000.f;
		int length = (int) f;
		this.playDuring(start, length);
		//System.err.println("offset: "+offset+" length: "+length+" start: "+start+" end: "+(start+length));
	}
	
	
	/**
	 * Play a sound during a determined duration.
	 * @param start Starting byte.
	 * @param length Length of the play (expressed in bytes).
	 */
	public void playDuring(int start, int length){				
		int played=0;
		if(start>=audioSize){
			start = start%audioSize;
		}
		if((start%frameSize)!=0){
			start-= start%frameSize;
		}

		offset = start;
		
		int written = 0;
		int toWrite =chunkSize;		
		
		//We need to write just until we reach end-chunkSize, so we could write the difference later
		while(played<(length-chunkSize)){
			if(offset>=audioSize){
				offset=offset%audioSize;
			}
			if(offset<(audioSize-chunkSize)){
				toWrite = chunkSize;
			}
			if(offset>=(audioSize-chunkSize)){
				toWrite = audioSize-offset;
			}
			written = line.write(data, offset, toWrite);
			played+=written;
			offset+=written;
		}
		if(toWrite>0){
			toWrite = length-played;
			if((toWrite%frameSize)!=0){
				toWrite-=(toWrite%frameSize);
			}

			if((offset+toWrite)>=audioSize){
				written = line.write(data, offset,(audioSize-offset));
				toWrite -=written;
				played+=written;
				offset=0;
			}

			if(toWrite>0)
				written = line.write(data, offset,toWrite);

			played+=written;
			offset+=written;		
		}		
	}
	
	
	
	/**
	 * Play a sound between the bytes indicated.
	 * @param start Starting byte position.
	 * @param end Ending byte position.
	 */
	public void play(int start, int end){
		setOffset(start%audioSize);
		
		
		//playOffset will provide a offset counter
		int playOffset = offset;
		int toWrite = chunkSize;
		int written = 0;

		//We need to write just until we reach end-chunkSize, so we could write the difference later
		while(playOffset<(end-chunkSize)){
			//If we're about to get over the audio size, we reset the actual offset
			if(offset >= audioSize){
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

		if((offset+toWrite)>=audioSize){
			written = line.write(data, offset,(audioSize-offset));
			toWrite -=written;
			offset=0;
		}
		written = line.write(data, offset,toWrite);
		playOffset += written;
		offset=playOffset;
		
	}
	
	
	/**
	 * Play a complete sound, from its beginning to its end (simulating the distance).
	 */
	public void play(){
		while(offset<(audioSize-chunkSize)){
				
				offset+= line.write(data, offset, chunkSize);
		}
		offset += line.write(data, offset, audioSize-offset);
		//line.drain();
	}


	
	/**
	 * Close the sound system.
	 */
	public void close() {		
		line.close();
	}	
}
