package game;

import game.world.World;
import game.world.gui.graphics.Graphics2D;
import game.world.sync.RequestManager;

import java.util.Arrays;

import controller.Camera;

public abstract class State {
	
	//If false, renderThread sleeps
	private boolean newStuffToRender = true;
	
	//Multithreading rendering handling (synchronizing update and render threads)
	RenderState[] renderStates = new RenderState[3];
	private RequestManager syncManager = new RequestManager();
	
	public State(){
		renderStates[0] = new RenderState(this, 0, 0);
		renderStates[1] = new RenderState(this, 1, -1);
		renderStates[2] = new RenderState(this, 2, -1);
		linkWorlds(renderStates[0].getWorld(), renderStates[1].getWorld(), renderStates[2].getWorld());
	}
	
	/**
	 * Gives all worlds same dynamic world and camera
	 * @param worlds
	 */
	private void linkWorlds(World...worlds){
		boolean first = true;
		World mainWorld = null;
		Camera mainCam = null;
		for(World w: worlds){
			if(first){
				mainWorld = w;
				w.setUpPhysics();
				mainCam = w.getCamera();
				mainCam.createCamera();
				first = false;
				continue;
			}
			w.linkCamera(mainCam);
			w.setDynamicsWorld(mainWorld.getDynamicsWorld());
		}
	}
	
	//ABSTRACT
	public abstract void init();
	
	public abstract void update(float dt);
	
	public abstract void render(Graphics2D g);
	
	public abstract void dispose();
	
	public abstract int getId();
	
	//SET
	public void setStuffToRender(boolean b){
		newStuffToRender = b;
	}
	
	//GET
	/**
	 * Used for getting frame which is the most up to date for rendering.
	 * @return RenderState which has the highest frame count and is not being updated.
	 */
	public RenderState getLatestState(){
		RenderState latestState = null;
		int highestFrame = 0;
		for(RenderState state: renderStates){
			//Select world which has higher frame count and is not updating
			if(state.getFrameCount() >= highestFrame && !state.isUpdating()){
				latestState = state;
				highestFrame = state.getFrameCount();
			}	
		}
		return latestState;
	}
	
	/**
	 * Used for getting state for updating.
	 * @return RenderState which has the lowest frame count and is not read-only.
	 */
	public RenderState getOldestState(){
		RenderState oldestState = null; 
		int lowestFrame = -2;
		for(RenderState state: renderStates){
			//Must not be rendering and has lower frame count
			if(!state.isRendering() && (lowestFrame == -2 || state.getFrameCount() <= lowestFrame)){
				oldestState = state;
				lowestFrame = state.getFrameCount();
			}		
		}
		return oldestState;
	}
	
	/**
	 * Overview of all RenderStates frame counts.
	 * @return
	 */
	public String getStatesCounts(){
		int[] counts = {0,0,0};
		int i = 0;
		for(RenderState state: renderStates){
			counts[i] = state.getFrameCount();
			i++;
		}
		return Arrays.toString(counts);
	}
	
	public RequestManager getSyncManager(){
		return syncManager;
	}
	
	public RenderState getUpdatingState(){
		return renderStates[RenderState.updatingId];
	}
	
	public RenderState getRenderingState(){
		return renderStates[RenderState.renderingId];
	}
	
	public RenderState getUpToDateState(){
		return renderStates[RenderState.upToDateId];
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
	public boolean newStuffToRender(){
		return newStuffToRender;
	}

}