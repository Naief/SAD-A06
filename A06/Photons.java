
public class Photons {

	static AsteroidsSprite[] photons = new AsteroidsSprite[Constants.MAX_SHOTS];

	// Photon data.

	int photonIndex; // Index to next available photon sprite.
	long photonTime; // Time value used to keep firing rate constant.
	
	
	public void initPhotons() {

		int i;

		for (i = 0; i < Constants.MAX_SHOTS; i++)
			photons[i].active = false;
		photonIndex = 0;
	}

	public void updatePhotons() {

		int i;

		// Move any active photons. Stop it when its counter has expired.

		for (i = 0; i < Constants.MAX_SHOTS; i++)
			if (photons[i].active) {
				if (!photons[i].advance())
					photons[i].render();
				else
					photons[i].active = false;
			}
	}
	
}
