package javaNK.util.sound;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javaNK.util.math.Percentage;
import javaNK.util.math.Range;

/**
 * This class controls the sounds of the application.
 * Load tunes into the class and play them when ever they're needed.
 * 
 * @author Niv Kor
 */
public class Sound
{
	/**
	 * This class represents a single tune.
	 * Sound holds a collection of tunes and manages them.
	 * 
	 * @author Niv Kor
	 */
	private static class Tune
	{
		private boolean loopable;
		private float[] volume;
		private Clip clip;
		
		/**
		 * @param path - The directory of the audio file
		 * @param loopable - True if the tune is meant to loop
		 * @throws LineUnavailableException when the audio file is already open in another program.
		 * @throws IOException when the file directory cannot be found.
		 * @throws UnsupportedAudioFileException when the audio file is not supported.
		 */
		public Tune(String path, boolean loopable) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
			this.loopable = loopable;
			this.volume = new float[2];
			
			InputStream in = Sound.class.getResourceAsStream(path);
			InputStream bin = new BufferedInputStream(in);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bin);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
									   AudioFormat.Encoding.PCM_SIGNED,
									   baseFormat.getSampleRate(),	16,
									   baseFormat.getChannels(),
									   baseFormat.getChannels() * 2,
									   baseFormat.getSampleRate(), false);
			
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			this.clip = AudioSystem.getClip();
			this.clip.open(dais);
			setVolume(VOLUME_RANGE.getMax(), false);
		}
		
		/**
		 * Play the tune.
		 */
		public void play() {
			if (isPlaying()) stop();
			
			clip.setFramePosition(0);
			if (loopable) loop();
			else resume();
		}
		
		/**
		 * Play the tune in loops.
		 */
		private void loop() {
			if (isPlaying()) stop();
			clip.setLoopPoints(0, clip.getFrameLength() - 1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		/**
		 * Stop the tune from playing.
		 */
		public void stop() { if (isPlaying()) clip.stop(); }
		
		/**
		 * Resume the tune from the last spot it played.
		 * Start from the beginning if it never played before.
		 */
		public void resume() { if (!isPlaying()) clip.start(); }
		
		/**
		 * Change the volume of the tune.
		 * The previous volume is stored as a backup for unmuting the tune.
		 * 
		 * @param vol - The new volume
		 * @param mute - True if this method was call from mute(true)
		 */
		public void setVolume(float vol, boolean mute) {
			vol = (float) Percentage.limit(vol, VOLUME_RANGE);
		    
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		    gainControl.setValue(20f * (float) Math.log10(vol));
		    
		    //save volume and a backup
		    if (!mute) volume[0] = vol;
		    volume[1] = vol;
		}
		
		/**
		 * Mute or unmute the tune.
		 * An unmuted tune restores its last saved volume.
		 * 
		 * @param flag - True to mute or false to unmute
		 */
		public void mute(boolean flag) {
			float vol = flag ? 0 : volume[0];
			setVolume(vol, flag);
		}
		
		/**
		 * @return the percentage of the tune's volume.
		 */
		public double getVolumePercent() {
			return volume[1] * 100;
		}
		
		/**
		 * @return true if the tune is now playing.
		 */
		public boolean isPlaying() { return clip.isRunning(); }
	}
	
	private static final Range<Float> VOLUME_RANGE = new Range<Float>(0f, 1f);
	
	private static Map<String, Tune> tunes = new HashMap<String, Tune>();
	
	/**
	 * Load a tune. It needs to be activated separately.
	 * 
	 * @param name - The tune's name
	 * @param path - The audio file's logical path
	 * @param loopable - True if the tune should loop
	 */
	public static void loadTune(String name, String path, boolean loopable) {
		if (tunes.containsKey(name)) return;
		
		try {
			Tune tune = new Tune(path, loopable);
			tunes.put(name, tune);
		}
		catch(IOException e1) {
			System.err.println("Could not find the sound file directory " + path + ".");
			e1.printStackTrace();
		}
		catch(UnsupportedAudioFileException e2) {
			System.err.println("The audio file " + path + " is not supported.");
		}
		catch(LineUnavailableException e3) {
			System.err.println("The audio file " + path + " is probably already playing in a nother program.");
			e3.printStackTrace();
		}
	}
	
	/**
	 * @param tune - The tune to play
	 */
	public static void play(String tune) {
		if (!tunes.containsKey(tune)) return;
		else tunes.get(tune).play();
	}
	
	/**
	 * @param tune - The tune to stop
	 */
	public static void stop(String tune) {
		if (!tunes.containsKey(tune)) return;
		else tunes.get(tune).stop();	
	}
	
	/**
	 * @param tune - The tune to resume
	 */
	public static void resume(String tune) {
		if (!tunes.containsKey(tune)) return;
		else tunes.get(tune).resume();
	}
	
	/**
	 * @param tune - The tune to remove
	 */
	public static void remove(String tune) {
		Tune tuneObj = tunes.get(tune);
		if (tuneObj == null) return;
		
		tuneObj.stop();
		tunes.remove(tune);
	}
	
	/**
	 * Change the volume of a tune.
	 * 
	 * @param tune - The tune to change
	 * @param vol - The new volume
	 */
	public static void setVolume(String tune, float vol) {
	    tunes.get(tune).setVolume(vol, false);
	}
	
	/**
	 * @param tune - The tune to check
	 * @return true if the tune is now playing.
	 */
	public static boolean isPlaying(String tune) {
		return tunes.get(tune).isPlaying();
	}
	
	/**
	 * Mute or unmute a tune.
	 * An unmuted tune restores its last saved volume.
	 * 
	 * @param tune - The tune to modify
	 * @param flag - True to mute or false to unmute
	 */
	public static void mute(String tune, boolean flag) {
		tunes.get(tune).mute(flag);
	}
	
	/**
	 * Get a volume percentage of a tune (from 0 to 100).
	 * 
	 * @param tune - The tune to get a volume percentage of
	 * @return the percentage of the tune's volume.
	 */
	public static double getVolumePercent(String tune) {
		return tunes.get(tune).getVolumePercent();
	}
}