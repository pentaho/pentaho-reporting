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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class ReportProcessTaskMetaDataXmlFactoryModule extends AbstractXmlFactoryModule {
  public ReportProcessTaskMetaDataXmlFactoryModule() {
    super( ClassicEngineBoot.METADATA_NAMESPACE, "meta-data" );
  }

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document information.
   *
   * @param documentInfo
   *          the document information that has been extracted from the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new ReportProcessTaskMetaDataReadHandler();
  }
}
