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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple;

import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.JFreeReportReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Creation-Date: Dec 15, 2006, 10:01:09 PM
 *
 * @author Thomas Morgner
 */
public class SimpleReportXmlFactoryModule implements XmlFactoryModule {
  public SimpleReportXmlFactoryModule() {
  }

  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( SimpleParserModule.NAMESPACE.equals( rootNamespace ) == false ) {
        return XmlFactoryModule.NOT_RECOGNIZED;
      } else if ( "report".equals( documentInfo.getRootElement() ) ) {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( "report".equals( documentInfo.getRootElement() ) ) {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;

  }

  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return SimpleParserModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new JFreeReportReadHandler();
  }
}
