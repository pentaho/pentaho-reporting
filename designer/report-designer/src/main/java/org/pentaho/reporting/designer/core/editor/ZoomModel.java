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


package org.pentaho.reporting.designer.core.editor;

import javax.swing.event.EventListenerList;

/**
 * User: Martin Date: 03.02.2006 Time: 19:28:50
 */
public class ZoomModel {
  private EventListenerList zoomModelListeners;
  private float zoomFactor;

  public ZoomModel() {
    zoomModelListeners = new EventListenerList();
    zoomFactor = 1f;
  }

  public float getZoomAsPercentage() {
    return zoomFactor;
  }

  public void setZoomAsPercentage( final float zoomFactor ) {
    final float oldZoomFactor = this.zoomFactor;
    this.zoomFactor = zoomFactor;

    if ( oldZoomFactor != zoomFactor ) {
      notifyListeners();
    }
  }


  public void addZoomModelListener( final ZoomModelListener zoomModelListener ) {
    zoomModelListeners.add( ZoomModelListener.class, zoomModelListener );
  }


  public void removeZoomModelListener( final ZoomModelListener zoomModelListener ) {
    zoomModelListeners.remove( ZoomModelListener.class, zoomModelListener );
  }


  private void notifyListeners() {
    final ZoomModelListener[] listeners = zoomModelListeners.getListeners( ZoomModelListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final ZoomModelListener listener = listeners[ i ];
      listener.zoomFactorChanged();
    }
  }
}
