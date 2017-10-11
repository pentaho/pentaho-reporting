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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.UseMinChunkWidthStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class AutoRenderBox extends RenderBox {
  private static StyleSheet DEFAULT_STYLE = new SimpleStyleSheet( new UseMinChunkWidthStyleSheet( true ) );
  private int rowIndex;

  public AutoRenderBox() {
    this( new InstanceID(), null, ReportAttributeMap.EMPTY_MAP );
  }

  public AutoRenderBox( final StyleSheet styleSheet ) {
    this( new InstanceID(), null, styleSheet, ReportAttributeMap.EMPTY_MAP );
  }

  public AutoRenderBox( final InstanceID instanceId, final ReportStateKey stateKey, final ReportAttributeMap attributes ) {
    this( instanceId, stateKey, DEFAULT_STYLE, attributes );
  }

  public AutoRenderBox( final InstanceID instanceId, final ReportStateKey stateKey, final StyleSheet styleSheet,
      final ReportAttributeMap attributes ) {
    this( instanceId, stateKey, styleSheet, attributes, AutoLayoutBoxType.INSTANCE );
  }

  public AutoRenderBox( final InstanceID instanceId, final ReportStateKey stateKey, final StyleSheet styleSheet,
      final ReportAttributeMap attributes, final ElementType elementType ) {
    this( instanceId, stateKey, styleSheet, BoxDefinition.EMPTY, attributes, elementType );
  }

  public AutoRenderBox( final InstanceID instanceId, final ReportStateKey stateKey, final StyleSheet styleSheet,
      final BoxDefinition boxDefinition, final ReportAttributeMap attributes, final ElementType elementType ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceId, boxDefinition, elementType,
        attributes, stateKey );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT;
  }

  public int getLayoutNodeType() {
    final RenderBox parent = getLayoutParent();
    if ( parent == null ) {
      return LayoutNodeTypes.TYPE_BOX_BLOCK;
    }
    return parent.getLayoutNodeType();
  }

  public boolean isAcceptInlineBoxes() {
    final RenderBox parent = getParent();
    if ( parent != null ) {
      return parent.isAcceptInlineBoxes();
    }
    return super.isAcceptInlineBoxes();
  }

  public boolean isEmptyNodesHaveSignificance() {
    final RenderBox parent = getParent();
    if ( parent != null ) {
      return parent.isEmptyNodesHaveSignificance();
    }
    return super.isEmptyNodesHaveSignificance();
  }

  public long extendHeight( final RenderNode child, final long heightOffset ) {
    final int layoutNodeType = getLayoutNodeType();
    if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE
        || ( layoutNodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW
        || ( layoutNodeType & LayoutNodeTypes.MASK_BOX_CANVAS ) == LayoutNodeTypes.MASK_BOX_CANVAS
        || ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) ) {
      return extendHeightInRowMode( child, heightOffset );
    } else {
      return extendHeightInBlockMode( child, heightOffset );
    }
  }

  protected boolean isBlockForPagebreakPurpose() {
    final RenderBox parent = getParent();
    if ( parent == null ) {
      return true;
    }
    return parent.isBlockForPagebreakPurpose();
  }

  public void setCachedY( final long cachedY ) {
    super.setCachedY( cachedY );
  }

  public void shiftCached( final long amount ) {
    super.shiftCached( amount );
  }

  public void setCachedHeight( final long cachedHeight ) {
    super.setCachedHeight( cachedHeight );
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex( final int rowIndex ) {
    this.rowIndex = rowIndex;
  }
}
