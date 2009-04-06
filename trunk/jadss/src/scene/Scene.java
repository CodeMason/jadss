package scene;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

public class Scene extends SimpleGame {
	static private int GROUND_SIZE = 100;
	
	private TextureState ts;

	public Scene(){
		super();
	}

	public void startScene(){
		this.setConfigShowMode(ConfigShowMode.AlwaysShow, Scene.class.getResource("imgs/logo.png"));
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
		                    Scene.class.getResource("imgs/floor.jpg"),
		                    Texture.MinificationFilter.BilinearNearestMipMap, 
		                    Texture.MagnificationFilter.Bilinear));
		 
		  m.setRenderState(ts);
		  m.setLocalScale(GROUND_SIZE);
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
		Quaternion quaternion = new Quaternion();
		quaternion.loadIdentity();
		
		Node s = (Node) ModelLoader.load3ds("models/laptop.3ds","models");
		quaternion.lookAt(new Vector3f(-1,600,-1), new Vector3f(0,1,0));
		s.setLocalTranslation(80, 0.5f, 80);
		s.setLocalRotation(new Quaternion(quaternion));
		s.setLocalScale(3);
		rootNode.attachChild(s);
		
		Node s1 = (Node) ModelLoader.load3ds("models/laptop.3ds","models");;
		quaternion.loadIdentity();
		quaternion.lookAt(new Vector3f(1,600,1), new Vector3f(0,1,0));
		s1.setLocalTranslation(-80, .5f, -80);
		s1.setLocalRotation(new Quaternion(quaternion));
		s1.setLocalScale(3);
		rootNode.attachChild(s1);
		
		Node s2 = (Node) ModelLoader.load3ds("models/laptop.3ds","models");;
		quaternion.loadIdentity();
		quaternion.lookAt(new Vector3f(1,600,-1), new Vector3f(0,1,0));
		s2.setLocalTranslation(-80, .5f, 80);
		s2.setLocalRotation(new Quaternion(quaternion));
		s2.setLocalScale(3);
		rootNode.attachChild(s2);
		
		Node s3 = (Node) ModelLoader.load3ds("models/laptop.3ds","models");;
		quaternion.loadIdentity();
		quaternion.lookAt(new Vector3f(-1,600,1), new Vector3f(0,1,0));
		s3.setLocalTranslation(80, .5f, -80);
		s3.setLocalRotation(new Quaternion(quaternion));
		s3.setLocalScale(3);
		rootNode.attachChild(s3);
	}
	
	private void addLight(){
		ColorRGBA diffuse  = new ColorRGBA( 0.65f, 0.65f, 0.65f, 0.65f );
		ColorRGBA ambient  = new ColorRGBA( 0.10f, 0.10f, 0.10f, 1.00f );
		ColorRGBA specular = new ColorRGBA( 0.80f, 0.80f, 0.80f, 0.80f );
		lightState.detachAll();
		lightState.setTwoSidedLighting(true); 

		LightNode ln = new LightNode("Lamp");
		SpotLight sl = new SpotLight();
		sl.setDiffuse(diffuse);
		sl.setAmbient(ambient);
		sl.setSpecular(specular);
		sl.setDirection(new Vector3f(0,0,1));
		sl.setLocation(new Vector3f(0,10,0));
		sl.setAngle(45);
		sl.setEnabled(true);
		ln.setLight(sl);
		rootNode.attachChild(ln);
        
        lightState.attach(sl);
        rootNode.setRenderState( lightState );
		rootNode.setLightCombineMode(Spatial.LightCombineMode.CombineClosest);
	
		/**
		PointLight light[] = new PointLight[5];
		for(int i=0;i<5;i++){
			light[i] = new PointLight();
			light[i].setDiffuse(diffuse);
	        light[i].setAmbient(ambient);
	        light[i].setSpecular(specular);
	        light[i].setConstant(100f);
	        light[i].setLinear(100f);
	        light[i].setQuadratic(100f);
		}
		
        light[0].setLocation(new Vector3f( 0, GROUND_SIZE,  0));
        light[1].setLocation(new Vector3f( -GROUND_SIZE, GROUND_SIZE/2, -GROUND_SIZE));
        light[2].setLocation(new Vector3f( GROUND_SIZE, GROUND_SIZE/2, -GROUND_SIZE));
        light[3].setLocation(new Vector3f( -GROUND_SIZE, GROUND_SIZE/2, GROUND_SIZE));
        light[4].setLocation(new Vector3f( GROUND_SIZE, GROUND_SIZE/2, GROUND_SIZE));
        
		for(int i=0;i<5;i++){
	        light[i].setEnabled( true );	        
	        lightState.attach(light[i]);
		}
		 */
	}
	
	private void addWalls(){
		Box b = new Box("Walls",new Vector3f(-GROUND_SIZE*1.1f,-0.1f,-GROUND_SIZE*1.1f),new Vector3f(GROUND_SIZE*1.1f,GROUND_SIZE*1.1f,GROUND_SIZE*1.1f));
		
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        ms.setEmissive(new ColorRGBA(0.6f, 0.6f, 0.6f, 0.6f));
        b.setRenderState(ms);
 
        b.setRandomColors();
		b.setModelBound(new BoundingSphere());
		b.updateModelBound();
		rootNode.attachChild(b);
	}
	
	private void addEverything(){
		//display.getRenderer().setBackgroundColor( ColorRGBA.darkGray.clone());
		addWalls();
		addModel();
		addMesh();
		addLight();

	}
	
	@Override
	protected void simpleInitGame() {
		display.setTitle("jADSS 3D Demo");
		
		cam.setLocation(new Vector3f(0,10,0));
		addEverything();
	}

}
