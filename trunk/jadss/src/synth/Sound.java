package synth;


import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound{
	/*AUDIO DEFAULT PARAMETERS*/
	private int playSampleCount = 65536;
	private int defaultRate = 22050;
	private int chunkSize = 8;
	private boolean usingFile=false;

	
	/*INITIAL AUDIO PARAMETERS*/
	private int offset = 0;
	private byte data[] = new byte[playSampleCount];
	
	
	/*FILE AND SYSTEM PARAMETERS*/
	private AudioInputStream audioInputStream = null;
	private AudioFormat format;
	private DataLine.Info info;
	private SourceDataLine line;
	
	
	public Sound(){
		format = new AudioFormat(defaultRate, 8, 1, true, true);
	    info = new DataLine.Info(SourceDataLine.class, format);
	    
 	    double x;
	    for(int i=0;i<playSampleCount;i++){
	    	x= Math.sin(i/(2*Math.PI));
	    	data[i]=(byte) (((x+1)*256/2-128));
	    }
	    
	    try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, playSampleCount);
	    } catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    usingFile=false;
	}
	
	public void start(){
		line.start();
	}
	
	public Sound(String filename){
		File f = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(f);
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		format = audioInputStream.getFormat();

		info = new DataLine.Info(SourceDataLine.class, format);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, playSampleCount);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		usingFile=true;
	}
	
	public void loop(){
    	//long ini=System.currentTimeMillis();
    	for(int i=0;i<=100;i++){
    		i=i%100;
    		/*if(i==0){
    			long end = System.currentTimeMillis();
    			System.out.println("pause: "+(end-ini));
    			Thread.sleep(5);
    			ini = end;
    		}*/
    		if (offset >= data.length)
    			offset = 0;
    		if(usingFile){
    			try {
					audioInputStream.read(data, offset, chunkSize);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		line.write(data, offset,chunkSize);
        	offset += chunkSize;
	    }
	}
	
	public void play(){
		int nBytes=0;
		if(usingFile){
			while (nBytes!= -1){
					try {
						nBytes= audioInputStream.read(data, 0, chunkSize);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(nBytes>=0)
						line.write(data, 0, nBytes);
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

	public void close() {
		
		if(usingFile)
			try {
				audioInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		line.close();
	}
	
}
