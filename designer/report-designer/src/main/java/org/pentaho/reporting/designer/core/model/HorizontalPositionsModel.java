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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class HorizontalPositionsModel {
  private HorizontalPositionsList backend;
  private EventListenerList listenerList;

  protected HorizontalPositionsModel() {
    this.backend = new HorizontalPositionsList();
    this.listenerList = new EventListenerList();
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    this.listenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    this.listenerList.remove( ChangeListener.class, changeListener );
  }

  public int size() {
    return backend.size();
  }

  public void fireChangeEvent() {
    final ChangeListener[] changeListeners = this.listenerList.getListeners( ChangeListener.class );
    if ( changeListeners == null ) {
      return;
    }
    final ChangeEvent changeEvent = new ChangeEvent( this );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener changeListener = changeListeners[ i ];
      changeListener.stateChanged( changeEvent );
    }
  }

  public synchronized long[] getBreaks() {
    return backend.getKeys();
  }

  public boolean add( final long[] breaks, final long age ) {
    boolean retval = false;
    for ( int i = 0; i < breaks.length; i++ ) {
      final long aBreak = breaks[ i ];
      if ( backend.add( aBreak, age ) ) {
        retval = true;
      }
    }
    return retval;
  }

  public boolean clear( final long age ) {
    return backend.removeAll( age );
  }

  public static HorizontalPositionsModel getHorizontalPositionsModel( final ReportRenderContext reportContext ) {
    final AbstractReportDefinition rootBand = reportContext.getReportDefinition();
    final Object maybeLinealModel = rootBand.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_POSITIONS_MODEL_OBJECT );
    if ( maybeLinealModel instanceof HorizontalPositionsModel ) {
      return (HorizontalPositionsModel) maybeLinealModel;
    }

    final HorizontalPositionsModel linealModel = new HorizontalPositionsModel();
    rootBand.setAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_POSITIONS_MODEL_OBJECT, linealModel, false );
    return linealModel;
  }
}
