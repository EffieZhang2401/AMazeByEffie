package edu.wm.cs.cs301.EffieZhang.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.fonts.Font;

/**
 * Implements the screens that are displayed whenever the game is not in 
 * the playing state. The screens shown are the title screen, 
 * the generating screen with the progress bar during maze generation,
 * and the final screen when the game finishes.
 * The only one that is not simple and not covered by this class
 * is the one that shows the first person view of the maze game
 * and the map of the maze when the user really navigates inside the maze.
 * 
 * {@code W&M} specific color settings
 * Green: #115740
 * Gold: #916f41
 * Black: #222222
 * 
 * Design: 
 * white background with green and gold frame 
 * title text large and gold, 
 * normal text small and green
 * small text small and black
 * 
 * @author Peter Kemper
 *
 */
public class SimpleScreens {
	// Color settings
	final int greenWM = Color.parseColor("#115740");
	final int goldWM = Color.parseColor("#916f41");
	final int blackWM = Color.parseColor("#222222");
	final String errorMsg = "SimpleScreens:can't get graphics object to draw on, skipping redraw operation";

	/**
	 * Draws the title screen, screen content is hard coded
	 * @param panel holds the graphics for the off-screen image
	 * @param filename is a string put on display for the file
	 * that contains the maze, can be null
	 */
//    public void redrawTitle(MazePanel panel, String filename) {
////        Graphics g = panel.getBufferGraphics() ;
//        if (!panel.isOperational()) {
//            System.out.println(errorMsg) ;
//        }
//        else {
//            redrawTitle(panel,filename);
//        }
//    }
	/**
	 * Helper method for redraw to draw the title screen, screen is hard coded
	 * @param  "gc" graphics is the off-screen image, can not be null
	 * @param filename is a string put on display for the file
	 * that contains the maze, can be null
	 */
	public void redrawTitle(MazePanel panel, String filename) {

		if (!panel.isOperational()) {
			System.out.println(errorMsg) ;
		}
		// produce white background
		drawBackground(panel);

		// write the title
		updateFontAndColor(panel, largeBannerFont, goldWM);
		centerString(panel, "MAZE", 100);
		// write the reference to Paul Falstad
		updateFontAndColor(panel, smallBannerFont, greenWM);
		centerString(panel, "by Paul Falstad", 160);
		centerString(panel, "www.falstad.com", 190);
		// write the instructions in black, same smallBannerFont as before
		panel.setColor(blackWM);
		if (filename == null) {
			// default instructions
			centerString(panel, "To start, select a skill level.", 250);
			centerString(panel, "(Press a number from 0 to 9,", 300);
			centerString(panel, "or a letter from a to f)", 320);
		}
		else {
			// message if maze is loaded from file
			centerString(panel, "Loading maze from file:", 250);
			centerString(panel, filename, 300);
		}
		centerString(panel, "Version 4.1", 350);
	}
	/**
	 * Updates the font and color settings of the given graphics object
	 * @param "gc"
	 * @param font
	 * @param color
	 */
	private void updateFontAndColor(MazePanel panel, Font font, int color) {
//        panel.setFont(font);
		panel.setColor(color);
	}
	/**
	 * Draws the background, a green and cold frame with
	 * a white center stage area
	 * @param "gc" the graphics to draw on
	 */
	private void drawBackground(MazePanel panel) {
		panel.setColor(greenWM);
		panel.addFilledRectangle(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		panel.setColor(goldWM);
		panel.addFilledRectangle(10, 10, Constants.VIEW_WIDTH-20, Constants.VIEW_HEIGHT-20);
		panel.setColor(Color.WHITE);
		panel.addFilledRectangle(15, 15, Constants.VIEW_WIDTH-30, Constants.VIEW_HEIGHT-30);
	}

	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	/**
	 * Draws the finish screen, screen content is hard coded
	 * @param panel holds the graphics for the off-screen image
	 */
//    void redrawFinish(MazePanel panel, int pathLength, float energyCons) {
//        if (!panel.isOperational()) {
//            System.out.println(errorMsg) ;
//        }
//        else {
//            redrawFinish(panel, pathLength, energyCons);
//        }
//    }
	/**
	 * Helper method for redraw to draw final screen, screen is hard coded
	 * @param "gc" graphics is the off-screen image
	 */
	public void redrawFinish(MazePanel panel, int pathLength, float energyCons) {
		// produce blue background
		drawBackground(panel);
		// write the title
		updateFontAndColor(panel, largeBannerFont, goldWM);
		centerString(panel, "You won!", 100);
		// write some extra blurb
		updateFontAndColor(panel, smallBannerFont, greenWM);
		centerString(panel, "Congratulations!", 160);
		centerString(panel, "\n\nPath Length: " + pathLength, 210);
		centerString(panel, "Energy Consumption: " + energyCons, 250);
		// write the instructions
		panel.setColor(blackWM);
		centerString(panel, "Hit any key to restart", 300);
	}
	/**
	 * Draws the generating screen, screen content is hard coded
	 * @param panel holds the graphics for the off-screen image
	 */
//    public void redrawGenerating(MazePanel panel, int percentDone) {
//        if (!panel.isOperational()) {
//            System.out.println(errorMsg) ;
//        }
//        else {
//            redrawGenerating(panel, percentDone);
//        }
//    }
	/**
	 * Helper method for redraw to draw screen during phase of maze generation,
	 * screen is hard coded, only percentage is dynamic
	 * @param "gc" graphics is the off screen image
	 * @param percentage is the percentage of progress to show
	 */
	private void redrawGenerating(MazePanel panel, int percentage) {
		// produce  background and  title
		drawBackground(panel);
		updateFontAndColor(panel, largeBannerFont, goldWM);
		centerString(panel, "Building maze", 150);
		// show progress
		updateFontAndColor(panel, smallBannerFont, greenWM);
		centerString(panel, percentage + "% completed", 200);
		// write the instructions
		panel.setColor(blackWM);
		centerString(panel, "Hit escape to stop", 300);
	}

	private void centerString(MazePanel panel, String str, int ypos) {
//        panel.drawString(str,
//                (Constants.VIEW_WIDTH-g.getFontMetrics().stringWidth(str))/2,
//                ypos);
		panel.addMarker(Constants.VIEW_WIDTH, ypos, str);


	}

//    final Font largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
//    final Font smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);

	final Font largeBannerFont = null;
	final Font smallBannerFont = null;

}
