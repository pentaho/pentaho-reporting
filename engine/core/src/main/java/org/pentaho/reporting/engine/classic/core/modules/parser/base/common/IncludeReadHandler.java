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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;

public class IncludeReadHandler extends AbstractPropertyXmlReadHandler {
  public IncludeReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String file = attrs.getValue( getUri(), "src" );
    if ( file == null ) {
      throw new ParseException( "Required attribute 'src' is missing.", getRootHandler().getDocumentLocator() );
    }

    try {
      final RootXmlReadHandler rootHandler = getRootHandler();
      final ResourceManager resourceManager = rootHandler.getResourceManager();
      final ResourceKey source = rootHandler.getSource();

      final HashMap map = new HashMap();
      final String[] names = rootHandler.getHelperObjectNames();
      for ( int i = 0; i < names.length; i++ ) {
        final String name = names[i];
        final FactoryParameterKey key = new FactoryParameterKey( name );
        map.put( key, rootHandler.getHelperObject( name ) );
      }
      map.put( new FactoryParameterKey( ReportParserUtil.INCLUDE_PARSING_KEY ), ReportParserUtil.INCLUDE_PARSING_VALUE );

      final ResourceKey target = resourceManager.deriveKey( source, file, map );
      final DependencyCollector dc = rootHandler.getDependencyCollector();

      final Object maybeReport = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
      if ( maybeReport == null ) {
        throw new ParseException( "Illegal State: No valid report", getRootHandler().getDocumentLocator() );
      }

      final Class targetType;
      if ( maybeReport instanceof SubReport ) {
        targetType = SubReport.class;
      } else if ( maybeReport instanceof MasterReport ) {
        targetType = MasterReport.class;
      } else {
        throw new ParseException( "Illegal State: No valid report", getRootHandler().getDocumentLocator() );
      }

      final Resource resource = resourceManager.create( target, rootHandler.getContext(), targetType );
      dc.add( resource );

    } catch ( ResourceKeyCreationException e ) {
      throw new ParseException( "Failure while building the resource-key.", e, getLocator() );
    } catch ( ResourceLoadingException e ) {
      throw new ParseException( "Failure while loading data.", e, getLocator() );
    } catch ( ResourceCreationException e ) {
      throw new ParseException( "Failure while loading data.", e, getLocator() );
    }
  }

  /**
   * Returns the object for this element (if any).
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
