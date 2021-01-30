package me.Nanook.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;

import me.Nanook.util.mathHelper;
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
	
	private Cube[] cubeList = new Cube[100];
	private Cube[] updateCubeList;
	private int counter;
											   
    private double fNear = 0.1f;
    private double fFar = 1000;
    private double fFov = 90;
    private double fAspectRatio;
	
	protected vec3d vCamera;
	protected vec3d vLookDir;
	protected vec3d vRight;
	protected float fYaw;
	private vec3d vLight;
	private Player player;
    
	private double[][] matTrans;
	private double[][] matWorld;
    private double[][] matProj;
    private double[][] matRotY;
    
    private ArrayList<triangle> triList;
    
    protected KeyManager keyManager;
    protected boolean showDebug;
	
	public Game(String title, int width, int height)
	{
		this.HEIGHT = height;
		this.WIDTH = width;
		this.TITLE = title;
		
		this.fAspectRatio = HEIGHT/WIDTH;
	    
		this.matProj = mathHelper.matrixMakeProjection(this.fFov, this.fAspectRatio, this.fNear, this.fFar);
		
		this.vCamera = new vec3d(0, 0, 0);
		this.vLight = new vec3d(-1.5, 0, -1);
		this.vLookDir = new vec3d(0, 0, 0);
		
		keyManager = new KeyManager();
		player = new Player(this);
		
		// make map
		int idx = 0;
		for(int i=0; i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				cubeList[idx] = new Cube(i, 0, j);
				idx++;
			}
		}
		updateCubeList = Cube.updateSides(cubeList);
	}
	
	private void init()
	{
		display = new Display(TITLE, WIDTH, HEIGHT);
		display.getFrame().addKeyListener(keyManager);
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
		
		// update position and orientation
		player.updateMovement();
		
		vec3d vUp = new vec3d(0, 1, 0);
		vec3d vTarget = new vec3d(0, 0, 1);
		matRotY = mathHelper.matrixMakeRotationY(fYaw);
		vLookDir = mathHelper.multiplyMartixVector(vTarget, matRotY);
		vTarget = mathHelper.vectorAdd(vCamera, vLookDir);
		
		// right vector is cross-product of vUp and vLookDir
		vRight = mathHelper.vectorCrossProduct(vUp, vLookDir);
		
		double[][] matCamera = mathHelper.matrixPointAt(vCamera, vTarget, vUp);
		
		// make view matrix from camera
		double[][] matView = mathHelper.matrixQuickInverse(matCamera);
		
		// triangle array
		triList = new ArrayList<triangle>();
		
		// math
		for(Cube cube : updateCubeList)
		{
			for (triangle tri : cube.getMesh().tris)
			{
				// check if side has to be rendered or its blocked from view
				if((counter >= 0 && counter < 2 && !cube.south) ||
					(counter >= 2 && counter < 4 && !cube.east) ||
					(counter >= 4 && counter < 6 && !cube.north) ||
					(counter >= 6 && counter < 8 && !cube.west) ||
					(counter >= 8 && counter < 10 && !cube.top) ||
					(counter >= 10 && counter < 12 && !cube.bottom))
				{
					counter++;
					continue;
				}

				triangle triProjected, triTransformed, triViewed = null;
				
				matTrans = mathHelper.matrixMakeTranslation(cube.getX(), cube.getY(), cube.getZ()); // what coordinates the cube has
				matWorld = mathHelper.matrixMakeIdentity();
				matWorld = mathHelper.matrixMultiplyMatrix(matWorld, matTrans);
				
				// transform triangle
				triTransformed = new triangle(mathHelper.multiplyMartixVector(tri.vec3dList[0], matWorld),
											  mathHelper.multiplyMartixVector(tri.vec3dList[1], matWorld),
											  mathHelper.multiplyMartixVector(tri.vec3dList[2], matWorld));
				
				vec3d normal, line1, line2;
				line1 = mathHelper.vectorSub(triTransformed.vec3dList[1], triTransformed.vec3dList[0]);
				line2 = mathHelper.vectorSub(triTransformed.vec3dList[2], triTransformed.vec3dList[0]);
				normal = mathHelper.vectorCrossProduct(line1, line2);
				normal = mathHelper.vectorNormalise(normal);
				
				vec3d vCameraRay = mathHelper.vectorSub(triTransformed.vec3dList[0], vCamera);
				
				if(mathHelper.vectorDotProduct(normal, vCameraRay) < 0.0f)
				{
					// light
					vLight = mathHelper.vectorNormalise(vLight);
	
					// how similar is normal to light direction
					float dp = (float) (normal.x * vLight.x + normal.y * vLight.y + normal.z * vLight.z);
					
					// world space to view space
					triViewed = new triangle(mathHelper.multiplyMartixVector(triTransformed.vec3dList[0], matView),
										     mathHelper.multiplyMartixVector(triTransformed.vec3dList[1], matView),
										     mathHelper.multiplyMartixVector(triTransformed.vec3dList[2], matView));
					
					triangle[] clipped;
					clipped = mathHelper.triangleClipAgainstPlane(new vec3d(0, 0, 0.5), new vec3d(0, 0, 0.5), triViewed);
					
					for(int i=0; i<clipped.length; i++)
					{
						// project from 3d -> 2d	
						triProjected = new triangle(mathHelper.multiplyMartixVector(clipped[i].vec3dList[0], matProj),
											   		mathHelper.multiplyMartixVector(clipped[i].vec3dList[1], matProj),
											   		mathHelper.multiplyMartixVector(clipped[i].vec3dList[2], matProj));
						
						// normalize
						triProjected.vec3dList[0] = mathHelper.vectorDiv(triProjected.vec3dList[0], triProjected.vec3dList[0].w);
						triProjected.vec3dList[1] = mathHelper.vectorDiv(triProjected.vec3dList[1], triProjected.vec3dList[1].w);
						triProjected.vec3dList[2] = mathHelper.vectorDiv(triProjected.vec3dList[2], triProjected.vec3dList[2].w);
						
						// scale into view
						triProjected.vec3dList[0] = mathHelper.vectorAdd(triProjected.vec3dList[0], new vec3d(1, 1, 1));
						triProjected.vec3dList[1] = mathHelper.vectorAdd(triProjected.vec3dList[1], new vec3d(1, 1, 1));
						triProjected.vec3dList[2] = mathHelper.vectorAdd(triProjected.vec3dList[2], new vec3d(1, 1, 1));
						triProjected.vec3dList[0] = mathHelper.vectorMul(triProjected.vec3dList[0], 0.5 * this.WIDTH);
						triProjected.vec3dList[1] = mathHelper.vectorMul(triProjected.vec3dList[1], 0.5 * this.WIDTH);
						triProjected.vec3dList[2] = mathHelper.vectorMul(triProjected.vec3dList[2], 0.5 * this.WIDTH);
						
						// calculate color
						int color = (int) (dp * 255);
						if(color < 40) {color = 40;}
						else if(color > 255) {color = 255;}
						
						triProjected.shade = color;
						triList.add(triProjected);
					}
				}
				counter++;
			}
			counter = 0;
		}
		
		// visuals
		for(triangle tri : triList)
		{
			// fill triangle
			g.setColor(new Color(tri.shade, tri.shade, tri.shade));
			g.fillPolygon(new int[] {(int) tri.vec3dList[0].x, (int) tri.vec3dList[1].x, (int) tri.vec3dList[2].x},
					  	  new int[] {(int) tri.vec3dList[0].y, (int) tri.vec3dList[1].y, (int) tri.vec3dList[2].y}, 3);
			
			// draw triangles
			g.setColor(Color.black);
			g.drawPolygon(new int[] {(int) tri.vec3dList[0].x, (int) tri.vec3dList[1].x, (int) tri.vec3dList[2].x},
				  	  new int[] {(int) tri.vec3dList[0].y, (int) tri.vec3dList[1].y, (int) tri.vec3dList[2].y}, 3);
			
		}
		
		// show debug info
		if(this.showDebug)
		{
			g.setColor(Color.BLACK);
			
			g.drawString("Debug", 0, 15);
			g.drawString("X: " + Math.round(vCamera.x * 100) / 100f + " Y: " + Math.round(vCamera.y * 100) / 100f + " Z: " + Math.round(vCamera.z * 100) / 100f, 0, 30);
			g.drawString("Yaw: " + Math.round(fYaw*180/Math.PI * 100) / 100f, 0, 45);
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
