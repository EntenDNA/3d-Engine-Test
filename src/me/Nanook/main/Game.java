package me.Nanook.main;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;

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
    private double fAspectRatio;
	private double fFovRad;
	private double fTheta;
	private double offset;
    
    private double[][] matProj;
    private double[][] matRotX;
    private double[][] matRotY;
    private double[][] matRotZ;
	
	public Game(String title, int width, int height)
	{
		this.HEIGHT = height;
		this.WIDTH = width;
		this.TITLE = title;
		
		this.fAspectRatio = HEIGHT/WIDTH;
		this.fFovRad = 1 / Math.tan(fFov * 0.5 / 180 * 3.1415);
	    
		this.matProj = new double[][] {{fAspectRatio * fFovRad, 0, 0, 0},
							     		{0, fFovRad, 0, 0},
							     		{0, 0, fFar / (fFar - fNear), 1},
							     		{0, 0, (-fFar * fNear) / (fFar - fNear), 0}};
							     				
		this.fTheta = 0;
		this.offset = 3;
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
		
		// calculate rotation matrices
		fTheta += 0.0002;
		double[][] matRotZ = {{Math.cos(fTheta), -Math.sin(fTheta), 0, 0},
				   			  {Math.sin(fTheta), Math.cos(fTheta), 0, 0},
				   			  {0, 0, 1, 0},
				   			  {0, 0, 0, 1}};
								
		double[][] matRotY = {{Math.cos(fTheta*0.3), 0, Math.sin(fTheta*0.3), 0},
	   			  			  {0, 1, 0, 0},
	   			  			  {-Math.sin(fTheta*0.3), 0, Math.cos(fTheta*0.3), 0},
	   			  			  {0, 0, 0, 1}};
							
		double[][] matRotX = {{1, 0, 0, 0},
		  			  		  {0, Math.cos(fTheta *0.5), -Math.sin(fTheta *0.5), 0},
		  			  		  {0, Math.sin(fTheta *0.5), Math.cos(fTheta *0.5), 0},
		  			  		  {0, 0, 0, 1}};
					
		for (triangle tri : meshCube.tris)
		{
			triangle triProjected, triTranslated, triRotatedX, triRotatedY, triRotatedZ;
			
			// rotation x
			triRotatedX = new triangle(MultiplyMartixVector(tri.vec3dList[0], matRotX),
										MultiplyMartixVector(tri.vec3dList[1], matRotX),
										MultiplyMartixVector(tri.vec3dList[2], matRotX));
			
			// rotation y
			triRotatedY = new triangle(MultiplyMartixVector(triRotatedX.vec3dList[0], matRotY),
										MultiplyMartixVector(triRotatedX.vec3dList[1], matRotY),
										MultiplyMartixVector(triRotatedX.vec3dList[2], matRotY));
			
			// rotation z
			triRotatedZ = new triangle(MultiplyMartixVector(triRotatedX.vec3dList[0], matRotZ),
										MultiplyMartixVector(triRotatedX.vec3dList[1], matRotZ),
										MultiplyMartixVector(triRotatedX.vec3dList[2], matRotZ));
			
			// translate triangle
			triTranslated = triRotatedZ.deepCopy();
			triTranslated.vec3dList[0].z += offset;
			triTranslated.vec3dList[1].z += offset;
			triTranslated.vec3dList[2].z += offset;
			
			// project onto screen
			triProjected = new triangle(MultiplyMartixVector(triTranslated.vec3dList[0], matProj),
										MultiplyMartixVector(triTranslated.vec3dList[1], matProj),
										MultiplyMartixVector(triTranslated.vec3dList[2], matProj));
			
			// scale into view
			triProjected.vec3dList[0].x += 1.0; triProjected.vec3dList[0].y += 1.0;
			triProjected.vec3dList[1].x += 1.0; triProjected.vec3dList[1].y += 1.0;
			triProjected.vec3dList[2].x += 1.0; triProjected.vec3dList[2].y += 1.0;
			triProjected.vec3dList[0].x *= 0.5 * this.WIDTH;
			triProjected.vec3dList[0].y *= 0.5 * this.HEIGHT;
			triProjected.vec3dList[1].x *= 0.5 * this.WIDTH;
			triProjected.vec3dList[1].y *= 0.5 * this.HEIGHT;
			triProjected.vec3dList[2].x *= 0.5 * this.WIDTH;
			triProjected.vec3dList[2].y *= 0.5 * this.HEIGHT;
			
			// draw triangle
			g.drawPolygon(new int[] {(int) triProjected.vec3dList[0].x, (int) triProjected.vec3dList[1].x, (int) triProjected.vec3dList[2].x},
						  new int[] {(int) triProjected.vec3dList[0].y, (int) triProjected.vec3dList[1].y, (int) triProjected.vec3dList[2].y}, 3);
		}
		
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
