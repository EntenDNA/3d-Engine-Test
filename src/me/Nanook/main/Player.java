package me.Nanook.main;

import me.Nanook.util.mathHelper;
import me.Nanook.util.vec3d;

public class Player {
	
	private Game game;
	private double vel;
	private double sens;
	
	public Player(Game game)
	{
		this.game = game;
		this.vel = 0.002;
		this.sens = 0.002;
	}
	
	public void updateMovement()
	{
		// move up and down
		if(game.keyManager.up)
		{
			game.vCamera.y -= this.vel;
		}
		else if(game.keyManager.down)
		{
			game.vCamera.y += this.vel;
		}
		
		// move left and right
		if(game.keyManager.left)
		{
			// normalize the right vector
			vec3d temp = mathHelper.vectorNormalise(game.vRight);
			
			// add the right vector multiplied by speed to the camera vector
			game.vCamera = mathHelper.vectorSub(game.vCamera, mathHelper.vectorMul(temp, this.vel));
		}
		else if(game.keyManager.right)
		{
			// normalize the right vector
			vec3d temp = mathHelper.vectorNormalise(game.vRight);
			
			// add the right vector multiplied by speed to the camera vector
			game.vCamera = mathHelper.vectorAdd(game.vCamera, mathHelper.vectorMul(temp, this.vel));
		}
		
		// move forwards and backwards
		vec3d vForward = mathHelper.vectorMul(game.vLookDir, this.vel);
		if(game.keyManager.forward)
		{
			game.vCamera = mathHelper.vectorAdd(game.vCamera, vForward);
		}
		else if(game.keyManager.backward)
		{
			game.vCamera = mathHelper.vectorSub(game.vCamera, vForward);
		}
		
		// look left and right
		if(game.keyManager.tLeft)
		{
			game.fYaw += this.sens;
		}
		else if(game.keyManager.tRight)
		{
			game.fYaw -= this.sens;
		}
	}
}
