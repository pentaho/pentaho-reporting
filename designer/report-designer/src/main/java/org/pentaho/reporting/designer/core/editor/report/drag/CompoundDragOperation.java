/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.editor.report.drag;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CompoundDragOperation implements MouseDragOperation {
  private ArrayList<MouseDragOperation> operations;

  public CompoundDragOperation() {
    operations = new ArrayList<MouseDragOperation>();
  }

  public void add( final MouseDragOperation operation ) {
    operations.add( operation );
  }

  public void update( final Point2D normalizedPoint, final double zoomFactor ) {
    for ( int i = 0; i < operations.size(); i++ ) {
      final MouseDragOperation operation = operations.get( i );
      operation.update( normalizedPoint, zoomFactor );
    }
  }

  public void finish() {
    for ( int i = 0; i < operations.size(); i++ ) {
      final MouseDragOperation operation = operations.get( i );
      operation.finish();
    }
  }

}
