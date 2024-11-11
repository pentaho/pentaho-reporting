/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

/**
 * Overlay renderers draw additional information on the canvas. This drawing happens after the complete logical page has
 * been rendered and always happens on the natural zoom level (where one point equals one pixel).
 *
 * @author Thomas Morgner.
 */
public interface OverlayRenderer {
  public void validate( final ReportDocumentContext context, final double zoomFactor, Point2D sectionOffset );

  public void draw( final Graphics2D graphics, final Rectangle2D bounds, final ImageObserver obs );
}
