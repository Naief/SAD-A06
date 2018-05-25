
public class Ufo {
	static AsteroidsSprite ufo = new AsteroidsSprite();

	// Flying saucer data.

	int ufoPassesLeft; // Counter for number of flying saucer passes.
	int ufoCounter; // Timer counter used to track each flying saucer pass.

	public void initUfo() {

		double angle, speed;

		// Randomly set flying saucer at left or right edge of the screen.

		ufo.active = true;
		ufo.x = -AsteroidsSprite.width / 2;
		ufo.y = Math.random() * 2 * AsteroidsSprite.height - AsteroidsSprite.height;
		angle = Math.random() * Math.PI / 4 - Math.PI / 2;
		speed = Constants.MAX_ROCK_SPEED / 2 + Math.random() * (Constants.MAX_ROCK_SPEED / 2);
		ufo.deltaX = speed * -Math.sin(angle);
		ufo.deltaY = speed * Math.cos(angle);
		if (Math.random() < 0.5) {
			ufo.x = AsteroidsSprite.width / 2;
			ufo.deltaX = -ufo.deltaX;
		}
		if (ufo.y > 0)
			ufo.deltaY = ufo.deltaY;
		ufo.render();
		main.saucerPlaying = true;
		if (main.sound)
			main.saucerSound.loop();
		ufoCounter = (int) Math.abs(AsteroidsSprite.width / ufo.deltaX);
	}

	public void updateUfo() {

		int i, d;
		boolean wrapped;

		// Move the flying saucer and check for collision with a photon. Stop it
		// when its counter has expired.

		if (ufo.active) {
			if (--ufoCounter <= 0) {
				if (--ufoPassesLeft > 0)
					initUfo();
				else
					stopUfo();
			}
			if (ufo.active) {
				ufo.advance();
				ufo.render();
				for (i = 0; i < Constants.MAX_SHOTS; i++)
					if (main.photons.photons[i].active && ufo.isColliding(main.photons.photons[i])) {
						if (main.sound)
							main.crashSound.play();
						main.explosions.explode(ufo);
						stopUfo();
						main.score += Constants.UFO_POINTS;
					}

				// On occassion, fire a missle at the ship if the saucer is not too
				// close to it.

				d = (int) Math.max(Math.abs(ufo.x - main.ship.ship.x), Math.abs(ufo.y - main.ship.ship.y));
				if (main.ship.ship.active && main.ship.hyperCounter <= 0 && ufo.active && !main.missle.missle.active
						&& d > Constants.MAX_ROCK_SPEED * Constants.FPS / 2
						&& Math.random() < Constants.MISSLE_PROBABILITY)
					main.missle.initMissle();
			}
		}
	}

	public void stopUfo() {

		ufo.active = false;
		ufoCounter = 0;
		ufoPassesLeft = 0;
		if (main.loaded)
			main.saucerSound.stop();
		main.saucerPlaying = false;
	}

}
