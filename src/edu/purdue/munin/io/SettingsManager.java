package edu.purdue.munin.io;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Generic settings manager class that is able to load multiple XML
 * settings files from a directory hierarchy.  Used in Munin for 
 * managing assemblies and space configurations.
 * 
 * @author elm
 */
public class SettingsManager<E extends Settings> implements Iterable<E> {
	private FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.getName().endsWith("xml");
		}
	};
	private Vector<E> items = new Vector<E>();
	private HashSet<File> files = new HashSet<File>();
	
	public void addSettingsFrom(File path, SettingsFactory<E> factory) {
		if (path.isDirectory()) {
			for (File subPath : path.listFiles(filter)) {
				addSettingsFrom(subPath, factory);
			}
		}
		else {
			if (files.contains(path)) {
				System.err.println("Settings file '" + path.getAbsolutePath() + " already loaded, skipping.");
				return;
			}
			E item = factory.create();
			files.add(path);
			boolean success = item.load(path);
			if (success) {
				items.add(item);
				System.err.println("...loaded setting " + path.getAbsolutePath());
			}
		}
	}
	
	public void add(E item) {
		items.add(item);
	}
	
	public Iterator<E> iterator() {
		return items.iterator();
	}
	
	public int getCount() {
		return items.size();
	}
	
	public E getItemAt(int index) {
		return items.get(index);
	}
	
	public Vector<E> getItems() {
		return items;
	}
	
	public void clear() {
		items.clear();
		files.clear();
	}
}
