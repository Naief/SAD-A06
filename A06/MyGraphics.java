import java.awt.*;
import java.awt.event.*;
import java.awt.Component.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

public class MyGraphics extends Applet {

	String copyName = "Asteroids";
	String copyVers = "Version 1.3";
	String copyInfo = "Copyright 1998-2001 by Mike Hall";
	String copyLink = "http://www.brainjar.com";
	
	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;
	

	int numStars;
	Point[] stars;
	
	Font font = new Font("Helvetica", Font.BOLD, 12);
	FontMetrics fm = getFontMetrics(font);
	int fontWidth = fm.getMaxAdvance();
	int fontHeight = fm.getHeight();
	
	public MyGraphics() {
		
	}
	
	public void drawShip() {
		Ship.ship = new AsteroidsSprite();
		Ship.ship.shape.addPoint(0, -10);
		Ship.ship.shape.addPoint(7, 10);
		Ship.ship.shape.addPoint(-7, 10);
	}
	
	public void drawThrusters(Ship ship) {
		ship.fwdThruster = new AsteroidsSprite();
		ship.fwdThruster.shape.addPoint(0, 12);
		ship.fwdThruster.shape.addPoint(-3, 16);
		ship.fwdThruster.shape.addPoint(0, 26);
		ship.fwdThruster.shape.addPoint(3, 16);
		ship.revThruster = new AsteroidsSprite();
		ship.revThruster.shape.addPoint(-2, 12);
		ship.revThruster.shape.addPoint(-4, 14);
		ship.revThruster.shape.addPoint(-2, 20);
		ship.revThruster.shape.addPoint(0, 14);
		ship.revThruster.shape.addPoint(2, 12);
		ship.revThruster.shape.addPoint(4, 14);
		ship.revThruster.shape.addPoint(2, 20);
		ship.revThruster.shape.addPoint(0, 14);
	}
	
	public void drawShots() {
		for (int i = 0; i < Constants.MAX_SHOTS; i++) {
			Photons.photons[i] = new AsteroidsSprite();
			Photons.photons[i].shape.addPoint(1, 1);
			Photons.photons[i].shape.addPoint(1, -1);
			Photons.photons[i].shape.addPoint(-1, 1);
			Photons.photons[i].shape.addPoint(-1, -1);
		}
	}
	
	public void drawUfo() {
		Ufo.ufo = new AsteroidsSprite();
		Ufo.ufo.shape.addPoint(-15, 0);
		Ufo.ufo.shape.addPoint(-10, -5);
		Ufo.ufo.shape.addPoint(-5, -5);
		Ufo.ufo.shape.addPoint(-5, -8);
		Ufo.ufo.shape.addPoint(5, -8);
		Ufo.ufo.shape.addPoint(5, -5);
		Ufo.ufo.shape.addPoint(10, -5);
		Ufo.ufo.shape.addPoint(15, 0);
		Ufo.ufo.shape.addPoint(10, 5);
		Ufo.ufo.shape.addPoint(-10, 5);
	}
	
	public void drawMissle() {
		Missle.missle = new AsteroidsSprite();
		Missle.missle.shape.addPoint(0, -4);
		Missle.missle.shape.addPoint(1, -3);
		Missle.missle.shape.addPoint(1, 3);
		Missle.missle.shape.addPoint(2, 4);
		Missle.missle.shape.addPoint(-2, 4);
		Missle.missle.shape.addPoint(-1, 3);
		Missle.missle.shape.addPoint(-1, -3);
	}
	
	public void drawAsteroids(Asteroids asteroids) {
		for (int i = 0; i < Constants.MAX_ROCKS; i++)
			asteroids.asteroids[i] = new AsteroidsSprite();
	}
	
	public void drawExplosions(Explosions explosions) {
		for (int i = 0; i < Constants.MAX_SCRAP; i++)
			explosions.explosions[i] = new AsteroidsSprite();
	}
	
	public void drawBackground() {
		numStars = AsteroidsSprite.width * AsteroidsSprite.height / 5000;
		stars = new Point[numStars];
		for (int i = 0; i < numStars; i++)
			stars[i] = new Point((int) (Math.random() * AsteroidsSprite.width),
					(int) (Math.random() * AsteroidsSprite.height));
	}
	
	public void paint(Graphics g, Explosions explosions, Photons photons, Missle missle, Asteroids asteroids, Ufo ufo, Ship ship, Dimension d, boolean playing, boolean paused, boolean up, boolean down, boolean loaded, boolean sound) {
		
		int i;
		int c;
		String s;
		int w, h;
		int x, y;

		// Create the off screen graphics context, if no good one exists.

		if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			offGraphics = offImage.getGraphics();
		}

		// Fill in background and stars.

		offGraphics.setColor(Color.black);
		offGraphics.fillRect(0, 0, d.width, d.height);
		if (explosions.detail) {
			offGraphics.setColor(Color.white);
			for (i = 0; i < numStars; i++)
				offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
		}

		// Draw photon bullets.

		offGraphics.setColor(Color.white);
		for (i = 0; i < Constants.MAX_SHOTS; i++)
			if (photons.photons[i].active)
				offGraphics.drawPolygon(Photons.photons[i].sprite);

		// Draw the guided missle, counter is used to quickly fade color to black
		// when near expiration.

		c = Math.min(missle.missleCounter * 24, 255);
		offGraphics.setColor(new Color(c, c, c));
		if (missle.missle.active) {
			   offGraphics.drawPolygon(missle.missle.sprite);
			      offGraphics.drawLine(missle.missle.sprite.xpoints[missle.missle.sprite.npoints - 1], missle.missle.sprite.ypoints[missle.missle.sprite.npoints - 1],
			    		  missle.missle.sprite.xpoints[0], missle.missle.sprite.ypoints[0]);
			    
		}

		// Draw the asteroids.

		 for (i = 0; i < Constants.MAX_ROCKS; i++)
		      if (asteroids.asteroids[i].active) {
		        if (explosions.detail) {
		          offGraphics.setColor(Color.black);
		          offGraphics.fillPolygon(asteroids.asteroids[i].sprite);
		        }
		        offGraphics.setColor(Color.white);
		        offGraphics.drawPolygon(asteroids.asteroids[i].sprite);
		        offGraphics.drawLine(asteroids.asteroids[i].sprite.xpoints[asteroids.asteroids[i].sprite.npoints - 1], asteroids.asteroids[i].sprite.ypoints[asteroids.asteroids[i].sprite.npoints - 1],
		        		asteroids.asteroids[i].sprite.xpoints[0], asteroids.asteroids[i].sprite.ypoints[0]);
		      }

		// Draw the flying saucer.

		 if (ufo.ufo.active) {
		      if (explosions.detail) {
		        offGraphics.setColor(Color.black);
		        offGraphics.fillPolygon(ufo.ufo.sprite);
		      }
		      offGraphics.setColor(Color.white);
		      offGraphics.drawPolygon(ufo.ufo.sprite);
		      offGraphics.drawLine(ufo.ufo.sprite.xpoints[ufo.ufo.sprite.npoints - 1], ufo.ufo.sprite.ypoints[ufo.ufo.sprite.npoints - 1],
		    		  ufo.ufo.sprite.xpoints[0], ufo.ufo.sprite.ypoints[0]);
		    }


		// Draw the ship, counter is used to fade color to white on hyperspace.

		  if (ship.ship.active) {
		      if (explosions.detail && ship.hyperCounter == 0) {
		        offGraphics.setColor(Color.black);
		        offGraphics.fillPolygon(ship.ship.sprite);
		      }
		      offGraphics.setColor(new Color(c, c, c));
		      offGraphics.drawPolygon(ship.ship.sprite);
		      offGraphics.drawLine(ship.ship.sprite.xpoints[ship.ship.sprite.npoints - 1], ship.ship.sprite.ypoints[ship.ship.sprite.npoints - 1],
		    		  ship.ship.sprite.xpoints[0], ship.ship.sprite.ypoints[0]);
		      
			// Draw thruster exhaust if thrusters are on. Do it randomly to get a
			// flicker effect.

			if (!paused && explosions.detail && Math.random() < 0.5) {
				if (up) {
					offGraphics.drawPolygon(ship.fwdThruster.sprite);
					offGraphics.drawLine(ship.fwdThruster.sprite.xpoints[ship.fwdThruster.sprite.npoints - 1],
							ship.fwdThruster.sprite.ypoints[ship.fwdThruster.sprite.npoints - 1], ship.fwdThruster.sprite.xpoints[0],
							ship.fwdThruster.sprite.ypoints[0]);
				}
				if (down) {
					offGraphics.drawPolygon(ship.revThruster.sprite);
					offGraphics.drawLine(ship.revThruster.sprite.xpoints[ship.revThruster.sprite.npoints - 1],
							ship.revThruster.sprite.ypoints[ship.revThruster.sprite.npoints - 1], ship.revThruster.sprite.xpoints[0],
							ship.revThruster.sprite.ypoints[0]);
				}
			}
		}

		// Draw any explosion debris, counters are used to fade color to black.

		    for (i = 0; i < Constants.MAX_SCRAP; i++)
		      if (explosions.explosions[i].active) {
		        c = (255 / Constants.SCRAP_COUNT) * explosions.explosionCounter [i];
		        offGraphics.setColor(new Color(c, c, c));
		        offGraphics.drawPolygon(explosions.explosions[i].sprite);
		      } 
		    
		// Display status and messages.

		offGraphics.setFont(font);
		offGraphics.setColor(Color.white);

		offGraphics.drawString("Score: " + main.score, fontWidth, fontHeight);
		offGraphics.drawString("Ships: " + ship.shipsLeft, fontWidth, d.height - fontHeight);
		s = "High: " + main.highScore;
		offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
		if (!sound) {
			s = "Mute";
			offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
		}

		if (!playing) {
			s = copyName;
			offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
			s = copyVers;
			offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
			s = copyInfo;
			offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
			s = copyLink;
			offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
			if (!loaded) {
				s = "Loading sounds...";
				w = 4 * fontWidth + fm.stringWidth(s);
				h = fontHeight;
				x = (d.width - w) / 2;
				y = 3 * d.height / 4 - fm.getMaxAscent();
				offGraphics.setColor(Color.black);
				offGraphics.fillRect(x, y, w, h);
				offGraphics.setColor(Color.gray);
				if (main.clipTotal > 0)
					offGraphics.fillRect(x, y, (int) (w * main.clipsLoaded / main.clipTotal), h);
				offGraphics.setColor(Color.white);
				offGraphics.drawRect(x, y, w, h);
				offGraphics.drawString(s, x + 2 * fontWidth, y + fm.getMaxAscent());
			} else {
				s = "Game Over";
				offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
				s = "'S' to Start";
				offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
			}
		} else if (paused) {
			s = "Game Paused";
			offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
		}

		// Copy the off screen buffer to the screen.

		g.drawImage(offImage, 0, 0, this);
	}
}
