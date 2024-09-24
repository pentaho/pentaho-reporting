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

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;

public class FooterLayoutModelBuilder extends LayoutModelBuilderWrapper {
  private static final Log logger = LogFactory.getLog( FooterLayoutModelBuilder.class );

  private ArrayList<RenderNode> slots;
  private int slotCounter;
  private RenderBox parentBox;
  private int inBoxDepth;
  private ReportStateKey stateKey;
  private boolean empty;
  private OutputProcessorMetaData metaData;

  public FooterLayoutModelBuilder( final LayoutModelBuilder backend ) {
    super( backend );
    backend.setLimitedSubReports( true );
    backend.setCollapseProgressMarker( false );
    this.slots = new ArrayList<RenderNode>();
  }

  public void initialize( final ProcessingContext metaData, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory ) {
    this.parentBox = parentBox;
    getParent().initialize( metaData, parentBox, renderNodeFactory );
    this.metaData = metaData.getOutputProcessorMetaData();
  }

  public void setLimitedSubReports( final boolean limitedSubReports ) {

  }

  public void updateState( final ReportStateKey stateKey ) {
    this.stateKey = stateKey;
    getParent().updateState( stateKey );
  }

  public InstanceID startBox( final ReportElement element ) {
    InstanceID instanceID = getParent().startBox( element );
    if ( inBoxDepth == 0 ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Started a Box: " + slotCounter + " " + element );
      }
    }
    inBoxDepth += 1;
    return instanceID;
  }

  public void startSection( final ReportElement element, final int sectionSize ) {
    throw new UnsupportedOperationException( "Global sections cannot be started for page headers" );
  }

  public InlineSubreportMarker processSubReport( final SubReport element ) {
    throw new UnsupportedOperationException( "SubReports cannot be started for page headers" );
  }

  public boolean finishBox() {
    if ( inBoxDepth == 1 ) {
      empty &= super.isEmpty();
    }

    super.finishBox();
    inBoxDepth -= 1;
    if ( inBoxDepth == 0 ) {
      slotCounter += 1;
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Finshed a Box: " + slotCounter + " - empty: " + super.isEmpty() );
      }
      return super.isEmpty();
    }
    return empty;
  }

  public boolean isEmpty() {
    if ( inBoxDepth == 0 ) {
      return empty;
    }
    return super.isEmpty();
  }

  public void endSubFlow() {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void addProgressMarkerBox() {
    super.addProgressMarkerBox();
    slotCounter += 1;
  }

  public void addManualPageBreakBox( final long range ) {
    throw new UnsupportedOperationException( "PageBreak sections cannot be started for page headers" );
  }

  public LayoutModelBuilder deriveForStorage( final RenderBox clonedContent ) {
    final FooterLayoutModelBuilder clone = (FooterLayoutModelBuilder) super.deriveForStorage( clonedContent );
    clone.slots = (ArrayList<RenderNode>) slots.clone();
    clone.slots.clear();
    clone.parentBox = clonedContent;
    return clone;
  }

  public LayoutModelBuilder deriveForPageBreak() {
    final FooterLayoutModelBuilder clone = (FooterLayoutModelBuilder) super.deriveForPageBreak();
    clone.slots = (ArrayList<RenderNode>) slots.clone();
    clone.slots.clear();
    return clone;
  }

  public void startSection() {
    empty = true;

    slots.clear();
    slotCounter = 0;
    // check what slots are filled and update the list
    final RenderNode firstChild = parentBox.getFirstChild();
    if ( firstChild instanceof RenderBox ) {
      final RenderBox slottedContent = (RenderBox) firstChild;
      RenderNode box = slottedContent.getFirstChild();
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Start Section: " + parentBox );
        logger.debug( "      Section: " + slottedContent );
        logger.debug( "      Section: " + box );
        logger.debug( "      Key    : " + stateKey );
      }

      boolean sticky = false;
      while ( box != null ) {
        if ( box.getStyleSheet().getBooleanStyleProperty( BandStyleKeys.STICKY ) ) {
          sticky = true;
        }
        if ( sticky ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Added Slot[]: " + box );
            logger.debug( "      Slot[]: " + box.getElementType() );
            logger.debug( "      Slot[]: " + box.getStateKey() );
          }
          slots.add( box );
        }
        box = box.getNext();

      }
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Added Reverse Section: " + slotCounter + " " + slots.size() + " " + firstChild );
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "Clear Footer for new print." );
    }
    parentBox.clear();
    super.startSection();
  }

  public void endSection() {
    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      super.legacyFlagNotEmpty();
    }
    super.endSection();

    /**
     * DOCMARK: This is a incomplete fix for PRD-3620 - this fix needs some work as the layouter code has changed
     * significantly since PRD-3.9 and currently does not behave exactly like the old version.
     *
     * To make sticky page-footers behave correctly, we need to ensure that progress-marker are not merged and that
     * empty bands produce exactly one progress marker.
     */
    if ( logger.isDebugEnabled() ) {
      logger.debug( "Slot counter: " + slotCounter + " " + slots.size() );
      for ( int i = 0; i < slots.size(); i++ ) {
        final RenderNode renderNode = slots.get( i );
        logger.debug( "Slots[" + i + "]: " + renderNode );
        logger.debug( "     [" + i + "]: " + renderNode.getStateKey() );
      }
    }
    // this is not correct ... we should insert the new band before the old one ..
    final RenderNode firstChild = parentBox.getFirstChild();
    if ( slotCounter < slots.size()
        && ( firstChild.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final ArrayList<RenderNode> childsAdded = new ArrayList<RenderNode>();

      // Store the added children until we need them ..
      final RenderBox sectionBox = (RenderBox) firstChild;
      RenderNode child = sectionBox.getFirstChild();
      while ( child != null ) {
        final RenderNode next = child.getNext();
        sectionBox.remove( child );
        childsAdded.add( child );
        if ( logger.isDebugEnabled() ) {
          logger.debug( "New[" + "]: " + child );
        }
        child = next;
      }

      sectionBox.clear();

      // first insert the saved ones ...
      for ( int i = slots.size() - slotCounter - 1; i >= 0; i-- ) {
        final RenderNode node = slots.get( i );
        final RenderNode derived = node.derive( true );

        if ( logger.isDebugEnabled() ) {
          logger.debug( "Rescued[" + i + "]: " + slots.get( i ) );
        }
        sectionBox.addGeneratedChild( derived );
      }

      for ( int i = 0; i < childsAdded.size(); i++ ) {
        final RenderNode node = childsAdded.get( i );
        if ( logger.isDebugEnabled() ) {
          logger.debug( "New[" + "]: " + node );
        }
        sectionBox.addGeneratedChild( node );
      }
    }
  }

  public InstanceID createSubflowPlaceholder( final ReportElement element ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void startSubFlow( final InstanceID insertationPoint ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void startSubFlow( final ReportElement element ) {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }

  public void suspendSubFlow() {
    throw new UnsupportedOperationException( "SubReport sections cannot be started for page headers" );
  }
}
