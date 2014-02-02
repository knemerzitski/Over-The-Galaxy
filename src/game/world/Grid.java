package game.world;

import java.util.ArrayList;

import math.Vector3fc;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import game.world.entities.Line;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.SyncManager;

public class Grid {
	
	//configure grid
	private int xsize = 800; //Size of lines parallel to x axis
	private int zsize = 800; //Size of lines parallel to z axis
	private int gap = 20; //Distance between two lines
	private boolean boldZeroAxisLines = true;
	
	private ArrayList<Line> xlines = new ArrayList<Line>();
	private ArrayList<Line> zlines = new ArrayList<Line>();
	
	World world;
	
	
	public Grid(World world){
		this.world = world;
		//add x lines
		SyncManager sync = world.getState().getSyncManager();
		for(int i=0;i<(int)(zsize/gap);i++){
			Line l = new Line(new Vector3fc(0,0,0), new Vector3fc(xsize,0,0));
			Request request = new RenderRequest(Action.CREATEVBO, l);
			sync.add(request);
			xlines.add(l);
		}
		//add z lines
		for(int i=0;i<(int)(xsize/gap);i++){
			Line l = new Line(new Vector3fc(0,0,0), new Vector3fc(0,0,zsize));
			Request request = new RenderRequest(Action.CREATEVBO, l);
			sync.add(request);
			zlines.add(l);
		}
	}
	
	public void addToWorld(World world){
		for(Line l: xlines)
			world.addEntity(l);
		for(Line l: zlines)
			world.addEntity(l);
	}
	
	public Vector3f getCamPos(){
		return world.getCamera().getPos();
	}
	
	public void update(){
		//set lines to correct pos
		Vector3f cam = getCamPos();
		int i = (int)(cam.z-zsize/2)/gap;
		for(Line xline: xlines){
			Vector3f pos = xline.getPos();
			int zpos = i*gap;
			if(boldZeroAxisLines)
				if(zpos == 0)
					xline.setLineWidth(5);
				else
					xline.setLineWidth(1);
			pos.z = zpos;
			pos.x = cam.x-xline.length()/2;
			i++;
		}
		i = (int)(cam.x-xsize/2)/gap;
		for(Line zline: zlines){
			Vector3f pos = zline.getPos();
			int xpos = i*gap;
			if(boldZeroAxisLines)
				if(xpos == 0)
					zline.setLineWidth(5);
				else
					zline.setLineWidth(1);
			pos.x = i*gap;
			pos.z = cam.z-zline.length()/2;
			i++;
		}
		
		
	}
	
	public void render(){
		GL11.glColor3f(1f, 1f, 1f);
		for(Line l: xlines){
			if(l.isVisible())
				l.render();
		}
		for(Line l: zlines){
			if(l.isVisible())
				l.render();
		}
	}
	
	public void dispose(){
		for(Line l: xlines){
			l.dispose();
		}
		for(Line l: zlines){
			l.dispose();
		}
	}
	
	public ArrayList<Line> getLinesX(){
		return xlines;
	}
	
	public ArrayList<Line> getLinesZ(){
		return zlines;
	}

}
