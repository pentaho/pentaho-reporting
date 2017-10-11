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

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 03.04.2007, 13:17:47
 *
 * @author Thomas Morgner
 */
public abstract class RenderBox extends RenderNode {
  public enum BreakIndicator {
    NO_MANUAL_BREAK, DIRECT_MANUAL_BREAK, INDIRECT_MANUAL_BREAK
  }

  public enum RestrictFinishClearOut {
    UNRESTRICTED, RESTRICTED, LEAF
  }

  protected static final int FLAG_BOX_TABLE_SECTION_RESERVED2 = 0x1000000;
  protected static final int FLAG_BOX_TABLE_SECTION_RESERVED3 = 0x2000000;
  protected static final int FLAG_BOX_TABLE_SECTION_RESERVED4 = 0x4000000;
  protected static final int FLAG_BOX_TABLE_SECTION_RESERVED5 = 0x8000000;
  protected static final int FLAG_BOX_INVALID_WIDOW_ORPHAN_NODE = 0x10000000;
  protected static final int FLAG_BOX_CONTAINS_PRESERVED_CONTENT = 0x20000000;
  private static final int FLAG_BOX_PREVENT_PAGINATION = 0x40000000;
  private static final int FLAG_BOX_OPEN = 0x10000;
  private static final int FLAG_BOX_MARKED_OPEN = 0x20000;
  private static final int FLAG_BOX_APPLIED_OPEN = 0x40000;
  protected static final int FLAG_BOX_TABLE_SECTION_RESERVED = 0x80000;
  private static final int FLAG_BOX_MARKED_SEEN = 0x100000;
  private static final int FLAG_BOX_APPLIED_SEEN = 0x200000;
  private static final int FLAG_BOX_DEEP_FINISHED = 0x400000;
  private static final int FLAG_BOX_CONTENT_REF_HOLDER = 0x800000;
  private int contentRefCount;
  private int tableRefCount;
  private int descendantCount;
  private int markedContentRefCount;
  private int appliedContentRefCount;
  private int orphanLeafCount;
  private int widowLeafCount;
  private BoxDefinition boxDefinition;
  private StaticBoxLayoutProperties staticBoxLayoutProperties;
  private RenderNode firstChildNode;
  private RenderNode lastChildNode;
  private Object rawValue;
  private ExtendedBaselineInfo baselineInfo;
  private String name;
  private BreakIndicator breakIndicator;
  private ReportStateKey stateKey;
  private RenderBox textEllipseBox;
  private Object tableExportState;
  private Boolean contentBox;
  private long staticBoxPropertiesAge;
  private long tableValidationAge;
  private long pinned;
  private long appliedPinPosition;
  private long markedPinPosition;
  private long contentAreaX1;
  private long contentAreaX2;
  private long contentAge;
  private long overflowAreaWidth;
  private long overflowAreaHeight;
  private long processKeyStepAge;
  private ReportStateKey processKeyCached;
  private boolean processKeyFinish;
  private int processKeyContentRefCount;

  /**
   * Is the amount of space reserved for orphans beginning at the y-position of the box.
   */
  private long orphanConstraintSize;
  /**
   * The amount of space reserved for widows, starting at the y2-positions of the box. If zero, the constraint points to
   * y2.
   */
  private long widowConstraintSize;
  private long widowConstraintSizeWithKeepTogether;
  private RestrictFinishClearOut restrictFinishClearOut;
  private int parentWidowContexts;

  protected RenderBox( final int majorAxis, final int minorAxis, final StyleSheet styleSheet,
      final InstanceID instanceId, final BoxDefinition boxDefinition, final ElementType elementType,
      final ReportAttributeMap attributes, final ReportStateKey stateKey ) {
    super( majorAxis, minorAxis, styleSheet, instanceId, elementType, attributes );
    if ( boxDefinition == null ) {
      throw new NullPointerException();
    }
    if ( boxDefinition.isLocked() == false ) {
      throw new InvalidReportStateException( "BoxDefinition must be read-only" );
    }

    this.pinned = -1;
    this.tableValidationAge = -1;
    this.boxDefinition = boxDefinition;
    this.setOpen( true );
    this.staticBoxLayoutProperties = new StaticBoxLayoutProperties();
    this.staticBoxPropertiesAge = -1;
    this.staticBoxLayoutProperties.setBreakAfter( getStyleSheet().getBooleanStyleProperty(
        BandStyleKeys.PAGEBREAK_AFTER ) );
    this.stateKey = stateKey;
    this.descendantCount = 1;
    this.restrictFinishClearOut = RestrictFinishClearOut.UNRESTRICTED;
    this.breakIndicator = BreakIndicator.NO_MANUAL_BREAK;
  }

  public RenderBox create( final StyleSheet styleSheet ) {
    final RenderBox b = (RenderBox) derive( false );
    b.reinit( styleSheet, AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, new InstanceID() );
    b.boxDefinition = BoxDefinition.EMPTY;
    b.staticBoxLayoutProperties = new StaticBoxLayoutProperties();
    b.staticBoxPropertiesAge = -1;
    b.stateKey = null;
    b.setOpen( true );
    b.setMarkedOpen( false );
    b.setMarkedSeen( false );
    b.markedContentRefCount = 0;
    b.setAppliedOpen( false );
    b.setAppliedSeen( false );
    b.appliedContentRefCount = 0;
    b.contentAge = 0;
    b.contentRefCount = 0;
    b.breakIndicator = BreakIndicator.NO_MANUAL_BREAK;
    b.staticBoxPropertiesAge = -1;
    b.pinned = -1;
    b.tableExportState = null;
    b.setDeepFinished( false );
    b.contentAreaX1 = 0;
    b.contentAreaX2 = 0;
    b.setContentRefHolder( false );
    b.descendantCount = 1;
    b.tableValidationAge = -1;
    b.orphanConstraintSize = 0;
    b.widowConstraintSize = 0;
    b.widowConstraintSizeWithKeepTogether = 0;
    b.restrictFinishClearOut = RestrictFinishClearOut.UNRESTRICTED;
    b.parentWidowContexts = 0;
    return b;
  }

  public void setParentWidowContexts( final int parentWidowContexts ) {
    this.parentWidowContexts = parentWidowContexts;
  }

  public int getParentWidowContexts() {
    return parentWidowContexts;
  }

  public int getDescendantCount() {
    return descendantCount;
  }

  public boolean isContentRefHolder() {
    return isFlag( FLAG_BOX_CONTENT_REF_HOLDER );
  }

  private void setContentRefHolder( final boolean flag ) {
    setFlag( FLAG_BOX_CONTENT_REF_HOLDER, flag );
  }

  public void markAsContentRefHolder() {
    if ( isContentRefHolder() ) {
      throw new IllegalStateException();
    }

    setContentRefHolder( true );
    increaseContentReferenceCount( 1, this );
  }

  public Object getRawValue() {
    return rawValue;
  }

  public void setRawValue( final Object rawValue ) {
    this.rawValue = rawValue;
  }

  public boolean isSizeSpecifiesBorderBox() {
    return boxDefinition.isSizeSpecifiesBorderBox();
  }

  public RenderBox getTextEllipseBox() {
    return textEllipseBox;
  }

  public void setTextEllipseBox( final RenderBox textEllipseBox ) {
    this.textEllipseBox = textEllipseBox;
  }

  public ReportStateKey getStateKey() {
    return stateKey;
  }

  protected void setStateKey( final ReportStateKey stateKey ) {
    this.stateKey = stateKey;
  }

  public BreakIndicator getManualBreakIndicator() {
    return breakIndicator;
  }

  public void setManualBreakIndicator( final BreakIndicator manualBreakIndicator ) {
    this.breakIndicator = manualBreakIndicator;
  }

  public BoxDefinition getBoxDefinition() {
    return boxDefinition;
  }

  public long getInsetsLeft() {
    return staticBoxLayoutProperties.getBorderLeft() + boxDefinition.getPaddingLeft();
  }

  public long getInsetsRight() {
    return staticBoxLayoutProperties.getBorderRight() + boxDefinition.getPaddingRight();
  }

  public long getEffectiveMinimumChunkSize() {
    return getMinimumChunkWidth() + getInsets();
  }

  public long getInsets() {
    return staticBoxLayoutProperties.getBorderLeft() + staticBoxLayoutProperties.getBorderRight()
        + boxDefinition.getPaddingLeft() + boxDefinition.getPaddingRight();
  }

  public RenderNode getFirstChild() {
    return firstChildNode;
  }

  protected void setFirstChild( final RenderNode firstChild ) {
    this.firstChildNode = firstChild;
    if ( isParanoidModelChecks() && firstChild != null ) {
      if ( firstChild.getPrev() != null ) {
        throw new NullPointerException();
      }
    }
  }

  public RenderNode getLastChild() {
    return lastChildNode;
  }

  protected void setLastChild( final RenderNode lastChild ) {
    this.lastChildNode = lastChild;
    if ( isParanoidModelChecks() && lastChild != null ) {
      if ( lastChild.getNext() != null ) {
        throw new NullPointerException();
      }
    }
  }

  public void addGeneratedChild( final RenderNode child ) {
    if ( child == null ) {
      throw new NullPointerException( "Child to be added must not be null." );
    }

    final RenderNode oldLastChild = getLastChild();
    setLastChild( child );
    if ( oldLastChild != null ) {
      oldLastChild.setNext( child );
    }

    child.setParent( this );
    child.setPrev( oldLastChild );
    child.setNext( null );

    final RenderNode oldFirstChild = getFirstChild();
    if ( oldFirstChild == null ) {
      setFirstChild( child );
    }

    if ( isFrozen() ) {
      child.freeze();
    }
    child.updateChangeTracker();
    onChildAdded( child );

    validateDescendantCounter();
  }

  private void validateDescendantCounter() {
    if ( isParanoidModelChecks() == false ) {
      return;
    }

    final CountBoxesStep step = new CountBoxesStep();
    final int count = step.countChildren( this );
    if ( count != descendantCount ) {
      throw new InvalidReportStateException( getClass().getSimpleName() + "(" + getName() + "): Counted boxes of "
          + count + " but claimed to have " + descendantCount );
    }
  }

  public void addChild( final RenderNode child ) {
    if ( child == null ) {
      throw new NullPointerException( "Child to be added must not be null." );
    }

    if ( isOpen() == false ) {
      throw new IllegalStateException( "Adding content to an already closed element: " + this );
    }

    if ( isParanoidModelChecks() ) {
      if ( ( getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
        if ( ( child.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
          throw new IllegalStateException(
              "Paranoid Check: A block box cannot contain a inline box directly. They must be wrapped into a "
                  + "paragraph." );
        }
      }
    }
    final RenderNode oldLastChild = getLastChild();
    setLastChild( child );
    if ( oldLastChild != null ) {
      oldLastChild.setNext( child );
    }

    child.setParent( this );
    child.setPrev( oldLastChild );
    child.setNext( null );

    final RenderNode oldFirstChild = getFirstChild();
    if ( oldFirstChild == null ) {
      setFirstChild( child );
    }

    if ( isFrozen() ) {
      child.freeze();
    }

    child.updateChangeTracker();
    onChildAdded( child );

    validateDescendantCounter();
  }

  /**
   * The content-ref-count counts inline-subreports or delayed-construction areas like crosstab-header.
   */
  protected void increaseContentReferenceCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }
    contentRefCount += count;
    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.increaseContentReferenceCount( count, this );
    }
  }

  /**
   * The content-ref-count counts tables.
   */
  protected void increaseTableReferenceCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }
    tableRefCount += count;
    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.increaseTableReferenceCount( count, this );
    }
  }

  /**
   * The content-ref-count counts inline-subreports.
   */
  protected void increaseDescendantCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }

    descendantCount += count;
    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.increaseDescendantCount( count, this );
    }
  }

  /**
   * The content-ref-count counts inline-subreports.
   */
  protected void decreaseContentReferenceCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }
    if ( ( contentRefCount - count ) < 0 ) {
      throw new IndexOutOfBoundsException( "New ContentRefCount would be negative" );
    }

    contentRefCount -= count;

    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.decreaseContentReferenceCount( count, this );
    }
  }

  /**
   * The content-ref-count counts table-render-boxes.
   */
  protected void decreaseTableReferenceCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }
    if ( ( tableRefCount - count ) < 0 ) {
      throw new IndexOutOfBoundsException( "New TableRefCount would be negative" );
    }

    tableRefCount -= count;

    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.decreaseTableReferenceCount( count, this );
    }
  }

  /**
   * The content-ref-count counts table-render-boxes.
   */
  protected void decreaseDescendantCount( final int count, final RenderNode source ) {
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count == 0 ) {
      return;
    }

    if ( ( descendantCount - count ) < 1 ) {
      throw new IndexOutOfBoundsException( "New Descendant-Count would be negative. " + descendantCount + " - " + count );
    }

    descendantCount -= count;

    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      renderBox.decreaseDescendantCount( count, this );
    }
  }

  /**
   * The content-ref-count counts inline-subreports.
   */
  public int getContentRefCount() {
    return contentRefCount;
  }

  public int getTableRefCount() {
    return tableRefCount;
  }

  public void replaceChild( final RenderNode old, final RenderNode replacement ) {
    if ( old.getParent() != this ) {
      throw new IllegalArgumentException( "None of my childs." );
    }
    if ( old == replacement ) {
      // nothing to do ...
      return;
    }

    final RenderNode oldFirstChild = getFirstChild();
    if ( old == oldFirstChild ) {
      setFirstChild( replacement );
    }
    final RenderNode oldLastChild = getLastChild();
    if ( old == oldLastChild ) {
      setLastChild( replacement );
    }

    final RenderNode prev = old.getPrev();
    final RenderNode next = old.getNext();
    replacement.setParent( this );
    replacement.setPrev( prev );
    replacement.setNext( next );

    if ( prev != null ) {
      prev.setNext( replacement );
    }
    if ( next != null ) {
      next.setPrev( replacement );
    }

    old.setNext( null );
    old.setPrev( null );
    old.setParent( null );
    old.updateChangeTracker();

    onChildRemoved( old );
    replacement.updateChangeTracker();
    onChildAdded( replacement );

    validateDescendantCounter();

    if ( isParanoidModelChecks() ) {
      if ( replacement.getNext() == null ) {
        if ( getLastChild() != replacement ) {
          throw new IllegalStateException();
        }
      }
    }
  }

  public void replaceChilds( final RenderNode old, final RenderNode[] replacement ) {
    if ( old.getParent() != this ) {
      throw new IllegalArgumentException( "None of my childs." );
    }

    final int replacementCount = replacement.length;
    if ( replacementCount == 0 ) {
      throw new IndexOutOfBoundsException( "Array is empty .." );
    }

    if ( old == replacement[0] ) {
      if ( replacementCount == 1 ) {
        // nothing to do ...
        return;
      }
      // throw new IllegalArgumentException
      // ("Thou shall not use the replace method to insert new elements!");
    }

    final RenderNode oldPrev = old.getPrev();
    final RenderNode oldNext = old.getNext();

    old.setNext( null );
    old.setPrev( null );
    old.setParent( null );

    // first, connect all replacements ...
    RenderNode first = null;
    RenderNode last = null;

    for ( int i = 0; i < replacementCount; i++ ) {
      if ( last == null ) {
        last = replacement[i];
        if ( last != null ) {
          first = last;
          first.setParent( this );
        }
        continue;
      }

      final RenderNode node = replacement[i];

      last.setNextUnchecked( node );
      node.setPrevUnchecked( last );
      node.setParent( this );
      last = node;
    }

    if ( first == null ) {
      throw new IndexOutOfBoundsException( "Array is empty (NullValues stripped).." );
    }

    if ( old == getFirstChild() ) {
      setFirstChild( first );
    }

    if ( old == getLastChild() ) {
      setLastChild( last );
    }

    // Something inbetween ...
    first.setPrev( oldPrev );
    last.setNext( oldNext );

    if ( oldPrev != null ) {
      oldPrev.setNext( first );
    }
    if ( oldNext != null ) {
      oldNext.setPrev( last );
    }

    old.updateChangeTracker();
    onChildRemoved( old );

    for ( int i = 0; i < replacementCount; i++ ) {
      final RenderNode renderNode = replacement[i];
      renderNode.updateChangeTracker();
      onChildAdded( renderNode );
    }

    validateDescendantCounter();
  }

  private void onChildAdded( final RenderNode child ) {
    increaseContentReferenceCount( child.getContentRefCount(), child );
    increaseTableReferenceCount( child.getTableRefCount(), child );
    increaseDescendantCount( child.getDescendantCount(), child );
  }

  private void onChildRemoved( final RenderNode old ) {
    decreaseContentReferenceCount( old.getContentRefCount(), old );
    decreaseTableReferenceCount( old.getTableRefCount(), old );
    decreaseDescendantCount( old.getDescendantCount(), old );
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, sibling, child or any other relationships with other nodes.
   *
   * @return
   */
  public RenderNode derive( final boolean deepDerive ) {
    final RenderBox box = (RenderBox) super.derive( deepDerive );

    if ( deepDerive ) {
      RenderNode node = getFirstChild();
      RenderNode currentNode = null;
      while ( node != null ) {
        final RenderNode previous = currentNode;

        currentNode = node.derive( true );
        currentNode.setParent( box );
        if ( previous == null ) {
          if ( isParanoidModelChecks() && currentNode.getPrev() != null ) {
            throw new IllegalStateException();
          }
          box.setFirstChild( currentNode );
        } else {
          previous.setNext( currentNode );
          currentNode.setPrev( previous );
        }
        node = node.getNext();
      }

      box.setLastChild( currentNode );

      validateDescendantCounter();

      if ( isParanoidModelChecks() && currentNode != null ) {
        if ( currentNode.getNext() != null ) {
          throw new IllegalStateException();
        }
      }
    } else {
      box.setLastChild( null );
      box.setFirstChild( null );
      box.contentRefCount = 0;
      box.descendantCount = 1;
      box.tableRefCount = 0;
    }
    return box;
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @return
   */
  public RenderNode deriveFrozen( final boolean deepDerive ) {
    final RenderBox box = (RenderBox) super.deriveFrozen( deepDerive );
    if ( deepDerive ) {
      RenderNode node = getFirstChild();
      RenderNode currentNode = null;
      while ( node != null ) {
        final RenderNode previous = currentNode;

        currentNode = node.deriveFrozen( true );
        currentNode.setParent( box );
        if ( previous == null ) {
          if ( isParanoidModelChecks() && currentNode.getPrev() != null ) {
            throw new IllegalStateException();
          }
          box.setFirstChild( currentNode );
        } else {
          previous.setNext( currentNode );
          currentNode.setPrev( previous );
        }
        node = node.getNext();
      }

      box.setLastChild( currentNode );

      validateDescendantCounter();

      if ( isParanoidModelChecks() && currentNode != null ) {
        if ( currentNode.getNext() != null ) {
          throw new IllegalStateException();
        }
      }
    } else {
      box.setLastChild( null );
      box.setFirstChild( null );
      box.descendantCount = 1;
      box.contentRefCount = 0;
      box.tableRefCount = 0;
    }
    return box;
  }

  public void addChilds( final RenderNode[] nodes ) {
    final int length = nodes.length;
    for ( int i = 0; i < length; i++ ) {
      addChild( nodes[i] );
    }
  }

  public void addGeneratedChilds( final RenderNode[] nodes ) {
    final int nodeLength = nodes.length;
    for ( int i = 0; i < nodeLength; i++ ) {
      addGeneratedChild( nodes[i] );
    }
  }

  public RenderNode findNodeById( final InstanceID instanceId ) {
    if ( instanceId == getInstanceId() ) {
      return this;
    }

    RenderNode child = getLastChild();
    while ( child != null ) {
      final RenderNode nodeById = child.findNodeById( instanceId );
      if ( nodeById != null ) {
        return nodeById;
      }
      child = child.getPrev();
    }
    return null;
  }

  public boolean isAppendable() {
    return isOpen();
  }

  /**
   * Removes all children.
   */
  public void clear() {
    RenderNode child = getFirstChild();
    while ( child != null ) {
      final RenderNode nextChild = child.getNext();
      child.setPrev( null );
      child.setNext( null );
      child.setParent( null );
      onChildRemoved( child );
      child = nextChild;
    }
    setFirstChild( null );
    setLastChild( null );
    updateChangeTracker();

    validateDescendantCounter();

  }

  protected void updateChangeTracker() {
    tableExportState = null;
    super.updateChangeTracker();
  }

  private RenderNode getFirstNonEmpty() {
    RenderNode firstChild = getFirstChild();
    while ( firstChild != null ) {
      if ( firstChild.isEmpty() == false ) {
        return firstChild;
      }
      firstChild = firstChild.getNext();
    }
    return null;
  }

  public boolean isEmpty() {
    if ( getBoxDefinition().isEmpty() == false ) {
      return false;
    }

    final RenderNode node = getFirstNonEmpty();
    if ( node != null ) {
      return false;
    }
    // Ok, the childs were not able to tell us some truth ..
    // lets try something else.
    return true;
  }

  public boolean isDiscardable() {
    if ( getBoxDefinition().isEmpty() == false ) {
      return false;
    }

    if ( getStyleSheet().getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR ) != null ) {
      return false;
    }

    RenderNode node = getFirstChild();
    while ( node != null ) {
      if ( node.isDiscardable() == false ) {
        return false;
      }
      node = node.getNext();
    }
    return true;
  }

  public void close() {
    if ( isOpen() == false ) {
      throw new IllegalStateException( "Double close.." );
    }

    this.setOpen( false );
    if ( isContentRefHolder() ) {
      decreaseContentReferenceCount( 1, this );
    }
  }

  public void remove( final RenderNode child ) {
    final RenderBox parent = child.getParent();
    if ( parent != this ) {
      throw new IllegalArgumentException( "None of my childs" );
    }

    final RenderNode prev = child.getPrev();
    final RenderNode next = child.getNext();

    if ( prev != null ) {
      prev.setNext( next );
    }

    if ( next != null ) {
      next.setPrev( prev );
    }

    child.setNext( null );
    child.setPrev( null );
    child.setParent( null );
    onChildRemoved( child );

    if ( getFirstChild() == child ) {
      setFirstChild( next );
    }
    if ( getLastChild() == child ) {
      setLastChild( prev );
    }
    child.updateChangeTracker();
    updateChangeTracker();

    validateDescendantCounter();

  }

  public boolean isOpen() {
    return isFlag( FLAG_BOX_OPEN ) || contentRefCount > 0;
  }

  protected void setOpen( final boolean open ) {
    if ( isOpen() == open ) {
      return;
    }

    updateChangeTracker();
    setFlag( FLAG_BOX_OPEN, open );
  }

  public void freeze() {
    if ( isFrozen() ) {
      return;
    }

    super.freeze();
    RenderNode node = getFirstChild();
    while ( node != null ) {
      node.freeze();
      node = node.getNext();
    }
  }

  /**
   * Performs a simple split. This box will be altered to form the left/top side of the split, and a derived empty box
   * will be returned, which makes up the right/bottom side.
   * <p/>
   * A split will only happen on inline-boxes during the line-break-step. In the ordinary layouting, splitting is not
   * necesary.
   *
   * @param axis
   * @return
   */
  public RenderBox split( final int axis ) {
    final RenderBox otherBox = (RenderBox) derive( false );
    if ( boxDefinition.isEmpty() == false ) {
      final BoxDefinition[] boxDefinitions = boxDefinition.split( axis );
      boxDefinition = boxDefinitions[0];
      otherBox.boxDefinition = boxDefinitions[1];
    }
    return otherBox;
  }

  public long getContentAreaX1() {
    return contentAreaX1;
  }

  public void setContentAreaX1( final long contentAreaX1 ) {
    this.contentAreaX1 = contentAreaX1;
  }

  public long getContentAreaX2() {
    return contentAreaX2;
  }

  public void setContentAreaX2( final long contentAreaX2 ) {
    this.contentAreaX2 = contentAreaX2;
  }

  public StaticBoxLayoutProperties getStaticBoxLayoutProperties() {
    return staticBoxLayoutProperties;
  }

  public ExtendedBaselineInfo getBaselineInfo() {
    return baselineInfo;
  }

  public void setBaselineInfo( final ExtendedBaselineInfo baselineInfo ) {
    this.baselineInfo = baselineInfo;
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    this.name = name;
  }

  public boolean isBreakAfter() {
    return staticBoxLayoutProperties.isBreakAfter();
  }

  public long getStaticBoxPropertiesAge() {
    return staticBoxPropertiesAge;
  }

  public void setStaticBoxPropertiesAge( final long staticBoxPropertiesAge ) {
    if ( staticBoxLayoutProperties.getNominalBaselineInfo() == null ) {
      throw new IllegalStateException(
          "Assertation: Cannot declare static-properties finished without a nominal baseline info" );
    }
    this.staticBoxPropertiesAge = staticBoxPropertiesAge;
  }

  public String toString() {
    return getClass().getName() + '{' + "name='" + name + '\'' + ", x='" + getX() + '\'' + ", y='" + getY() + '\''
        + ", width='" + getWidth() + '\'' + ", height='" + getHeight() + '\'' + ", elementType='" + getElementType()
        + '\'' + ", finishedPaginate='" + isFinishedPaginate() + '\'' + ", finishedTable='" + isFinishedTable() + '\''
        + ", committed='" + isCommited() + '\'' + '}';
  }

  public void commit() {
    appliedPinPosition = markedPinPosition;
    appliedContentRefCount = markedContentRefCount;
    setAppliedOpen( isMarkedOpen() );
    setAppliedSeen( isMarkedSeen() );

    validateDescendantCounter();
  }

  public int getAppliedContentRefCount() {
    return appliedContentRefCount;
  }

  public boolean isAppliedOpen() {
    return isFlag( FLAG_BOX_APPLIED_OPEN );
  }

  private void setAppliedOpen( final boolean flag ) {
    setFlag( FLAG_BOX_APPLIED_OPEN, flag );
  }

  public boolean isAppliedSeen() {
    return isFlag( FLAG_BOX_APPLIED_SEEN );
  }

  private void setAppliedSeen( final boolean flag ) {
    setFlag( FLAG_BOX_APPLIED_SEEN, flag );
  }

  public boolean isMarkedOpen() {
    return isFlag( FLAG_BOX_MARKED_OPEN );
  }

  private void setMarkedOpen( final boolean flag ) {
    setFlag( FLAG_BOX_MARKED_OPEN, flag );
  }

  public boolean isMarkedSeen() {
    return isFlag( FLAG_BOX_MARKED_SEEN );
  }

  private void setMarkedSeen( final boolean flag ) {
    setFlag( FLAG_BOX_MARKED_SEEN, flag );
  }

  public void markBoxSeen() {
    setMarkedOpen( isOpen() );
    markedContentRefCount = contentRefCount;
    setMarkedSeen( true );
    markedPinPosition = pinned;

    validateDescendantCounter();
  }

  public boolean isCommited() {
    return isAppliedOpen() == false && isAppliedSeen() == true && appliedContentRefCount == 0;
  }

  public void rollback( final boolean deepDirty ) {
    setOpen( isAppliedOpen() );
    this.contentRefCount = appliedContentRefCount;
    setMarkedOpen( isAppliedOpen() );
    this.markedContentRefCount = appliedContentRefCount;
    this.markedPinPosition = appliedPinPosition;
    this.overflowAreaHeight = getCachedHeight();
    this.overflowAreaWidth = 0;
    // todo PRD-4606
    resetCacheState( false );

    validateDescendantCounter();
  }

  public void resetCacheState( final boolean deepDirty ) {
    resetValidateModelResult();
    setLinebreakAge( -1 );
    setCachedAge( -1 );
    if ( deepDirty ) {
      updateCacheState( CACHE_DEEP_DIRTY );
    } else {
      updateCacheState( CACHE_DIRTY );
    }
    updateChangeTracker();
  }

  public boolean isDeepFinishedTable() {
    return isFlag( FLAG_BOX_DEEP_FINISHED );
  }

  public void setDeepFinished( final boolean deepFinished ) {
    setFlag( FLAG_BOX_DEEP_FINISHED, deepFinished );
  }

  public long getContentAge() {
    return contentAge;
  }

  public void setContentAge( final long contentAge ) {
    this.contentAge = contentAge;
  }

  public Boolean getContentBox() {
    return contentBox;
  }

  public void setContentBox( final Boolean contentBox ) {
    this.contentBox = contentBox;
  }

  public Object getTableExportState() {
    return tableExportState;
  }

  public void setTableExportState( final Object tableExportState ) {
    this.tableExportState = tableExportState;
  }

  public void markPinned( final long pinPosition ) {
    if ( isPinned() ) {
      return;
    }
    pinned = pinPosition;
    final RenderBox renderBox = getParent();
    if ( renderBox != null ) {
      // Mark this box pinned at its currently layouted position.
      renderBox.markPinned( renderBox.getY() );
    }
  }

  public boolean isPinned() {
    return pinned != -1;
  }

  public long getPinned() {
    return pinned;
  }

  public void setMinimumChunkWidth( final long minimumChunkWidth ) {
    super.setMinimumChunkWidth( minimumChunkWidth );
  }

  public boolean isBoxOverflowX() {
    return staticBoxLayoutProperties.isOverflowX();
  }

  public boolean isBoxOverflowY() {
    return staticBoxLayoutProperties.isOverflowY();
  }

  public boolean isEmptyNodesHaveSignificance() {
    return getNodeLayoutProperties().getStyleSheet()
        .getBooleanStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE );
  }

  public boolean isAcceptInlineBoxes() {
    return false;
  }

  public long getTableValidationAge() {
    return tableValidationAge;
  }

  public void setTableValidationAge( final long tableValidationAge ) {
    this.tableValidationAge = tableValidationAge;
  }

  public boolean useMinimumChunkWidth() {
    return getStyleSheet().getBooleanStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH );
  }

  public long getOverflowAreaHeight() {
    return overflowAreaHeight;
  }

  public void setOverflowAreaHeight( final long overflowAreaHeight ) {
    this.overflowAreaHeight = overflowAreaHeight;
  }

  public long getOverflowAreaWidth() {
    return Math.max( getWidth(), overflowAreaWidth );
  }

  public void setOverflowAreaWidth( final long overflowAreaWidth ) {
    this.overflowAreaWidth = overflowAreaWidth;
  }

  public void addOverflowArea( final long width, final long height ) {
    if ( width > overflowAreaWidth ) {
      setOverflowAreaWidth( width );
    }
    if ( height > overflowAreaHeight ) {
      setOverflowAreaHeight( height );
    }
  }

  public void apply() {
    super.apply();
    this.overflowAreaHeight = getCachedHeight();
    this.staticBoxPropertiesAge = getChangeTracker();
    this.tableValidationAge = getChangeTracker();
  }

  /**
   * Notifies a box that one of its childs has extended its height. The child's height property already contains the new
   * height. The <code>amount</code> given is the offset from the old height to the new height, and is always a positive
   * number.
   *
   * @param child
   * @param heightOffset
   */
  public long extendHeight( final RenderNode child, final long heightOffset ) {
    return extendHeightInBlockMode( child, heightOffset );
  }

  protected long extendHeightInBlockMode( final RenderNode child, final long heightOffset ) {
    setHeight( getHeight() + heightOffset );
    setOverflowAreaHeight( getOverflowAreaHeight() + heightOffset );
    // updateCacheState(CACHE_DIRTY);
    return heightOffset;
  }

  /**
   * Match the y2 of the child with the y2 of the parent. If the box extends over the y2 of the parent, then extend the
   * parent. If the parent has overflow-y, then we must not extend by more than heightOffset.
   *
   * @param child
   * @param heightOffset
   */
  protected long extendHeightInRowMode( final RenderNode child, final long heightOffset ) {
    final long parentY2 = getY() + getHeight();
    final long childY2 = child.getY() + child.getHeight();
    final long deltaToBase = childY2 - parentY2;
    if ( deltaToBase <= 0 ) {
      // child expands without expanding this parent band. There was enough space available to contain the
      // child inside the parent box.
      return 0;
    }

    final long delta = Math.min( deltaToBase, heightOffset );
    setHeight( getHeight() + delta );
    setOverflowAreaHeight( getOverflowAreaHeight() + delta );
    // updateCacheState(CACHE_DIRTY);
    return delta;
  }

  public int getChildCount() {
    int count = 0;
    RenderNode next = firstChildNode;
    while ( next != null ) {
      count += 1;
      next = next.getNext();
    }
    return count;
  }

  public long getOrphanConstraintSize() {
    return orphanConstraintSize;
  }

  public void setOrphanConstraintSize( final long orphanConstraintSize ) {
    this.orphanConstraintSize = orphanConstraintSize;
  }

  public long getWidowConstraintSize() {
    return widowConstraintSize;
  }

  public void setWidowConstraintSize( final long widowConstraintSize ) {
    this.widowConstraintSize = widowConstraintSize;
  }

  public long getWidowConstraintSizeWithKeepTogether() {
    return widowConstraintSizeWithKeepTogether;
  }

  public void setWidowConstraintSizeWithKeepTogether( final long widowConstraintSizeWithKeepTogether ) {
    this.widowConstraintSizeWithKeepTogether = widowConstraintSizeWithKeepTogether;
  }

  public boolean isInvalidWidowOrphanNode() {
    return isFlag( FLAG_BOX_INVALID_WIDOW_ORPHAN_NODE );
  }

  public void setInvalidWidowOrphanNode( final boolean invalidWidowOrphanNode ) {
    setFlag( FLAG_BOX_INVALID_WIDOW_ORPHAN_NODE, invalidWidowOrphanNode );
  }

  public RestrictFinishClearOut getRestrictFinishedClearOut() {
    return restrictFinishClearOut;
  }

  public void setRestrictFinishedClearOut( final RestrictFinishClearOut restrictFinishedClearOut ) {
    if ( this.restrictFinishClearOut == restrictFinishedClearOut ) {
      return;
    }

    this.restrictFinishClearOut = restrictFinishedClearOut;
    final RenderBox parent = getParent();
    // only propagate across block-elements. Canvas, Inline or row-elements do not carry
    // the pagebreak-restrictions upwards.
    if ( parent != null && parent.isBlockForPagebreakPurpose()
        && restrictFinishedClearOut != RestrictFinishClearOut.UNRESTRICTED ) {
      parent.setRestrictFinishedClearOut( RestrictFinishClearOut.RESTRICTED );
    }
  }

  protected boolean isBlockForPagebreakPurpose() {
    return false;
  }

  public boolean isOrphanLeaf() {
    return this.restrictFinishClearOut == RestrictFinishClearOut.LEAF;
  }

  public long getVerticalInsets() {
    final long insetBottom = staticBoxLayoutProperties.getBorderBottom() + boxDefinition.getPaddingBottom();
    final long insetTop = staticBoxLayoutProperties.getBorderTop() + boxDefinition.getPaddingTop();
    return insetBottom + insetTop;
  }

  public boolean isContainsReservedContent() {
    return isFlag( FLAG_BOX_CONTAINS_PRESERVED_CONTENT );
  }

  public void setContainsReservedContent( final boolean containsReservedContent ) {
    setFlag( FLAG_BOX_CONTAINS_PRESERVED_CONTENT, containsReservedContent );
  }

  public boolean isPreventPagination() {
    return isFlag( FLAG_BOX_PREVENT_PAGINATION );
  }

  public void setPreventPagination( final boolean preventPagination ) {
    setFlag( FLAG_BOX_PREVENT_PAGINATION, preventPagination );
    updateChangeTracker();
  }

  public boolean isRenderBox() {
    return true;
  }

  public void setProcessKeyCached( final ReportStateKey processKeyCached ) {
    this.processKeyStepAge = getChangeTracker();
    this.processKeyCached = processKeyCached;
    this.processKeyFinish = isFinishedPaginate();
    this.processKeyContentRefCount = getDescendantCount();
  }

  public long getProcessKeyStepAge() {
    return processKeyStepAge;
  }

  public ReportStateKey getProcessKeyCached() {
    return processKeyCached;
  }

  public boolean isProcessKeyFinish() {
    return processKeyFinish;
  }

  public boolean isProcessKeyCacheValid() {
    if ( processKeyCached == null ) {
      return false;
    }
    if ( getContentRefCount() != 0 ) {
      // subreport content cannot be cached ..
      return false;
    }
    return ( getProcessKeyStepAge() == getChangeTracker() && isProcessKeyFinish() == isFinishedPaginate() && this.processKeyContentRefCount == getDescendantCount() );

  }

  public int getOrphanLeafCount() {
    return orphanLeafCount;
  }

  public void setOrphanLeafCount( final int orphanLeafCount ) {
    this.orphanLeafCount = orphanLeafCount;
  }

  public int getWidowLeafCount() {
    return widowLeafCount;
  }

  public void setWidowLeafCount( final int widowLeafCount ) {
    this.widowLeafCount = widowLeafCount;
  }
}
