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

package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A table column defines a limited set of style properties, which may be applied to the cells.
 * <p/>
 * Border, if the border-model is the collapsing border model. Background, if both cell and row have a transparent
 * background Width, is a minimum width. If the cell exceeds that size, the table cannot be rendered in incremental mode
 * anymore. We may have to use the validation run to check for that rule. visibility, if set to collapse, the column
 * will not be rendered. Not yet.
 *
 * @author Thomas Morgner
 */
public class TableColumnNode extends RenderBox {
  private int colspan;
  private int columnIndex;

  public TableColumnNode( final StyleSheet styleSheet, final InstanceID instanceId, final BoxDefinition boxDefinition,
      final ElementType elementType, final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( HORIZONTAL_AXIS, VERTICAL_AXIS, styleSheet, instanceId, boxDefinition, elementType, attributes, stateKey );
    final Integer colspan =
        (Integer) attributes.getAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN );
    if ( colspan == null ) {
      this.colspan = 1;
    } else {
      this.colspan = colspan;
    }
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public void setColumnIndex( final int columnIndex ) {
    this.columnIndex = columnIndex;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_TABLE_COL;
  }

  public int getColspan() {
    return colspan;
  }
}
