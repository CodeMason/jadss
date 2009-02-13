/**
 * 
 */
package synth;

import javax.sound.midi.*;

/**
 * @author Marcelino Lopez
 *
 */
public class Main {
	static public Synthesizer synt;
	static public Sequencer sequencer;
	static public Sequence seq;
	static public Soundbank bank;
	static public Instrument[] inst;
	static public MidiChannel[] chan;
	
	/**
	 * @param args String array where line arguments are placed 
	 * @throws MidiUnavailableException 
	 * @throws InvalidMidiDataException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
		System.out.println("Hi");

		synt =MidiSystem.getSynthesizer();
		sequencer = MidiSystem.getSequencer();
		seq= new Sequence(Sequence.PPQ, 5);
		synt.open();
		bank = synt.getDefaultSoundbank();
		if (bank != null)
			inst = synt.getDefaultSoundbank().getInstruments();
		else{
			inst = synt.getAvailableInstruments();
		}
				
		chan = synt.getChannels();
		System.out.println("There are "+inst.length+" instruments");
		System.out.println("There are "+chan.length+" channels");
		
		/*for(int i = 0;i<chan.length;i++){
			MidiChannel c = chan[i];
			c.programChange(inst[i*5].getPatch().getProgram());
			if(c != null){
				c.resetAllControllers();
				System.out.println("Channel "+i+" playing");
				c.noteOn(60,93);
				
			}
			else System.out.println("oh, noes!");
		}
		*/
		for(int i = 100;i<inst.length;i++){
			System.out.println("Playing "+i+": "+inst[i].getName());
			chan[2].programChange(inst[i].getPatch().getProgram());
			chan[2].noteOn(65,93);
			//Thread.sleep(1800);
			Thread.sleep(100);
			chan[2].allNotesOff();	
		}
		
		
        
        System.out.println("Bye!!");
		for(MidiChannel c : chan){
			c.allSoundOff();
		}

	}

}
