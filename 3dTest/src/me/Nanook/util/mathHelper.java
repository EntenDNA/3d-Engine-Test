package me.Nanook.util;

import java.util.ArrayList;

public class mathHelper {
	
	public static vec3d multiplyMartixVector(vec3d i, double[][] m)
	{
		vec3d v = new vec3d(0, 0, 0);
		v.x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + i.w * m[3][0];
		v.y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + i.w * m[3][1];
		v.z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + i.w * m[3][2];
		v.w = i.x * m[0][3] + i.y * m[1][3] + i.z * m[2][3] + i.w * m[3][3];
		
		return v;
	}
	
	public static double[][] matrixMakeIdentity()
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = 1.0f;
		matrix[1][1] = 1.0f;
		matrix[2][2] = 1.0f;
		matrix[3][3] = 1.0f;
		
		return matrix;
	}
	
	public static double[][] matrixMakeRotationX(double fAngleRad)
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = 1.0f;
		matrix[1][1] = Math.cos(fAngleRad);
		matrix[1][2] = Math.sin(fAngleRad);
		matrix[2][1] = -Math.sin(fAngleRad);
		matrix[2][2] = Math.cos(fAngleRad);
		matrix[3][3] = 1.0f;
		
		return matrix;
	}

	public static double[][] matrixMakeRotationY(double fAngleRad)
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = Math.cos(fAngleRad);
		matrix[0][2] = Math.sin(fAngleRad);
		matrix[2][0] = -Math.sin(fAngleRad);
		matrix[1][1] = 1.0f;
		matrix[2][2] = Math.cos(fAngleRad);
		matrix[3][3] = 1.0f;
		
		return matrix;
	}

	public static double[][] matrixMakeRotationZ(double fAngleRad)
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = Math.cos(fAngleRad);
		matrix[0][1] = Math.sin(fAngleRad);
		matrix[1][0] = -Math.sin(fAngleRad);
		matrix[1][1] = Math.cos(fAngleRad);
		matrix[2][2] = 1.0f;
		matrix[3][3] = 1.0f;
		
		return matrix;
	}
	
	public static double[][] matrixMakeTranslation(float x, float y, float z)
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = 1.0f;
		matrix[1][1] = 1.0f;
		matrix[2][2] = 1.0f;
		matrix[3][3] = 1.0f;
		matrix[3][0] = x;
		matrix[3][1] = y;
		matrix[3][2] = z;
		
		return matrix;
	}

	public static double[][] matrixMakeProjection(double fFovDegrees, double fAspectRatio, double fNear, double fFar)
	{
		float fFovRad = (float) (1 / Math.tan(fFovDegrees * 0.5 / 180 * 3.1415));
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		matrix[0][0] = fAspectRatio * fFovRad;
		matrix[1][1] = fFovRad;
		matrix[2][2] = fFar / (fFar - fNear);
		matrix[3][2] = (-fFar * fNear) / (fFar - fNear);
		matrix[2][3] = 1.0f;
		
		return matrix;
	}
	
	public static double[][] matrixMakeOrthographic(double left, double right, double bottom, double top, double fNear, double fFar)
	{
		double[][] matrix = new double[][] {{2/(right-left), 0, 0, -(right+left)/(right-left)},
											{0, 2/(top-bottom), 0, -(top+bottom)/(top-bottom)},
											{0, 0, -2/(fFar-fNear), -(fFar+fNear)/(fFar-fNear)},
											{0, 0, 0, 1}};
		
		return matrix;
	}
	
	public static double[][] matrixMultiplyMatrix(double[][] m1, double[][] m2)
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
		for (int c = 0; c < 4; c++)
			for (int r = 0; r < 4; r++)
				matrix[r][c] = m1[r][0] * m2[0][c] + m1[r][1] * m2[1][c] + m1[r][2] * m2[2][c] + m1[r][3] * m2[3][c];
		
		return matrix;
	}
	
	public static double[][] matrixPointAt(vec3d pos, vec3d target, vec3d up)
	{
		// Calculate new forward direction
		vec3d newForward = vectorSub(target, pos);
		newForward = vectorNormalise(newForward);

		// Calculate new Up direction
		vec3d a = vectorMul(newForward, vectorDotProduct(up, newForward));
		vec3d newUp = vectorSub(up, a);
		newUp = vectorNormalise(newUp);

		// New Right direction is easy, its just cross product
		vec3d newRight = vectorCrossProduct(newUp, newForward);

		// Construct Dimensioning and Translation Matrix	
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
											
		matrix[0][0] = newRight.x;		matrix[0][1] = newRight.y;		matrix[0][2] = newRight.z;		matrix[0][3] = 0.0f;
		matrix[1][0] = newUp.x;			matrix[1][1] = newUp.y;			matrix[1][2] = newUp.z;			matrix[1][3] = 0.0f;
		matrix[2][0] = newForward.x;	matrix[2][1] = newForward.y;	matrix[2][2] = newForward.z;	matrix[2][3] = 0.0f;
		matrix[3][0] = pos.x;			matrix[3][1] = pos.y;			matrix[3][2] = pos.z;			matrix[3][3] = 1.0f;
		return matrix;
	}
	
	public static double[][] matrixQuickInverse(double[][] m) // Only for Rotation/Translation Matrices
	{
		double[][] matrix = new double[][] {{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0},
											{0, 0, 0, 0}};
											
		matrix[0][0] = m[0][0]; matrix[0][1] = m[1][0]; matrix[0][2] = m[2][0]; matrix[0][3] = 0.0f;
		matrix[1][0] = m[0][1]; matrix[1][1] = m[1][1]; matrix[1][2] = m[2][1]; matrix[1][3] = 0.0f;
		matrix[2][0] = m[0][2]; matrix[2][1] = m[1][2]; matrix[2][2] = m[2][2]; matrix[2][3] = 0.0f;
		matrix[3][0] = -(m[3][0] * matrix[0][0] + m[3][1] * matrix[1][0] + m[3][2] * matrix[2][0]);
		matrix[3][1] = -(m[3][0] * matrix[0][1] + m[3][1] * matrix[1][1] + m[3][2] * matrix[2][1]);
		matrix[3][2] = -(m[3][0] * matrix[0][2] + m[3][1] * matrix[1][2] + m[3][2] * matrix[2][2]);
		matrix[3][3] = 1.0f;
		return matrix;
	}
	
	public static vec3d vectorAdd(vec3d v1, vec3d v2)
	{
		return new vec3d(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static vec3d vectorSub(vec3d v1, vec3d v2)
	{
		return new vec3d(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static vec3d vectorMul(vec3d v1, double vel)
	{
		return new vec3d(v1.x * vel, v1.y * vel, v1.z * vel);
	}

	public static vec3d vectorDiv(vec3d v1, double w)
	{
		return new vec3d(v1.x / w, v1.y / w, v1.z / w);
	}

	public static float vectorDotProduct(vec3d v1, vec3d v2)
	{
		return (float) (v1.x*v2.x + v1.y*v2.y + v1.z * v2.z);
	}

	public static float vectorLength(vec3d v)
	{
		return (float) Math.sqrt(vectorDotProduct(v, v));
	}

	public static vec3d vectorNormalise(vec3d v)
	{
		float l = vectorLength(v);
		return new vec3d(v.x / l, v.y / l, v.z / l );
	}

	public static vec3d vectorCrossProduct(vec3d v1, vec3d v2)
	{
		vec3d v = new vec3d(0, 0, 0);
		v.x = v1.y * v2.z - v1.z * v2.y;
		v.y = v1.z * v2.x - v1.x * v2.z;
		v.z = v1.x * v2.y - v1.y * v2.x;
		return v;
	}
	
	public static vec3d vectorIntersectPlane(vec3d plane_p, vec3d plane_n, vec3d lineStart, vec3d lineEnd)
	{
		plane_n = vectorNormalise(plane_n);
		float plane_d = -vectorDotProduct(plane_n, plane_p);
		float ad = vectorDotProduct(lineStart, plane_n);
		float bd = vectorDotProduct(lineEnd, plane_n);
		float t = (-plane_d - ad) / (bd - ad);
		vec3d lineStartToEnd = vectorSub(lineEnd, lineStart);
		vec3d lineToIntersect = vectorMul(lineStartToEnd, t);
		return vectorAdd(lineStart, lineToIntersect);
	}
	
	public static triangle[] triangleClipAgainstPlane(vec3d planeP, vec3d planeN, triangle tri)
	{
		// normalize normal if necessary
		planeN = vectorNormalise(planeN);
		
		vec3d[] insidePoints = new vec3d[3];
		vec3d[] outsidePoints = new vec3d[3];
		int nInsidePointCount = 0;
		int nOutsidePointCount = 0;
		
		float d0 = dist(planeP, planeN, tri.vec3dList[0]);
		float d1 = dist(planeP, planeN, tri.vec3dList[1]);
		float d2 = dist(planeP, planeN, tri.vec3dList[2]);
		
		if (d0 >= 0) { insidePoints[nInsidePointCount++] = tri.vec3dList[0]; }
		else { outsidePoints[nOutsidePointCount++] = tri.vec3dList[0]; }
		if (d1 >= 0) { insidePoints[nInsidePointCount++] = tri.vec3dList[1]; }
		else { outsidePoints[nOutsidePointCount++] = tri.vec3dList[1]; }
		if (d2 >= 0) { insidePoints[nInsidePointCount++] = tri.vec3dList[2]; }
		else { outsidePoints[nOutsidePointCount++] = tri.vec3dList[2]; }
		
		triangle outTri1 = null;
		triangle outTri2 = null;

		if (nInsidePointCount == 3)
		{
			outTri1 = tri.deepCopy();

			return new triangle[] {outTri1};
		}

		if (nInsidePointCount == 1 && nOutsidePointCount == 2)
		{
			
			outTri1 = new triangle(insidePoints[0], vectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]),
													vectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]));
			// Copy appearance info to new triangle
			outTri1.shade = tri.shade;

			return new triangle[] {outTri1};
		}

		if (nInsidePointCount == 2 && nOutsidePointCount == 1)
		{
			
			// The first triangle consists of the two inside points and a new
			// point determined by the location where one side of the triangle
			// intersects with the plane
			outTri1 = new triangle(insidePoints[0], insidePoints[1], vectorIntersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]));
			
			// The second triangle is composed of one of he inside points, a
			// new point determined by the intersection of the other side of the 
			// triangle and the plane, and the newly created point above
			outTri2 = new triangle(insidePoints[1], outTri1.vec3dList[2], vectorIntersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]));
			
			// Copy appearance info to new triangles
			outTri1.shade = tri.shade;
			outTri2.shade = tri.shade;

			return new triangle[] {outTri1, outTri2};
		}
		
		return new triangle[] {};
	}
	
	private static float dist(vec3d planeP, vec3d planeN, vec3d p)
	{
		//vec3d n = vectorNormalise(p);
		return (float) (planeN.x * p.x + planeN.y * p.y + planeN.z * p.z - vectorDotProduct(planeN, planeP));
	}
}
