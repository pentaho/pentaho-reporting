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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;

public class TextFieldReportElementReadHandler extends AbstractTextElementReadHandler {
  public TextFieldReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new TextFieldType() );
    setElement( element );
  }
}
