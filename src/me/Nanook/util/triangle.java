package me.Nanook.util;

public class triangle {
	
	public vec3d[] vec3dList = new vec3d[3];
	
	public triangle(vec3d v1, vec3d v2, vec3d v3)
	{
		this.vec3dList[0] = v1;
		this.vec3dList[1] = v2;
		this.vec3dList[2] = v3;
	}
}
