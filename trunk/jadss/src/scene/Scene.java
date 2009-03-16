package scene;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

public class Scene extends SimpleGame {

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
				  new Vector3f(-1,0,-1), new Vector3f(1,0,-1), new Vector3f(-1,0,1),  new Vector3f(1,0,1),
		  };
		  Vector3f[] normals={
				  new Vector3f(0,0,1),new Vector3f(0,0,1),new Vector3f(0,0,1),new Vector3f(0,0,1)
		  };
		  /*ColorRGBA[] colors={
				   new ColorRGBA(1,0,0,1),
				   new ColorRGBA(0,1,0,1),
				   new ColorRGBA(0,0,1,1),
				   new ColorRGBA(1,1,1,1)
		  };*/

		  // Texture Coordinates for each position
	      Vector2f[] texCoords={
	    		  new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1), new Vector2f(1,1)
	      };
		  		  
	      int[] indexes={
				   0,1,2,3,2,1,
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
		  m.setLocalScale(100);
		  m.setModelBound(new BoundingBox());
		  m.updateModelBound();
		  rootNode.attachChild(m);

	}

	protected void addObjects(){		
		float posx[]= {-1f, 1f,-1f, 1f};
		float posz[]= {-1f,-1f, 1f, 1f};
		Sphere v_s[] = new Sphere[4];
		for(int i=0;i<4;i++){
			v_s[i] = new Sphere(("Sphere_"+i),20,20,.15f);
			v_s[i].setModelBound(new BoundingSphere());
			v_s[i].setLocalTranslation(posx[i],.2f,posz[i]);
			v_s[i].updateModelBound();
			v_s[i].setRandomColors();
			rootNode.attachChild(v_s[i]);
		}
		
	}

	private void addModel(){
		
		Node s = (Node) ModelLoader.load3ds("models/laptop.3ds","models");
		s.setLocalTranslation(90, 0, 90);
		s.setLocalScale(3);

		rootNode.attachChild(s);
		
	}
	
	private void addLight(){
		ColorRGBA diffuse = new ColorRGBA( 0.65f, 0.65f, 0.65f, 0.65f );
		ColorRGBA ambient = new ColorRGBA( 0.1f, 0.1f, 0.1f, 1f );
		lightState.detachAll();
		
		PointLight light[] = new PointLight[5];
/*		light[0] = new PointLight();
		light[0].setDiffuse(diffuse);
        light[0].setAmbient(ambient);
        light[0].setLocation(new Vector3f( 0, 100,  0));
        light[0].setEnabled( true );
		lightState.attach(light[0]);
*/		light[1] = new PointLight();
		light[1].setDiffuse(diffuse);
        light[1].setAmbient(ambient);
        light[1].setLocation(new Vector3f( -50, 50, -50));
        light[1].setEnabled( true );
		lightState.attach(light[1]);
		light[2] = new PointLight();
		light[2].setDiffuse(diffuse);
        light[2].setAmbient(ambient);
        light[2].setLocation(new Vector3f( 50, 50, -50));
        light[2].setEnabled( true );
		lightState.attach(light[2]);
		light[3] = new PointLight();
		light[3].setDiffuse(diffuse);
        light[3].setAmbient(ambient);
        light[3].setLocation(new Vector3f( -50, 50, 50));
        light[3].setEnabled( true );
		lightState.attach(light[3]);
		light[4] = new PointLight();
		light[4].setDiffuse(diffuse);
        light[4].setAmbient(ambient);
        light[4].setLocation(new Vector3f( 50, 50, 50));
        light[4].setEnabled( true );
		lightState.attach(light[4]);
		rootNode.setRenderState( lightState );
		rootNode.setLightCombineMode(Spatial.LightCombineMode.CombineFirst);
	}
	
	private void addWalls(){
		Box b = new Box("Walls",new Vector3f(-101f,-0.1f,-101f),new Vector3f(101f,101f,101f));
		b.setModelBound(new BoundingBox());
		b.updateModelBound();
		b.setRandomColors();
		rootNode.attachChild(b);
	}
	
	private void addEverything(){
		//display.getRenderer().setBackgroundColor( ColorRGBA.darkGray.clone());
		addLight();
		addWalls();
		addModel();
		addMesh();

	}
	
	@Override
	protected void simpleInitGame() {
		display.setTitle("jADSS 3D Demo");
		

		cam.setLocation(new Vector3f(0,10,0));
		addEverything();
	}

}
