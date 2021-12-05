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

public class MazePanel extends View implements P5PanelF21{
    private static final long serialVersionUID = 2787329533730973905L;
    private Color col;
    private static final String TAG = "MazePanel";  //string message
    /**
     * Default minimum value for RGB values.
     * For Wall class
     */
    private static final int RGB_DEF = 20;
    private static final int RGB_DEF_GREEN = 60;

    private Bitmap.Config config; //bitmap configuration settings
    private Bitmap bitmap;  //bitmap with graphics drawn on it
    private Canvas canvas;  //canvas to draw images on the bitmap
    private Paint paint;  //paint being used at the time

    private int color;  //int color that paint is currently set on
    private static final int greenWM = Color.parseColor("#115740");
    private static final int goldWM = Color.parseColor("#916f41");
    private static final int yellowWM = Color.parseColor("#FFFF99");
    private boolean top = true;  //boolean value that tells if top or bottom background rectangle is being drawn
    private String markerFont;  //current font of the marker
    private Paint shaderPaint;

    /**
     * Constructor. Object is not focusable.
     */
    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
        config = Bitmap.Config.ARGB_8888;
        bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, config);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        shaderPaint = new Paint();
    }

    /**
     * This method tests to make sure that the different drawing properties of
     * MazePanel work in android studio by drawing practice lines and shapes
     *
     * it is only used for testing purposes
     */
    private void myTestImage(Canvas c){
        getBufferGraphics();
        setColor(Color.RED);
        addFilledOval(0, 20, 150, 150);
        setColor(Color.GREEN);
        addFilledOval(0, 10, 60, 60);
        setColor(Color.YELLOW);
        addFilledRectangle(200, 200, 100, 49);
        setColor(Color.BLUE);
        int[] x = new int[]{0, 50, 30};
        int[] y = new int[]{200, 200, 400};
        addFilledPolygon(x, y, 3);
        addLine(100, 0, 200, 200);
        addArc(200, 200, 100, 100, 30, 50);
        setColor(Color.BLACK);
        int[] x2 = new int[]{60, 100, 80};
        int[] y2 = new int[]{80, 80, 70};
        addPolygon(x2, y2, 3);
    }

    /**
     * This method scales the bitmap so that it fits into the view that is
     * in playManuallyActivity and PlayAnimationActivity, then paints the
     * scaled bitmap onto the canvas of the view that was passed in
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas){
        //myTestImage(canvas);
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, 1050, 1050, true);
        canvas.drawBitmap(myBitmap, 0, 0, paint);
    }

    public void update(Canvas c){
        paint(c);
    }

    public void update(){
        paint(canvas);
    }
    /**
     * Draws the buffer image to the given graphics object.
     * This method is called when this panel should redraw itself.
     * The given graphics object is the one that actually shows
     * on the screen.
     */
    public void paint(Canvas canvas) {
        if (null == canvas) {
            System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
            Log.v("MazePanel.paint", "no graphics object, skipping drawImage operation");
        }
        else {
            canvas.drawBitmap(bitmap,0,0,null);
        }
    }

    /**
     * Obtains a canvas object that can be used for drawing.
     * This MazePanel object internally stores the canvas object
     * and will return the same graphics object over multiple method calls.
     * The canvas object acts like a notepad where all clients draw
     * on to store their contribution to the overall image that is to be
     * delivered later.
     * To make the drawing visible on screen, one needs to trigger
     * a call of the paint method, which happens
     * when calling the update method.
     * @return graphics object to draw on, null if impossible to obtain image
     */
    public Canvas getBufferGraphics() {
        // if necessary instantiate and store a graphics object for later use
        if (null == canvas) {
            if (null == canvas) {
                config = Bitmap.Config.ARGB_8888;
                bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, config);
                if (null == bitmap)
                {
                    System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
                    return null; // still no buffer image, give up
                }
            }
            canvas = new Canvas(bitmap);
            if (null == canvas) {
                System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
            }
            else{
                Log.v("MazePanel", "Using Rendering Hint");
            }
        }
        return canvas;
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    public void commit() {
        Log.v(TAG, "Updating maze panel");
        invalidate();

    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    public boolean isOperational() {
        if(canvas != null) {
            return true;
        }
        return false;
    }


    /**
     * @param rgb value of color
     */
    public void setColor(int rgb) {
        paint.setColor(rgb);
        color = rgb;

    }

    /**
     * Takes the hex string value of a color
     * and returns that color's int value
     *
     * @param str string of a color
     * @return int value of the color passed in
     */
    public static int decodeColor(String str) {
        return Color.parseColor(str);
    }

    /**
     * @return rgb value of color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the marker font to the
     * value passed in and repaints the
     * compass rose with that font
     * @param fontName name of font
     */
    public void setFont(String fontName) {
        markerFont = fontName;
        paint.setFontFeatureSettings(fontName);
    }

    /**
     * Return the wall color in int
     * @param d
     * @param cc
     * @param extensionX
     * @return
     */
    public static int getWallColor(final int d, final int cc, int extensionX) {
        // compute rgb value, depends on distance and x direction
        final int distance = d / 4;
        // mod used to limit the number of colors to 6
        final int part1 = distance & 7;
        final int add = (extensionX != 0) ? 1 : 0;
        final int rgbValue = ((part1 + 2 + add) * 70) / 8 + 80;
        int wallColor = 0;
        switch (((d >> 3) ^ cc) % 6) {
            case 0:
                wallColor = rgb(rgbValue, RGB_DEF, RGB_DEF);
                break;
            case 1:
                wallColor = rgb(RGB_DEF, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 2:
                wallColor = rgb(RGB_DEF, RGB_DEF, rgbValue);
                break;
            case 3:
                wallColor = rgb(rgbValue, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 4:
                wallColor = rgb(RGB_DEF, RGB_DEF_GREEN, rgbValue);
                break;
            case 5:
                wallColor = rgb(rgbValue, RGB_DEF, rgbValue);
                break;
            default:
                wallColor = rgb(RGB_DEF, RGB_DEF, RGB_DEF);
                break;
        }
        return wallColor;
    }

    /**
     * Calculates the weighted average of the two given colors
     * @param c0 the one color
     * @param c1 the other color
     * @param weight0 of c0
     * @return blend of colors c0 and c1 as weighted average
     */
    private int blend(Color c0, Color c1, double weight0) {
        if (weight0 < 0.1)
            return c1.toArgb();
        if (weight0 > 0.95)
            return c0.toArgb();
        double r = weight0 * c0.red() + (1-weight0) * c1.red();
        double g = weight0 * c0.green() + (1-weight0) * c1.green();
        double b = weight0 * c0.blue() + (1-weight0) * c1.blue();
        double a = Math.max(c0.alpha(), c1.alpha());

        return Color.valueOf((int) r, (int) g, (int) b, (int) a).toArgb();
    }


    /**
     * Determine the background color for the top and bottom
     * rectangle as a blend between starting color settings
     * of black and grey towards gold and green as final
     * color settings close to the exit
     * @param percentToExit
     * @param top is true for the top triangle, false for the bottom
     * @return the color to use for the background rectangle
     */
    private int getBackgroundColor(float percentToExit, boolean top) {
        return top? blend(Color.valueOf(yellowWM), Color.valueOf(goldWM), percentToExit) :
                blend(Color.valueOf(Color.LTGRAY), Color.valueOf(greenWM), percentToExit);
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
        //graphics.setColor(getBackgroundColor(percentToExit, true));
        //graphics.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);
        // grey rectangle in lower half of screen
        //graphics.setColor(getBackgroundColor(percentToExit, false));
        //graphics.fillRect(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);

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
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float right = x + width;
        float bottom = y + height;
        canvas.drawRect((float) x, (float) y, right, bottom, paint);
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
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = new Path();
        path.reset();
        if(xPoints != null & yPoints != null){
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i = 1; i < nPoints; i++){
                path.lineTo((float)xPoints[i], (float)yPoints[i]);
            }
            path.close();
            canvas.drawPath(path, paint);
        }

    }

    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.reset();
        if(xPoints != null & yPoints != null){
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i = 1; i < nPoints; i++){
                path.lineTo((float)xPoints[i], (float)yPoints[i]);
            }
            path.close();
            canvas.drawPath(path, paint);
        }

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
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawLine((float)startX, (float)startY, (float)endX, (float)endY,paint);

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
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float right = width + x;
        float bottom = height + y;
        canvas.drawOval((float) x, (float) y, right, bottom, paint);

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
        paint.setStyle(Paint.Style.STROKE);
        float right = x + width;
        float bottom = y + height;
        canvas.drawArc((float)x, (float) y, right, bottom, (float)startAngle, (float)arcAngle, false, paint);
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
        canvas.drawText(str, x, y, paint);
    }

    /**
     * Takes the float values of a color and
     * returns that color's int value
     *
     * @param r value of the red value of the color
     * @param g value of the green value of the color
     * @param b value of the blue value of the color
     * @param a value of the alpha value of the color
     * @return int value of the color passed in
     */
    public static int createNewColor(float r, float g, float b, float a) {
        return Color.valueOf(r, g, b, a).toArgb();
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
        //switch (hintKey) {
            //case KEY_RENDERING:
                //if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                //}
                //else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //}
                //else {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                //}
                //break;
            //case KEY_INTERPOLATION:
                //if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                //}
                //else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //}
                //else {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                //}
                //break;
            //case KEY_ANTIALIASING:
                //if (hintValue == P5RenderingHints.VALUE_ANTIALIAS_ON) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                //}
                //else if (hintValue == P5RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //}
                //else {
                    //graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                //}
                //break;
            //default:
                //break;
        //}

    }
}
