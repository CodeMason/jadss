package synth;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.*;

public class Synth {
	private Synthesizer synt;
	private Sequencer sequencer;
	private Receiver receiver;
	private Sequence seq;
	private Soundbank bank;
	private Instrument[] inst;
	private MidiChannel[] chan;
	
	private int selectedChannel = 0;
	private int selectedInstrument = 0;
	private int volRef = 0;
	private int volume = 0;
	private double distance = 0;
	private long delayMicros = 0;
	
	/** SINGLETON METHODS**/
	// Private Constructor needed for Singleton
	private Synth(){
		try {
			synt =MidiSystem.getSynthesizer();
			sequencer = MidiSystem.getSequencer();
	        
			seq= new Sequence(Sequence.PPQ, 10);
			sequencer.setSequence(seq);
			sequencer.open();
			synt.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		bank = synt.getDefaultSoundbank();
		try {
			if (bank != null){
				inst = synt.getDefaultSoundbank().getInstruments();
        		receiver = synt.getReceiver();
			}
			else{
				inst = synt.getAvailableInstruments();
				receiver = MidiSystem.getReceiver();
			}
			sequencer.getTransmitter().setReceiver(receiver);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		
		chan = synt.getChannels();
		
		setVolumeReference(60);
	}

	   
    
    /**SELECTORS */
    public void selectChannel(int channel){
    	selectedChannel = channel;
    }
    
    public void setVolumeReference( int value ) {
    	if(value != volRef){
    		if(value>127) value = 127;
    		else if(value<0) value = 0;
    		volRef = value;
	    	try {
	    		ShortMessage volumeMessage = new ShortMessage();
	    		for (int i=0; i<chan.length; i++ ) {
	    			volumeMessage.setMessage( ShortMessage.CONTROL_CHANGE, i, 7, volRef);
	    			receiver.send( volumeMessage, -1 );
	    		}
	    	} catch ( Exception e ) {
	    		e.printStackTrace();
	    	}
    	}
    } 
   
    public void selectInstrument(int i){
    	selectedInstrument = i;
    	chan[selectedChannel].programChange(inst[selectedInstrument].getPatch().getProgram());
    }
    
    public String getInstrumentName(){
    	return inst[selectedInstrument].getName();
    }
    public int getCurrentInstrument(){
    	return selectedInstrument;
    }

    public int getCurrentChannel(){
    	return selectedChannel;
    }
    
    public int getInstrumentSize(){
    	return inst.length;
    }
    
    public int getChannelSize(){
    	return chan.length;
    }
    
    
    /*PLAY AND STOP NOTES*/
    public void noteOn(int note, int v){
    	chan[selectedChannel].noteOn(note,v);	
    }
    public void noteOff(int note, int v){
    	chan[selectedChannel].noteOff(note,v);
    }
    
    public void noteOff(int note){
    	chan[selectedChannel].noteOff(note);
    }

    public void allNotesOff(){
    	chan[selectedChannel].allNotesOff();
    }
    
    public void turnOff(){
		for(MidiChannel c : chan){
			c.allSoundOff();
		}
		synt.close();
    }
    
    public void playNote(int note, int v, long millis, int nanos) throws InterruptedException{
    	long delay_time,initial_time,end_time;
		if(delayMicros!=0){
	    	delay_time = System.nanoTime();
			TimeUnit.MICROSECONDS.sleep(delayMicros);
		}		
		else delay_time=System.nanoTime();
   		initial_time = System.nanoTime();
  		chan[selectedChannel].noteOn(note,v);
       	Thread.sleep(millis, nanos);
       	chan[selectedChannel].noteOff(note);
       	end_time =System.nanoTime(); 
       	System.out.println((initial_time-delay_time)/1000+" , "+(end_time-initial_time)/1000);

    }
    
    
    //TODO: Check the results
    public void setDistance(double meters){
    	if(distance != meters){
    		//		TDi = [max(di=1,..,4) - di] / 343
    		delayMicros = (long) (meters/343* 1000000);

    		
    		//     	VOLi = VOLRef - 19.93 log(di / max(di = 1,..., 4))
    		if(meters>1){
    			volume = (int) Math.round(volRef + 19.93 *Math.log10(meters));
    		}
    		else volume=volRef;
    		
    		if(volume!=volRef){
	    		if(volume>127) volume = 127;
	    		else if(volume<0) volume = 0;
		    	try {
		    		ShortMessage volumeMessage = new ShortMessage();
		    		for (int i=0; i<chan.length; i++ ) {
		    			volumeMessage.setMessage( ShortMessage.CONTROL_CHANGE, i, 7, volume);
		    			receiver.send( volumeMessage, -1 );
		    		}
		    	} catch ( Exception e ) {
		    		e.printStackTrace();
		    	}
    		}
    		
    		distance = meters;
    		
    		System.out.println(distance+"m :\n\t delay = "+delayMicros+"\n\t att = "+ (volume-volRef));    		
    	}
    }
}
