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

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;

/**
 * @author David Kincade
 */
public class JdbcQueryDesignerDialog extends CommonDialog {
  private class PreviewButtonAction extends AbstractAction {
    private PreviewButtonAction() {
      putValue( Action.NAME, getBundleSupport().getString( "JdbcDataSourceDialog.Preview" ) );
    }

    public void actionPerformed( final ActionEvent arg0 ) {
      try {
        final String query = getQuery();
        final DataPreviewDialog dialog = new DataPreviewDialog( JdbcQueryDesignerDialog.this );

        MasterReport report = (MasterReport) designTimeContext.getReport();
        ReportParameterDefinition parameters = null;

        if ( report != null ) {
          parameters = report.getParameterDefinition();
        }

        dialog.showData( new JdbcPreviewWorker( new SimpleSQLReportDataFactory( getConnectionDefinition() ), query, 0, 0, parameters ) );
      } catch ( Exception e ) {
        log.warn( "QueryPanel.actionPerformed ", e );
        if ( designTimeContext != null ) {
          designTimeContext.userError( e );
        }
      }
    }
  }

  private static final Log log = LogFactory.getLog( JdbcQueryDesignerDialog.class );
  private QueryBuilder queryBuilder;
  private ConnectionProvider connectionProvider;
  private ResourceBundleSupport bundleSupport;
  private DesignTimeContext designTimeContext;

  public JdbcQueryDesignerDialog( final JDialog owner, final QueryBuilder queryBuilder ) {
    super( owner );

    if ( queryBuilder == null ) {
      throw new NullPointerException();
    }

    setModal( true );
    bundleSupport = new ResourceBundleSupport( Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader( JdbcDataSourceModule.class ) );
    setTitle( bundleSupport.getString( "JdbcDataSourceDialog.SQLLeonardoTitle" ) );
    this.queryBuilder = queryBuilder;

    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    init();
  }

  protected void performInitialResize() {
    setSize( 800, 600 );
    setLocationRelativeTo( getParent() );
  }

  protected String getDialogId() {
    return "JdbcDataSourceEditor.QueryDesigner";
  }

  protected Component createContentPane() {
    return queryBuilder;
  }

  protected Action[] getExtraActions() {
    return new Action[]{new PreviewButtonAction()};
  }

  public String designQuery( final DesignTimeContext designTimeContext,
                            final ConnectionProvider jndiSource,
                            final String schema, final String query ) {
    this.designTimeContext = designTimeContext;
    this.connectionProvider = jndiSource;

    try {
      final QueryModel queryModel = SQLParser.toQueryModel( query );
      queryBuilder.setQueryModel( queryModel );
    } catch ( Exception e1 ) {
      log.warn( "QueryPanel.actionPerformed ", e1 );
    }

    try {
      if ( schema != null ) {
        final QueryModel qm = queryBuilder.getQueryModel();
        qm.setSchema( schema );
        queryBuilder.setQueryModel( qm );
      }
    } catch ( Exception e1 ) {
      log.warn( "QueryPanel.actionPerformed ", e1 );
    }

    if ( performEdit() ) {
      return getQuery();
    }
    return null;
  }

  protected ConnectionProvider getConnectionDefinition() {
    return connectionProvider;
  }

  protected String getQuery() {
    return queryBuilder.getQueryModel().toString( true );
  }

  protected ResourceBundleSupport getBundleSupport() {
    return bundleSupport;
  }
}
