package scene;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.geom.BufferUtils;

public class Scene extends BaseGame {

	private int width, height, depth, freq;
	private boolean fullscreen=false;
	private Node scene;
	private TextureState ts;
	
	@Override
	protected void cleanup() {
		   ts.deleteAll();
	}

	@Override
	protected void initGame() {
		scene = new Node("Root Node");
		addObjects();
		addMesh();
		scene.updateGeometricState(0.0f, true);
		scene.updateRenderState();
	}

	@Override
	protected void initSystem() {
		//store the properties information
		width = properties.getWidth();
		height = properties.getHeight();
		depth = properties.getDepth();
		freq = properties.getFreq();
		fullscreen = properties.getFullscreen();

		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(width, height, depth, freq, fullscreen);
	
			cam = display.getRenderer().createCamera(width, height);
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//set the background to black
		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		//initialize the camera
		cam.setFrustumPerspective(45.0f, (float)width / (float)height, 1, 1000);
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		// Move our camera to a correct place and orientation.
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		display.getRenderer().setCamera(cam);

		KeyBindingManager.getKeyBindingManager().set("exit",
		KeyInput.KEY_ESCAPE);
	}

	@Override
	protected void reinit() {
		   display.recreateWindow(width, height, depth, freq, fullscreen);
	}

	@Override
	protected void render(float interpolation) {
		   //Clear the screen
		   display.getRenderer().clearBuffers();
		   display.getRenderer().draw(scene);
	}

	@Override
	protected void update(float interpolation) {
	    timer.update();
	    interpolation = timer.getTimePerFrame();
	     if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit")) {
	       finished = true;
	    }
	}

	public void startScene(){
		setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		start();
	}

	
	protected void addMesh(){
		  TriMesh m=new TriMesh("Mesh1");
		  Vector3f[] vertexes={
				   new Vector3f(-5,-.2f,-5),
				   new Vector3f(5,-.2f,-5),
				   new Vector3f(-5,-.2f,5),
				   new Vector3f(5,-.2f,5)
		  };
		  Vector3f[] normals={
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1)
		  };
		  ColorRGBA[] colors={
				   new ColorRGBA(1,0,0,1),
				   new ColorRGBA(0,1,0,1),
				   new ColorRGBA(0,0,1,1),
				   new ColorRGBA(1,1,1,1)
		  };

		  int[] indexes={
				   0,1,2,1,2,3
		  };
		  m.reconstruct(BufferUtils.createFloatBuffer(vertexes),BufferUtils.createFloatBuffer(normals),
					BufferUtils.createFloatBuffer(colors),null,
					BufferUtils.createIntBuffer(indexes)
		  );
		  
		  m.setModelBound(new BoundingBox());
		  m.updateModelBound();
		  scene.attachChild(m);

	}

	protected void addObjects(){
		
		int posx[]= {-5,5,-5,5};
		int posz[]= {-5,-5,5,5};
		Sphere v_s[] = new Sphere[4];
		for(int i=0;i<4;i++){
			v_s[i] = new Sphere(("Sphere_"+i),20,20,.5f);
			v_s[i].setModelBound(new BoundingSphere());
			v_s[i].setLocalTranslation(posx[i],.3f,posz[i]);
			v_s[i].updateModelBound();
			v_s[i].setRandomColors();
			scene.attachChild(v_s[i]);
		}
		
				
	}

}
