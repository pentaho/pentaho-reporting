/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
