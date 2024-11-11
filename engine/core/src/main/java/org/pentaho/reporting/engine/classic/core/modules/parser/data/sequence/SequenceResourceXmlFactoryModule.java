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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence;

import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class SequenceResourceXmlFactoryModule implements XmlFactoryModule {
  public SequenceResourceXmlFactoryModule() {
  }

  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( SequenceDataFactoryModule.NAMESPACE.equals( rootNamespace ) == false ) {
        return XmlFactoryModule.NOT_RECOGNIZED;
      } else if ( "sequence-datasource".equals( documentInfo.getRootElement() ) ) {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( "sequence-datasource".equals( documentInfo.getRootElement() ) ) {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return SequenceDataFactoryModule.NAMESPACE;
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new SequenceDataSourceReadHandler();
  }
}
