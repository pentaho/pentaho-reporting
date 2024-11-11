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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class DataDefinitionXmlFactoryModule implements XmlFactoryModule {
  public DataDefinitionXmlFactoryModule() {
  }

  /**
   * Checks the given document data to compute the propability of whether this factory module would be able to handle
   * the given data.
   *
   * @param documentInfo
   *          the document information collection.
   * @return an integer value indicating how good the document matches the factories requirements.
   */
  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      if ( BundleNamespaces.DATADEFINITION.equals( rootNamespace ) == false ) {
        return XmlFactoryModule.NOT_RECOGNIZED;
      } else if ( "data-definition".equals( documentInfo.getRootElement() ) ) {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    } else if ( "data-definition".equals( documentInfo.getRootElement() ) ) {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;

  }

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document information.
   *
   * @param documentInfo
   *          the document information that has been extracted from the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new DataDefinitionRootElementHandler();
  }

  /**
   * Returns the default namespace for a document with the characteristics given in the XmlDocumentInfo.
   *
   * @param documentInfo
   *          the document information.
   * @return the default namespace uri for the document.
   */
  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return BundleNamespaces.DATADEFINITION;
  }
}
