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

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;
import java.util.Locale;

public abstract class AbstractReportElementDragHandler implements ReportElementDragHandler {
  protected static final Float DEFAULT_WIDTH = new Float( 100 );
  protected static final Float DEFAULT_HEIGHT = new Float( 20 );

  private DndElementOverlay representation;

  public AbstractReportElementDragHandler() {
    representation = new DndElementOverlay();
  }

  protected DndElementOverlay getRepresentation() {
    return representation;
  }

  public int dragStarted( final DropTargetDragEvent event,
                          final ReportElementEditorContext dragContext,
                          final ElementMetaData elementMetaData,
                          final String fieldName ) {
    final Container representationContainer = dragContext.getRepresentationContainer();
    final ReportDocumentContext renderContext = dragContext.getRenderContext();
    final Point pos = event.getLocation();
    final Point2D point = dragContext.normalize( pos );
    if ( point.getX() < 0 || point.getY() < 0 ) {
      representationContainer.removeAll();
      return DnDConstants.ACTION_NONE;
    }

    if ( isFilteredDropZone( event, dragContext, elementMetaData, point ) ) {
      representationContainer.removeAll();
      return DnDConstants.ACTION_NONE;
    }
    representation.setZoom( renderContext.getZoomModel().getZoomAsPercentage() );
    representation.setVisible( true );
    representation.setText( elementMetaData.getDisplayName( Locale.getDefault() ) );
    representation.setLocation( pos.x, pos.y );
    representation.setSize( 100, 20 );
    representationContainer.removeAll();
    representationContainer.add( representation );
    return DnDConstants.ACTION_COPY;
  }

  protected boolean isFilteredDropZone( final DropTargetEvent event,
                                        final ReportElementEditorContext dragContext,
                                        final ElementMetaData elementMetaData,
                                        final Point2D point ) {
    return false;
  }

  public int dragUpdated( final DropTargetDragEvent event,
                          final ReportElementEditorContext dragContext,
                          final ElementMetaData elementMetaData,
                          final String fieldName ) {
    return dragStarted( event, dragContext, elementMetaData, fieldName );
  }

  public void dragAborted( final DropTargetEvent event,
                           final ReportElementEditorContext dragContext ) {
    final Container representationContainer = dragContext.getRepresentationContainer();
    representationContainer.removeAll();
  }


  public void drop( final DropTargetDropEvent event,
                    final ReportElementEditorContext dragContext,
                    final ElementMetaData elementMetaData,
                    final String fieldName ) {
    try {
      final Point2D point = dragContext.normalize( event.getLocation() );
      if ( isFilteredDropZone( event, dragContext, elementMetaData, point ) ) {
        event.dropComplete( false );
        return;
      }

      final Band band = getInsertionBand( event, dragContext, point );
      if ( band == null ) {
        event.dropComplete( false );
        return;
      }

      final ReportDocumentContext context = dragContext.getRenderContext();
      final Element visualElement = createElement( elementMetaData, fieldName, context );
      final ElementStyleSheet styleSheet = visualElement.getStyle();

      final double parentX = getParentX( band );
      final double parentY = getParentY( band );

      styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( Math.max( 0, point.getX() - parentX ) ) );
      styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( Math.max( 0, point.getY() - parentY ) ) );

      postProcessDrop( visualElement, band, dragContext, point );

      dragContext.getRenderContext().getSelectionModel().setSelectedElements( new Object[] { visualElement } );

      event.acceptDrop( DnDConstants.ACTION_COPY );
      getRepresentation().setVisible( false );
      dragContext.getRepresentationContainer().removeAll();
      event.dropComplete( true );
    } catch ( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      dragContext.getRepresentationContainer().removeAll();
      event.dropComplete( false );
    }
  }

  protected void postProcessDrop( final Element visualElement,
                                  final Band target,
                                  final ReportElementEditorContext dragContext,
                                  final Point2D point ) {
    final ReportDocumentContext context = dragContext.getRenderContext();
    final UndoManager undo = context.getUndo();
    undo.addChange( Messages.getString( "DefaultReportElementDragHandler.AddElementUndoEntry" ),
      new ElementEditUndoEntry( target.getObjectID(), target.getElementCount(), null, visualElement ) );
    target.addElement( visualElement );
  }

  protected abstract Element createElement( final ElementMetaData elementMetaData,
                                            final String fieldName,
                                            final ReportDocumentContext context ) throws InstantiationException;


  protected Band getInsertionBand( final DropTargetDropEvent event,
                                   final ReportElementEditorContext dragContext,
                                   final Point2D point ) {
    final Element elementForLocation = dragContext.getElementForLocation( point, false );
    Band band;
    if ( elementForLocation instanceof Band ) {
      band = (Band) elementForLocation;
    } else if ( elementForLocation != null ) {
      band = elementForLocation.getParent();
    } else {
      band = null;
    }

    if ( band == null ) {
      final Element defaultEntry = dragContext.getDefaultElement();
      if ( defaultEntry instanceof Band == false ) {
        event.rejectDrop();
        dragContext.getRepresentationContainer().removeAll();
        return null;
      }
      band = (Band) defaultEntry;
    }
    return band;
  }

  protected double getParentX( final Section band ) {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData( band );
    if ( data.getLayoutAge() == -1 ) {
      return getParentX( band.getParentSection() );
    }
    return StrictGeomUtility.toExternalValue( data.getX() );
  }

  protected double getParentY( final Section band ) {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData( band );
    if ( data.getLayoutAge() == -1 ) {
      return getParentY( band.getParentSection() );
    }
    return StrictGeomUtility.toExternalValue( data.getY() );
  }

}
