package me.Nanook.main;

import java.util.ArrayList;
import java.util.Arrays;

import me.Nanook.util.mesh;
import me.Nanook.util.triangle;
import me.Nanook.util.vec3d;

public class Cube {
	
	private triangle[] cube;
	private mesh mesh;
	private ArrayList<triangle> cubeArrayList;
	
	private int x;
	private int y;
	private int z;
	
	public boolean south;
	public boolean east;
	public boolean north;
	public boolean west;
	public boolean top;
	public boolean bottom;
	
	public Cube(int x, int y, int z)
	{
		// create the cube mesh
		cube = new triangle[]{
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
		
		cubeArrayList = new ArrayList<triangle>(Arrays.asList(cube));
		mesh = new mesh(cubeArrayList);
		
		// coordinates
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.south = true;
		this.east = true;
		this.north = true;
		this.west = true;
		this.top = true;
		this.bottom = true;
	}
	
	public static Cube[] updateSides(Cube[] cubes)
	{	
		for(Cube cube1 : cubes)
		{
			for(Cube cube2 : cubes)
			{
				// check x coords
				if(cube1.x == cube2.x - 1 && cube1.y == cube2.y && cube1.z == cube2.z)
				{
					cube1.east = false;
				}
				
				if(cube1.x == cube2.x + 1 && cube1.y == cube2.y && cube1.z == cube2.z)
				{
					cube1.west = false;
				}
				
				// check y coords
				if(cube1.x == cube2.x && cube1.y == cube2.y - 1 && cube1.z == cube2.z)
				{
					cube1.top = false;
				}
				
				if(cube1.x == cube2.x && cube1.y == cube2.y + 1 && cube1.z == cube2.z)
				{
					cube1.bottom = false;
				}
				
				// check z coords
				if(cube1.x == cube2.x && cube1.y == cube2.y && cube1.z == cube2.z - 1)
				{
					cube1.north = false;
				}
				
				if(cube1.x == cube2.x && cube1.y == cube2.y && cube1.z == cube2.z + 1)
				{
					cube1.south = false;
				}
			}
		}
		return cubes;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public mesh getMesh()
	{
		return mesh;
	}
}
