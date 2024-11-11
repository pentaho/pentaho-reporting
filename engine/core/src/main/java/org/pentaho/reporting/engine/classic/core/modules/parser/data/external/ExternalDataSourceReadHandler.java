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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ExternalDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.SAXException;

public class ExternalDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ExternalDataFactory externalDataFactory;

  public ExternalDataSourceReadHandler() {
    externalDataFactory = new ExternalDataFactory();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return getDataFactory();
  }

  public DataFactory getDataFactory() {
    return externalDataFactory;
  }
}
