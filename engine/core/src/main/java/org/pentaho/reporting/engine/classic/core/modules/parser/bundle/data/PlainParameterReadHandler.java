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

import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.xml.sax.SAXException;

public class PlainParameterReadHandler extends AbstractParameterReadHandler {
  private PlainParameter result;

  public PlainParameterReadHandler() {
  }

  protected void doneParsing() throws SAXException {
    result = new PlainParameter( getName(), getType() );
    result.setMandatory( isMandatory() );
    result.setDefaultValue( getDefaultValue() );

    applyAttributes( result );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
