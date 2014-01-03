package edu.purdue.munin.config;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Surface implements Iterable<Surface> {
	private String id;
	private String name;
	private Surface parent = null;
	private Rectangle2D worldGeometry = null;
	private Rectangle2D screenGeometry = null;
	private boolean fullscreen = false;
	private ArrayList<Surface> children = new ArrayList<Surface>();
	public Surface(String id, String name) { 
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getFullId() { 
		StringBuffer sbuf = new StringBuffer(getId());
		Surface curr = getParent();
		while (curr != null) { 
			sbuf.insert(0, curr.getId() + ".");
			curr = curr.getParent();
		}
		return sbuf.toString();
	}
	public void setWorldGeometry(Rectangle2D worldGeometry) {
		this.worldGeometry = worldGeometry;
	}
	public void setScreenGeometry(Rectangle2D screenGeometry) { 
		this.screenGeometry = screenGeometry;
	}
	public Rectangle2D getWorldGeometry() {
		return worldGeometry;
	}
	public Rectangle2D getScreenGeometry() {
		return screenGeometry;
	}
	public void setFullscreen(boolean fullscreen) { 
		this.fullscreen = fullscreen;
	}
	public boolean getFullscreen() {
		return fullscreen;
	}
	public Surface find(String fullId) {
		if (fullId.equals(getFullId())) return this;
		for (Surface child : children) {
			Surface result = child.find(fullId);
			if (result != null) return result;
		}
		return null;
	}
	public Surface getParent() {
		return parent;
	}
	public void setParent(Surface parent) { 
		this.parent = parent;
	}
	public void addChild(Surface child) {
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(Surface child) {
		if (children.remove(child)) {
			child.setParent(null);
		}
	}
	public Iterator<Surface> iterator() {
		return children.iterator();
	}
	public int getChildCount() {
		return children.size();
	}
	public Surface getChild(int index) {
		return children.get(index);
	}
	public int getIndexOfChild(Surface child) {
		return children.indexOf(child);
	}
	public Rectangle2D getBoundingBox() {
		if (worldGeometry != null) return (Rectangle2D) worldGeometry.clone();
		Rectangle2D bbox = null;
		for (Surface child : children) {
			if (bbox == null) bbox = child.getBoundingBox();
			else bbox = bbox.createUnion(child.getBoundingBox());
		}
		return bbox;
	}
	public String toString() {
		if (getName() == null) return "id=" + getFullId();
		else return getName() + " (id=" + getFullId() + ")";
	}
}
