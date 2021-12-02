package edu.wm.cs.cs301.EffieZhang.gui;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import static android.graphics.Color.rgb;
import android.view.View;

public class MazePanel extends View{
    private static final long serialVersionUID = 2787329533730973905L;
    private Color col;
    private static final String TAG = "MazePanel";  //string message
    /**
     * Default minimum value for RGB values.
     * For Wall class
     */
    private static final int RGB_DEF = 20;
    private static final int RGB_DEF_GREEN = 60;

    // bufferImage can only be initialized if the container is displayable,
    // uses a delayed initialization and relies on client class to call initBufferImage()
    // before first use
    private Image bufferImage;
    private Graphics2D graphics; // obtained from bufferImage,
    // graphics is stored to allow clients to draw on the same graphics object repeatedly
    // has benefits if color settings should be remembered for subsequent drawing operations

    /**
     * Constructor. Object is not focusable.
     */
    public MazePanel() {
        setFocusable(false);
        bufferImage = null; // bufferImage initialized separately and later
        graphics = null;	// same for graphics
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Method to draw the buffer image on a graphics object that is
     * obtained from the superclass.
     * Warning: do not override getGraphics() or drawing might fail.
     */
    public void update() {
        paint(getGraphics());
    }

    /**
     * Draws the buffer image to the given graphics object.
     * This method is called when this panel should redraw itself.
     * The given graphics object is the one that actually shows
     * on the screen.
     */
    @Override
    public void paint(Graphics g) {
        if (null == g) {
            System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
        }
        else {
            g.drawImage(bufferImage,0,0,null);
        }
    }

    /**
     * Obtains a graphics object that can be used for drawing.
     * This MazePanel object internally stores the graphics object
     * and will return the same graphics object over multiple method calls.
     * The graphics object acts like a notepad where all clients draw
     * on to store their contribution to the overall image that is to be
     * delivered later.
     * To make the drawing visible on screen, one needs to trigger
     * a call of the paint method, which happens
     * when calling the update method.
     * @return graphics object to draw on, null if impossible to obtain image
     */
    public Graphics getBufferGraphics() {
        // if necessary instantiate and store a graphics object for later use
        if (null == graphics) {
            if (null == bufferImage) {
                bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
                if (null == bufferImage)
                {
                    System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
                    return null; // still no buffer image, give up
                }
            }
            graphics = (Graphics2D) bufferImage.getGraphics();
            if (null == graphics) {
                System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
            }
            else {
                // System.out.println("MazePanel: Using Rendering Hint");
                // For drawing in FirstPersonDrawer, setting rendering hint
                // became necessary when lines of polygons
                // that were not horizontal or vertical looked ragged
                setRenderingHint(P5RenderingHints.KEY_ANTIALIASING,
                        P5RenderingHints.VALUE_ANTIALIAS_ON);
                setRenderingHint(P5RenderingHints.KEY_INTERPOLATION,
                        P5RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        }
        return graphics;
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        paint(getGraphics());

    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        if(graphics == null) {
            return false;
        }
        return true;
    }


    /**
     * @param rgb value of color
     */
    @Override
    public void setColor(int rgb) {
        col = new Color(rgb);
        graphics.setColor(col);
    }

    /**
     * sets Color with rgba value
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public void setColor(float[] rgba) {
        col = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
        graphics.setColor(col);
    }

    /**
     * @return rgb value of color
     */
    @Override
    public int getColor() {
        return col.getRGB();
    }

    /**
     * @param color to be decoded
     * @return rgb value of decoded color
     */
    public static int getColor(String decode) {
        return Color.decode(decode).getRGB();
    }

    //enum some commoncolors
    public enum CommonColors {
        WHITE,
        GRAY,
        BLACK,
        RED,
        YELLOW
    }
    /**
     * @param enumerated color
     * @return
     */
    public static int getColor(CommonColors color) {
        switch (color) {
            case WHITE:
                return Color.white.getRGB();
            case GRAY:
                return Color.gray.getRGB();
            case BLACK:
                return Color.black.getRGB();
            case RED:
                return Color.red.getRGB();
            case YELLOW:
                return Color.yellow.getRGB();
        }
        return 0;
    }

    /**
     *
     * @param fontCode
     * @return font name
     */
    public static String getFontName(String fontCode) {
        return Font.decode(fontCode).getFontName();
    }

    /**
     *
     * @param fontCode
     * @return font style int
     */
    public static int getFontStyle(String fontCode) {
        return Font.decode(fontCode).getStyle();
    }

    /**
     *
     * @param fontCode
     * @return font size
     */
    public static int getFontSize(String fontCode) {
        return Font.decode(fontCode).getSize();
    }

    // private Font fields
    private String fontName;
    private int fontStyle;
    private int fontSize;

    /**
     * set the font by name of the font
     * @param String fn
     */
    public void setFontName(String fn) {
        fontName = fn;
    }

    /**
     * set the font by int of the style
     * @param fstyle
     */
    public void setFontStyle(int fstyle) {
        fontStyle = fstyle;
    }

    /**
     * set the font's size
     * @param fsize
     */
    public void setFontSize(int fsize) {
        fontSize = fsize;
    }

    /**
     * Return the wall color in int
     * @param int distance
     * @param int cc
     * @param int extensionX
     * @return
     */
    public int getWallColor(final int d, final int cc, int rgbValue) {
        // compute rgb value, depends on distance and x direction
        final int distance = d/4;
        Color col;
        switch (((distance >> 3) ^ cc) % 6) {
            case 0:
                col = new Color(rgbValue, RGB_DEF, RGB_DEF);
                break;
            case 1:
                col = new Color(RGB_DEF, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 2:
                col = new Color(RGB_DEF, RGB_DEF, rgbValue);
                break;
            case 3:
                col = new Color(rgbValue, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 4:
                col = new Color(RGB_DEF, RGB_DEF_GREEN, rgbValue);
                break;
            case 5:
                col = new Color(rgbValue, RGB_DEF, rgbValue);
                break;
            default:
                col = new Color(RGB_DEF, RGB_DEF, RGB_DEF);
                break;
        }
        return col.getRGB();
    }

    /**
     * Calculates the weighted average of the two given colors
     * @param c0 the one color
     * @param c1 the other color
     * @param weight0 of c0
     * @return blend of colors c0 and c1 as weighted average
     */
    private Color blend(Color c0, Color c1, double weight0) {
        if (weight0 < 0.1)
            return c1;
        if (weight0 > 0.95)
            return c0;
        double r = weight0 * c0.getRed() + (1-weight0) * c1.getRed();
        double g = weight0 * c0.getGreen() + (1-weight0) * c1.getGreen();
        double b = weight0 * c0.getBlue() + (1-weight0) * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    final Color greenWM = Color.decode("#115740");
    final Color goldWM = Color.decode("#916f41");
    final Color yellowWM = Color.decode("#FFFF99");
    /**
     * Determine the background color for the top and bottom
     * rectangle as a blend between starting color settings
     * of black and grey towards gold and green as final
     * color settings close to the exit
     * @param percentToExit
     * @param top is true for the top triangle, false for the bottom
     * @return the color to use for the background rectangle
     */
    private Color getBackgroundColor(float percentToExit, boolean top) {
        return top? blend(yellowWM, goldWM, percentToExit) :
                blend(Color.lightGray, greenWM, percentToExit);
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        // black rectangle in upper half of screen
        graphics.setColor(getBackgroundColor(percentToExit, true));
        graphics.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);
        // grey rectangle in lower half of screen
        graphics.setColor(getBackgroundColor(percentToExit, false));
        graphics.fillRect(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);

    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);

    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.fillPolygon(xPoints, yPoints, nPoints);

    }

    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.drawPolygon(xPoints, yPoints, nPoints);

    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        graphics.drawLine(startX, startY, endX, endY);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Graphics.fillOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);

    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * Substitute for Graphics.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    /**
     * Adds a string at the given position.
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        Font f = new Font(fontName, fontStyle, fontSize);
        GlyphVector gv = f.createGlyphVector(graphics.getFontRenderContext(), str);
        Rectangle2D rect = gv.getVisualBounds();

        x -= rect.getWidth() / 2;
        y += rect.getHeight() / 2;

        graphics.drawGlyphVector(gv, x, y);
    }

    /**
     * Sets the value of a single preference for the rendering algorithms.
     * It internally maps given parameter values into corresponding java.awt.RenderingHints
     * and assigns that to the internal graphics object.
     * Hint categories include controls for rendering quality
     * and overall time/quality trade-off in the rendering process.
     * Refer to the awt RenderingHints class for definitions of some common keys and values.
     * @param hintKey the key of the hint to be set.
     * @param hintValue the value indicating preferences for the specified hint category.
     */
    @Override
    public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {
        switch (hintKey) {
            case KEY_RENDERING:
                if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                }
                else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                }
                else {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                }
                break;
            case KEY_INTERPOLATION:
                if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                }
                else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                }
                else {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                }
                break;
            case KEY_ANTIALIASING:
                if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                }
                else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                }
                else {
                    graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                }
                break;
            default:
                break;
        }

    }
}
