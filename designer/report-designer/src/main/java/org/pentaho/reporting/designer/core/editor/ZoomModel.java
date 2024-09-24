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
