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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;

public class DrawableFieldReportElementReadHandler extends AbstractTextElementReadHandler {
  public DrawableFieldReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new ContentFieldType() );
    setElement( element );
  }
}
