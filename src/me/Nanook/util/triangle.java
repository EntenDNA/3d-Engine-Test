package me.Nanook.util;

public class triangle implements Comparable{
	
	public vec3d[] vec3dList = new vec3d[3];
	public int shade;
	
	public triangle(vec3d v1, vec3d v2, vec3d v3)
	{
		this.vec3dList[0] = v1;
		this.vec3dList[1] = v2;
		this.vec3dList[2] = v3;
	}
	
	public triangle deepCopy()
	{
		return new triangle(new vec3d(this.vec3dList[0].x, this.vec3dList[0].y, this.vec3dList[0].z),
							new vec3d(this.vec3dList[1].x, this.vec3dList[1].y, this.vec3dList[1].z),
							new vec3d(this.vec3dList[2].x, this.vec3dList[2].y, this.vec3dList[2].z));
	}

	@Override
	public int compareTo(Object o)
	{
		triangle tri = (triangle) o;
		double thisZ = (this.vec3dList[0].z + this.vec3dList[1].z + this.vec3dList[2].z) / 3;
		double otherZ = (tri.vec3dList[0].z + tri.vec3dList[1].z + tri.vec3dList[2].z) / 3;
		return (int) (otherZ - thisZ);
	}
}
