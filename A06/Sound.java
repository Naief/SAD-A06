import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;


public class Sound extends Applet {

	//TODO
	//load all sounds
	/** with controls
	 * mute all sounds
	 * sound when firing
	 * sound when warping
	 * 
	 */
	
	// Sound clips.

		AudioClip crashSound;
		AudioClip explosionSound;
		AudioClip fireSound;
		AudioClip missleSound;
		AudioClip saucerSound;
		AudioClip thrustersSound;
		AudioClip warpSound;

		// Flags for looping sound clips.

		static boolean thrustersPlaying;
		static boolean saucerPlaying;
		static boolean misslePlaying;
		
		static boolean sound;
	
		// Counter and total used to track the loading of the sound clips.

		private int clipTotal = 0;
		private int clipsLoaded = 0;
		
	public boolean getSound() {
		return sound;
	}
	
	public void setSound(boolean newSound) {
		sound = newSound;
	}
	
	public int getClipTotal() {
		return clipTotal;
	}
	
	public int getClipsLoaded() {
		return clipsLoaded;
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
	
	public void stopAllSound() {
		crashSound.stop();
		explosionSound.stop();
		fireSound.stop();
		missleSound.stop();
		saucerSound.stop();
		thrustersSound.stop();
		warpSound.stop();
	}
	public void pause(boolean pause) {
		if (pause) {
			if (getSound() && misslePlaying)
				missleSound.loop();
			if (getSound() && saucerPlaying)
				saucerSound.loop();
			if (getSound() && thrustersPlaying)
				thrustersSound.loop();
		} else {
			if (misslePlaying)
				missleSound.stop();
			if (saucerPlaying)
				saucerSound.stop();
			if (thrustersPlaying)
				thrustersSound.stop();
		}
	}
}
