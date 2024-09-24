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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.pentaho.reporting.engine.classic.core.layout.model.InlineProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class GroupSection implements Cloneable {
  private static class FlatGroupSectionStrategy implements Cloneable {
    protected static final double COMMON_GROWTH = 0.5;
    protected static final int INITIAL_COMMON_SIZE = 50;
    protected static final int MAXIMUM_COMMON_SIZE = 5000;

    private int childCount;
    private int nextBoxStart;
    private StyleSheet styleSheet;
    private RenderBox addBox;
    private RenderBox groupBox;

    private FlatGroupSectionStrategy( final RenderBox addBox, final StyleSheet styleSheet ) {
      this.groupBox = addBox;
      this.addBox = addBox;
      this.styleSheet = styleSheet;
      this.childCount = 0;
      this.nextBoxStart = INITIAL_COMMON_SIZE;
    }

    protected void initGrowth( final int growth ) {
      this.nextBoxStart = growth;
    }

    public Object clone() {
      try {
        return super.clone();
      } catch ( CloneNotSupportedException cse ) {
        throw new IllegalStateException( cse );
      }
    }

    public boolean mergeSection( final ReportStateKey stateKey ) {
      final RenderNode lastSection = getAddBox().getLastChild();
      if ( lastSection == null ) {
        return false;
      }
      if ( ( lastSection.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX ) != LayoutNodeTypes.MASK_BOX ) {
        return false;
      }

      final RenderBox lastSectionBox = (RenderBox) lastSection;
      final RenderNode maybeMarker = lastSectionBox.getLastChild();
      if ( maybeMarker == null ) {
        return false;
      }
      final int nodeType = maybeMarker.getNodeType();
      if ( nodeType == LayoutNodeTypes.TYPE_BOX_INLINE_PROGRESS_MARKER ) {
        final InlineProgressMarkerRenderBox markerRenderBox = (InlineProgressMarkerRenderBox) maybeMarker;
        markerRenderBox.setStateKey( stateKey );
        return true;
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER ) {
        final ProgressMarkerRenderBox markerRenderBox = (ProgressMarkerRenderBox) maybeMarker;
        markerRenderBox.setStateKey( stateKey );
        return true;
      }
      return false;
    }

    public RenderBox getGroupBox() {
      return groupBox;
    }

    public void setAddBox( final RenderBox addBox ) {
      this.addBox = addBox;
    }

    public RenderBox getAddBox() {
      return addBox;
    }

    public int getChildCount() {
      return childCount;
    }

    public void addedSection( final RenderNode node ) {
      childCount += 1;
      if ( childCount == nextBoxStart ) {
        if ( addBox != groupBox ) {
          addBox.close();
        }

        final RenderBox commonBox = addBox.create( styleSheet );
        commonBox.setName( "Common-Section" ); // NON-NLS
        groupBox.addChild( commonBox );
        addBox = commonBox;

        nextBoxStart += computeGrowth();
      }
      addBox.addChild( node );
    }

    public void removedLastSection( final RenderNode child ) {
      childCount -= 1;
      addBox.remove( child );
    }

    protected int computeGrowth() {
      if ( nextBoxStart == 0 ) {
        return INITIAL_COMMON_SIZE;
      }
      return (int) Math.min( MAXIMUM_COMMON_SIZE, nextBoxStart * COMMON_GROWTH );
    }

    public void close() {
      if ( addBox != groupBox ) {
        addBox.close();
      }
      groupBox.close();
    }

    public FlatGroupSectionStrategy deriveForStorage( final RenderBox clonedParent ) {
      final FlatGroupSectionStrategy clone = (FlatGroupSectionStrategy) clone();
      final InstanceID groupBoxInstanceId = groupBox.getInstanceId();
      final RenderBox groupBoxClone;
      if ( clonedParent == null ) {
        groupBoxClone = (RenderBox) clone.groupBox.derive( true );
      } else {
        groupBoxClone = (RenderBox) clonedParent.findNodeById( groupBoxInstanceId );
        if ( groupBoxClone == null ) {
          throw new IllegalStateException( "The pagebox did no longer contain the stored node." );
        }
        if ( groupBoxClone == groupBox ) {
          throw new IllegalStateException( "Thought you wanted a groupBoxClone" );
        }
      }

      final RenderBox addBox = getAddBox();
      final RenderBox addBoxClone;
      if ( addBox == groupBox ) {
        addBoxClone = groupBoxClone;
      } else {
        final InstanceID addBoxInstanceId = addBox.getInstanceId();
        addBoxClone = (RenderBox) groupBoxClone.findNodeById( addBoxInstanceId );
        if ( addBoxClone == null ) {
          throw new IllegalStateException( "The pagebox did no longer contain the stored node." );
        }
        if ( addBoxClone == addBox ) {
          throw new IllegalStateException( "Thought you wanted a groupBoxClone" );
        }
      }

      clone.addBox = addBoxClone;
      clone.groupBox = groupBoxClone;
      return clone;
    }

    public void restoreStateAfterRollback() {
      if ( addBox.isOpen() == false && addBox.getParent() == null ) {
        addBox = groupBox.create( styleSheet );
      }
    }
  }

  private static class QuadraticGroupSection extends FlatGroupSectionStrategy {
    private int predictedGrowth;
    private int predictedSize;

    public QuadraticGroupSection( final RenderBox groupBox, final StyleSheet styleSheet, final int predictedSize ) {
      super( groupBox, styleSheet );
      this.predictedSize = predictedSize;
      this.predictedGrowth = (int) Math.ceil( Math.pow( predictedSize, 0.4 ) );

      final RenderBox commonBox = groupBox.create( styleSheet );
      commonBox.setName( "Common-Section" ); // NON-NLS
      setAddBox( commonBox );
      getGroupBox().addChild( getAddBox() );

      initGrowth( Math.max( 5, predictedGrowth ) );
    }

    public void addedSection( final RenderNode node ) {
      super.addedSection( node );
      // if ( getAddBox().getParent() == null ) {
      // getGroupBox().addChild(getAddBox());
      // }
    }

    protected int computeGrowth() {
      return predictedGrowth;
    }
  }

  private static class LegacyGroupSectionStrategy extends FlatGroupSectionStrategy {
    private static final int INITIAL_COMMON_SIZE = 50;
    private int predictedGrowth;
    private int predictedSize;

    protected LegacyGroupSectionStrategy( final RenderBox groupBox, final StyleSheet styleSheet, final int predictedSize ) {
      super( groupBox, styleSheet );
      this.predictedSize = predictedSize;
      if ( predictedSize == 0 ) {
        initGrowth( INITIAL_COMMON_SIZE );
      } else {
        predictedGrowth = (int) Math.max( 5, Math.sqrt( predictedSize ) );
        initGrowth( predictedGrowth );
      }
    }

    protected int computeGrowth() {
      if ( predictedSize == 0 || getChildCount() > predictedSize ) {
        return (int) Math.min( MAXIMUM_COMMON_SIZE, getChildCount() * COMMON_GROWTH );
      }
      return predictedGrowth;
    }

  }

  private StyleSheet styleSheet;
  private FlatGroupSectionStrategy strategy;

  public GroupSection( final RenderBox groupBox, final StyleSheet styleSheet, final int predictedSize,
      final boolean legacyMode ) {
    if ( groupBox == null ) {
      throw new NullPointerException();
    }
    this.styleSheet = styleSheet;

    if ( predictedSize <= 15 ) {
      this.strategy = new FlatGroupSectionStrategy( groupBox, styleSheet );
    } else {
      if ( legacyMode ) {
        this.strategy = new LegacyGroupSectionStrategy( groupBox, styleSheet, 0 );
      } else {
        this.strategy = new QuadraticGroupSection( groupBox, styleSheet, predictedSize );
      }
    }
  }

  public boolean mergeSection( final ReportStateKey stateKey ) {
    return strategy.mergeSection( stateKey );
  }

  public void addedSection( final RenderNode node ) {
    strategy.addedSection( node );
  }

  public void removedLastSection( final RenderNode child ) {
    strategy.removedLastSection( child );
  }

  public void close() {
    strategy.close();
  }

  public RenderBox getGroupBox() {
    return strategy.getGroupBox();
  }

  public boolean isEmpty() {
    return getChildCount() == 0;
  }

  public int getChildCount() {
    return strategy.getChildCount();
  }

  public StyleSheet getStyleSheet() {
    return styleSheet;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public GroupSection deriveForPagebreak() {
    final GroupSection clone = (GroupSection) clone();
    clone.strategy = (FlatGroupSectionStrategy) strategy.clone();
    return clone;
  }

  public GroupSection deriveForStorage( final RenderBox clonedParent ) {
    final GroupSection clone = (GroupSection) clone();
    clone.strategy = strategy.deriveForStorage( clonedParent );
    return clone;
  }

  public void performParanoidModelCheck() {
    // step 1: Check whether addbox is a child of groupbox
    RenderBox c = strategy.getAddBox();
    if ( strategy.getChildCount() > 0 ) {
      while ( c != strategy.getGroupBox() ) {
        c = c.getParent();
        if ( c == null ) {
          throw new IllegalStateException( "Failed to locate parent" );
        }
      }
    }

    c = strategy.getAddBox();
    while ( c != null ) {
      if ( c.isOpen() == false ) {
        throw new IllegalStateException( "Add-Box is not open: " + c.isMarkedOpen() + ' ' + c.isMarkedSeen() + ' ' + c );
      }
      c = c.getParent();
    }
  }

  public void performPostCommitModelCheck() {
    final RenderBox groupBox = strategy.getGroupBox();
    if ( groupBox.isMarkedSeen() == false ) {
      throw new IllegalStateException( "No seen-marker at " + groupBox );
    }
    if ( strategy.getChildCount() > 0 ) {
      final RenderBox addBox = strategy.getAddBox();
      if ( addBox.getParent() == null ) {
        throw new IllegalStateException( "No longer there" );
      }
      if ( addBox.isMarkedSeen() == false ) {
        throw new IllegalStateException( "No seen-marker at add-box " + addBox );
      }
      if ( addBox.isMarkedOpen() == false ) {
        throw new IllegalStateException( "No open-marker at " + addBox );
      }
    }
  }

  public void restoreStateAfterRollback() {
    strategy.restoreStateAfterRollback();
  }

  public RenderBox getAddBox() {
    return strategy.getAddBox();
  }
}
