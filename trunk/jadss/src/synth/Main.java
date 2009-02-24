/**
 * 
 */
package synth;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.util.geom.BufferUtils;

import scene.Scene;

/**
 * @author Marcelino Lopez
 *
 */
public class Main {
	
	/**
	 * @param args String array where line arguments are placed 
	 * @throws MidiUnavailableException 
	 * @throws InvalidMidiDataException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Hi");
	
/*		Synth.getInstance();
		int iLength = Synth.getInstrumentSize();
		int cLength = Synth.getChannelSize();
		System.out.println("There are "+ iLength +" instruments.");
		System.out.println("There are "+ cLength +" channels.");

//		for(int i = 0;i<iLength;i++){
		int i=80;
			Synth.selectInstrument(i);
			Synth.setVolumeReference(80);
			Synth.setDistance(300);
			System.out.println("Playing "+i+": "+Synth.getInstrumentName());
			Synth.playNote(60, 100, 3000, 0);
//		}

		
        
        Synth.turnOff();
*/
		Scene s = new Scene();
		s.startScene();
		
		System.out.println("Bye!!");

	}

}
