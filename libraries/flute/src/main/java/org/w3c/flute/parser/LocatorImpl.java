/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: LocatorImpl.java 1830 2006-04-23 14:51:03Z taqua $
 */
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
 * Copyright (c) 1999 - 2017 Hitachi Vantara, World Wide Web Consortium.  All rights reserved.
 */

package org.w3c.flute.parser;

import org.w3c.css.sac.Locator;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class LocatorImpl implements Locator {

  // W3C DEBUG mode
  private static boolean W3CDebug;

  static {
    try {
      W3CDebug = ( Boolean.getBoolean( "debug" )
        || Boolean.getBoolean( "org.w3c.flute.parser.LocatorImpl.debug" )
        || Boolean.getBoolean( "org.w3c.flute.parser.debug" )
        || Boolean.getBoolean( "org.w3c.flute.debug" )
        || Boolean.getBoolean( "org.w3c.debug" )
        || Boolean.getBoolean( "org.debug" ) );
    } catch ( Exception e ) {
      // nothing
    }
  }

  String uri;
  int line;
  int column;

  public String getURI() {
    return uri;
  }

  public int getLineNumber() {
    return line;
  }

  public int getColumnNumber() {
    return column;
  }

  /**
   * Creates a new LocatorImpl
   */
  public LocatorImpl( Parser p ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::newLocator(" + p + ");" );
    }
    uri = p.source.getURI();
    line = p.token.beginLine;
    column = p.token.beginColumn;
  }

  /**
   * Reinitializes a LocatorImpl
   */
  public LocatorImpl( Parser p, Token tok ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::newLocator(" + p
        + ", " + tok + ");" );
    }
    uri = p.source.getURI();
    line = tok.beginLine;
    column = tok.beginColumn;
  }

  /**
   * Reinitializes a LocatorImpl
   */
  public LocatorImpl( Parser p, int line, int column ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::newLocator(" + p
        + ", " + line
        + ", " + column + ");" );
    }
    uri = p.source.getURI();
    this.line = line;
    this.column = column;
  }

  /**
   * Reinitializes a LocatorImpl
   */
  public LocatorImpl reInit( Parser p ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::reInit(" + p + ");" );
    }
    uri = p.source.getURI();
    line = p.token.beginLine;
    column = p.token.beginColumn;
    return this;
  }

  /**
   * Reinitializes a LocatorImpl
   */
  public LocatorImpl reInit( Parser p, Token tok ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::reInit(" + p
        + ", " + tok + ");" );
    }
    uri = p.source.getURI();
    line = tok.beginLine;
    column = tok.beginColumn;
    return this;
  }

  /**
   * Reinitializes a LocatorImpl
   */
  public LocatorImpl reInit( Parser p, int line, int column ) {
    if ( W3CDebug ) {
      System.err.println( "LocatorImpl::reInit(" + p
        + ", " + line
        + ", " + column + ");" );
    }
    uri = p.source.getURI();
    this.line = line;
    this.column = column;
    return this;
  }
}
