/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
