package me.Nanook.util;

public class mathHelper {
	public static vec3d MultiplyMartixVector(vec3d vec, double[][] matrix)
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
