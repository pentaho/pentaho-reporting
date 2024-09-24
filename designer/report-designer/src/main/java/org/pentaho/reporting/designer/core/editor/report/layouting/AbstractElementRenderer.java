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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.DesignerPageDrawable;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Thomas Morgner
 */
public abstract class AbstractElementRenderer implements ElementRenderer {
  private class VisualHeightUpdateListener implements ReportModelListener {
    private VisualHeightUpdateListener() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() != element ) {
        if ( event.getParameter() instanceof AttributeChange ) {
          final AttributeChange attributeChange = (AttributeChange) event.getParameter();
          if ( ReportDesignerBoot.DESIGNER_NAMESPACE.equals( attributeChange.getNamespace() ) &&
            ReportDesignerBoot.VISUAL_HEIGHT.equals( attributeChange.getName() ) ) {
            fireChangeEvent();
          }
        }
      }
    }
  }

  private class SharedLayoutUpdateHandler implements ChangeListener {
    private SharedLayoutUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      refreshLayoutFromSharedRenderer();
    }
  }

  private final AbstractElementRenderer.SharedLayoutUpdateHandler sharedLayoutUpdateHandler;

  private SharedElementRenderer sharedRenderer;
  private Section element;
  private ReportDocumentContext reportRenderContext;
  private EventListenerList listenerList;
  private Rectangle2D computedBounds;
  private BreakPositionsList verticalEdgePositions;
  private DesignerPageDrawable logicalPageDrawable;
  private ResourceManager resourceManager;
  private Map<InstanceID, Element> elementsById;

  protected AbstractElementRenderer( final Section element,
                                     final ReportDocumentContext reportRenderContext ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }

    this.sharedLayoutUpdateHandler = new AbstractElementRenderer.SharedLayoutUpdateHandler();

    this.sharedRenderer = reportRenderContext.getSharedRenderer();
    this.sharedRenderer.addChangeListener( sharedLayoutUpdateHandler );

    this.element = element;
    this.reportRenderContext = reportRenderContext;
    this.elementsById = new HashMap<InstanceID, Element>();
    this.listenerList = new EventListenerList();
    this.verticalEdgePositions = new BreakPositionsList();
    this.resourceManager = reportRenderContext.getResourceManager();

    reportRenderContext.getReportDefinition().addReportModelListener( new VisualHeightUpdateListener() );

    final Object d = element.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT );
    if ( d instanceof Double == false ) {
      if ( element.getElementType() instanceof ReportHeaderType ) {
        setVisualHeight( Unit.INCH.getDotsPerUnit() * 1.5 );
      } else if ( element.getElementType() instanceof ReportFooterType ) {
        setVisualHeight( Unit.INCH.getDotsPerUnit() * 1.5 );
      } else if ( element.getElementType() instanceof ItemBandType ) {
        setVisualHeight( Unit.INCH.getDotsPerUnit() * 1.5 );
      } else {
        setVisualHeight( Unit.INCH.getDotsPerUnit() );
      }
    }
  }

  public void dispose() {
    sharedRenderer.removeChangeListener( sharedLayoutUpdateHandler );
  }

  public ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  public Section getElement() {
    return element;
  }

  public ElementType getElementType() {
    return element.getElementType();
  }

  public InstanceID getRepresentationId() {
    return element.getObjectID();
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    listenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    listenerList.remove( ChangeListener.class, changeListener );
  }

  public void fireChangeEvent() {
    final ChangeEvent ce = new ChangeEvent( this );
    final ChangeListener[] changeListeners = listenerList.getListeners( ChangeListener.class );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener listener = changeListeners[ i ];
      listener.stateChanged( ce );
    }
  }

  public double getVisualHeight() {
    final Object d = element.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT );
    if ( d instanceof Double ) {
      return (Double) d;
    }
    return 0;
  }

  public void setVisualHeight( final double visualHeight ) {
    if ( visualHeight < 0 ) {
      throw new IllegalArgumentException();
    }
    final double oldHeight = getVisualHeight();
    if ( visualHeight != oldHeight ) {
      this.element.setAttribute
        ( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT, visualHeight, false );
      fireChangeEvent();
    }
  }

  public boolean isHideInLayout() {
    return ModelUtility.isHideInLayoutGui( element );
  }

  public LinealModel getVerticalLinealModel() {
    return ModelUtility.getVerticalLinealModel( element );
  }

  public synchronized double getLayoutHeight() {
    if ( computedBounds == null || sharedRenderer.isLayoutValid() == false ) {
      computedBounds = performLayouting();
    }
    return Math.max( computedBounds.getHeight(), getVisualHeight() );
  }

  public synchronized void invalidateLayout() {
    // Set computedBounds to null to allow performLayouting() to recalculate them.
    computedBounds = null;
  }

  public Rectangle2D getBounds() {
    if ( computedBounds == null || sharedRenderer.isLayoutValid() == false ) {
      computedBounds = performLayouting();
    }
    return new Rectangle2D.Double( 0, computedBounds.getY(), computedBounds.getWidth(),
      Math.max( computedBounds.getHeight(), getVisualHeight() ) );
  }

  public StrictBounds getRootElementBounds() {
    if ( logicalPageDrawable == null ) {
      return new StrictBounds();
    }
    return (StrictBounds) logicalPageDrawable.getRootElementBounds().clone();
  }

  protected Rectangle2D performLayouting() {
    if ( sharedRenderer.performLayouting() ) {
      fireChangeEvent();
      if ( computedBounds == null ) {
        refreshLayoutFromSharedRenderer();
      }
      return computedBounds;
    } else {
      logicalPageDrawable = null;
      fireChangeEvent();
      return new Rectangle2D.Double();
    }
  }

  private void refreshLayoutFromSharedRenderer() {
    final LogicalPageBox pageBox = sharedRenderer.getPageBox();
    if ( pageBox == null ) {
      computedBounds = sharedRenderer.getFallbackBounds();
      return;
    }

    elementsById.clear();
    sharedRenderer.transferLocalLayout( getElement(), elementsById, verticalEdgePositions );
    final OutputProcessorMetaData outputProcessorMetaData = sharedRenderer.getLayouter().getOutputProcessorMetaData();

    logicalPageDrawable = new DesignerPageDrawable( pageBox, outputProcessorMetaData, resourceManager, element );
    final StrictBounds bounds = logicalPageDrawable.getRootElementBounds();
    computedBounds = StrictGeomUtility.createAWTRectangle( 0, bounds.getY(), pageBox.getWidth(), bounds.getHeight() );
    if ( getVisualHeight() < computedBounds.getHeight() ) {
      setVisualHeight( computedBounds.getHeight() );
    }
  }

  public boolean draw( final Graphics2D graphics2D ) {
    // this also computes the pagebox.
    final Rectangle2D bounds1 = getBounds();
    if ( logicalPageDrawable == null ) {
      return false;
    }
    final Graphics2D graphics = (Graphics2D) graphics2D.create();

    graphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    graphics.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

    logicalPageDrawable.draw( graphics, bounds1 );

    graphics.dispose();
    return true;
  }

  public void handleError( final ReportDesignerContext designerContext, final ReportDocumentContext reportContext ) {
    if ( sharedRenderer.isMigrationError() ) {
      SwingUtilities.invokeLater
        ( new MigrateReportTask( designerContext, reportContext, sharedRenderer.getMinimumVersionNeeded() ) );
      sharedRenderer.clearMigrationError();
    }
  }

  public BreakPositionsList getHorizontalEdgePositions() {
    return sharedRenderer.getHorizontalEdgePositions();
  }

  public long[] getHorizontalEdgePositionKeys() {
    return getHorizontalEdgePositions().getKeys();
  }

  public BreakPositionsList getVerticalEdgePositions() {
    return verticalEdgePositions;
  }

  public Element[] getElementsAt( final double x, final double y, final double width, final double height ) {
    if ( logicalPageDrawable == null ) {
      return new Element[ 0 ];
    }

    final RenderNode[] nodes = logicalPageDrawable.getNodesAt( x, y, width, height, null, null );
    if ( nodes.length == 0 ) {
      return new Element[ 0 ];
    }

    final LinkedHashSet<Element> elements = new LinkedHashSet<Element>( nodes.length );
    for ( int i = 0; i < nodes.length; i++ ) {
      final RenderNode node = nodes[ i ];
      final Element reportElement = elementsById.get( node.getInstanceId() );
      if ( reportElement != null ) {
        elements.add( reportElement );
      }
    }
    return elements.toArray( new Element[ elements.size() ] );
  }

  public Element[] getElementsAt( final double x, final double y ) {
    if ( logicalPageDrawable == null ) {
      return new Element[ 0 ];
    }

    final RenderNode[] nodes = logicalPageDrawable.getNodesAt( x, y, null, null );
    if ( nodes.length == 0 ) {
      return new Element[ 0 ];
    }

    final LinkedHashSet<Element> elements = new LinkedHashSet<Element>( nodes.length );
    for ( int i = 0; i < nodes.length; i++ ) {
      final RenderNode node = nodes[ i ];
      final Element reportElement = elementsById.get( node.getInstanceId() );
      if ( reportElement != null ) {
        elements.add( reportElement );
      }
    }
    return elements.toArray( new Element[ elements.size() ] );
  }

  protected DesignerPageDrawable getLogicalPageDrawable() {
    return logicalPageDrawable;
  }
}
