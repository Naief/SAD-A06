
/******************************************************************************
  Asteroids, Version 1.3

  Copyright 1998-2001 by Mike Hall.
  Please see http://www.brainjar.com for terms of use.

  Revision History:

  1.01, 12/18/1999: Increased number of active photons allowed.
                    Improved explosions for more realism.
                    Added progress bar for loading of sound clips.
  1.2,  12/23/1999: Increased frame rate for smoother animation.
                    Modified code to calculate game object speeds and timer
                    counters based on the frame rate so they will remain
                    constant.
                    Improved speed limit checking for ship.
                    Removed wrapping of photons around screen and set a fixed
                    firing rate.
                    Added sprites for ship's thrusters.
  1.3,  01/25/2001: Updated to JDK 1.1.8.

  Usage:

  <applet code="Asteroids.class" width=w height=h></applet>

  Keyboard Controls:

  S            - Start Game    P           - Pause Game
  Cursor Left  - Rotate Left   Cursor Up   - Fire Thrusters
  Cursor Right - Rotate Right  Cursor Down - Fire Retro Thrusters
  Spacebar     - Fire Cannon   H           - Hyperspace
  M            - Toggle Sound  D           - Toggle Graphics Detail

******************************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

/******************************************************************************
  The AsteroidsSprite class defines a game object, including it's shape,
  position, movement and rotation. It also can detemine if two objects collide.
******************************************************************************/

/*
 * group:2
 * Naief Jobsen 
 * Filip Fatic
 */

/******************************************************************************
 * Main applet code.
 ******************************************************************************/

public class main extends Applet implements Runnable, KeyListener {

	// Copyright information.

	String copyName = "Asteroids";
	String copyVers = "Version 1.3";
	String copyInfo = "Copyright 1998-2001 by Mike Hall";
	String copyLink = "http://www.brainjar.com";
	String copyText = copyName + '\n' + copyVers + '\n' + copyInfo + '\n' + copyLink;

	// Thread control variables.

	Thread loadThread;
	Thread loopThread;


	// Game data.

	static int score;
	static int highScore;
	int newShipScore;
	int newUfoScore;

	// Sprite objects.
	static MyGraphics graphics = new MyGraphics();
	static Explosions explosions = new Explosions();
	static Photons photons = new Photons();
	static Asteroids asteroids = new Asteroids();
	static Ufo ufo = new Ufo();
	static Missle missle = new Missle();
	static Ship ship = new Ship();

	// Flags for game state and options.

	static boolean loaded = false;
	static boolean paused;
	static boolean playing;
	static boolean sound;

	// Key flags.

	boolean left = false;
	boolean right = false;
	boolean up = false;
	boolean down = false;


	// Flying saucer data.

	int ufoPassesLeft; // Counter for number of flying saucer passes.
	int ufoCounter; // Timer counter used to track each flying saucer pass.

	// Sound clips.

	static AudioClip crashSound;
	static AudioClip explosionSound;
	static AudioClip fireSound;
	static AudioClip missleSound;
	static AudioClip saucerSound;
	static AudioClip thrustersSound;
	static AudioClip warpSound;

	// Flags for looping sound clips.

	static boolean thrustersPlaying;
	static boolean saucerPlaying;
	static boolean misslePlaying;

	// Counter and total used to track the loading of the sound clips.

	static int clipTotal = 0;
	static int clipsLoaded = 0;

	// Off screen image.

	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;

	// Data for the screen font.

	Font font = new Font("Helvetica", Font.BOLD, 12);
	FontMetrics fm = getFontMetrics(font);
	int fontWidth = fm.getMaxAdvance();
	int fontHeight = fm.getHeight();
	
	public String getAppletInfo() {

		// Return copyright information.

		return (copyText);
	}

	public void init() {

		Dimension d = getSize();
		int i;

		// Display copyright information.

		System.out.println(copyText);

		// Set up key event handling and set focus to applet window.

		addKeyListener(this);
		requestFocus();

		// Save the screen size.

		AsteroidsSprite.width = d.width;
		AsteroidsSprite.height = d.height;

		// Generate the starry background.

		graphics.drawBackground();

		// Create shape for the ship sprite.

		graphics.drawShip();

		// Create shapes for the ship thrusters.

		graphics.drawThrusters(ship);

		// Create shape for each photon sprites.

		graphics.drawShots();

		// Create shape for the flying saucer.

		graphics.drawUfo();

		// Create shape for the guided missle.

		graphics.drawMissle();

		// Create asteroid sprites.

		graphics.drawAsteroids(asteroids);

		// Create explosion sprites.

		graphics.drawExplosions(explosions);

		// Initialize game data and put us in 'game over' mode.

		highScore = 0;
		sound = true;
		explosions.detail = true;
		initGame();
		endGame();
	}

	public void initGame() {

		// Initialize game data and sprites.


	    score = 0;
	    ship.shipsLeft = Constants.MAX_SHIPS;
	    asteroids.asteroidsSpeed = Constants.MIN_ROCK_SPEED;
	    newShipScore = Constants.NEW_SHIP_POINTS;
	    newUfoScore = Constants.NEW_UFO_POINTS;
	    ship.initShip();
	    photons.initPhotons();
	    ufo.stopUfo();
	    missle.stopMissle();
	    asteroids.initAsteroids();
	    explosions.initExplosions();
	    playing = true;
	    paused = false;
	    photons.photonTime = System.currentTimeMillis();
	  }


	public static void endGame() {

		// Stop ship, flying saucer, guided missle and associated sounds.

		  playing = false;
		    ship.stopShip();
		    ufo.stopUfo();
		    missle.stopMissle();
	}

	public void updateShip() {

		  double dx, dy, speed;

		    if (!main.playing)
		      return;

		    // Rotate the ship if left or right cursor key is down.

		    if (left) {
		      ship.ship.angle += Constants.SHIP_ANGLE_STEP;
		      if (ship.ship.angle > 2 * Math.PI)
		    	  ship.ship.angle -= 2 * Math.PI;
		    }
		    if (right) {
		    	ship.ship.angle -= Constants.SHIP_ANGLE_STEP;
		      if (ship.ship.angle < 0)
		    	  ship.ship.angle += 2 * Math.PI;
		    }

		    // Fire thrusters if up or down cursor key is down.

		    dx = Constants.SHIP_SPEED_STEP * -Math.sin(ship.ship.angle);
		    dy = Constants.SHIP_SPEED_STEP *  Math.cos(ship.ship.angle);
		    if (up) {
		    	ship.ship.deltaX += dx;
		    	ship.ship.deltaY += dy;
		    }
		    if (down) {
		    	ship.ship.deltaX -= dx;
		    	ship.ship.deltaY -= dy;
		    }

		    // Don't let ship go past the speed limit.

		    if (up || down) {
		      speed = Math.sqrt(ship.ship.deltaX * ship.ship.deltaX + ship.ship.deltaY * ship.ship.deltaY);
		      if (speed > Constants.MAX_SHIP_SPEED) {
		        dx = Constants.MAX_SHIP_SPEED * -Math.sin(ship.ship.angle);
		        dy = Constants.MAX_SHIP_SPEED *  Math.cos(ship.ship.angle);
		        if (up)
		        	ship.ship.deltaX = dx;
		        else
		        	ship.ship.deltaX = -dx;
		        if (up)
		        	ship.ship.deltaY = dy;
		        else
		        	ship.ship.deltaY = -dy;
		      }
		    }

		    // Move the ship. If it is currently in hyperspace, advance the countdown.

		    if (ship.ship.active) {
		    	ship.ship.advance();
		    	ship.ship.render();
		      if (ship.hyperCounter > 0)
		    	  ship. hyperCounter--;

		      // Update the thruster sprites to match the ship sprite.

		      ship.fwdThruster.x = ship.ship.x;
		      ship.fwdThruster.y = ship.ship.y;
		      ship.fwdThruster.angle = ship.ship.angle;
		      ship.fwdThruster.render();
		      ship.revThruster.x = ship.ship.x;
		      ship.revThruster.y = ship.ship.y;
		      ship.revThruster.angle = ship.ship.angle;
		      ship.revThruster.render();
		    }

		    // Ship is exploding, advance the countdown or create a new ship if it is
		    // done exploding. The new ship is added as though it were in hyperspace.
		    // (This gives the player time to move the ship if it is in imminent
		    // danger.) If that was the last ship, end the game.

		    else
		      if (--ship.shipCounter <= 0)
		        if (ship.shipsLeft > 0) {
		        	ship.initShip();
		        	ship.hyperCounter = Constants.HYPER_COUNT;
		        }
		        else
		        	main.endGame();
	}

	public void start() {

		if (loopThread == null) {
			loopThread = new Thread(this);
			loopThread.start();
		}
		if (!loaded && loadThread == null) {
			loadThread = new Thread(this);
			loadThread.start();
		}
	}

	public void stop() {

		if (loopThread != null) {
			loopThread.stop();
			loopThread = null;
		}
		if (loadThread != null) {
			loadThread.stop();
			loadThread = null;
		}
	}

	public void run() {

		int i, j;
		long startTime;

		// Lower this thread's priority and get the current time.

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		startTime = System.currentTimeMillis();

		// Run thread for loading sounds.

		if (!loaded && Thread.currentThread() == loadThread) {
			loadSounds();
			loaded = true;
			loadThread.stop();
		}

		// This is the main loop.

		while (Thread.currentThread() == loopThread) {

			if (!paused) {

				// Move and process all sprites.

				updateShip();
		        photons.updatePhotons();
		        ufo.updateUfo();
		        missle.updateMissle();
		        asteroids.updateAsteroids();
		        explosions.updateExplosions();


				// Check the score and advance high score, add a new ship or start the
				// flying saucer as necessary.
		        if (score > highScore)
		            highScore = score;
		          if (score > newShipScore) {
		            newShipScore += Constants.NEW_SHIP_POINTS;
		            ship.shipsLeft++;
		          }
		          if (playing && score > newUfoScore && !ufo.ufo.active) {
		            newUfoScore += Constants.NEW_UFO_POINTS;
		            ufo.ufoPassesLeft = Constants.UFO_PASSES;
		            ufo.initUfo();
		          }

		          // If all asteroids have been destroyed create a new batch.

		          if (asteroids.asteroidsLeft <= 0)
		              if (--asteroids.asteroidsCounter <= 0)
		                asteroids.initAsteroids();
		        }

		        // Update the screen and set the timer for the next loop.

		        repaint();
		        try {
		          startTime += Constants.DELAY;
		          Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
		        }
		        catch (InterruptedException e) {
		          break;
		        }
		      }
		    }



	public void loadSounds() {

		// Load all sound clips by playing and immediately stopping them. Update
		// counter and total for display.

		try {
			crashSound = getAudioClip(new URL(getCodeBase(), "crash.au"));
			clipTotal++;
			explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));
			clipTotal++;
			fireSound = getAudioClip(new URL(getCodeBase(), "fire.au"));
			clipTotal++;
			missleSound = getAudioClip(new URL(getCodeBase(), "missle.au"));
			clipTotal++;
			saucerSound = getAudioClip(new URL(getCodeBase(), "saucer.au"));
			clipTotal++;
			thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));
			clipTotal++;
			warpSound = getAudioClip(new URL(getCodeBase(), "warp.au"));
			clipTotal++;
		} catch (MalformedURLException e) {
		}

		try {
			crashSound.play();
			crashSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			explosionSound.play();
			explosionSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			fireSound.play();
			fireSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			missleSound.play();
			missleSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			saucerSound.play();
			saucerSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			thrustersSound.play();
			thrustersSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
			warpSound.play();
			warpSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(Constants.DELAY);
		} catch (InterruptedException e) {
		}
	}


	public void keyPressed(KeyEvent e) {

		char c;

		// Check if any cursor keys have been pressed and set flags.

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;

		if ((up || down) && ship.ship.active && !thrustersPlaying) {
			if (sound && !paused)
				thrustersSound.loop();
			thrustersPlaying = true;
		}

		// Spacebar: fire a photon and start its counter.

		if (e.getKeyChar() == ' ' && ship.ship.active) {
			if (sound & !paused)
				fireSound.play();
			  photons.photonTime = System.currentTimeMillis();
		      photons.photonIndex++;
		      if ( photons.photonIndex >= Constants.MAX_SHOTS)
		    	  photons.photonIndex = 0;
		      photons.photons[ photons.photonIndex].active = true;
		      photons.photons[ photons.photonIndex].x = ship.ship.x;
		      photons.photons[ photons.photonIndex].y = ship.ship.y;
		      photons.photons[ photons.photonIndex].deltaX = 2 * Constants.MAX_ROCK_SPEED * -Math.sin(ship.ship.angle);
		      photons.photons[ photons.photonIndex].deltaY = 2 * Constants.MAX_ROCK_SPEED *  Math.cos(ship.ship.angle);
		    
		}

		// Allow upper or lower case characters for remaining keys.

		c = Character.toLowerCase(e.getKeyChar());

		// 'H' key: warp ship into hyperspace by moving to a random location and
		// starting counter.

		 if (c == 'h' && ship.ship.active && ship.hyperCounter <= 0) {
		    	ship.ship.x = Math.random() * AsteroidsSprite.width;
		    	ship.ship.y = Math.random() * AsteroidsSprite.height;
		      ship.hyperCounter = Constants.HYPER_COUNT;
		      if (sound & !paused)
		        warpSound.play();
		    }

		// 'P' key: toggle pause mode and start or stop any active looping sound
		// clips.

		if (c == 'p') {
			if (paused) {
				if (sound && misslePlaying)
					missleSound.loop();
				if (sound && saucerPlaying)
					saucerSound.loop();
				if (sound && thrustersPlaying)
					thrustersSound.loop();
			} else {
				if (misslePlaying)
					missleSound.stop();
				if (saucerPlaying)
					saucerSound.stop();
				if (thrustersPlaying)
					thrustersSound.stop();
			}
			paused = !paused;
		}

		// 'M' key: toggle sound on or off and stop any looping sound clips.

		if (c == 'm' && loaded) {
			if (sound) {
				crashSound.stop();
				explosionSound.stop();
				fireSound.stop();
				missleSound.stop();
				saucerSound.stop();
				thrustersSound.stop();
				warpSound.stop();
			} else {
				if (misslePlaying && !paused)
					missleSound.loop();
				if (saucerPlaying && !paused)
					saucerSound.loop();
				if (thrustersPlaying && !paused)
					thrustersSound.loop();
			}
			sound = !sound;
		}

		// 'D' key: toggle graphics detail on or off.

		if (c == 'd')
		      explosions.detail = !explosions.detail;

		// 'S' key: start the game, if not already in progress.

		if (c == 's' && loaded && !playing)
			initGame();

		// 'HOME' key: jump to web site (undocumented).

		if (e.getKeyCode() == KeyEvent.VK_HOME)
			try {
				getAppletContext().showDocument(new URL(copyLink));
			} catch (Exception excp) {
			}
	}

	public void keyReleased(KeyEvent e) {

		// Check if any cursor keys where released and set flags.

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			left = false;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			right = false;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			up = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			down = false;

		if (!up && !down && thrustersPlaying) {
			thrustersSound.stop();
			thrustersPlaying = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void update(Graphics g) {
		Dimension d = getSize();
		graphics.paint(g, explosions, photons, missle, asteroids, ufo, ship, d, playing, paused, up, down, loaded, sound);
	}

}
