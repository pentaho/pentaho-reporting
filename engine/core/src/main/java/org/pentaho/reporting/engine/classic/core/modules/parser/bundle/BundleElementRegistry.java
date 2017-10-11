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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.GenericElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.GenericElementWriteHandler;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.util.HashMap;

public class BundleElementRegistry {
  private static final BundleElementRegistry instance = new BundleElementRegistry();
  private HashMap<String, Class<? extends BundleElementWriteHandler>> writeHandlers;
  private AttributeMap<Class<? extends ElementReadHandler>> readHandlers;
  private static final Log logger = LogFactory.getLog( BundleElementRegistry.class );

  public static BundleElementRegistry getInstance() {
    return instance;
  }

  private BundleElementRegistry() {
    writeHandlers = new HashMap<String, Class<? extends BundleElementWriteHandler>>();
    readHandlers = new AttributeMap<Class<? extends ElementReadHandler>>();
  }

  public BundleElementWriteHandler getWriteHandler( final ReportElement element ) throws BundleWriterException {
    return getWriteHandler( element.getElementType().getMetaData().getName() );
  }

  public BundleElementWriteHandler getWriteHandler( final String element ) throws BundleWriterException {
    try {
      final Class<? extends BundleElementWriteHandler> c = writeHandlers.get( element );
      if ( c != null ) {
        return c.newInstance();
      }
      throw new BundleWriterException( "Failed to locate write-handler for [" + element + "]" );
    } catch ( BundleWriterException e ) {
      throw e;
    } catch ( Exception e ) {
      throw new BundleWriterException( "Failed to instantiate write-handler for [" + element + "]" );
    }
  }

  public ElementReadHandler getReadHandler( final String namespace, final String tagName, final Locator locator )
    throws ParseException {
    try {
      final Class<? extends ElementReadHandler> attribute = readHandlers.getAttribute( namespace, tagName );
      if ( attribute != null ) {
        return attribute.newInstance();
      }

      // this is valid ..
      // logger.debug("No handler for [" + namespace + "|" + tagName + "] at " + locator);
      return null;
    } catch ( Exception e ) {
      throw new ParseException( "Failed to instantiate element-read-handler for [" + namespace + "|" + tagName + "]",
          locator );
    }
  }

  public void registerReader( final ElementType elementType, final Class<? extends ElementReadHandler> readHandler ) {
    ElementMetaData metaData = elementType.getMetaData();
    register( metaData.getNamespace(), metaData.getName(), readHandler );
  }

  public void register( final String namespace, final String tagName,
      final Class<? extends ElementReadHandler> readHandler ) {
    if ( readHandler == null ) {
      throw new IllegalStateException();
    }
    if ( namespace == null ) {
      throw new IllegalStateException();
    }
    if ( tagName == null ) {
      throw new IllegalStateException();
    }

    readHandlers.setAttribute( namespace, tagName, readHandler );
  }

  public void register( final ElementType elementType, final Class<? extends BundleElementWriteHandler> writeHandler ) {
    ElementMetaData metaData = elementType.getMetaData();
    register( metaData.getName(), writeHandler );
  }

  public void register( final String elementType, final Class<? extends BundleElementWriteHandler> writeHandler ) {
    if ( writeHandler == null ) {
      return;
    }
    writeHandlers.put( elementType, writeHandler );
  }

  public void registerGenericReader( final ElementType elementType ) {
    ElementMetaData metaData = elementType.getMetaData();
    register( metaData.getNamespace(), metaData.getName(), GenericElementReadHandler.class );
  }

  public void registerGenericWriter( final ElementType elementType ) {
    registerGenericWriter( elementType.getMetaData().getName() );
  }

  public void registerGenericWriter( final String elementType ) {
    writeHandlers.put( elementType, GenericElementWriteHandler.class );
  }

  public void registerGenericElement( final ElementType elementType ) {
    registerGenericWriter( elementType );
    registerGenericReader( elementType );
  }

}
