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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.base.config.Configuration;

public abstract class RenderNode implements Cloneable {
  public enum CacheState {
    CLEAN, DIRTY, DEEP_DIRTY
  }

  private static final boolean paranoidModelChecks;

  static {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks" ) ) ) {
      paranoidModelChecks = true;
    } else {
      paranoidModelChecks = false;
    }
  }

  public static boolean isParanoidModelChecks() {
    return paranoidModelChecks;
  }

  public static final int HORIZONTAL_AXIS = 0;
  public static final int VERTICAL_AXIS = 1;

  public static final CacheState CACHE_CLEAN = CacheState.CLEAN;
  public static final CacheState CACHE_DIRTY = CacheState.DIRTY;
  public static final CacheState CACHE_DEEP_DIRTY = CacheState.DEEP_DIRTY;

  private static final int FLAG_FROZEN = 0x01;
  private static final int FLAG_FINISHED_PAGINATE = 0x02;
  private static final int FLAG_FINISHED_TABLE = 0x04;
  private static final int FLAG_VIRTUAL_NODE = 0x04;
  private static final int FLAG_WIDOW_BOX = 0x08;
  private static final int FLAG_RESERVED = 0xFFF0;

  private int flags;
  private CacheState cacheState;
  private CacheState applyState;

  private RenderBox parentNode;
  private RenderNode nextNode;
  private RenderNode prevNode;

  private NodeLayoutProperties nodeLayoutProperties;

  private long changeTracker;
  private long minimumChunkWidth;
  private long maximumBoxWidth;
  private long validateModelAge;
  private ValidationResult validateModelResult;
  private long linebreakAge;

  private long cachedX;
  private long cachedY;
  private long cachedWidth;
  private long cachedHeight;

  private long x;
  private long y;
  private long width;
  private long height;

  private long cachedAge;

  protected RenderNode( final int majorAxis, final int minorAxis, final StyleSheet styleSheet,
      final InstanceID instanceID, final ElementType elementType, final ReportAttributeMap<Object> attributes ) {
    this( new NodeLayoutProperties( majorAxis, minorAxis, styleSheet, attributes, instanceID, elementType ) );
  }

  protected RenderNode( final NodeLayoutProperties nodeLayoutProperties ) {
    if ( nodeLayoutProperties == null ) {
      throw new NullPointerException();
    }

    this.nodeLayoutProperties = nodeLayoutProperties;
    this.cacheState = RenderNode.CACHE_DIRTY;
  }

  protected void reinit( final StyleSheet styleSheet, final ElementType elementType,
      final ReportAttributeMap<Object> attributes, final InstanceID instanceId ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( instanceId == null ) {
      throw new NullPointerException();
    }
    if ( elementType == null ) {
      throw new NullPointerException();
    }
    this.flags = 0;
    this.changeTracker = -1;
    this.validateModelAge = 0;
    this.cachedX = 0;
    this.cachedY = 0;
    this.cachedWidth = 0;
    this.cachedHeight = 0;
    this.x = 0;
    this.y = 0;
    this.width = 0;
    this.height = 0;
    this.minimumChunkWidth = 0;
    this.maximumBoxWidth = 0;

    this.cacheState = RenderNode.CACHE_DIRTY;
    this.nodeLayoutProperties =
        new NodeLayoutProperties( this.nodeLayoutProperties.getMajorAxis(), this.nodeLayoutProperties.getMinorAxis(),
            styleSheet, attributes, instanceId, elementType );
  }

  public ElementType getElementType() {
    return nodeLayoutProperties.getElementType();
  }

  public ReportAttributeMap<Object> getAttributes() {
    return nodeLayoutProperties.getAttributes();
  }

  /**
   * The content-ref-count counts inline-subreports.
   */
  public int getContentRefCount() {
    return 0;
  }

  public int getTableRefCount() {
    return 0;
  }

  public int getDescendantCount() {
    return 1;
  }

  public boolean isSizeSpecifiesBorderBox() {
    return true;
  }

  public abstract int getNodeType();

  public int getLayoutNodeType() {
    return getNodeType();
  }

  public int getMinorAxis() {
    return this.nodeLayoutProperties.getMinorAxis();
  }

  public int getMajorAxis() {
    return this.nodeLayoutProperties.getMajorAxis();
  }

  public final NodeLayoutProperties getNodeLayoutProperties() {
    return nodeLayoutProperties;
  }

  public final long getX() {
    return x;
  }

  public final void setX( final long x ) {
    this.x = x;
    // this.updateChangeTracker();
  }

  public final long getY() {
    return y;
  }

  public void shift( final long amount ) {
    this.y += amount;
  }

  public void setY( final long y ) {
    this.y = y;
  }

  protected final void updateCacheState( final CacheState state ) {
    switch ( state ) {
      case CLEAN:
        break;
      case DIRTY:
        if ( cacheState == RenderNode.CACHE_CLEAN ) {
          this.cacheState = RenderNode.CACHE_DIRTY;
          final RenderBox parent = getParent();
          if ( parent != null ) {
            parent.updateCacheState( RenderNode.CACHE_DIRTY );
          }
        }
        // if cache-state either dirty or deep-dirty, no need to update.
        break;
      case DEEP_DIRTY:
        if ( cacheState == RenderNode.CACHE_CLEAN ) {
          final RenderBox parent = getParent();
          if ( parent != null ) {
            parent.updateCacheState( RenderNode.CACHE_DEEP_DIRTY );
          }
        }
        this.cacheState = RenderNode.CACHE_DEEP_DIRTY;
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public final long getWidth() {
    return width;
  }

  public final void setWidth( final long width ) {
    if ( width < 0 ) {
      throw new IndexOutOfBoundsException( "Width cannot be negative" );
    }

    this.width = width;
    this.updateCacheState( RenderNode.CACHE_DIRTY );
    // this.updateChangeTracker();
  }

  public final long getHeight() {
    return height;
  }

  public void setHeight( final long height ) {
    if ( height < 0 ) {
      throw new IndexOutOfBoundsException( "Height cannot be negative" );
    }
    this.height = height;
    // this.updateCacheState(RenderNode.CACHE_DIRTY);
  }

  public final StyleSheet getStyleSheet() {
    return nodeLayoutProperties.getStyleSheet();
  }

  public InstanceID getInstanceId() {
    return nodeLayoutProperties.getInstanceId();
  }

  protected void updateChangeTracker() {
    changeTracker += 1;
    if ( cacheState == RenderNode.CACHE_CLEAN ) {
      cacheState = RenderNode.CACHE_DIRTY;
    }
    final RenderBox parent = getParent();
    if ( parent != null ) {
      parent.updateChangeTracker();
    }
  }

  public final long getChangeTracker() {
    return changeTracker;
  }

  public final RenderBox getParent() {
    return parentNode;
  }

  public RenderBox getLayoutParent() {
    if ( parentNode != null ) {
      if ( parentNode.getNodeType() == LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT ) {
        return parentNode.getLayoutParent();
      }
    }
    return parentNode;
  }

  protected final void setParent( final RenderBox parent ) {
    if ( isParanoidModelChecks() ) {
      final RenderNode prev = getPrev();
      if ( parent != null && prev == parent ) {
        throw new IllegalStateException( "Assertation failed: Cannot have a parent that is the same as a silbling." );
      }
      if ( parent == null ) {
        final RenderNode next = getNext();
        if ( next != null ) {
          throw new NullPointerException();
        }
        if ( prev != null ) {
          throw new NullPointerException();
        }
      }
    }

    this.parentNode = parent;
  }

  public final RenderNode getPrev() {
    return prevNode;
  }

  protected final void setPrevUnchecked( final RenderNode prev ) {
    this.prevNode = prev;
  }

  protected final void setPrev( final RenderNode prev ) {
    this.prevNode = prev;
    if ( isParanoidModelChecks() && prev != null ) {
      final RenderBox parent = getParent();
      if ( prev == parent ) {
        throw new IllegalStateException();
      }

      if ( parent != null ) {
        if ( parent.getFirstChild() == this ) {
          throw new NullPointerException( "Cannot have a prev node if the parent has me as first child." );
        }
      }
    }
  }

  public final RenderNode getNext() {
    return nextNode;
  }

  protected final void setNextUnchecked( final RenderNode next ) {
    this.nextNode = next;
  }

  protected final void setNext( final RenderNode next ) {
    this.nextNode = next;
    if ( isParanoidModelChecks() && next != null ) {
      final RenderBox parent = getParent();
      if ( next == parent ) {
        throw new IllegalStateException();
      }

      if ( parent != null ) {
        if ( parent.getLastChild() == this ) {
          throw new NullPointerException( "Cannot have a next-node, if the parent has me as last child." );
        }
      }
    }
  }

  public LogicalPageBox getLogicalPage() {
    RenderNode parent = this;
    while ( parent != null ) {
      if ( parent.getNodeType() == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
        return (LogicalPageBox) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  /**
   * Clones this node. Be aware that cloning can get you into deep trouble, as the relations this node has may no longer
   * be valid.
   *
   * @return
   * @noinspection CloneDoesntDeclareCloneNotSupportedException
   */
  public Object clone() {
    try {
      return super.clone();
    } catch ( final CloneNotSupportedException e ) {
      // ignored ..
      throw new IllegalStateException( "Clone failed for some reason." );
    }
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @param deep
   * @return
   */
  public RenderNode derive( final boolean deep ) {
    final RenderNode node = (RenderNode) clone();
    node.parentNode = null;
    node.nextNode = null;
    node.prevNode = null;
    if ( deep ) {
      node.cachedAge = this.changeTracker;
      node.validateModelAge = -1;
      // todo PRD-4606
      node.cacheState = CACHE_DIRTY;
    }
    return node;
  }

  public RenderNode deriveFrozen( final boolean deep ) {
    final RenderNode node = (RenderNode) clone();
    node.parentNode = null;
    node.nextNode = null;
    node.prevNode = null;
    node.freeze();
    return node;
  }

  public boolean isFrozen() {
    return isFlag( FLAG_FROZEN );
  }

  public RenderNode findNodeById( final InstanceID instanceId ) {
    if ( instanceId == getInstanceId() ) {
      return this;
    }
    return null;
  }

  public boolean isOpen() {
    return false;
  }

  public boolean isEmpty() {
    return false;
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
    return isEmpty();
  }

  public void freeze() {
    setFlag( FLAG_FROZEN, true );
  }

  public long getMaximumBoxWidth() {
    return maximumBoxWidth;
  }

  public void setMaximumBoxWidth( final long maximumBoxWidth ) {
    this.maximumBoxWidth = maximumBoxWidth;
  }

  public long getMinimumChunkWidth() {
    return minimumChunkWidth;
  }

  protected void setMinimumChunkWidth( final long minimumChunkWidth ) {
    if ( minimumChunkWidth < 0 ) {
      throw new IllegalArgumentException();
    }
    this.minimumChunkWidth = minimumChunkWidth;
  }

  public long getEffectiveMarginTop() {
    return 0;
  }

  public long getEffectiveMarginBottom() {
    return 0;
  }

  public VerticalTextAlign getVerticalTextAlignment() {
    return nodeLayoutProperties.getVerticalTextAlign();
  }

  //
  // /**
  // * The sticky-Marker contains the original Y of this node.
  // * @return
  // */
  // public long getStickyMarker()
  // {
  // return stickyMarker;
  // }
  //
  // public void setStickyMarker(final long stickyMarker)
  // {
  // this.stickyMarker = stickyMarker;
  // }

  public String getName() {
    return null;
  }

  public boolean isBreakAfter() {
    return false;
  }

  public long getValidateModelAge() {
    return validateModelAge;
  }

  protected void resetValidateModelResult() {
    this.validateModelAge = -1;
  }

  public void setValidateModelResult( final ValidationResult result ) {
    this.validateModelAge = changeTracker;
    this.validateModelResult = result;
  }

  public ValidationResult isValidateModelResult() {
    return validateModelResult;
  }

  public long getLinebreakAge() {
    return linebreakAge;
  }

  public void setLinebreakAge( final long linebreakAge ) {
    this.linebreakAge = linebreakAge;
  }

  /**
   * Returns the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @return the cached x position
   */
  public final long getCachedX() {
    return cachedX;
  }

  /**
   * Defines the cached x position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @param cachedX
   *          the cached x position
   */
  public void setCachedX( final long cachedX ) {
    this.cachedX = cachedX;
  }

  /**
   * Returns the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @return the cached y position
   */
  public final long getCachedY() {
    return cachedY;
  }

  public final long getCachedY2() {
    return cachedY + cachedHeight;
  }

  /**
   * Defines the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @param cachedY
   *          the cached y position
   */
  public void setCachedY( final long cachedY ) {
    this.cachedY = cachedY;
  }

  public void shiftCached( final long amount ) {
    this.cachedY += amount;
  }

  public final long getCachedWidth() {
    return cachedWidth;
  }

  public final long getCachedX2() {
    return cachedX + cachedWidth;
  }

  public void setCachedWidth( final long cachedWidth ) {
    if ( cachedWidth < 0 ) {
      throw new IndexOutOfBoundsException( "'cached width' cannot be negative." );
    }
    this.cachedWidth = cachedWidth;
  }

  public final long getCachedHeight() {
    return cachedHeight;
  }

  public void setCachedHeight( final long cachedHeight ) {
    if ( cachedHeight < 0 ) {
      throw new IndexOutOfBoundsException( "'cached height' cannot be negative, was " + cachedHeight );
    }
    this.cachedHeight = cachedHeight;
  }

  public void apply() {
    this.x = this.cachedX;
    this.y = this.cachedY;
    this.width = this.cachedWidth;
    this.height = this.cachedHeight;
    this.cachedAge = this.changeTracker;
    this.cacheState = CacheState.CLEAN;
    this.applyState = CacheState.CLEAN;

    final RenderBox parent = getParent();
    if ( parent != null ) {
      parent.addOverflowArea( x + getOverflowAreaWidth() - parent.getX(), y + getOverflowAreaHeight() - parent.getY() );
    }
  }

  public final boolean isLinebreakCacheValid() {
    if ( linebreakAge != changeTracker ) {
      return false;
    }
    return true;
  }

  public final boolean isValidateModelCacheValid() {
    if ( validateModelAge != changeTracker ) {
      return false;
    }
    if ( validateModelResult == ValidationResult.UNKNOWN ) {
      return false;
    }
    return true;
  }

  /**
   * Checks whether this node can be removed. This flag is used by iterative streaming output targets to mark nodes that
   * have been fully processed.
   *
   * @return
   */
  public boolean isFinishedPaginate() {
    return isFlag( FLAG_FINISHED_PAGINATE );
  }

  public void setFinishedPaginate( final boolean finished ) {
    if ( isFinishedPaginate() == true && finished == false ) {
      throw new IllegalStateException( "Cannot undo a finished-marker" );
    }
    setFlag( FLAG_FINISHED_PAGINATE, finished );
  }

  public boolean isFinishedTable() {
    return isFlag( FLAG_FINISHED_TABLE );
  }

  public void setFinishedTable( final boolean finished ) {
    if ( isFinishedTable() == true && finished == false ) {
      throw new IllegalStateException( "Cannot undo a finished-marker" );
    }
    setFlag( FLAG_FINISHED_TABLE, finished );
  }

  public boolean isDeepFinishedTable() {
    return isFinishedTable();
  }

  public CacheState getCacheState() {
    return cacheState;
  }

  public ReportStateKey getStateKey() {
    return null;
  }

  public boolean isBoxOverflowX() {
    return false;
  }

  public boolean isBoxOverflowY() {
    return false;
  }

  public final boolean isNodeVisible( final StrictBounds drawArea, final boolean overflowX, final boolean overflowY ) {
    final long drawAreaX0 = drawArea.getX();
    final long drawAreaY0 = drawArea.getY();
    return isNodeVisible( drawAreaX0, drawAreaY0, drawArea.getWidth(), drawArea.getHeight(), overflowX, overflowY );
  }

  public final boolean isNodeVisible( final StrictBounds drawArea ) {
    final long drawAreaX0 = drawArea.getX();
    final long drawAreaY0 = drawArea.getY();
    return isNodeVisible( drawAreaX0, drawAreaY0, drawArea.getWidth(), drawArea.getHeight() );
  }

  public final boolean isNodeVisible( final long drawAreaX0, final long drawAreaY0, final long drawAreaWidth,
      final long drawAreaHeight ) {
    return isNodeVisible( drawAreaX0, drawAreaY0, drawAreaWidth, drawAreaHeight, isBoxOverflowX(), isBoxOverflowY() );
  }

  public final boolean isNodeVisible( final long drawAreaX0, final long drawAreaY0, final long drawAreaWidth,
      final long drawAreaHeight, final boolean overflowX, final boolean overflowY ) {
    if ( getStyleSheet().getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return false;
    }

    final long drawAreaX1 = drawAreaX0 + drawAreaWidth;
    final long drawAreaY1 = drawAreaY0 + drawAreaHeight;

    final long x2 = x + width;
    final long y2 = y + height;

    if ( width == 0 ) {
      if ( x2 < drawAreaX0 ) {
        return false;
      }
      if ( x > drawAreaX1 ) {
        return false;
      }
    } else if ( overflowX == false ) {
      if ( x2 <= drawAreaX0 ) {
        return false;
      }
      if ( x >= drawAreaX1 ) {
        return false;
      }
    }
    if ( height == 0 ) {
      if ( y2 < drawAreaY0 ) {
        return false;
      }
      if ( y > drawAreaY1 ) {
        return false;
      }
    } else if ( overflowY == false ) {
      if ( y2 <= drawAreaY0 ) {
        return false;
      }
      if ( y >= drawAreaY1 ) {
        return false;
      }
    }
    return true;
  }

  public boolean isVirtualNode() {
    return isFlag( FLAG_VIRTUAL_NODE );
  }

  public void setVirtualNode( final boolean virtualNode ) {
    setFlag( FLAG_VIRTUAL_NODE, virtualNode );
  }

  protected void setFlag( final int flag, final boolean value ) {
    if ( value ) {
      flags = flags | flag;
    } else {
      flags = flags & ( ~flag );
    }
  }

  protected boolean isFlag( final int flag ) {
    return ( flags & flag ) != 0;
  }

  public final boolean isBoxVisible( final StrictBounds drawArea ) {
    return isBoxVisible( drawArea.getX(), drawArea.getY(), drawArea.getWidth(), drawArea.getHeight() );
  }

  public final boolean isBoxVisible( final long x, final long y, final long width, final long height ) {
    if ( isNodeVisible( x, y, width, height ) == false ) {
      return false;
    }

    final RenderBox parent = getParent();
    if ( parent == null ) {
      return true;
    }

    final StyleSheet styleSheet = getStyleSheet();
    if ( styleSheet.getStyleProperty( ElementStyleKeys.ANCHOR_NAME ) != null ) {
      return true;
    }

    if ( parent.getNodeType() != LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT
        && parent.getStaticBoxLayoutProperties().isOverflowX() == false ) {
      final long parentX1 = parent.getX();
      final long parentX2 = parentX1 + parent.getWidth();

      if ( getWidth() == 0 ) {
        // could be a line ..
        return true;
      }

      final long boxX1 = getX();
      final long boxX2 = boxX1 + getWidth();

      if ( boxX2 <= parentX1 ) {
        return false;
      }
      if ( boxX1 >= parentX2 ) {
        return false;
      }
    }

    final int layoutNodeType = getLayoutNodeType();
    if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_TABLE_CELL || layoutNodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      // we cannot perform an overflow test for table-rows or table-cells based on the parent node.
      // With col and row-spanning, this can be non-deterministic.
      return true;
    }

    if ( parent.getNodeType() != LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT
        && parent.getStaticBoxLayoutProperties().isOverflowY() == false ) {
      // Compute whether the box is at least partially contained in the parent's bounding box.
      final long parentY1 = parent.getY();
      final long parentY2 = parentY1 + parent.getHeight();

      if ( getHeight() == 0 ) {
        // could be a line ..
        return true;
      }

      final long boxY1 = getY();
      final long boxY2 = boxY1 + getHeight();

      if ( boxY2 <= parentY1 ) {
        return false;
      }
      if ( boxY1 >= parentY2 ) {
        return false;
      }
    }
    return true;
  }

  public long getOverflowAreaHeight() {
    return getHeight();
  }

  public long getOverflowAreaWidth() {
    return getWidth();
  }

  public long getEffectiveMinimumChunkSize() {
    return minimumChunkWidth;
  }

  public int getChildCount() {
    return 0;
  }

  public boolean isWidowBox() {
    return isFlag( FLAG_WIDOW_BOX );
  }

  public void setWidowBox( final boolean widowBox ) {
    setFlag( FLAG_WIDOW_BOX, widowBox );
  }

  public boolean isOrphanLeaf() {
    return false;
  }

  public RenderBox.RestrictFinishClearOut getRestrictFinishedClearOut() {
    return RenderBox.RestrictFinishClearOut.UNRESTRICTED;
  }

  public long getCachedAge() {
    return cachedAge;
  }

  public boolean isCacheValid() {
    if ( cachedAge != changeTracker ) {
      return false;
    }

    if ( this.cacheState != CACHE_CLEAN ) {
      return false;
    }

    return true;
  }

  protected final void setCachedAge( final long cachedAge ) {
    this.cachedAge = cachedAge;
  }

  public final long getY2() {
    return y + height;
  }

  public boolean isVisible() {
    return nodeLayoutProperties.isVisible();
  }

  public boolean isContainsReservedContent() {
    return false;
  }

  public void markApplyStateDirty() {
    if ( applyState != CacheState.CLEAN ) {
      return;
    }
    applyState = CACHE_DIRTY;
    RenderBox parent = getParent();
    if ( parent != null ) {
      parent.markApplyStateDirty();
    }
  }

  public CacheState getApplyState() {
    return applyState;
  }

  public int getRowIndex() {
    return 0;
  }

  public boolean isRenderBox() {
    return false;
  }

  public int getWidowLeafCount() {
    return 0;
  }

  public int getOrphanLeafCount() {
    return 0;
  }
}
