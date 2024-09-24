/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.editor.report.drag;

import java.awt.geom.Point2D;

public interface MouseDragOperation {
  public void update( final Point2D normalizedPoint, final double zoomFactor );

  public void finish();
}
