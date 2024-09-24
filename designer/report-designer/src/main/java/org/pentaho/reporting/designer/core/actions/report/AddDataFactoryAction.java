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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChange;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

public class AddDataFactoryAction extends AbstractReportContextAction {
  private DataFactoryMetaData dataSourcePlugin;

  public AddDataFactoryAction( final DataFactoryMetaData dataSourcePlugin ) {
    this.dataSourcePlugin = dataSourcePlugin;
    putValue( Action.NAME, dataSourcePlugin.getDisplayName( Locale.getDefault() ) );
    putValue( Action.SHORT_DESCRIPTION, dataSourcePlugin.getDescription( Locale.getDefault() ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final DataSourcePlugin editor = dataSourcePlugin.createEditor();
    if ( editor == null ) {
      return;
    }

    final DefaultDataFactoryChangeRecorder recorder = new DefaultDataFactoryChangeRecorder();
    final ReportDesignerDesignTimeContext designTimeContext =
      new ReportDesignerDesignTimeContext( getReportDesignerContext() );
    final DataFactory dataFactory = editor.performEdit( designTimeContext, null, null, recorder );
    if ( dataFactory == null ) {
      return;
    }

    final Window parentWindow = designTimeContext.getParentWindow();
    try {
      parentWindow.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

      addDataFactory( activeContext, dataFactory, recorder.getChanges() );
    } finally {
      final ContextAwareDataSchemaModel dataSchemaModel = activeContext.getReportDataSchemaModel();
      if ( dataSchemaModel.getDataFactoryException() != null ) {
        UncaughtExceptionsModel.getInstance().addException( dataSchemaModel.getDataFactoryException() );
      }

      parentWindow.setCursor( Cursor.getDefaultCursor() );
    }
  }

  public static void addDataFactory( final ReportDocumentContext activeContext,
                                     final DataFactory dataFactory,
                                     final DataFactoryChange[] dataFactoryChanges ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }

    final AbstractReportDefinition report = activeContext.getReportDefinition();
    final DataFactory originalDataFactory = report.getDataFactory();
    if ( originalDataFactory == null ) {
      throw new IllegalStateException( "A report in design-mode should have its data-factory normalized." );
    }

    final String queryAttribute =
      (String) report.getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY );
    if ( StringUtils.isEmpty( queryAttribute ) && dataFactory.getQueryNames().length > 0 ) {
      final String queryName = dataFactory.getQueryNames()[ 0 ];
      report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, queryName );
      activeContext.getUndo().addChange( ActionMessages.getString( "AddDataFactoryAction.SetQuery.UndoText" ),
        new AttributeEditUndoEntry( report.getObjectID(),
          AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY,
          queryAttribute, queryName ) );
    }


    if ( isLegacyDefaultDataFactory( originalDataFactory ) ) {
      final CompoundDataFactory compoundDataFactory = CompoundDataFactory.normalize( dataFactory );
      DefaultDataFactoryChangeRecorder.applyChanges( compoundDataFactory, dataFactoryChanges );
      report.setDataFactory( compoundDataFactory );
      activeContext.getUndo().addChange( ActionMessages.getString( "AddDataFactoryAction.UndoText" ),
        new DataSourceEditUndoEntry( 0, null, dataFactory.derive() ) );
    } else {
      final CompoundDataFactory reportDf = CompoundDataFactory.normalize( originalDataFactory );
      DefaultDataFactoryChangeRecorder.applyChanges( reportDf, dataFactoryChanges );
      final int position = reportDf.size();
      reportDf.add( dataFactory );
      activeContext.getUndo().addChange( ActionMessages.getString( "AddDataFactoryAction.UndoText" ),
        new DataSourceEditUndoEntry( position, null, dataFactory.derive() ) );
      report.setDataFactory( reportDf );
    }
  }

  private static boolean isLegacyDefaultDataFactory( final DataFactory dataFactory ) {
    final String[] queryNames = dataFactory.getQueryNames();
    if ( queryNames.length == 0 ) {
      return true;
    }

    if ( queryNames.length != 1 ) {
      return false;
    }
    if ( "default".equals( queryNames[ 0 ] ) ) {
      try {
        // check for legacy-built-in defaults and selectively ignore them ..
        final TableModel tableModel = dataFactory.queryData( "default", null );
        if ( tableModel.getRowCount() == 0 && tableModel.getColumnCount() == 0 ) {
          return true;
        }
      } catch ( final Exception e ) {
        return false;
      }
    }
    return false;
  }
}
