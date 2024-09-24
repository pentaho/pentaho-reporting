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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 27.06.2006, 12:09:08
 *
 * @author Thomas Morgner
 */
public class DataFactoryRefReadHandler extends AbstractPropertyXmlReadHandler implements DataFactoryReadHandler {
  private DataFactory dataFactory;

  public DataFactoryRefReadHandler() {
  }

  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String href = attrs.getValue( getUri(), "href" );
    // we have a HREF given, ...
    if ( href != null ) {
      // load ..

      final ResourceKey key = getRootHandler().getSource();
      final ResourceManager manager = getRootHandler().getResourceManager();
      try {
        final ResourceKey derivedKey = manager.deriveKey( key, href );
        final Resource resource = manager.create( derivedKey, null, DataFactory.class );
        getRootHandler().getDependencyCollector().add( resource );
        dataFactory = (DataFactory) resource.getResource();
      } catch ( ResourceKeyCreationException e ) {
        throw new ParseException( "Unable to derive key for " + key + " and " + href, e, getLocator() );
      } catch ( ResourceCreationException e ) {
        throw new ParseException( "Unable to parse resource for " + key + " and " + href, e, getLocator() );
      } catch ( ResourceLoadingException e ) {
        throw new ParseException( "Unable to load resource data for " + key + " and " + href, e, getLocator() );
      } catch ( ResourceException e ) {
        throw new ParseException( "Unable to parse resource for " + key + " and " + href, e, getLocator() );
      }
      return;
    }

    final String dfType = CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), "type" ) );
    if ( dfType != null ) {
      final Object o = ObjectUtilities.loadAndInstantiate( dfType, getClass(), DataFactory.class );
      if ( o == null ) {
        throw new ParseException( "'type' did not point to a usable DataFactory implementation.", getLocator() );
      }
      dataFactory = (DataFactory) o;
    }
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return dataFactory;
  }
}
