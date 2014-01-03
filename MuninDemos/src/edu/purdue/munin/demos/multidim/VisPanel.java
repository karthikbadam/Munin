/* ------------------------------------------------------------------
 * VisPanel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.util.List;

import edu.purdue.pivotlib.data.Column;

public interface VisPanel {
	public enum VisType { None, BarChart, ScatterPlot, LineChart, ParallelCoords };
	
	public String getTabTitle();
	public VisType getType();
	public List<Column> getMapping();
//	public void createVis(Platform platform);
}
