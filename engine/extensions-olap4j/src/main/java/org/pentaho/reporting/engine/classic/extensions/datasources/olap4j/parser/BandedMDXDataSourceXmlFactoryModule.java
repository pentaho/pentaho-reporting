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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class BandedMDXDataSourceXmlFactoryModule implements XmlFactoryModule {
  public BandedMDXDataSourceXmlFactoryModule() {
  }

  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( Olap4JDataFactoryModule.NAMESPACE.equals( rootNamespace ) == false ) {
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
    return Olap4JDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new BandedMDXDataSourceReadHandler();
  }

}
