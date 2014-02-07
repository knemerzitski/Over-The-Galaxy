package game.world;

import javax.vecmath.Vector3f;

import game.Game;
import game.world.entities.Entity;

import org.lwjgl.opengl.Display;

import utils.BoundingAxis;
import utils.BoundingSphere;
import utils.Utils;
import utils.math.Vector3;
import controller.Camera;

public class FrustumCulling {
	
	public enum Frustum{
		OUTSIDE, INTERSECT, INSIDE;
	}
	
	private Camera camera;
	private Vector3 X, Y, Z, camPos;
	
	private float tang;
	private float ratio;
	private float sphereFactorY;
	private float sphereFactorX;
	private float Hfar, Wfar;
	
	public FrustumCulling(Camera camera){
		this.camera = camera;
		
		ratio = (float)Game.width/Game.height;
		
		float angle = (float) Math.toRadians(Game.fov/2);
		tang = (float) Math.tan(angle);
		
		sphereFactorY = 1/(float)Math.cos(angle);
		
		float anglex = (float) Math.atan(tang*ratio);
		sphereFactorX = 1/(float)Math.cos(anglex);

		Hfar = 2 * tang * Game.zFar;
		Wfar = Hfar * ratio;
	}
	
	public void setCamera(Camera c){
		camera = c;
	}
	
	public void update(){
		Vector3f viewRay = camera.getViewRay();
		Z = new Vector3(viewRay.x, viewRay.y, viewRay.z);
		Vector3f rightVector = camera.getRightVector();
		X = new Vector3(rightVector.x, rightVector.y, rightVector.z);
		Vector3f upVector = camera.getUpVector();
		Y = new Vector3(upVector.x, upVector.y, upVector.z);
		Vector3f cam = camera.getPos();
		camPos = new Vector3(cam.x, cam.y, cam.z);
	}
	
	//http://www.lighthouse3d.com/tutorials/view-frustum-culling/radar-approach-implementation-ii/
	public Frustum inView(Entity e){
		Frustum sphereResult = sphereLocation(e.getBoundingSphere());
		if(sphereResult == Frustum.INTERSECT){ //Sphere is touching Frustum
			Frustum boxResult = boxLocation(e.getBoundingAxis());
			return boxResult; //box can be inside, intersect or outside
		}else{ //sphere is either outside or inside
			return sphereResult; 
		}
	}
	
	private Frustum sphereLocation(BoundingSphere sphere){
		if(sphere == null) //No sphere found
			return Frustum.INTERSECT; //Just assume object is in view
		
		Vector3f vertex = sphere.pos; //Sphere midpoint
		float radius = sphere.radius;
		
		float d;
		float az, ax, ay;
		Frustum result = Frustum.INSIDE; //Assume sphere is in the view
		
		Vector3f v = new Vector3f(vertex.x-camPos.x, vertex.y-camPos.y, vertex.z-camPos.z); //Vector that points from pos to vertex
		
		//Compute and test the Z coordinate
		az = v.dot(Z);
		if(az > Game.zFar+radius || az < Game.zNear-radius){
			return Frustum.OUTSIDE; 
		}
		if(az > Game.zFar - radius || az < Game.zNear+radius)
			result = Frustum.INTERSECT;
		
		//Compute and test the Y coordinate
		ay =  v.dot(Y);
		d = radius * sphereFactorY;
		az *= tang; //Frustum far plane height
		if(ay > az+d || ay < -az-d){
			return Frustum.OUTSIDE; 
		}
		if(ay > az-d || ay < -az+d)
			result = Frustum.INTERSECT;
		
		//Compute and test the X coordinate
		ax =  v.dot(X);
		az *= ratio; //Frustum far plane width
		d = radius * sphereFactorX;
		if(ax > az+d || ax < -az-d){
			return Frustum.OUTSIDE; 
		}
		if(ax > az-d || ax < -az+d)
			result = Frustum.INTERSECT;
		
		return result;
	}
	
	public Frustum boxLocation(BoundingAxis ob){
		if(ob == null)
			return Frustum.INSIDE;

		Vector3f[] fb = getFrustumBox();
		Vector3f fmin = fb[0];
		Vector3f fmax = fb[1];
		//System.out.println(fmin + " " + fmax);
		
		if(fmin.x > ob.getMax().x || fmax.x < ob.getMin().x ||
				fmin.y > ob.getMax().y || fmax.y < ob.getMin().y ||
				fmin.z > ob.getMax().z || fmax.z < ob.getMin().z)
			return Frustum.OUTSIDE;
		else if(fmin.x < ob.getMin().x && fmax.x > ob.getMax().x &&
				fmin.y < ob.getMin().y && fmax.y > ob.getMax().y &&
				fmin.z < ob.getMin().z && fmax.z > ob.getMax().z)
			return Frustum.INSIDE;
		else{
			return Frustum.INTERSECT;
		}
	}

	public Vector3f[] getFrustumBox(){
		Vector3 fc = camPos.getAdd(Z.getMultiply(Game.zFar)); //temp variable
		Vector3 ftl = fc.getAdd(Y.getMultiply(Hfar/2f)).getAdd(X.getMultiply(Wfar/2f).getNegate()); //frustum to left corner
		Vector3 fbr = fc.getAdd(Y.getMultiply(Hfar/2f).getNegate()).getAdd(X.getMultiply(Wfar/2f)); //frustum bottom right corner
		
		Vector3 ftr = fc.getAdd(Y.getMultiply(Hfar/2f)).getAdd(X.getMultiply(Wfar/2f)); //frustum to left corner
		Vector3 fbl = fc.getAdd(Y.getMultiply(Hfar/2f).getNegate()).getAdd(X.getMultiply(Wfar/2f).getNegate()); //frustum bottom right corner
		
		return Utils.getMinMaxVectors(ftl, fbr, camPos, ftr, fbl);

	}

}