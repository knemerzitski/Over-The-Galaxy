package game.world.entities;

import game.vbo.ModelVBO;
import game.world.World;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import utils.BoundingAxis;
import utils.BoundingSphere;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;

public interface Entity {
	
	public enum Motion {
		STATIC, DYNAMIC;
	}
	
	/**
	 * @return Success of setting object static.
	 */
	public boolean setStatic();
	
	/**
	 * @return Success of setting object dynamic.
	 */
	public boolean setDynamic();
	
	public void setRigidBody(RigidBody rigidShape);
	
	public RigidBody getRigidBody();
	
	public RigidBodyConstructionInfo getRigidBodyConstructionInfo();
	
	public void setRigidBodyConstructionInfo(RigidBodyConstructionInfo r);

	public void setModel(ModelVBO modelShape);
	
	public ModelVBO getModel();
	
	public boolean isDynamic();
	
	public boolean isStatic();
	
	public ModelVBO getVBOOBject();
	
	public void setVBOObject(ModelVBO o);
	
	public void update(float dt);
	
	public void render();
	
	public void dispose();
	
	public void createVBO();
	
	public void setMotionState(Transform t);
	
	public Transform getMotionState();
	
	public void setPos(Vector3f v);
	
	public Vector3f getPos();
	
	public void setVisible(boolean b);
	
	public boolean isVisible();
	
	public void setId(int id);
	
	public int getId();
	
	public void setWorld(World world);
	
	public World getWorld();
	
	public Vector3f getPosToMid();

	public BoundingSphere getBoundingSphere();
	
	public BoundingAxis getBoundingAxis();

	public Entity copy();
	
	public void createPhysicsModel();
	
	public void preparePhysicsModel();

}
