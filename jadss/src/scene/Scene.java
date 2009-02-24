package scene;

import com.jme.app.SimpleGame;
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
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.BufferUtils;

public class Scene extends SimpleGame {

	private Node scene;
	private TextureState ts;

	public Scene(){
		super();
	}

	public void startScene(){
		this.setConfigShowMode(ConfigShowMode.AlwaysShow, Scene.class.getResource("/imgs/logo.png"));
		this.start();
	}

	
	protected void addMesh(){
		  TriMesh m=new TriMesh("Floor");
		  Vector3f[] vertexes={
				  new Vector3f(-1f,0,-1f),
				  new Vector3f(1f,0,-1f),
				  new Vector3f(-1f,0,1f),
				  new Vector3f(1f,0,1f) 
		  };
		  Vector3f[] normals={
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1),
				   new Vector3f(0,0,1)
		  };
		  /*ColorRGBA[] colors={
				   new ColorRGBA(1,0,0,1),
				   new ColorRGBA(0,1,0,1),
				   new ColorRGBA(0,0,1,1),
				   new ColorRGBA(1,1,1,1)
		  };*/

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
			   null,TexCoords.makeNew(texCoords),
			   BufferUtils.createIntBuffer(indexes)
		  );

		  ts = display.getRenderer().createTextureState();
		  ts.setEnabled(true);
		  ts.setTexture(TextureManager.loadTexture(
		                    Scene.class.getResource("/imgs/floor.jpg"),
		                    Texture.MinificationFilter.BilinearNearestMipMap, 
		                    Texture.MagnificationFilter.Bilinear));
		 
		  m.setRenderState(ts);
		 
		  m.setModelBound(new BoundingBox());
		  m.updateModelBound();
		  scene.attachChild(m);

	}

	protected void addObjects(){		
		float posx[]= {-1f, 1f,-1f, 1f};
		float posz[]= {-1f,-1f, 1f, 1f};
		Sphere v_s[] = new Sphere[4];
		for(int i=0;i<4;i++){
			v_s[i] = new Sphere(("Sphere_"+i),20,20,.2f);
			v_s[i].setModelBound(new BoundingSphere());
			v_s[i].setLocalTranslation(posx[i],.2f,posz[i]);
			v_s[i].updateModelBound();
			v_s[i].setRandomColors();
			scene.attachChild(v_s[i]);
		}
		
	}

	private void addLight(){
		scene.setLightCombineMode(LightCombineMode.Off);
	}
	
	private void addEverything(){
		scene.setLocalScale(40);
		addObjects();
		addMesh();
		addLight();
	}
	
	@Override
	protected void simpleInitGame() {
		display.setTitle("jADSS 3D Demo");
		
		scene = new Node("Root Node");
		cam.setLocation(new Vector3f(0,10,0));
		rootNode.attachChild(scene);
		addEverything();
	}

}
