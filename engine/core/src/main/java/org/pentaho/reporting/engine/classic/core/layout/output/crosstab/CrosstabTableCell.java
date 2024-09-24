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

package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class CrosstabTableCell extends Element {
  public CrosstabTableCell( final int colSpan, final int rowSpan ) {
    getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, true );
    setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, colSpan );
    setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, rowSpan );
  }

}
