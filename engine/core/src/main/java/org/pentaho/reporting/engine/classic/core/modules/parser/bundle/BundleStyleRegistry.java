/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
