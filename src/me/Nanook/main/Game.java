package me.Nanook.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;

import me.Nanook.util.mathHelper;
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
	
	private vec3d vCamera;
	private vec3d vLight;
    
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
		this.vCamera = new vec3d(0, 0, 0);
		this.vLight = new vec3d(0, 0, -1);
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
			triRotatedX = new triangle(mathHelper.MultiplyMartixVector(tri.vec3dList[0], matRotX),
									   mathHelper.MultiplyMartixVector(tri.vec3dList[1], matRotX),
									   mathHelper.MultiplyMartixVector(tri.vec3dList[2], matRotX));
			
			// rotation y
			triRotatedY = new triangle(mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[0], matRotY),
									   mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[1], matRotY),
									   mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[2], matRotY));
			
			// rotation z
			triRotatedZ = new triangle(mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[0], matRotZ),
									   mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[1], matRotZ),
									   mathHelper.MultiplyMartixVector(triRotatedX.vec3dList[2], matRotZ));
			
			// translate triangle
			triTranslated = triRotatedZ.deepCopy();
			triTranslated.vec3dList[0].z += offset;
			triTranslated.vec3dList[1].z += offset;
			triTranslated.vec3dList[2].z += offset;
			
			vec3d normal, line1, line2;
			line1 = new vec3d(triTranslated.vec3dList[1].x - triTranslated.vec3dList[0].x,
							  triTranslated.vec3dList[1].y - triTranslated.vec3dList[0].y,
							  triTranslated.vec3dList[1].z - triTranslated.vec3dList[0].z);
			
			line2 = new vec3d(triTranslated.vec3dList[2].x - triTranslated.vec3dList[0].x,
					  		  triTranslated.vec3dList[2].y - triTranslated.vec3dList[0].y,
					  		  triTranslated.vec3dList[2].z - triTranslated.vec3dList[0].z);
			
			// cross-product for normal calculation
			normal = new vec3d(line1.y * line2.z - line1.z * line2.y,
							   line1.z * line2.x - line1.x * line2.z,
							   line1.x * line2.y - line1.y * line2.x);
			
			// It's normally normal to normalize the normal
			float l = (float) Math.sqrt(normal.x*normal.x + normal.y*normal.y + normal.z*normal.z);
			normal.x /= l; normal.y /= l; normal.z /= l;
			
			if(normal.x * (triTranslated.vec3dList[0].x - vCamera.x) + 
			   normal.y * (triTranslated.vec3dList[0].y - vCamera.y) +
			   normal.z * (triTranslated.vec3dList[0].z - vCamera.z) < 0.0f)
			{
				vLight = new vec3d(0, 0, -1);
				float length = (float) Math.sqrt(vLight.x*vLight.x + vLight.y*vLight.y + vLight.z*vLight.z);
				vLight.x /= length; vLight.y /= length; vLight.z /= length;

				// How similar is normal to light direction
				float dp = (float) (normal.x * vLight.x + normal.y * vLight.y + normal.z * vLight.z);
				
				// project from 3d -> 2d
				triProjected = new triangle(mathHelper.MultiplyMartixVector(triTranslated.vec3dList[0], matProj),
									   		mathHelper.MultiplyMartixVector(triTranslated.vec3dList[1], matProj),
									   		mathHelper.MultiplyMartixVector(triTranslated.vec3dList[2], matProj));
				
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
				
				// calculate color
				int color = (int) (dp * 255);
				if(color < 0) {color = 0;}
				else if(color > 255) {color = 255;}
				
				// draw triangle
				g.setColor(new Color(color, color, color));
				g.fillPolygon(new int[] {(int) triProjected.vec3dList[0].x, (int) triProjected.vec3dList[1].x, (int) triProjected.vec3dList[2].x},
						  	  new int[] {(int) triProjected.vec3dList[0].y, (int) triProjected.vec3dList[1].y, (int) triProjected.vec3dList[2].y}, 3);
				
				g.setColor(Color.black);
				g.drawPolygon(new int[] {(int) triProjected.vec3dList[0].x, (int) triProjected.vec3dList[1].x, (int) triProjected.vec3dList[2].x},
					  	  new int[] {(int) triProjected.vec3dList[0].y, (int) triProjected.vec3dList[1].y, (int) triProjected.vec3dList[2].y}, 3);
			}
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
}
