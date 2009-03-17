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
	private int chunkSize = 8;
	
	/**Shows whether if we're using a sound file or our standard tone*/
	private boolean usingFile=false;
	
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
	
	/*AUDIO FILE PARAMETERS*/
	/** Size of the sound data within the file (in bytes)*/
	private int fileSize = 0;
	
	/** Array that contains the whole data sound in file specified*/
	private byte fileData[];
	
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


	
	/**
	 * Default constructor, generates a Sin wave sound sampled directly
	 */
	public Sound(){
		data = new byte[playSampleCount];
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
	    
	    usingFile=false;
	}
	

	/**
	 * Constructor with parameters we will use in case we need to play a sound file
	 * @param filename String that contains the path to the file we will use
	 */
	public Sound(String filename){
		data = new byte[playSampleCount];

		File f = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
			fileSize = audioInputStream.available();
			format = audioInputStream.getFormat();
			frameSize = audioInputStream.getFormat().getFrameSize();

			bitsPerSample = format.getSampleSizeInBits();
			defaultRate = format.getSampleRate();

			//Calculate time factor
		    bytesSec = (long) (defaultRate*bitsPerSample/8);
		    
		    //Calculate appropiate chunkSize
		    if(bytesSec<=22050) chunkSize = 8*bitsPerSample/8;
		    //else if(bytesSec<=) chunkSize = 32*bitsPerSample/8;
		    else if(bytesSec<=44100) chunkSize = 128*bitsPerSample/8;
		    else chunkSize = 1024*bitsPerSample/8;
		    
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		fileData = new byte[fileSize+chunkSize];
		try {
			audioInputStream.read(fileData,0,fileSize);
		} catch (IOException e1) {
			e1.printStackTrace();
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
		usingFile=true;
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
		if(usingFile && offset>=fileSize){
			System.err.println("Offset Bigger than file size");
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
		if(usingFile){
			while(true){
					line.write(fileData, offset,chunkSize);
					offset+=chunkSize;
					if (offset >= fileSize){
						offset = 0;
					}
			}
		}
		else{	
			while(true){
				line.write(data, offset,chunkSize);
				offset+=chunkSize;
				if (offset >= data.length ){
					offset = 0;
				}
			}
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
		if((end-start)%frameSize!=0){
			int pEnd = end;
			end-=(end-start)%frameSize;
			System.err.println("Warning, the end time needs to be rounded to match an integral frame size. Old value: "+pEnd+" New value: "+ end);
		}
		if(usingFile && end>fileSize){
			int pEnd = end;
			end=fileSize;
			System.err.println("Warning, End can't be greater than the file size, reducing to its maximum "+pEnd+" New value: "+ end);
		}
		
		System.out.println("t0: ("+uStart+" , "+start+")     te: ("+uEnd+" , "+end+") in (us, B)");
		this.play(start,end);
	}
	
	/**
	 * Play a sound between the bytes indicated.
	 * @param start
	 * @param end
	 */
	public void play(int start, int end){		
		if(usingFile){
			int playoffset;
				for(playoffset = offset+start; playoffset < (end-chunkSize); playoffset += chunkSize){
					line.write(fileData, playoffset, chunkSize);
				}
	
				line.write(fileData, playoffset, end-playoffset);
				line.drain();
				playoffset+= end-playoffset;
				offset = playoffset;
				
		}
		else{
			int playoffset; 
				for(playoffset = offset+start; playoffset <= (end-chunkSize); playoffset += chunkSize){
					offset = (playoffset)%(data.length-chunkSize);
					line.write(data, offset, chunkSize);
				}
				line.write(data, playoffset%data.length, end-playoffset);
				playoffset+= end-playoffset;
				line.drain();
				offset = playoffset;

			/*			while(offset<=(end-chunkSize)){
				line.write(data, offset%data.length, chunkSize);
				offset += chunkSize;	
			}
			line.write(data, offset, end%chunkSize);
			offset+=end%chunkSize;
			line.drain();
			 */		
		}

	}
	
	
	/**
	 * Play a complete sound, from the beginning to the end. 
	 */
	public void play(){
		if(usingFile){
			while (offset < fileSize){
				line.write(fileData, offset, chunkSize);
				offset+=chunkSize;
			}
		}
		else{
			while(offset<data.length){
				line.write(data, offset, chunkSize);
				offset += chunkSize;
			}
		}
		line.drain();
	}

	/**
	 * Close the sound system (will close the stream and the line
	 */
	public void close() {
		
		if(usingFile){
			try {
				audioInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		line.close();
	}

	
}
