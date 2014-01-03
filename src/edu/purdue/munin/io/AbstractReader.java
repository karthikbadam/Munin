/* ------------------------------------------------------------------
 * AbstractReader.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * Based on code by Jean-Daniel Fekete from the IVTK (ivtk.sf.net).
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public abstract class AbstractReader {

	private InputStream in;
    private String      name;
    private String      encoding;
    
    public AbstractReader(InputStream in, String name) {
    	this.in = in;
    	this.name = name;
    }

    public AbstractReader(String name) throws IOException, FileNotFoundException {
        this(open(name), name);
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public void close() throws IOException {
        in.close();
        in = null;
    }
    
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public static InputStream open(String name) throws FileNotFoundException, IOException {
        InputStream is = null;
        if (name.indexOf(':') != -1) {
            try {
                URL url = new URL(name);
                is = url.openStream();
            }
            catch (Exception e) {}
        }
        if (is == null) {
            is = new FileInputStream(name);
        }

        if (name.endsWith(".gz") || name.endsWith(".Z")) {
            is = new GZIPInputStream(is);
        }
        if (is != null && ! (is instanceof BufferedInputStream)) {
            is = new BufferedInputStream(is);
        }

        return is;
    }

}
