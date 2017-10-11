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
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class TableColumnGroupNode extends RenderBox {
  private int colspan;
  private int columnIndex;

  public TableColumnGroupNode( final StyleSheet styleSheet, final ReportAttributeMap attributes ) {
    super( RenderNode.HORIZONTAL_AXIS, RenderNode.VERTICAL_AXIS, styleSheet, new InstanceID(), BoxDefinition.EMPTY,
        AutoLayoutBoxType.INSTANCE, attributes, null );
    final Integer colspan =
        (Integer) attributes.getAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN );
    if ( colspan == null ) {
      this.colspan = 1;
    } else {
      this.colspan = colspan;
    }
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP;
  }

  public int getColSpan() {
    return colspan;
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public void setColumnIndex( final int columnIndex ) {
    this.columnIndex = columnIndex;
  }

  public boolean isDiscardable() {
    return false;
  }

  /**
   * If that method returns true, the element will not be used for rendering. For the purpose of computing sizes or
   * performing the layouting (in the validate() step), this element will treated as if it is not there.
   * <p/>
   * If the element reports itself as non-empty, however, it will affect the margin computation.
   *
   * @return
   */
  public boolean isIgnorableForRendering() {
    return true;
  }

}
