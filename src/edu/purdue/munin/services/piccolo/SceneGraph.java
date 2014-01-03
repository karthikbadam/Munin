/* ------------------------------------------------------------------
 * SceneGraph.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services.piccolo;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.purdue.munin.data.SharedArray;
import edu.purdue.munin.space.SharedObject;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class SceneGraph {
	
	public static final String TYPE_TAG 			= "svg.type";
	public static final String SHAPE_TAG			= "svg.shape";
	
	public static final String IMAGE_FILE_ATTR		= "svg.file";
	public static final String TEXT_TEXT_ATTR		= "svg.text";
	
	private static Random random = new Random();
	
	public static AffineTransform getTransform(SharedObject so) {

		// Make sure there is a transform
		AffineTransform transform = new AffineTransform();
		if (!so.has("svg.transform")) return transform;
		
		// Get the transform string
		String transformString = so.getString("svg.transform");
		if (transformString.length() == 0) return transform;
		
		// Match commands in the transform string
		Pattern splitter = Pattern.compile("(\\w+)\\(([^\\)]+)\\)");
		Matcher m = splitter.matcher(transformString);
		
		// Match transform commands, one at a time (http://www.w3.org/TR/SVG/coords.html)
		while (m.find()) {
			
			// Extract command and arguments
			if (m.groupCount() != 2) continue;
			String cmd = m.group(1);
			String argStrings[] = m.group(2).split("[, ]+");
			double args[] = new double[argStrings.length];
			for (int i = 0; i < argStrings.length; i++) {
				args[i] = Double.parseDouble(argStrings[i]);
			}

			// Determine which command to apply
			if (cmd.equals("translate") && args.length >= 2) {
				transform.translate(args[0], args[1]);
			}
			else if (cmd.equals("matrix") && args.length >= 6) { 
				AffineTransform tx = new AffineTransform(args[0], args[1], args[2], args[3], args[4], args[5]);
				transform.concatenate(tx);
			}
			else if (cmd.equals("rotate") && args.length >= 1) {
				if (args.length == 3) {
					transform.rotate(Math.toRadians(args[0]), args[1], args[2]);
				}
				else {					
					transform.rotate(Math.toRadians(args[0]));
				}
			}
			else if (cmd.equals("scale") && args.length >= 1) {
				transform.scale(args[0], args.length > 1 ? args[1] : args[0]);
			}
			else if (cmd.equals("skewX") && args.length >= 1) {
				transform.shear(Math.tan(Math.toRadians(args[0])), 0);
			}
			else if (cmd.equals("skewY") && args.length >= 1) {
				transform.shear(0, Math.tan(Math.toRadians(args[0])));
			}
		}
		
		return transform;
	}
	
	public static void setTransform(SharedObject so, AffineTransform transform) {
		double m[] = new double[6];
		transform.getMatrix(m);
		so.put("svg.transform", "matrix(" + m[0] + "," + m[1] + "," + m[2] + "," + m[3] + "," + m[4] + "," + m[5] + ")");
	}
	
	public static void clearTransform(SharedObject so) {
		so.put("svg.transform", "");
	}

	public static boolean isTransform(String key) {
		return key.equals("svg.transform");
	}
	
	public static void setTranslationMatrix(SharedObject so, double tx, double ty) {		
		so.put("svg.transform", "translate(" + tx + "," + ty + ")");
	}
		
	public static void translate(SharedObject so, double tx, double ty) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " translate(" + tx + "," + ty + ")");
	}

	public static void scale(SharedObject so, double s) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " scale(" + s + ")");
	}

	public static void scale(SharedObject so, double sx, double sy) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " scale(" + sx + "," + sy + ")");
	}

	public static void setScalingMatrix(SharedObject so, double s) {		
		so.put("svg.transform", "scale(" + s + "," + s + ")");
	}

	public static void setScalingMatrix(SharedObject so, double sx, double sy) {		
		so.put("svg.transform", "scale(" + sx + "," + sy + ")");
	}
	
	public static void rotate(SharedObject so, double angle) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " rotate(" + angle + ")");		
	}

	public static void rotate(SharedObject so, double angle, double cx, double cy) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " rotate(" + angle + "," + cx + "," + cy + ")");		
	}

	public static void setRotationMatrix(SharedObject so, double angle) {		
		so.put("svg.transform", "rotate(" + angle + ")");
	}

	public static void setRotationMatrix(SharedObject so, double angle, double cx, double cy) {
		so.put("svg.transform", "rotate(" + angle + "," + cx + "," + cy + ")");		
	}

	public static void skewX(SharedObject so, double s) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " skewX(" + s + ")");
	}

	public static void skewY(SharedObject so, double s) {
		String transform = so.getString("svg.transform");
		if (transform == null) transform = "";
		so.put("svg.transform", transform + " skewY(" + s + ")");
	}

	public static void setSkewMatrix(SharedObject so, double sx, double sy) {
		so.put("svg.transform", "skewX(" + sx + ") skewY(" + sy + ")");		
	}
	
	public static String getColorString(Color color) {
		return "#" +
		String.format("%02x", color.getAlpha()) + 
		String.format("%02x", color.getRed()) +
		String.format("%02x", color.getGreen()) + 
		String.format("%02x", color.getBlue());
	}

	public static Color getRandomColor(double alpha) {
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), (int) (alpha * 255));		
	}
	
	public static Color getRandomColor() {
		return getRandomColor(1.0);
	}
	
	public static String getRandomColorString() {
		return getColorString(getRandomColor());
	}
	
	public static Color parseColorString(String color) {
		color = color.trim();
		if (color.length() == 0) return null;
		if (color.charAt(0) == '#') color = color.substring(1, color.length());
		int rgba = (int) Long.parseLong(color, 16);
		return new Color(rgba, true);
	}
	
	public static boolean hasStrokeColor(SharedObject so) {
		return so.has("svg.stroke");
	}
	
	public static boolean isStrokeColor(String key) {
		return key.equals("svg.stroke");
	}

	public static void setStrokeColor(SharedObject so, Color color) {
		so.put("svg.stroke", getColorString(color));				
	}
	
	public static Color getStrokeColor(SharedObject so) {
		return parseColorString(so.getString("svg.stroke"));
	}
	
	public static void clearStrokeColor(SharedObject so) {
		so.put("svg.stroke", "");
	}
	
	public static boolean hasFillColor(SharedObject so) {
		return so.has("svg.fill");
	}
	
	public static boolean isFillColor(String key) {
		return key.equals("svg.fill");
	}

	public static void setFillColor(SharedObject so, Color color) {
		so.put("svg.fill", getColorString(color));				
	}

	public static void clearFillColor(SharedObject so) {
		so.put("svg.fill", "");
	}
	
	public static Color getFillColor(SharedObject so) {
		return parseColorString(so.getString("svg.fill"));
	}
	
	public static boolean hasStrokeWidth(SharedObject so) {
		return so.has("svg.stroke-width");
	}

	public static boolean isStrokeWidth(String key) {
		return key.equals("svg.stroke-width");
	}

	public static float getStrokeWidth(SharedObject so) {
		return (float) so.getDouble("svg.stroke-width");
	}
	
	public static void setStrokeWidth(SharedObject so, double width) {
		so.put("svg.stroke-width", width);
	}
	
	public static boolean isNode(SharedObject so) {
		return so.has(TYPE_TAG);
	}
	
	public static boolean isShape(SharedObject so) {
		return so.has(SHAPE_TAG);
	}

	public static boolean isGroup(SharedObject so) {
		return so.hasValue(TYPE_TAG, "g");
	}
	
	public static void createShape(SharedObject so) {
		so.put(SHAPE_TAG, "shape");
	}
	
	public static void createRectangle(SharedObject so, double x, double y, double width, double height) {
		createShape(so);
		so.put(TYPE_TAG, "rect");
		so.putDouble("svg.x", x);
		so.putDouble("svg.y", y);
		so.putDouble("svg.width", width);
		so.putDouble("svg.height", height);
	}

	public static boolean isRect(SharedObject so) {
		return so.hasValue(TYPE_TAG, "rect");
	}
	
	public static PPath getRect(SharedObject so) {
		return PPath.createRectangle((float) so.getDouble("svg.x"), (float) so.getDouble("svg.y"), (float) so.getDouble("svg.width"), (float) so.getDouble("svg.height"));
	}
	
	public static void createCircle(SharedObject so, double cx, double cy, double r) {
		createShape(so);
		so.put(TYPE_TAG, "circle");
		so.putDouble("svg.cx", cx);
		so.putDouble("svg.cy", cy);
		so.putDouble("svg.r", r);
	}

	public static boolean isCircle(SharedObject so) {
		return so.hasValue(TYPE_TAG, "circle");
	}
	
	public static PPath getCircle(SharedObject so) {
		double cy = so.getDouble("svg.cy");
		double cx = so.getDouble("svg.cx");
		double r = so.getDouble("svg.r");
		return PPath.createEllipse((float) (cx - r), (float) (cy - r), 2 * (float) r, 2 * (float) r);
	}

	public static void createEllipse(SharedObject so, double cx, double cy, double rx, double ry) {
		createShape(so);
		so.put(TYPE_TAG, "ellipse");
		so.putDouble("svg.cx", cx);
		so.putDouble("svg.cy", cy);
		so.putDouble("svg.rx", rx);
		so.putDouble("svg.ry", ry);
	}

	public static boolean isEllipse(SharedObject so) {
		return so.hasValue(TYPE_TAG, "ellipse");
	}
	
	public static PPath getEllipse(SharedObject so) {
		double cy = so.getDouble("svg.cx");
		double cx = so.getDouble("svg.cy");
		double rx = so.getDouble("svg.rx");
		double ry = so.getDouble("svg.ry");
		return PPath.createEllipse((float) (cx - rx), (float) (cy - ry), 2 * (float) rx, 2 * (float) ry);
	}

	public static void createLine(SharedObject so, double x1, double y1, double x2, double y2) {
		createShape(so);
		so.put(TYPE_TAG, "line");
		so.putDouble("svg.x1", x1);
		so.putDouble("svg.y1", y1);
		so.putDouble("svg.x2", x2);
		so.putDouble("svg.y2", y2);
	}

	public static boolean isLine(SharedObject so) {
		return so.hasValue(TYPE_TAG, "line");
	}
	
	public static PPath getLine(SharedObject so) {
		return PPath.createLine((float) so.getDouble("svg.x1"), (float) so.getDouble("svg.y1"), (float) so.getDouble("svg.x2"), (float) so.getDouble("svg.y2"));
	}
	
	public static SharedArray getPolyLinePoints(SharedObject so) {
		return so.getArray("svg.points");
	}

	public static void createPolyLine(SharedObject so, Collection<Double> points) {
		createShape(so);
		so.put(TYPE_TAG, "polyline");
		SharedArray pointsArray = new SharedArray();
		for (Double coord : points) {
			pointsArray.add(coord);
		}
		so.put("svg.points", pointsArray);
	}

	public static boolean isPolyLine(SharedObject so) {
		return so.hasValue(TYPE_TAG, "polyline");
	}
	
	public static PPath getPolyLine(SharedObject so) {
		SharedArray pointsArray = so.getArray("svg.points");
		int numPoints = pointsArray.size() / 2;
		float xp[] = new float [numPoints];
		float yp[] = new float [numPoints];
		for (int i = 0; i < numPoints; i++) {
			xp[i] = ((Double) pointsArray.get(2 * i)).floatValue();
			yp[i] = ((Double) pointsArray.get(2 * i + 1)).floatValue();
		}
		return PPath.createPolyline(xp, yp);
	}

	public static void createPolygon(SharedObject so, Collection<Double> points) {
		createShape(so);
		so.put(TYPE_TAG, "polygon");
		SharedArray pointsArray = new SharedArray();
		for (Double coord : points) {
			pointsArray.add(coord);
		}
		so.put("svg.points", pointsArray);
	}

	public static boolean isPolygon(SharedObject so) {
		return so.hasValue(TYPE_TAG, "polygon");
	}
	
	public static PPath getPolygon(SharedObject so) {
		SharedArray pointsArray = so.getArray("svg.points");
		int numPoints = pointsArray.size() / 2;
		GeneralPath polygon = new GeneralPath();
		for (int i = 0; i < numPoints; i++) {
			double x = (Double) pointsArray.get(2 * i);
			double y = (Double) pointsArray.get(2 * i + 1);
			if (i == 0) polygon.moveTo(x, y);
			else polygon.lineTo(x, y);
		}
		polygon.closePath();
		return new PPath(polygon);
	}
	
	public static void createImage(SharedObject so, String filename) {
		so.put(TYPE_TAG, "image");
		so.put(IMAGE_FILE_ATTR, filename);
	}
	
	public static boolean isImage(SharedObject so) {
		return so.hasValue(TYPE_TAG, "image");
	}
	
	public static String getImageFilename(SharedObject so) {
		return so.getString(IMAGE_FILE_ATTR);
	}

	public static void createText(SharedObject so, double x, double y, String text) {
		so.put(TYPE_TAG, "text");
		so.putDouble("svg.x", x);
		so.putDouble("svg.y", y);
		so.put(TEXT_TEXT_ATTR, text);
	}
	
	public static boolean isText(SharedObject so) {
		return so.hasValue(TYPE_TAG, "text");
	}
	
	public static PText getText(SharedObject so) {
		PText text = new PText(so.getString(TEXT_TEXT_ATTR));
		text.setOffset(so.getDouble("svg.x"), so.getDouble("svg.y"));
		return text;
	}	
	
	public static void createGroup(SharedObject so) {
		so.put(TYPE_TAG, "g");
	}
	
	public static void addChild(SharedObject parent, SharedObject child) {
		child.put("svg.parent", parent.getId());
	}
	
	public static boolean isParent(String key) {
		return key.equals("svg.parent");
	}
	
	public static UUID getParent(SharedObject so) {
		return (UUID) so.get("svg.parent");
	}
	
	public static void setIndex(SharedObject so, int index) {
		so.putInt("svg.index", index);
	}
	
	public static int getIndex(SharedObject so) {
		return so.getInt("svg.index");
	}
	
	public static boolean hasIndex(SharedObject so) {
		return so.has("svg.index");
	}
	
	public static boolean isGeometry(String key) {
		return
		key.equals("svg.x") || key.equals("svg.y") || key.equals("svg.width") || key.equals("svg.height") ||
		key.equals("svg.cx") || key.equals("svg.cy") || key.equals("svg.rx") || key.equals("svg.ry") ||
		key.equals("svg.x1") || key.equals("svg.y1") || key.equals("svg.x2") || key.equals("svg.y2") ||
		key.equals("svg.r") || key.equals("svg.points");
	}
	
	public static double getX(SharedObject so) {
		return so.getDouble("svg.x");
	}
	
	public static double getY(SharedObject so) {
		return so.getDouble("svg.y");
	}
	
	public static double getWidth(SharedObject so) {
		return so.getDouble("svg.width");
	}
	
	public static double getHeight(SharedObject so) {
		return so.getDouble("svg.height");
	}
	
	public static double getCenterX(SharedObject so) {
		return so.getDouble("svg.cx");
	}
	
	public static double getCenterY(SharedObject so) {
		return so.getDouble("svg.cy");
	}
	
	public static double getRadius(SharedObject so) {
		return so.getDouble("svg.r");
	}
	
	public static double getRadiusX(SharedObject so) {
		return so.getDouble("svg.rx");
	}

	public static double getRadiusY(SharedObject so) {
		return so.getDouble("svg.ry");
	}

	public static double getX1(SharedObject so) {
		return so.getDouble("svg.x1");
	}

	public static double getY1(SharedObject so) {
		return so.getDouble("svg.y1");
	}

	public static double getX2(SharedObject so) {
		return so.getDouble("svg.x2");
	}

	public static double getY2(SharedObject so) {
		return so.getDouble("svg.width");
	}

	public static boolean hasGeometryChanges(Collection<String> keys) {
		for (String key : keys) {
			if (isGeometry(key)) return true;
		}
		return false;
	}
		
	public static final void main(String[] args) {
		Pattern splitter = Pattern.compile("(\\w+)\\(([^\\)]+)\\)");
		Matcher m = splitter.matcher("matrix(1, 2, 3, 4) translate(10, 20)");
		while (m.find()) {
			if (m.groupCount() != 2) continue;
			String cmd = m.group(1);
			String arguments[] = m.group(2).split("[, ]+");
			System.err.println("command: " + cmd);
			for (String arg : arguments) {
				System.err.print("(" + arg + ") ");
			}
			System.err.println();
		}
		Color color = Color.red;
		String format = "#" +
		String.format("%02x", color.getRed()) +
		String.format("%02x", color.getGreen()) + 
		String.format("%02x", color.getBlue());
		System.err.println("Format: " + format);
		System.err.println("Color: " + parseColorString(format));
		
		int test = (int) Long.parseLong("ff404040", 16);
		System.err.println(" - " + Long.parseLong("ff404040", 16) +", " + test);
	}
}
