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

import org.apache.pekko.dispatch.OnFailure;
import org.apache.pekko.dispatch.OnSuccess;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeDataSchemaModelChangeTracker;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import scala.PartialFunction;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class AsynchronousDataSchemaManager implements DataSchemaManager, ReportModelListener {
  private final MasterReport masterReport;
  private final AbstractReportDefinition report;
  private final ArrayList<ChangeListener> listeners;
  private final DefaultDesignTimeDataSchemaModelChangeTracker changeTracker;
  private ContextAwareDataSchemaModel model;
  private QueryMetaDataActor actor;

  public AsynchronousDataSchemaManager( final MasterReport masterReport,
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

  public synchronized ContextAwareDataSchemaModel getModel() {
    if ( model == null ) {
      model = new TemporaryDataSchemaModel( masterReport, report );
      startQueryModel();
    }
    return model;
  }

  public synchronized void nodeChanged( final ReportModelEvent event ) {
    if ( changeTracker.isReportChanged() ) {
      model = new TemporaryDataSchemaModel( masterReport, report );
      startQueryModel();
    }
  }

  private synchronized void startQueryModel() {
    if ( this.actor == null ) {
      this.actor = ActorSystemHost.INSTANCE.createActor( QueryMetaDataActor.class, QueryMetaDataActorImpl.class );
    }
    Future<ContextAwareDataSchemaModel> retrieve = this.actor.retrieve( masterReport, report );
    // IntelliJ does not know how to handle this construct, thinks it is not valid.
    retrieve.foreach( new SuccessHandler(), ActorSystemHost.INSTANCE.getSystem().dispatcher() );
    retrieve.failed().foreach( new FailureHandler(), ActorSystemHost.INSTANCE.getSystem().dispatcher() );
  }

  public void close() {
    synchronized ( this ) {
      ActorSystemHost.INSTANCE.stopNow( actor );
      actor = null;
    }
  }

  protected void fireChangeEvent() {
    if ( listeners.isEmpty() ) {
      return;
    }

    final ChangeEvent event = new ChangeEvent( this );
    for ( final ChangeListener listener : listeners ) {
      listener.stateChanged( event );
    }
  }

  private void processResultOnEDT( final Runnable r ) {
    synchronized ( AsynchronousDataSchemaManager.this ) {
      changeTracker.updateChangeTrackers();
    }
    SwingUtilities.invokeLater( r );
  }

  private class SuccessHandler extends OnSuccess<ContextAwareDataSchemaModel>
    implements PartialFunction<ContextAwareDataSchemaModel, BoxedUnit> {
    private SuccessHandler() {
    }

    public void onSuccess( final ContextAwareDataSchemaModel result ) throws Throwable {
      processResultOnEDT( new SuccessTask( result ) );
    }
  }

  private class SuccessTask implements Runnable {
    private ContextAwareDataSchemaModel model;

    private SuccessTask( final ContextAwareDataSchemaModel model ) {
      this.model = model;
    }

    public void run() {
      AsynchronousDataSchemaManager.this.model = model;
      fireChangeEvent();
    }
  }

  private class FailureHandler extends OnFailure
    implements PartialFunction<Throwable, BoxedUnit> {
    public void onFailure( final Throwable failure ) throws Throwable {
      processResultOnEDT( new FailureTask( failure ) );
    }
  }

  private static class FailureTask implements Runnable {
    private Throwable t;

    private FailureTask( final Throwable t ) {
      ArgumentNullException.validate( "t", t );
      this.t = t;
    }

    public void run() {
      UncaughtExceptionsModel.getInstance().addException( t );
    }
  }
}
