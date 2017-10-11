/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.ParagraphPoolboxStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 03.04.2007, 13:38:00
 *
 * @author Thomas Morgner
 */
public final class ParagraphRenderBox extends BlockRenderBox {
  private static class LineBoxRenderBox extends BlockRenderBox {
    protected LineBoxRenderBox( final StyleSheet styleSheet, final ReportStateKey stateKey ) {
      super( styleSheet, new InstanceID(), BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE,
          ReportAttributeMap.EMPTY_MAP, stateKey );
    }

    public boolean isAcceptInlineBoxes() {
      return true;
    }
  }

  private ParagraphPoolBox pool;
  private LineBoxRenderBox lineboxContainer;
  private ElementAlignment textAlignment;
  private ElementAlignment lastLineAlignment;
  private long lineBoxAge;
  private long minorLayoutAge;
  private long minorLayoutValidationX1;
  private long minorLayoutValidationX2;
  private int poolSize;
  private long textIndent;
  private long firstLineIndent;
  private long cachedMaxChildX2;

  public ParagraphRenderBox( final StyleSheet styleSheet, final InstanceID instanceID,
      final BoxDefinition boxDefinition, final ElementType elementType, final ReportAttributeMap attributeMap,
      final ReportStateKey stateKey ) {
    super( styleSheet, instanceID, boxDefinition, elementType, attributeMap, stateKey );

    pool = new ParagraphPoolBox( new ParagraphPoolboxStyleSheet( styleSheet ), instanceID, stateKey );
    pool.setParent( this );

    // level 3 means: Add all lineboxes to the paragraph
    // This gets auto-generated ..
    this.textAlignment =
        (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    this.lastLineAlignment = textAlignment;
    if ( this.textAlignment == ElementAlignment.JUSTIFY ) {
      this.lastLineAlignment = ElementAlignment.LEFT;
    }

    final double rawTextIndent = styleSheet.getDoubleStyleProperty( TextStyleKeys.TEXT_INDENT, 0 );
    final double rawFirstLineIndent =
        styleSheet.getDoubleStyleProperty( TextStyleKeys.FIRST_LINE_INDENT, rawTextIndent );

    this.textIndent = RenderLength.resolveLength( 0, Math.max( 0, rawTextIndent ) );
    this.firstLineIndent = RenderLength.resolveLength( 0, Math.max( 0, rawFirstLineIndent ) );
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @return
   */
  public RenderNode derive( final boolean deepDerive ) {
    final ParagraphRenderBox box = (ParagraphRenderBox) super.derive( deepDerive );
    box.pool = (ParagraphPoolBox) pool.derive( deepDerive );
    box.pool.setParent( box );

    if ( lineboxContainer != null ) {
      box.lineboxContainer = (LineBoxRenderBox) lineboxContainer.derive( deepDerive );
      box.lineboxContainer.setParent( box );
    }
    if ( !deepDerive ) {
      box.lineBoxAge = 0;
    }
    return box;
  }

  public final void addChild( final RenderNode child ) {
    pool.addChild( child );
  }

  /**
   * Removes all children.
   */
  public final void clear() {
    pool.clear();
    if ( lineboxContainer != null ) {
      lineboxContainer.clear();
    }
    super.clear();
    lineBoxAge = 0;
  }

  public final void clearLayout() {
    super.clear();
    minorLayoutAge = 0;
  }

  public boolean isAppendable() {
    return pool.isAppendable();
  }

  public boolean isEmpty() {
    return pool.isEmpty();
  }

  public boolean isDiscardable() {
    return pool.isDiscardable();
  }

  public ElementAlignment getLastLineAlignment() {
    return lastLineAlignment;
  }

  public ElementAlignment getTextAlignment() {
    return textAlignment;
  }

  public RenderBox getLineboxContainer() {
    return lineboxContainer;
  }

  public boolean isComplexParagraph() {
    return lineboxContainer != null;
  }

  public RenderBox createLineboxContainer() {
    if ( lineboxContainer == null ) {
      this.lineboxContainer = new LineBoxRenderBox( pool.getStyleSheet(), getStateKey() );
      this.lineboxContainer.setParent( this );
    }
    return lineboxContainer;
  }

  public RenderBox getEffectiveLineboxContainer() {
    if ( lineboxContainer == null ) {
      return pool;
    }
    return lineboxContainer;
  }

  public ParagraphPoolBox getPool() {
    return pool;
  }

  public long getLineBoxAge() {
    return lineBoxAge;
  }

  public void setLineBoxAge( final long lineBoxAge ) {
    this.lineBoxAge = lineBoxAge;
  }

  public long getMinorLayoutAge() {
    return minorLayoutAge;
  }

  public void updateMinorLayoutAge() {
    this.minorLayoutAge = getEffectiveLineboxContainer().getChangeTracker();
    this.minorLayoutValidationX1 = getContentAreaX1();
    this.minorLayoutValidationX2 = getContentAreaX2();
  }

  /**
   * The public-id for the paragraph is the pool-box.
   *
   * @return
   */
  public InstanceID getInstanceId() {
    return pool.getInstanceId();
  }

  public int getPoolSize() {
    return poolSize;
  }

  public void setPoolSize( final int poolSize ) {
    this.poolSize = poolSize;
  }

  public void close() {
    pool.close();
    super.close();
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_PARAGRAPH;
  }

  protected void increaseContentReferenceCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.increaseContentReferenceCount( count, source );
  }

  protected void increaseTableReferenceCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.increaseTableReferenceCount( count, source );
  }

  protected void decreaseContentReferenceCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.decreaseContentReferenceCount( count, source );
  }

  protected void decreaseTableReferenceCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.decreaseTableReferenceCount( count, source );
  }

  protected void increaseDescendantCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.increaseDescendantCount( count, source );
  }

  protected void decreaseDescendantCount( final int count, final RenderNode source ) {
    if ( source != pool ) {
      return;
    }
    super.decreaseDescendantCount( count, source );
  }

  public long getTextIndent() {
    return textIndent;
  }

  public long getFirstLineIndent() {
    return firstLineIndent;
  }

  public boolean isAcceptInlineBoxes() {
    return true;
  }

  public boolean isLineBoxUnchanged() {
    final long lineBoxChangeTracker = getEffectiveLineboxContainer().getChangeTracker();
    if ( lineBoxChangeTracker == getMinorLayoutAge() && minorLayoutValidationX1 == getContentAreaX1()
        && minorLayoutValidationX2 == getContentAreaX2() ) {
      // testing for both content-changes and positional changes due to subreports or other delayed content
      // inserting new data at an earlier point in the model.
      return true;
    }
    return false;
  }

  public long getCachedMaxChildX2() {
    return cachedMaxChildX2;
  }

  public void setCachedMaxChildX2( final long cachedMaxChildX2 ) {
    this.cachedMaxChildX2 = cachedMaxChildX2;
  }

  public void setCachedWidth( final long cachedWidth ) {
    super.setCachedWidth( cachedWidth );
  }
}
