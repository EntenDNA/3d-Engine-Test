package me.Nanook.main;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

import me.Nanook.util.mesh;
import me.Nanook.util.triangle;
import me.Nanook.util.vec3d;

public class Game implements Runnable{
	
	private Thread thread;
	private boolean running = false;
	
	private Display display;
	public int WIDTH, HEIGHT;
	public String TITLE;
	
	private BufferStrategy bs;
	private Graphics g;
	
	private triangle[] cubeList = new triangle[]{
			// south
			new triangle(new vec3d(0, 0, 0), new vec3d(0, 1, 0), new vec3d(1, 1, 0)),
			new triangle(new vec3d(0, 0, 0), new vec3d(1, 1, 0), new vec3d(1, 0, 0)),
			
			// east
			new triangle(new vec3d(1, 0, 0), new vec3d(1, 1, 0), new vec3d(1, 1, 1)),
			new triangle(new vec3d(1, 0, 0), new vec3d(1, 1, 1), new vec3d(1, 0, 1)),
			
			// north
			new triangle(new vec3d(1, 0, 1), new vec3d(1, 1, 1), new vec3d(0, 1, 1)),
			new triangle(new vec3d(1, 0, 1), new vec3d(0, 1, 1), new vec3d(0, 0, 1)),
			
			// west
			new triangle(new vec3d(0, 0, 1), new vec3d(0, 1, 1), new vec3d(0, 1, 0)),
			new triangle(new vec3d(0, 0, 1), new vec3d(0, 1, 0), new vec3d(0, 0, 0)),
			
			// top
			new triangle(new vec3d(0, 1, 0), new vec3d(0, 1, 1), new vec3d(1, 1, 1)),
			new triangle(new vec3d(0, 1, 0), new vec3d(1, 1, 1), new vec3d(1, 1, 0)),
			
			// bottom
			new triangle(new vec3d(1, 0, 1), new vec3d(0, 0, 1), new vec3d(0, 0, 0)),
			new triangle(new vec3d(1, 0, 1), new vec3d(0, 0, 0), new vec3d(1, 0, 0))
	};
	
	private ArrayList<triangle> cubeArrayList = new ArrayList<triangle>(Arrays.asList(cubeList));
	private mesh meshCube = new mesh(cubeArrayList);
											   
    private double fNear = 0.1f;
    private double fFar = 1000;
    private double fFov = 90;
    private double fAspectRatio = HEIGHT/WIDTH;
	private double fFovRad = 1 / Math.tan(fFov * 0.5 / 180 * 3.1415);
    
    private double[][] matProj = new double[][] {{fAspectRatio * fFovRad, 0, 0, 0},
											     {0, fFovRad, 0, 0},
											     {0, 0, fFar / (fFar - fNear), 1},
											     {0, 0, (-fFar * fNear) / (fFar - fNear), 0}};
	
	public Game(String title, int width, int height)
	{
		this.HEIGHT = height;
		this.WIDTH = width;
		this.TITLE = title;
	}
	
	private void init()
	{
		display = new Display(TITLE, WIDTH, HEIGHT);
	}
	
	private void tick()
	{
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null)
		{
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		// clear screen
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		for (triangle tri : meshCube.tris)
		{
			triangle triProjected = new triangle(MultiplyMartixVector(tri.vec3dList[0], matProj),
												MultiplyMartixVector(tri.vec3dList[1], matProj),
												MultiplyMartixVector(tri.vec3dList[2], matProj));
			
			g.drawPolygon(new int[] {triProjected.vec3dList[0].x, triProjected.vec3dList[1].x, triProjected.vec3dList[2].x},
						  new int[] {triProjected.vec3dList[0].y, triProjected.vec3dList[1].y, triProjected.vec3dList[2].y}, 3);
		}
		
		// end drawing
		bs.show();
		g.dispose();
		
	}
	
	private void render()
	{
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null)
		{
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		// clear screen
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		// start drawing
		
		// end drawing
		bs.show();
		g.dispose();
	}
	
	@Override
	public void run() 
	{
		init();
		
		while(running)
		{
			tick();
			render();
		}
		
		stop();
	}
	
	public synchronized void start()
	{
		if(running != true)
		{
			running = true;
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public synchronized void stop()
	{
		if(running == true)
		{
			running = false;
			try 
			{
				thread.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public vec3d MultiplyMartixVector(vec3d vec, double[][] matrix)
	{
		vec3d vecOut = new vec3d(0, 0, 0);
		vecOut.x = vec.x * matrix[0][0] + vec.y * matrix[1][0] + vec.z * matrix[2][0] + matrix[3][0];
		vecOut.y = vec.x * matrix[0][1] + vec.y * matrix[1][1] + vec.z * matrix[2][1] + matrix[3][1];
	    vecOut.z = vec.x * matrix[0][2] + vec.y * matrix[1][2] + vec.z * matrix[2][2] + matrix[3][2];
	    double w = vec.x * matrix[0][3] + vec.y * matrix[1][3] + vec.z * matrix[2][3] + matrix[3][3];
	    
	    if(w != 0)
	    {
	    	vecOut.x /= w;
	    	vecOut.y /= w;
	    	vecOut.z /= w;
	    }
	    
		return vecOut;
	}
}
