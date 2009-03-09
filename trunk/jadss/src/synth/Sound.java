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


public class Sound{
	/*AUDIO DEFAULT INITIAL PARAMETERS*/
	private int playSampleCount = 131072;
	private float defaultRate = 44100;
	private int chunkSize = 8;
	private boolean usingFile=false;
	private int offset = 0;
	private int bitsPerSample=8;
	private float distance = 1f;
	private float refGain = -15.944f;
	private float gain=refGain;
	private long bytesSec;
	
	/*AUDIO FILE PARAMETERS*/
	private int fileSize = 0;
	private byte fileData[];
	private byte data[];
	
	
	/*SYSTEM PARAMETERS*/
	private AudioInputStream audioInputStream = null;
	private AudioFormat format;
	private DataLine.Info info;
	private SourceDataLine line;
	private FloatControl gainControl;
	
	
	public Sound(){
		data = new byte[playSampleCount];
		format = new AudioFormat(defaultRate, bitsPerSample, 1, true, true);
	    info = new DataLine.Info(SourceDataLine.class, format);
	
	    //Calculate time factor
	    // 
	    bytesSec = (long) defaultRate*bitsPerSample/8;
	    
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
	

	
	public Sound(String filename){
		data = new byte[playSampleCount];

		File f = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
			fileSize = audioInputStream.available();
			format = audioInputStream.getFormat();

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

	public void setRefGain(float refGain) {
		this.refGain = refGain;
		gainControl.setValue(this.refGain);
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
		if(meters<getMaximumDistance()){
			distance = meters;
			gain = refGain+(float)(19.93f*Math.log10(distance));
			gainControl.setValue(gain);
		}
		else {
			System.err.println("Distance input is higher than maximum distance: Adjusting to maximum distance");
			distance = getMaximumDistance();
			gain = refGain+(float)(19.93f*Math.log10(distance));
			gainControl.setValue(gain);
		}
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
		float p;
		p = ((float)chunkSize)/bytesSec;
		
		return p;
	}
	
	public float getMaximumDistance() {
		float max = gainControl.getMaximum();
		
		return (float) Math.pow( 10, (max-refGain)/19.93);
	}
	
	
	public void start(){
		line.start();
	}
	
	
	public void loop(){
		if(usingFile){
			while(true){
					line.write(fileData, offset,chunkSize);
					offset+=chunkSize;
					if (offset >= fileSize){
						line.drain();
						offset = 0;
					}
			}
		}
		else{	
			while(true){
				line.write(data, offset,chunkSize);
				offset+=chunkSize;
				if (offset >= data.length )
					offset = 0;
			}
		}
	}
	
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
		offset = 0;
	}

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
