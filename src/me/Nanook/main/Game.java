package me.Nanook.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	private double fTheta;
	
	protected vec3d vCamera;
	protected vec3d vLookDir;
	protected vec3d vRight;
	protected float fYaw;
	private vec3d vLight;
	private Player player;
    
    private double[][] matProj;
    private double[][] matRotX;
    private double[][] matRotY;
    private double[][] matRotZ;
    private ArrayList<triangle> triList;
    
    protected KeyManager keyManager;
	
	public Game(String title, int width, int height)
	{
		this.HEIGHT = height;
		this.WIDTH = width;
		this.TITLE = title;
		
		this.fAspectRatio = HEIGHT/WIDTH;
	    
		this.matProj = mathHelper.matrixMakeProjection(this.fFov, this.fAspectRatio, this.fNear, this.fFar);
		
		this.fTheta = 0;
		this.vCamera = new vec3d(0, 0, 0);
		this.vLight = new vec3d(0, 0, -1);
		this.vLookDir = new vec3d(0, 0, 0);
		
		keyManager = new KeyManager();
		player = new Player(this);
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
		
		// calculate rotation matrices
		//fTheta += 0.0002;
		matRotX = mathHelper.matrixMakeRotationX(fTheta);			
		matRotZ = mathHelper.matrixMakeRotationZ(fTheta);
		
		// transformation and rotation matrices
		double[][] matTrans, matWorld;
		matTrans = mathHelper.matrixMakeTranslation(0, 0, 3); // what coordinates the cube has
		matWorld = mathHelper.matrixMakeIdentity();
		matWorld = mathHelper.matrixMultiplyMatrix(matRotZ, matRotX);
		matWorld = mathHelper.matrixMultiplyMatrix(matWorld, matTrans);
		
		vec3d vUp = new vec3d(0, 1, 0);
		vec3d vTarget = new vec3d(0, 0, 1);
		matRotY = mathHelper.matrixMakeRotationY(fYaw);
		vLookDir = mathHelper.multiplyMartixVector(vTarget, matRotY);
		vTarget = mathHelper.vectorAdd(vCamera, vLookDir);
		
		// right vector is crossproduct of vUp and vLookDir
		vRight = mathHelper.vectorCrossProduct(vUp, vLookDir);
		
		double[][] matCamera = mathHelper.matrixPointAt(vCamera, vTarget, vUp);
		
		// make view matrix from camera
		double[][] matView = mathHelper.matrixQuickInverse(matCamera);
		
		// triangle array
		triList = new ArrayList<triangle>();
		
		// math
		for (triangle tri : meshCube.tris)
		{
			triangle triProjected, triTransformed, triViewed = null;
			
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
				vLight = new vec3d(0, 0, -1);
				vLight = mathHelper.vectorNormalise(vLight);

				// How similar is normal to light direction
				float dp = (float) (normal.x * vLight.x + normal.y * vLight.y + normal.z * vLight.z);
				
				// world space to view space
				triViewed = new triangle(mathHelper.multiplyMartixVector(triTransformed.vec3dList[0], matView),
									     mathHelper.multiplyMartixVector(triTransformed.vec3dList[1], matView),
									     mathHelper.multiplyMartixVector(triTransformed.vec3dList[2], matView));
				
				// project from 3d -> 2d	
				triProjected = new triangle(mathHelper.multiplyMartixVector(triViewed.vec3dList[0], matProj),
									   		mathHelper.multiplyMartixVector(triViewed.vec3dList[1], matProj),
									   		mathHelper.multiplyMartixVector(triViewed.vec3dList[2], matProj));
				
				// normalize
				triProjected.vec3dList[0] = mathHelper.vectorDiv(triProjected.vec3dList[0], triProjected.vec3dList[0].w);
				triProjected.vec3dList[1] = mathHelper.vectorDiv(triProjected.vec3dList[1], triProjected.vec3dList[1].w);
				triProjected.vec3dList[2] = mathHelper.vectorDiv(triProjected.vec3dList[2], triProjected.vec3dList[2].w);
				
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
				
				triProjected.shade = color;
				triList.add(triProjected);
			}
		}
		
		// visuals
		for(triangle tri : triList)
		{
			// fill triangle
			g.setColor(new Color(tri.shade, tri.shade, tri.shade));
			g.fillPolygon(new int[] {(int) tri.vec3dList[0].x, (int) tri.vec3dList[1].x, (int) tri.vec3dList[2].x},
					  	  new int[] {(int) tri.vec3dList[0].y, (int) tri.vec3dList[1].y, (int) tri.vec3dList[2].y}, 3);
			
			// draw triangles
			/*g.setColor(Color.black);
			g.drawPolygon(new int[] {(int) tri.vec3dList[0].x, (int) tri.vec3dList[1].x, (int) tri.vec3dList[2].x},
				  	  new int[] {(int) tri.vec3dList[0].y, (int) tri.vec3dList[1].y, (int) tri.vec3dList[2].y}, 3);*/
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
