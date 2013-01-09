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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.elements;

import java.awt.Container;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.DndElementOverlay;
import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BandReportElementDragHandler implements ReportElementDragHandler
{
  protected static final Float DEFAULT_WIDTH = new Float(100);
  protected static final Float DEFAULT_HEIGHT = new Float(20);

  private DndElementOverlay representation;

  public BandReportElementDragHandler()
  {
    representation = new DndElementOverlay();
  }

  public int dragStarted(final DropTargetDragEvent event,
                         final ReportElementEditorContext dragContext,
                         final ElementMetaData elementMetaData,
                         final String fieldName)
  {
    final Container representationContainer = dragContext.getRepresentationContainer();
    final ReportRenderContext renderContext = dragContext.getRenderContext();
    final Point pos = event.getLocation();
    final Point2D point = dragContext.normalize(pos);
    if (point.getX() < 0 || point.getY() < 0)
    {
      representationContainer.removeAll();
      return DnDConstants.ACTION_NONE;
    }

    representation.setZoom(renderContext.getZoomModel().getZoomAsPercentage());
    representation.setVisible(true);
    representation.setText(elementMetaData.getDisplayName(Locale.getDefault()));
    representation.setLocation(pos.x, pos.y);
    representation.setSize(representation.getMinimumSize());
    representationContainer.removeAll();
    representationContainer.add(representation);
    return DnDConstants.ACTION_COPY;
  }

  public int dragUpdated(final DropTargetDragEvent event,
                         final ReportElementEditorContext dragContext,
                         final ElementMetaData elementMetaData,
                         final String fieldName)
  {
    return dragStarted(event, dragContext, elementMetaData, fieldName);
  }

  public void dragAborted(final DropTargetEvent event,
                          final ReportElementEditorContext dragContext)
  {
    final Container representationContainer = dragContext.getRepresentationContainer();
    representationContainer.removeAll();
  }

  public void drop(final DropTargetDropEvent event,
                   final ReportElementEditorContext dragContext,
                   final ElementMetaData elementMetaData,
                   final String fieldName)
  {
    try
    {
      final ElementType type = elementMetaData.create();
      final Element visualElement = new Band();
      type.configureDesignTimeDefaults(visualElement, Locale.getDefault());

      final ElementStyleSheet styleSheet = visualElement.getStyle();
      final Point2D point = dragContext.normalize(event.getLocation());
      styleSheet.setStyleProperty(ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH);
      styleSheet.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT);

      final Element elementForLocation = dragContext.getElementForLocation(point, false);
      final Band band;
      if (elementForLocation instanceof Band)
      {
        band = (Band) elementForLocation;
      }
      else if (elementForLocation != null)
      {
        band = elementForLocation.getParent();
      }
      else
      {
        final Element defaultEntry = dragContext.getDefaultElement();
        if (defaultEntry instanceof Band == false)
        {
          event.rejectDrop();
          dragContext.getRepresentationContainer().removeAll();
          return;
        }
        band = (Band) defaultEntry;
      }

      event.acceptDrop(DnDConstants.ACTION_COPY);

      styleSheet.setStyleProperty(ElementStyleKeys.POS_X, new Float(Math.max(0, point.getX() - getParentX(band))));
      styleSheet.setStyleProperty(ElementStyleKeys.POS_Y, new Float(Math.max(0, point.getY() - getParentY(band))));

      final ReportRenderContext context = dragContext.getRenderContext();
      final UndoManager undo = context.getUndo();
      undo.addChange(new ElementEditUndoEntry(band.getObjectID(), band.getElementCount(), null, visualElement));
      band.addElement(visualElement);

      dragContext.getRenderContext().getSelectionModel().setSelectedElements(new Object[]{visualElement});

      representation.setVisible(false);
      dragContext.getRepresentationContainer().removeAll();
      event.dropComplete(true);
    }
    catch (final Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      dragContext.getRepresentationContainer().removeAll();
      event.dropComplete(false);
    }
  }

  private double getParentX(final Band band)
  {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData(band);
    return StrictGeomUtility.toExternalValue(data.getX());
  }

  private double getParentY(final Band band)
  {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData(band);
    return StrictGeomUtility.toExternalValue(data.getY());
  }

}