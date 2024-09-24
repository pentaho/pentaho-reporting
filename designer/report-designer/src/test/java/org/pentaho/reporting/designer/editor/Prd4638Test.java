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

package org.pentaho.reporting.designer.editor;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.styles.StyleEditorPanel;
import org.pentaho.reporting.designer.core.editor.styles.StyleTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.concurrent.Executor;

public class Prd4638Test extends TestCase {
  public Prd4638Test() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEventTransition() throws ReportDataFactoryException {
    // the report we use for the simulated edit
    final MasterReport report = new MasterReport();
    final Element elementX = new Element();
    elementX.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );

    report.getReportHeader().addElement( elementX );

    // setup a dummy environment that simulates the setup of a full prd
    final ReportDesignerContext rdc = new TestReportDesignerContext();
    rdc.addMasterReport( report );
    final ReportRenderContext activeContext = rdc.getReportRenderContext( 0 );
    rdc.setActiveDocument( activeContext );
    assertNotNull( activeContext );

    // use the style editor panel in isolation to make the whole process debuggable ..
    final TestStyleEditorPanel sep = new TestStyleEditorPanel();
    sep.setReportDesignerContext( rdc );

    assertEquals( 0, sep.getData().length );
    final Element element = report.getReportHeader().getElement( 0 );
    assertTrue( activeContext.getSelectionModel().add( element ) );
    assertEquals( 1, sep.getData().length );
    assertEquals( new Float( 0 ), findStyleValue( sep, ElementStyleKeys.POS_Y ) );

    sep.setRefreshDataCalled( false );
    final MonitorTableListener monitorTableListener = new MonitorTableListener();

    sep.getDataModel().addTableModelListener( monitorTableListener );
    element.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 10f );
    assertTrue( sep.isRefreshDataCalled() );
    assertTrue( monitorTableListener.isEventReceived() );

    assertEquals( new Float( 10 ), findStyleValue( sep, ElementStyleKeys.POS_Y ) );
  }

  private Object findStyleValue( final TestStyleEditorPanel sep,
                                 final StyleKey styleKey ) {
    final StyleTableModel dataModel = sep.getDataModel();
    for ( int i = 0; i < dataModel.getRowCount(); i += 1 ) {
      final Object valueAt = dataModel.getValueAt( i, 0 );
      if ( valueAt instanceof GroupedName == false ) {
        continue;
      }

      final GroupedName name = (GroupedName) valueAt;
      final StyleMetaData sm = (StyleMetaData) name.getMetaData();
      if ( sm != null ) {
        if ( styleKey.equals( sm.getStyleKey() ) ) {
          return dataModel.getValueAt( i, 2 );
        }
      }
    }
    return null;
  }

  public static class MonitorTableListener implements TableModelListener {
    private boolean eventReceived;

    public MonitorTableListener() {
    }

    public boolean isEventReceived() {
      return eventReceived;
    }

    public void tableChanged( final TableModelEvent e ) {
      eventReceived = true;
    }
  }

  public static class TestStyleEditorPanel extends StyleEditorPanel {
    private boolean refreshDataCalled;

    public TestStyleEditorPanel() {
      getDataModel().setSynchronous( true );
    }

    protected StyleTableModel createDataModel() {
      return new StyleTableModel( new InlineExecutor() );
    }

    public StyleTableModel getDataModel() {
      return super.getDataModel();
    }

    public boolean isRefreshDataCalled() {
      return refreshDataCalled;
    }

    public void setRefreshDataCalled( final boolean refreshDataCalled ) {
      this.refreshDataCalled = refreshDataCalled;
    }

    protected void refreshData() {
      this.refreshDataCalled = true;
      super.refreshData();
    }
  }

  public static class InlineExecutor implements Executor {
    public InlineExecutor() {
    }

    public void execute( final Runnable command ) {
      command.run();
    }
  }
}

