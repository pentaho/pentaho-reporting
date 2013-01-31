package org.pentaho.reporting.designer.core.editor.report.elements;

import java.awt.Container;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.DndElementOverlay;
import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

abstract public class BaseReportElementDragHandler implements ReportElementDragHandler
{
  protected static final Float DEFAULT_WIDTH = new Float(100);
  protected static final Float DEFAULT_HEIGHT = new Float(20);

  protected DndElementOverlay representation;

  public BaseReportElementDragHandler()
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

    final Element rootBand = findRootBand(dragContext, point);
    if (rootBand instanceof PageHeader ||
        rootBand instanceof PageFooter ||
        rootBand instanceof DetailsHeader ||
        rootBand instanceof DetailsFooter ||
        rootBand instanceof Watermark)
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

  protected Element findRootBand(final ReportElementEditorContext dragContext,
                               final Point2D point)
  {
    Element element = dragContext.getElementForLocation(point, false);
    while (element != null && ((element instanceof RootLevelBand) == false))
    {
      element = element.getParent();
    }

    if (element != null)
    {
      return element;
    }

    return dragContext.getDefaultElement();
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


  protected double getParentX(final Band band)
  {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData(band);
    return StrictGeomUtility.toExternalValue(data.getX());
  }

  protected double getParentY(final Band band)
  {
    final CachedLayoutData data = ModelUtility.getCachedLayoutData(band);
    return StrictGeomUtility.toExternalValue(data.getY());
  }
}
