package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.PerformanceTestSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.StopWatch;

public class Prd4606IT extends TestCase {
  public Prd4606IT() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testValidTable() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0001.prpt" );
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SequenceDataFactory sequenceDf = (SequenceDataFactory) dataFactory.getReference( 0 );
    PerformanceTestSequence sequence = (PerformanceTestSequence) sequenceDf.getSequence( "Query 1" );
    assertEquals( 10, sequence.getParameter( "limit" ) );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode[] tables = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    RenderNode[] tableRows = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );

    assertEquals( 1, tables.length );
    assertEquals( StrictGeomUtility.toInternalValue( 240 ), tables[0].getHeight() );
    TableRenderBox table = (TableRenderBox) tables[0];
    assertEquals( 1, table.getColumnModel().getColumnCount() );

    assertEquals( 12, tableRows.length );
    for ( RenderNode tableRow : tableRows ) {
      assertEquals( StrictGeomUtility.toInternalValue( 20 ), tableRow.getHeight() );
    }
  }

  public void testInvalidTable() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0002.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode[] tables = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    RenderNode[] tableRows = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    assertEquals( 1, tables.length );
    assertEquals( StrictGeomUtility.toInternalValue( 240 ), tables[0].getHeight() );
    TableRenderBox table = (TableRenderBox) tables[0];
    assertEquals( 2, table.getColumnModel().getColumnCount() );

    assertEquals( 12, tableRows.length );
    for ( RenderNode tableRow : tableRows ) {
      assertEquals( StrictGeomUtility.toInternalValue( 20 ), tableRow.getHeight() );
    }
  }

  public void testLargeValidTableInExcelMode() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }

    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.process.EnableCountBoxesStep", "true" );
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks", "false" );
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0001.prpt" );
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SequenceDataFactory sequenceDf = (SequenceDataFactory) dataFactory.getReference( 0 );
    PerformanceTestSequence sequence = (PerformanceTestSequence) sequenceDf.getSequence( "Query 1" );
    sequence.setParameter( "limit", 20000 );

    StopWatch sw = StopWatch.startNew();
    DebugReportRunner.createXmlFlow( report );
    DebugLog.log( sw );
  }

  public void testLargeValidTable() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }

    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.process.EnableCountBoxesStep", "true" );
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks", "false" );
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0001.prpt" );
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SequenceDataFactory sequenceDf = (SequenceDataFactory) dataFactory.getReference( 0 );
    PerformanceTestSequence sequence = (PerformanceTestSequence) sequenceDf.getSequence( "Query 1" );
    sequence.setParameter( "limit", 20000 );

    // ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 0));

    StopWatch sw = StopWatch.startNew();
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 5 );
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    DebugLog.log( sw );

    // ModelPrinter.INSTANCE.print(logicalPageBox);
    RenderNode[] tables = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    RenderNode[] tableRows = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );

    assertEquals( 1, tables.length );
    // assertEquals(StrictGeomUtility.toInternalValue(240), tables[0].getHeight());
    TableRenderBox table = (TableRenderBox) tables[0];
    // assertEquals(1, table.getColumnModel().getColumnCount());

    // assertEquals(12, tableRows.length);
    for ( RenderNode tableRow : tableRows ) {
      assertEquals( StrictGeomUtility.toInternalValue( 20 ), tableRow.getHeight() );
    }

    // DebugReportRunner.showDialog(report);

  }

  public void testPageSpanningAcrossPages() throws Exception {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.process.EnableCountBoxesStep", "false" );
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks", "false" );
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0003.prpt" );
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 500, 100 ) ) );

    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SequenceDataFactory sequenceDf = (SequenceDataFactory) dataFactory.getReference( 0 );
    PerformanceTestSequence sequence = (PerformanceTestSequence) sequenceDf.getSequence( "Query 1" );
    sequence.setParameter( "limit", 10 );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 1 );
  }
}
