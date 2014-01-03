/* ------------------------------------------------------------------
 * MultiDim.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 16, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.services.piccolo.SceneGraph;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class MultiDim implements ServiceFactory {
	
	private Color colorList[] = { Color.green, Color.red, Color.blue, Color.yellow, Color.orange, Color.magenta };
	
	public class MultiDimService implements Service, SharedSpaceEventListener  {
		
		private boolean active = false;
		private double mx, my;
		private SharedObject root, panel;
		private Platform platform;
		private JMultiDimFrame frame;
		
		public MultiDimService(Platform platform, Surface surface) {
			this.platform = platform;
			this.root = this.platform.getRoot(surface);
			this.frame = new JMultiDimFrame();
			this.platform.getSpace().addEventListener(this);
		}
		
		public ServiceFactory getFactory() {
			return MultiDim.this;
		}

		public void start() {
			frame.show();
		}

		public void stop() {
		}
		
		public void eventReceived(SharedEvent event) {
			if (event.hasValue("input", "mouse")) {
				
				// Mouse press
				if (event.has("button1") && event.getBoolean("button1") == true) {

					// Get start point
					mx = event.getDouble("x");
					my = event.getDouble("y");
					active = true;
					
					// Create the new element
					panel = platform.getSpace().createObject();
					SceneGraph.createRectangle(panel, 0, 0, 1, 1);
					SceneGraph.setFillColor(panel, Color.white);
					SceneGraph.setStrokeColor(panel, Color.black);
					SceneGraph.setTranslationMatrix(panel, mx, my);
					SceneGraph.addChild(root, panel);
					panel.commit();
				}
				// Mouse drag
				else if (!event.has("button1") && panel != null && active) {
					
					// Determine the rubber box dimensions
					double x = mx;
					double y = my;
					double diffX = event.getDouble("x") - mx;
					double diffY = event.getDouble("y") - my;
					if (diffX < 0) {
						x = mx + diffX;
						diffX = -diffX;
					}
					if (diffY < 0) {
						y = my + diffY;
						diffY = -diffY;
					}
					
					// Update the node
					SceneGraph.setTranslationMatrix(panel, x, y);
					SceneGraph.createRectangle(panel, 0, 0, diffX, diffY);
					panel.commit();
				}
				// Mouse release
				else if (event.has("button1") && event.getBoolean("button1") == false && panel != null) {
					
					active = false;
					
					// Create a visualization in response
					switch (frame.getSelectedType()) {
					case ScatterPlot:
						createScatterPlot(panel, frame.getTable(), frame.getSelectedMapping());
						break;
						
					case ParallelCoords:
						createParCoordPlot(panel, frame.getTable(), frame.getSelectedMapping());
						break;
			
					case BarChart:
						createBarChart(panel, frame.getTable(), frame.getSelectedMapping());
						break;

					case LineChart:
						createLineChart(panel, frame.getTable(), frame.getSelectedMapping());
						break;
					}
				}
			}
		}

		public void createLineChart(SharedObject panel, Table table, List<Column> mapping) {

			// Sanity check
			if (mapping.size() < 1) return;
			
			
			// Figure out how much space we have
			double padding = 0;
			double width = SceneGraph.getWidth(panel) - 2 * padding;
			double height = SceneGraph.getHeight(panel) - 2 * padding;
			int rowCount = mapping.get(0).getRowCount();			
			double step = width / (rowCount - 1);
			
			// Find the global extrema
			double max = -Double.MAX_VALUE;
			double min =  Double.MAX_VALUE;
			for (Column c : mapping) {
				if (c.getMin() < min) min = c.getMin();
				if (c.getMax() > max) max = c.getMax();
			}
			double range = max - min;
						
			// Create the polylines
			for (int i = 0; i < mapping.size(); i++) {
				
				// Figure out data parameters
				Column c = mapping.get(i);
				Color color = colorList[i % colorList.length];
				
				// Create the polyline
				SharedObject polyLine = platform.getSpace().createObject();
				SceneGraph.setStrokeColor(polyLine, color);
				SceneGraph.setStrokeWidth(polyLine, 2.0);
				SceneGraph.addChild(panel, polyLine);

				// Create the points
				ArrayList<Double> coords = new ArrayList<Double>(); 
				for (int row = 0; row < rowCount; row++) {
					double value = (c.getRealValueAt(row) - min) / range;
					coords.add(row * step + padding);
					coords.add((1.0 - value) * height + padding);
				}
				
				// Save the points data
				SceneGraph.createPolyLine(polyLine, coords);
				polyLine.commit();
			}
		}
		
		public void createBarChart(SharedObject panel, Table table, List<Column> mapping) {

			// Sanity check
			if (mapping.size() != 2) return;
			
			Column dataAxis = mapping.get(0);
			Column labelAxis = mapping.get(1);
			
			Color barColor = SceneGraph.getRandomColor(0.25);

			// Count the labels and calculate their aggregate (sum)
			ArrayList<String> labels = new ArrayList<String>();
			HashMap<String, Double> data = new HashMap<String, Double>();
			for (int i = 0; i < table.getRowCount(); i++) {
				String label = labelAxis.getStringValueAt(i);
				double value = dataAxis.getRealValueAt(i);
				if (!labels.contains(label)) {
					labels.add(label);
					data.put(label, 0.0);
				}
				data.put(label, data.get(label) + value);
			}

			// Figure out how much space we have
			double padding = 10;
			double width = SceneGraph.getWidth(panel) - 2 * padding;
			double height = SceneGraph.getHeight(panel) - 2 *padding;
			
			double gridWidth = width / labels.size();
			double barWidth = 0.8 * gridWidth;
	
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			for (Double value : data.values()) {
				if (value < min) min = value;
				if (value > max) max = value;
			}
			double range = max; // - min;
			
			// Add the bars
			double prevLabelPos = -Integer.MAX_VALUE;
			for (int i = 0; i < labels.size(); i++) {

				// Retrieve data
				String label = labels.get(i);
				double value = data.get(label) / range;
				
				// Bar
				SharedObject bar = platform.getSpace().createObject();
				SceneGraph.createRectangle(bar, (i + 0.1) * gridWidth + padding, (1 - value) * height + padding, barWidth, value * height);
				SceneGraph.setStrokeColor(bar, Color.gray);
				SceneGraph.setFillColor(bar, barColor);
				SceneGraph.addChild(panel, bar);
				bar.commit();

				// Label
				double labelPos = (i + 0.5) * gridWidth + padding;
				if (Math.abs(prevLabelPos - labelPos) > 20) { 
					SharedObject labelObject = platform.getSpace().createObject();
					SceneGraph.createText(labelObject, labelPos, height, label);
					SceneGraph.addChild(panel, labelObject);
					labelObject.commit();
					prevLabelPos = labelPos;
				}
			}

			// Add label
			SharedObject horzLabel = platform.getSpace().createObject();
			SceneGraph.createText(horzLabel, 0, -15, dataAxis.getName());
			SceneGraph.addChild(panel, horzLabel);
			horzLabel.commit();
		}

		public void createParCoordPlot(SharedObject panel, Table table, List<Column> mapping) {
			
			// Sanity check
			if (mapping.size() != 1) return;
			
			Column colorAxis = mapping.get(0);
			HashMap<String, Integer> valueLookup = new HashMap<String, Integer>();

			// Figure out how much space we have
			double padding = 10;
			double width = SceneGraph.getWidth(panel) - 2 * padding;
			double height = SceneGraph.getHeight(panel) - 2 *padding;
			
			// How many columns?
			ArrayList<Column> columns = new ArrayList<Column>();
			for (int i = 0; i < table.getColumnCount(); i++) {
				if (table.getColumnAt(i).isNumeric()) columns.add(table.getColumnAt(i));
			}
			double columnSpacing = width / (columns.size() - 1);
			
			// Add the columns
			for (int i = 0; i < columns.size(); i++) {
				
				// Axis
				SharedObject axis = platform.getSpace().createObject();
				SceneGraph.createLine(axis, i * columnSpacing + padding, padding, i * columnSpacing + padding, height + padding);
				SceneGraph.setStrokeColor(axis, Color.darkGray);
				SceneGraph.addChild(panel, axis);
				axis.commit();
				
				// Label
				SharedObject label = platform.getSpace().createObject();
				SceneGraph.createText(label, i * columnSpacing, 0, columns.get(i).getName());
				SceneGraph.addChild(panel, label);
				label.commit();
			}

			// Add points (as polylines)
			for (int i = 0; i < table.getRowCount(); i++) {
				Color pointColor = Color.red;
				if (colorAxis.isAlpha()) {
					if (!valueLookup.containsKey(colorAxis.getStringValueAt(i))) {
						valueLookup.put(colorAxis.getStringValueAt(i), valueLookup.size() % colorList.length);
					}
					int ndx = valueLookup.get(colorAxis.getStringValueAt(i));
					pointColor = colorList[ndx];
				}

				// Create the polyline
				SharedObject polyLine = platform.getSpace().createObject();
				SceneGraph.setStrokeColor(polyLine, pointColor);
				SceneGraph.addChild(panel, polyLine);
				
				// Find the value for each pair of columns
				ArrayList<Double> points = new ArrayList<Double>(); 
				for (int j = 0; j < columns.size(); j++) {
					Column c = columns.get(j);
					double range = c.getMax() - c.getMin();
					double value = (c.getRealValueAt(i) - c.getMin()) / range;
					points.add(j * columnSpacing + padding);
					points.add((1.0 - value) * height + padding);
				}

				// Save the points data
				SceneGraph.createPolyLine(polyLine, points);
				polyLine.commit();
			}
			
		}
		
		public void createScatterPlot(SharedObject panel, Table table, List<Column> mapping) {
			
			// Sanity check
			if (mapping.size() != 3) return;
			if (!mapping.get(0).isNumeric() ||
				!mapping.get(1).isNumeric())
				return;
			
			// Get the columns
			Column xAxis = mapping.get(0);
			Column yAxis = mapping.get(1);
			Column colorAxis = mapping.get(2);
			
			HashMap<String, Integer> valueLookup = new HashMap<String, Integer>();
			
			// Figure out how much space we have
			double markSize = 10.0;
			double alphaColor = 0.25;
			
			double width = SceneGraph.getWidth(panel) - 2 * markSize;
			double height = SceneGraph.getHeight(panel) - 2 * markSize;
			double xRange = xAxis.getMax() - xAxis.getMin();
			double yRange = yAxis.getMax() - yAxis.getMin();
			double colorRange = colorAxis.getMax() - colorAxis.getMin();
			
			// Add points
			for (int i = 0; i < table.getRowCount(); i++) {
				
				double xVal = (xAxis.getRealValueAt(i) - xAxis.getMin()) / xRange; 
				double yVal = (yAxis.getRealValueAt(i) - yAxis.getMin()) / yRange;
				
				Color pointColor;
				if (colorAxis.isNumeric()) {
					double colorVal = (colorAxis.getRealValueAt(i) - colorAxis.getMin()) / colorRange;
					pointColor = new Color((float) colorVal, 0.0f, 0.0f, (float) alphaColor);
				}
				else {
					if (!valueLookup.containsKey(colorAxis.getStringValueAt(i))) {
						valueLookup.put(colorAxis.getStringValueAt(i), valueLookup.size() % colorList.length);
					}
					int ndx = valueLookup.get(colorAxis.getStringValueAt(i));
					pointColor = new Color(colorList[ndx].getRed(), colorList[ndx].getGreen(), colorList[ndx].getBlue(), (int) (alphaColor * 256));
				}
				
				SharedObject point = platform.getSpace().createObject();
				SceneGraph.createCircle(point, xVal * width + markSize, (1.0 - yVal) * height + markSize, markSize / 2.0);
				SceneGraph.setFillColor(point, pointColor);
				SceneGraph.clearStrokeColor(point);
				SceneGraph.addChild(panel, point);
				point.commit();
			}
			
			// Add labels
			SharedObject vertLabel = platform.getSpace().createObject();
			SceneGraph.createText(vertLabel, 10, 10, yAxis.getName());
			SceneGraph.rotate(vertLabel, 90);
			SceneGraph.addChild(panel, vertLabel);
			vertLabel.commit();
			
			SharedObject horzLabel = platform.getSpace().createObject();
			SceneGraph.createText(horzLabel, 0, -15, xAxis.getName());
			SceneGraph.addChild(panel, horzLabel);
			horzLabel.commit();
		}
	}

	public String getName() {
		return "MultiDim";
	}

	public int getVersion() {
		return 0;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}

	public Service create(Platform platform, Surface surface) {
		MultiDimService service = new MultiDimService(platform, surface);
		return service;
	}
}
