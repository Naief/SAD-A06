
public class Missle {

	// Missle data.

	int missleCounter; // Counter for life of missle.

	static AsteroidsSprite missle = new AsteroidsSprite();

	public void initMissle() {

		missle.active = true;
		missle.angle = 0.0;
		missle.deltaAngle = 0.0;
		missle.x = main.ufo.ufo.x;
		missle.y = main.ufo.ufo.y;
		missle.deltaX = 0.0;
		missle.deltaY = 0.0;
		missle.render();
		missleCounter = Constants.MISSLE_COUNT;
		if (main.sound)
			main.missleSound.loop();
		main.misslePlaying = true;
	}

	public void updateMissle() {

		int i;

		// Move the guided missle and check for collision with ship or photon. Stop
		// it when its counter has expired.

		if (missle.active) {
			if (--missleCounter <= 0)
				stopMissle();
			else {
				guideMissle();
				missle.advance();
				missle.render();
				for (i = 0; i < Constants.MAX_SHOTS; i++)
					if (main.photons.photons[i].active && missle.isColliding(main.photons.photons[i])) {
						if (main.sound)
							main.crashSound.play();
						main.explosions.explode(missle);
						stopMissle();
						main.score += Constants.MISSLE_POINTS;
					}
				if (missle.active && main.ship.ship.active && main.ship.hyperCounter <= 0
						&& main.ship.ship.isColliding(missle)) {
					if (main.sound)
						main.crashSound.play();
					main.explosions.explode(main.ship.ship);
					main.ship.stopShip();
					main.ufo.stopUfo();
					stopMissle();
				}
			}
		}
	}

	public void guideMissle() {

		double dx, dy, angle;

		if (!main.ship.ship.active || main.ship.hyperCounter > 0)
			return;

		// Find the angle needed to hit the ship.

		dx = main.ship.ship.x - missle.x;
		dy = main.ship.ship.y - missle.y;
		if (dx == 0 && dy == 0)
			angle = 0;
		if (dx == 0) {
			if (dy < 0)
				angle = -Math.PI / 2;
			else
				angle = Math.PI / 2;
		} else {
			angle = Math.atan(Math.abs(dy / dx));
			if (dy > 0)
				angle = -angle;
			if (dx < 0)
				angle = Math.PI - angle;
		}

		// Adjust angle for screen coordinates.

		missle.angle = angle - Math.PI / 2;

		// Change the missle's angle so that it points toward the ship.

		missle.deltaX = 0.75 * Constants.MAX_ROCK_SPEED * -Math.sin(missle.angle);
		missle.deltaY = 0.75 * Constants.MAX_ROCK_SPEED * Math.cos(missle.angle);
	}

	public void stopMissle() {

		missle.active = false;
		missleCounter = 0;
		if (main.loaded)
			main.missleSound.stop();
		main.misslePlaying = false;
	}

}
