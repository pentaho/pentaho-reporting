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


package org.pentaho.reporting.designer.core.model.data;

import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeDataSchemaModelChangeTracker;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class SynchronousDataSchemaManager implements DataSchemaManager, ReportModelListener {
  private final MasterReport masterReport;
  private final AbstractReportDefinition report;
  private final ArrayList<ChangeListener> listeners;
  private ContextAwareDataSchemaModel model;
  private DefaultDesignTimeDataSchemaModelChangeTracker changeTracker;
  private boolean isHandlingChange;

  public SynchronousDataSchemaManager( final MasterReport masterReport,
                                       final AbstractReportDefinition report ) {
    ArgumentNullException.validate( "masterReport", masterReport );
    ArgumentNullException.validate( "report", report );

    this.listeners = new ArrayList<ChangeListener>();
    this.masterReport = masterReport;
    this.report = report;
    this.report.addReportModelListener( this );
    this.changeTracker = new DefaultDesignTimeDataSchemaModelChangeTracker( report );
  }

  public void addChangeListener( final ChangeListener l ) {
    ArgumentNullException.validate( "l", l );

    this.listeners.add( l );
  }

  public void removeChangeListener( final ChangeListener l ) {
    ArgumentNullException.validate( "l", l );

    this.listeners.remove( l );
  }

  public void close() {
  }

  public synchronized void nodeChanged( final ReportModelEvent event ) {
    if ( isHandlingChange ) {
      return;
    }
    if ( changeTracker.isReportChanged() ) {
      try {
        fireChangeEvent();
      } finally {
        changeTracker.updateChangeTrackers();
      }
    }
  }

  protected void fireChangeEvent() {
    if ( listeners.isEmpty() ) {
      return;
    }

    try {
      isHandlingChange = true;
      ChangeEvent event = new ChangeEvent( this );
      for ( final ChangeListener listener : listeners ) {
        listener.stateChanged( event );
      }
    } finally {
      isHandlingChange = false;
    }
  }

  public ContextAwareDataSchemaModel getModel() {
    if ( model == null ) {
      model = new ReportDataSchemaModel( masterReport, report );
    }
    return model;
  }
}
