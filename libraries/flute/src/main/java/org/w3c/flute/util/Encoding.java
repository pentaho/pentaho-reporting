/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: Encoding.java 1830 2006-04-23 14:51:03Z taqua $
 * 
 * Copyright (c) 1999 - 2017 Hitachi Vantara, World Wide Web Consortium.  All rights reserved.
 */

package org.w3c.flute.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class Encoding {
  private Encoding() {
  }

  /**
   * Converts the format encoding information into Java encoding information.
   */
  public static String getJavaEncoding( String encoding ) {
    String _result = encodings.getProperty( encoding );
    if ( _result == null ) {
      return encoding;
    }
    return _result;
  }

  static Properties encodings;

  static {
    encodings = new Properties();

    try {
      URL url = Encoding.class.getResource( "encoding.properties" );
      InputStream f = url.openStream();
      encodings.load( f );
      f.close();
    } catch ( Exception e ) {
      System.err.println( Encoding.class
        + ": couldn't load encoding properties " );
      e.printStackTrace();
    }
  }
}
