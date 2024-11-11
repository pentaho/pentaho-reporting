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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BandedMDXDataSourceXmlFactoryModule implements XmlFactoryModule {
  public BandedMDXDataSourceXmlFactoryModule() {
  }

  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( MondrianDataFactoryModule.NAMESPACE.equals( rootNamespace ) == false ) {
        return NOT_RECOGNIZED;
      } else if ( "banded-mdx-datasource".equals( documentInfo.getRootElement() ) ) {
        return RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( "banded-mdx-datasource".equals( documentInfo.getRootElement() ) ) {
      return RECOGNIZED_BY_TAGNAME;
    }

    return NOT_RECOGNIZED;
  }

  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return MondrianDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    final DataFactoryReadHandlerFactory factory = DataFactoryReadHandlerFactory.getInstance();
    final XmlReadHandler result = factory.getHandler( MondrianDataFactoryModule.NAMESPACE, "banded-mdx-datasource" );
    if ( result == null ) {
      throw new IllegalStateException( "Failed to return a valid readhandler" );
    }
    return result;
  }

}
