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

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.AutoRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.SeparateColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A table render box contains table header, table footer and the table body. The table body itself may also contain
 * table header cells - which get repeated after pagebreaks.
 * <p/>
 * Tables contain more than just rows, in fact, they are separated into three sections.
 *
 * @author Thomas Morgner
 */
public class TableRenderBox extends BlockRenderBox {
  private TableColumnModel columnModel;

  private TableLayoutInfo tableInfo;
  /**
   * This is not set to true unless the *closed* table has been validated.
   */
  private boolean structureValidated;
  private boolean predefinedColumnsValidated;

  public TableRenderBox() {
    this( SimpleStyleSheet.EMPTY_STYLE, new InstanceID(), BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE,
        ReportAttributeMap.EMPTY_MAP, null );
  }

  public TableRenderBox( final StyleSheet styleSheet, final InstanceID instanceID, final BoxDefinition boxDefinition,
      final ElementType elementType, final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( styleSheet, instanceID, boxDefinition, elementType, attributes, stateKey );

    this.columnModel = new SeparateColumnModel();
    this.tableInfo = new TableLayoutInfo();
    this.tableInfo.setDisplayEmptyCells( true );
    this.tableInfo.setCollapsingBorderModel( false );
    final Object styleProperty = styleSheet.getStyleProperty( BandStyleKeys.TABLE_LAYOUT );
    this.tableInfo.setAutoLayout( TableLayout.auto.equals( styleProperty ) );
    this.tableInfo.setRowSpacing( RenderLength.EMPTY );
    increaseTableReferenceCount( 1, this );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_TABLE;
  }

  public boolean isPredefinedColumnsValidated() {
    return predefinedColumnsValidated;
  }

  public void setPredefinedColumnsValidated( final boolean predefinedColumnsValidated ) {
    this.predefinedColumnsValidated = predefinedColumnsValidated;
  }

  public boolean isStructureValidated() {
    return structureValidated;
  }

  public void setStructureValidated( final boolean structureValidated ) {
    this.structureValidated = structureValidated;
  }

  public TableColumnModel getColumnModel() {
    return columnModel;
  }

  public RenderLength getRowSpacing() {
    return tableInfo.getRowSpacing();
  }

  public boolean isDisplayEmptyCells() {
    return tableInfo.isDisplayEmptyCells();
  }

  public boolean isCollapsingBorderModel() {
    return tableInfo.isCollapsingBorderModel();
  }

  public boolean isAutoLayout() {
    return tableInfo.isAutoLayout();
  }

  public boolean useMinimumChunkWidth() {
    return true;
  }

  public Object clone() {
    try {
      final TableRenderBox box = (TableRenderBox) super.clone();
      if ( box.isStructureValidated() == false ) {
        box.columnModel = (TableColumnModel) columnModel.clone();
      }
      return box;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Clone failed for some reason." );
    }
  }

  public void addChild( final RenderNode child ) {
    if ( isValid( child ) == false ) {
      TableSectionRenderBox tsrb = new TableSectionRenderBox();
      tsrb.addChild( child );
      addChild( tsrb );
      tsrb.close();
      return;
    }

    super.addChild( child );
  }

  private boolean isValid( final RenderNode child ) {
    if ( ( child.getNodeType() & LayoutNodeTypes.MASK_BOX ) != LayoutNodeTypes.MASK_BOX ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP ) {
      return true;
    }

    if ( child.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL ) {
      return true;
    }
    return false;
  }

  public RenderBox create( final StyleSheet styleSheet ) {
    return new AutoRenderBox( styleSheet );
  }

}
