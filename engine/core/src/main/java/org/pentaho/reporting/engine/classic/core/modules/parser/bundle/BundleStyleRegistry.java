/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.StyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BundleStyleSetWriteHandler;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.util.ArrayList;

public class BundleStyleRegistry {
  private static final BundleStyleRegistry instance = new BundleStyleRegistry();
  private ArrayList<Class<? extends BundleStyleSetWriteHandler>> styleSetWriteHandlers;
  private AttributeMap<Class<? extends StyleReadHandler>> styleReadHandlers;

  public static BundleStyleRegistry getInstance() {
    return instance;
  }

  private BundleStyleRegistry() {
    styleSetWriteHandlers = new ArrayList<Class<? extends BundleStyleSetWriteHandler>>();
    styleReadHandlers = new AttributeMap<Class<? extends StyleReadHandler>>();
  }

  public BundleStyleSetWriteHandler[] getWriteHandlers() {
    final ArrayList<BundleStyleSetWriteHandler> retval = new ArrayList<BundleStyleSetWriteHandler>();
    for ( int i = 0; i < styleSetWriteHandlers.size(); i++ ) {
      try {
        final Class<? extends BundleStyleSetWriteHandler> c = styleSetWriteHandlers.get( i );
        retval.add( c.newInstance() );
      } catch ( Exception e ) {
        // ignore
      }
    }
    return retval.toArray( new BundleStyleSetWriteHandler[retval.size()] );
  }

  public StyleReadHandler getReadHandler( final String namespace, final String tagName, final Locator locator )
    throws ParseException {
    try {
      final Class<? extends StyleReadHandler> attribute = styleReadHandlers.getAttribute( namespace, tagName );
      if ( attribute != null ) {
        return attribute.newInstance();
      }
      throw new ParseException( "Failed to locate style-read-handler for [" + namespace + "|" + tagName + "]", locator );
    } catch ( ParseException e ) {
      throw e;
    } catch ( Exception e ) {
      throw new ParseException( "Failed to instantiate style-read-handler for [" + namespace + "|" + tagName + "]",
          locator );
    }
  }

  public void register( final String namespace, final String tagName,
      final Class<? extends StyleReadHandler> readHandler ) {
    if ( readHandler == null ) {
      throw new IllegalStateException();
    }
    if ( namespace == null ) {
      throw new IllegalStateException();
    }
    if ( tagName == null ) {
      throw new IllegalStateException();
    }

    styleReadHandlers.setAttribute( namespace, tagName, readHandler );
  }

  public void register( final Class<? extends BundleStyleSetWriteHandler> writeHandler ) {
    if ( writeHandler == null ) {
      return;
    }
    styleSetWriteHandlers.add( writeHandler );
  }
}
