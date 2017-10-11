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
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A table section box does not much rendering or layouting at all. It represents one of the three possible sections and
 * behaves like any other block box. But (here it comes!) it refuses to be added to anything else than a TableRenderBox
 * (a small check to save me a lot of insanity ..).
 *
 * @author Thomas Morgner
 */
public class TableCellRenderBox extends BlockRenderBox {
  private int colSpan;
  private int rowSpan;
  private Border effectiveBorder;
  private int columnIndex;
  private boolean bodySection;

  public TableCellRenderBox( final StyleSheet styleSheet, final InstanceID instanceID,
      final BoxDefinition boxDefinition, final ElementType elementType, final ReportAttributeMap attributes,
      final ReportStateKey stateKey ) {
    super( styleSheet, instanceID, boxDefinition, elementType, attributes, stateKey );

    this.rowSpan = 1;
    this.columnIndex = -1;
    final Integer colspan =
        (Integer) attributes.getAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN );
    if ( colspan == null ) {
      this.colSpan = 1;
    } else {
      this.colSpan = Math.max( 1, colspan );
    }

    final Integer rowSpan =
        (Integer) attributes.getAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN );
    if ( rowSpan == null ) {
      this.rowSpan = 1;
    } else {
      this.rowSpan = Math.max( 1, rowSpan );
    }
  }

  protected void reinit( final StyleSheet styleSheet, final ElementType elementType,
      final ReportAttributeMap attributes, final InstanceID instanceId ) {
    super.reinit( styleSheet, elementType, attributes, instanceId );
    this.colSpan = 1;
    this.rowSpan = 1;
    this.columnIndex = -1;
  }

  public TableCellRenderBox() {
    this.colSpan = 1;
    this.rowSpan = 1;
    this.columnIndex = -1;
  }

  public boolean useMinimumChunkWidth() {
    return true;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_TABLE_CELL;
  }

  public int getColSpan() {
    return colSpan;
  }

  public int getRowSpan() {
    return rowSpan;
  }

  public void update( final int rowSpan, final int colSpan ) {
    this.rowSpan = Math.max( 1, rowSpan );
    this.colSpan = Math.max( 1, colSpan );
    // todo PRD-4606
    this.resetCacheState( false );
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
    return false;
  }

  public Border getEffectiveBorder() {
    return effectiveBorder;
  }

  public void setEffectiveBorder( final Border effectiveBorder ) {
    this.effectiveBorder = effectiveBorder;
  }

  public Border getOriginalBorder() {
    return getBoxDefinition().getBorder();
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public void setColumnIndex( final int columnIndex ) {
    this.columnIndex = columnIndex;
  }

  public boolean isBodySection() {
    return bodySection;
  }

  public void setBodySection( final boolean bodySection ) {
    this.bodySection = bodySection;
  }

  public void apply() {
    super.apply();
  }
}
