
public class Ship {

	// ship data
	int shipsLeft; // Number of ships left in game, including current one.
	int shipCounter; // Timer counter for ship explosion.
	int hyperCounter; // Timer counter for hyperspace.

	// Key flags.
	boolean left = false;
	boolean right = false;
	boolean up = false;
	boolean down = false;

	static AsteroidsSprite ship = new AsteroidsSprite();
	static AsteroidsSprite fwdThruster = new AsteroidsSprite();
	static AsteroidsSprite revThruster = new AsteroidsSprite();

	public void initShip() {

		// Reset the ship sprite at the center of the screen.

		ship.active = true;
		ship.angle = 0.0;
		ship.deltaAngle = 0.0;
		ship.x = 0.0;
		ship.y = 0.0;
		ship.deltaX = 0.0;
		ship.deltaY = 0.0;
		ship.render();

		// Initialize thruster sprites.

		fwdThruster.x = ship.x;
		fwdThruster.y = ship.y;
		fwdThruster.angle = ship.angle;
		fwdThruster.render();
		revThruster.x = ship.x;
		revThruster.y = ship.y;
		revThruster.angle = ship.angle;
		revThruster.render();

		if (main.loaded)
			main.sound.thrustersSound.stop();
		main.sound.thrustersPlaying = false;
		hyperCounter = 0;
	}



	public void stopShip() {

		ship.active = false;
		shipCounter = Constants.SCRAP_COUNT;
		if (shipsLeft > 0)
			shipsLeft--;
		if (main.loaded)
			main.sound.thrustersSound.stop();
		main.sound.thrustersPlaying = false;
	}

	
}
