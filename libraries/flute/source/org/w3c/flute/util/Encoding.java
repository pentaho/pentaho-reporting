/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 1999 - 2009 Pentaho Corporation, World Wide Web Consortium.  All rights reserved.
 */

package org.w3c.flute.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class Encoding {
    private Encoding() {}

    /**
     * Converts the format encoding information into Java encoding information.
     */
    public static String getJavaEncoding(String encoding) {
	String _result = encodings.getProperty(encoding);
	if (_result == null) {
	    return encoding;
	}
	return _result;
    }

    static Properties encodings;

    static {
	encodings = new Properties();
	
	try {
            URL url = Encoding.class.getResource("encoding.properties");
            InputStream f = url.openStream();
            encodings.load(f);
            f.close();
        } catch (Exception e) {
            System.err.println(Encoding.class
                               + ": couldn't load encoding properties ");
            e.printStackTrace();
	}
    }
}
