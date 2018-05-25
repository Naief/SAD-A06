import java.awt.Polygon;

public class Asteroids {

	// Asteroid data.

	boolean[] asteroidIsSmall = new boolean[Constants.MAX_ROCKS]; // Asteroid size flag.
	int asteroidsCounter; // Break-time counter.
	double asteroidsSpeed; // Asteroid speed.
	int asteroidsLeft; // Number of active asteroids.

	AsteroidsSprite[] asteroids = new AsteroidsSprite[Constants.MAX_ROCKS];

	public void initAsteroids() {

		int i, j;
		int s;
		double theta, r;
		int x, y;

		// Create random shapes, positions and movements for each asteroid.

		for (i = 0; i < Constants.MAX_ROCKS; i++) {

			// Create a jagged shape for the asteroid and give it a random rotation.

			asteroids[i].shape = new Polygon();
			s = Constants.MIN_ROCK_SIDES + (int) (Math.random() * (Constants.MAX_ROCK_SIDES - Constants.MIN_ROCK_SIDES));
			for (j = 0; j < s; j++) {
				theta = 2 * Math.PI / s * j;
				r = Constants.MIN_ROCK_SIZE + (int) (Math.random() * (Constants.MAX_ROCK_SIZE - Constants.MIN_ROCK_SIZE));
				x = (int) -Math.round(r * Math.sin(theta));
				y = (int) Math.round(r * Math.cos(theta));
				asteroids[i].shape.addPoint(x, y);
			}
			asteroids[i].active = true;
			asteroids[i].angle = 0.0;
			asteroids[i].deltaAngle = Math.random() * 2 * Constants.MAX_ROCK_SPIN - Constants.MAX_ROCK_SPIN;

			// Place the asteroid at one edge of the screen.

			if (Math.random() < 0.5) {
				asteroids[i].x = -AsteroidsSprite.width / 2;
				if (Math.random() < 0.5)
					asteroids[i].x = AsteroidsSprite.width / 2;
				asteroids[i].y = Math.random() * AsteroidsSprite.height;
			} else {
				asteroids[i].x = Math.random() * AsteroidsSprite.width;
				asteroids[i].y = -AsteroidsSprite.height / 2;
				if (Math.random() < 0.5)
					asteroids[i].y = AsteroidsSprite.height / 2;
			}

			// Set a random motion for the asteroid.

			asteroids[i].deltaX = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				asteroids[i].deltaX = -asteroids[i].deltaX;
			asteroids[i].deltaY = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				asteroids[i].deltaY = -asteroids[i].deltaY;

			asteroids[i].render();
			asteroidIsSmall[i] = false;
		}

		asteroidsCounter = Constants.STORM_PAUSE;
		asteroidsLeft = Constants.MAX_ROCKS;
		if (asteroidsSpeed < Constants.MAX_ROCK_SPEED)
			asteroidsSpeed += 0.5;
	}

	public void initSmallAsteroids(int n) {

		int count;
		int i, j;
		int s;
		double tempX, tempY;
		double theta, r;
		int x, y;

		// Create one or two smaller asteroids from a larger one using inactive
		// asteroids. The new asteroids will be placed in the same position as the
		// old one but will have a new, smaller shape and new, randomly generated
		// movements.

		count = 0;
		i = 0;
		tempX = asteroids[n].x;
		tempY = asteroids[n].y;
		do {
			if (!asteroids[i].active) {
				asteroids[i].shape = new Polygon();
				s = Constants.MIN_ROCK_SIDES + (int) (Math.random() * (Constants.MAX_ROCK_SIDES - Constants.MIN_ROCK_SIDES));
				for (j = 0; j < s; j++) {
					theta = 2 * Math.PI / s * j;
					r = (Constants.MIN_ROCK_SIZE + (int) (Math.random() * (Constants.MAX_ROCK_SIZE - Constants.MIN_ROCK_SIZE))) / 2;
					x = (int) -Math.round(r * Math.sin(theta));
					y = (int) Math.round(r * Math.cos(theta));
					asteroids[i].shape.addPoint(x, y);
				}
				asteroids[i].active = true;
				asteroids[i].angle = 0.0;
				asteroids[i].deltaAngle = Math.random() * 2 * Constants.MAX_ROCK_SPIN - Constants.MAX_ROCK_SPIN;
				asteroids[i].x = tempX;
				asteroids[i].y = tempY;
				asteroids[i].deltaX = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				asteroids[i].deltaY = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				asteroids[i].render();
				asteroidIsSmall[i] = true;
				count++;
				asteroidsLeft++;
			}
			i++;
		} while (i < Constants.MAX_ROCKS && count < 2);
	}

	public void updateAsteroids() {

		int i, j;

		// Move any active asteroids and check for collisions.

		for (i = 0; i < Constants.MAX_ROCKS; i++)
			if (asteroids[i].active) {
				asteroids[i].advance();
				asteroids[i].render();

				// If hit by photon, kill asteroid and advance score. If asteroid is
				// large, make some smaller ones to replace it.

				for (j = 0; j < Constants.MAX_SHOTS; j++)
					if (main.photons.photons[j].active && asteroids[i].active && asteroids[i].isColliding(main.photons.photons[j])) {
						asteroidsLeft--;
						asteroids[i].active = false;
						main.photons.photons[j].active = false;
						if (main.sound)
							main.explosionSound.play();
						 main.explosions.explode(asteroids[i]);
						if (!asteroidIsSmall[i]) {
							main.score += Constants.BIG_POINTS;
							initSmallAsteroids(i);
						} else
							main.score += Constants.SMALL_POINTS;
					}

				// If the ship is not in hyperspace, see if it is hit.

				if (main.ship.ship.active && main.ship.hyperCounter <= 0 && asteroids[i].active && asteroids[i].isColliding(main.ship.ship)) {
					if (main.sound)
						main.crashSound.play();
					main.explosions.explode(main.ship.ship);
					main.ship.stopShip();
					main.ufo.stopUfo();
					main.missle.stopMissle();
				}
			}
	}
}
