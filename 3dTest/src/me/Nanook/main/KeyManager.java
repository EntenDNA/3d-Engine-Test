package me.Nanook.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener{
	
	private boolean[] keys;
	public boolean up, down, left, right, forward, backward, sprint;
	public boolean tLeft, tRight, tUp, tDown;
	public boolean debug;
	public boolean place;
	
	public KeyManager()
	{
		keys = new boolean[256];
		updateKeys();
	}
	
	@Override
	public void keyTyped(KeyEvent e) 
	{
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		keys[e.getKeyCode()] = true;
		updateKeys();
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		keys[e.getKeyCode()] = false;
		updateKeys();
	}
	
	private void updateKeys()
	{
		// update important keys
		up = keys[KeyEvent.VK_SPACE];
		down = keys[KeyEvent.VK_SHIFT];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		forward = keys[KeyEvent.VK_W];
		backward = keys[KeyEvent.VK_S];
		sprint = keys[KeyEvent.VK_CONTROL];
		
		tLeft = keys[KeyEvent.VK_LEFT];
		tRight = keys[KeyEvent.VK_RIGHT];
		tUp = keys[KeyEvent.VK_UP];
		tDown = keys[KeyEvent.VK_DOWN];
		
		debug = keys[KeyEvent.VK_F3];
		
		place = keys[KeyEvent.VK_F];
	}
}
