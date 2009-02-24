package scene;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.BufferUtils;

public class SceneBackup extends BaseGame {

	private int width, height, depth, freq;
	private boolean fullscreen=false;
	private Node scene;
	private TextureState ts;
	private Camera cam;
	protected Timer timer;
	
	@Override
	protected void cleanup() {
		   ts.deleteAll();
	}

	@Override
	protected void initGame() {
		scene = new Node("Root Node");
		addObjects();
		addMesh();
		 
		//update the scene graph for rendering
		scene.updateGeometricState(0.0f, true);
		scene.updateRenderState();
	}

	@Override
	protected void initSystem() {
		//store the properties information
		width = this.settings.getWidth();
		height = this.settings.getHeight();
		depth = this.settings.getDepth();
		freq = this.settings.getFrequency();
		fullscreen = this.settings.isFullscreen();
 
		try {
			display = DisplaySystem.getDisplaySystem(this.getNewSettings().getRenderer());
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
		Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
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
		this.setConfigShowMode(ConfigShowMode.ShowIfNoConfig, SceneBackup.class.getResource("/imgs/logo.png"));
		this.start();
	}

	
	protected void addMesh(){
		  TriMesh m=new TriMesh("Ground");
		  Vector3f[] vertexes={
				  new Vector3f(0,0,0),
				  new Vector3f(1,0,0),
				  new Vector3f(0,1,0),
				  new Vector3f(1,1,0) 
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

		  // Texture Coordinates for each position
	      Vector2f[] texCoords={
	    		  new Vector2f(0,0),
	    		  new Vector2f(1,0),
	    		  new Vector2f(0,1),
	    		  new Vector2f(1,1)
	      };
		  		  
	      int[] indexes={
				   0,1,2,1,2,3
		  };

	      
		  m.reconstruct(BufferUtils.createFloatBuffer(vertexes),BufferUtils.createFloatBuffer(normals),
			   BufferUtils.createFloatBuffer(colors),TexCoords.makeNew(texCoords),
			   BufferUtils.createIntBuffer(indexes)
		  );

		  ts = display.getRenderer().createTextureState();
		  ts.setEnabled(true);
		  ts.setTexture(TextureManager.loadTexture(
		                    SceneBackup.class.getResource("/imgs/ground.jpg"),
		                    Texture.MinificationFilter.BilinearNearestMipMap, 
		                    Texture.MagnificationFilter.Bilinear));
		 
		  m.setRenderState(ts);
		 
		  m.setLocalTranslation(-.5f, -.2f, -.5f);
		  m.setLocalScale(new Vector3f(5,1,5));
		  
		  m.setModelBound(new BoundingBox());
		  m.updateModelBound();
		  scene.attachChild(m);

	}

	protected void addObjects(){		
		int posx[]= {-5,5,-5,5};
		int posz[]= {-5,-5,5,5};
		Sphere v_s[] = new Sphere[4];
		for(int i=0;i<4;i++){
			v_s[i] = new Sphere(("Sphere_"+i),20,20,1f);
			v_s[i].setModelBound(new BoundingSphere());
			v_s[i].setLocalTranslation(posx[i],.3f,posz[i]);
			v_s[i].updateModelBound();
			v_s[i].setRandomColors();
			scene.attachChild(v_s[i]);
		}
		
	}

}
