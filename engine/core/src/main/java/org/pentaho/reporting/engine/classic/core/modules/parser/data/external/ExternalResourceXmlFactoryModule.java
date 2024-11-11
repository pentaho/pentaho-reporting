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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.external;

import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.05.2009 Time: 11:56:51
 *
 * @author Thomas Morgner.
 */
public class ExternalResourceXmlFactoryModule implements XmlFactoryModule {
  public ExternalResourceXmlFactoryModule() {
  }

  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( ExternalDataFactoryModule.NAMESPACE.equals( rootNamespace ) == false ) {
        return XmlFactoryModule.NOT_RECOGNIZED;
      } else if ( "external-datasource".equals( documentInfo.getRootElement() ) ) {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( "external-datasource".equals( documentInfo.getRootElement() ) ) {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return ExternalDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new ExternalDataSourceReadHandler();
  }
}
