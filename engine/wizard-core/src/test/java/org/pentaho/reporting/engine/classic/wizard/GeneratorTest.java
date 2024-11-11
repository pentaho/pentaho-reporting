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


package org.pentaho.reporting.engine.classic.wizard;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.NoOpPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;

@Ignore( "Needs to be repaired!" )
public class GeneratorTest {
  public GeneratorTest() {
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCrosstab() throws Exception {
    final GroupDefinition[] groupDefs = new GroupDefinition[ 3 ];
    groupDefs[ 0 ] = new DefaultGroupDefinition( GroupType.RELATIONAL, "group-field1" );
    groupDefs[ 1 ] = new DefaultGroupDefinition( GroupType.CT_ROW, "group-field2" );
    groupDefs[ 2 ] = new DefaultGroupDefinition( GroupType.CT_COLUMN, "group-field3" );

    final DetailFieldDefinition[] detailFields = new DetailFieldDefinition[ 1 ];
    detailFields[ 0 ] = new DefaultDetailFieldDefinition( "detail-field1" );

    final DefaultWizardSpecification wizardSpecification = new DefaultWizardSpecification();
    wizardSpecification.setGroupDefinitions( groupDefs );
    wizardSpecification.setDetailFieldDefinitions( detailFields );

    final MasterReport report = new MasterReport();
    report.setAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec", wizardSpecification );

    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DefaultFlowController flowController = new DefaultFlowController
      ( processingContext, report.getDataSchemaDefinition(), new ReportParameterValues(),
        new NoOpPerformanceMonitorContext() );

    final WizardProcessor processor = new WizardProcessor();
    final MasterReport masterReport = processor.performPreProcessing( report, flowController );

    final int count = masterReport.getGroupCount();
    assertEquals( groupDefs.length + 2, count );
    final RelationalGroup defaultGroup = (RelationalGroup) masterReport.getRootGroup();
    assertEquals( "Default-Group detector", 0, defaultGroup.getFieldsArray().length );
    final SubGroupBody body = (SubGroupBody) defaultGroup.getBody();
    final RelationalGroup group1 = (RelationalGroup) body.getGroup();
    assertEquals( "Rel-Group", "group-field1", group1.getFieldsArray()[ 0 ] );
    final SubGroupBody body1 = (SubGroupBody) group1.getBody();
    final CrosstabGroup ctGroup = (CrosstabGroup) body1.getGroup();
    final CrosstabRowGroupBody body2 = (CrosstabRowGroupBody) ctGroup.getBody();
    final CrosstabRowGroup rowGroup = body2.getGroup();
    assertEquals( "Row-Group", "group-field2", rowGroup.getField() );

    final CrosstabColumnGroupBody body3 = (CrosstabColumnGroupBody) rowGroup.getBody();
    final CrosstabColumnGroup colGroup = body3.getGroup();
    assertEquals( "Col-Group", "group-field3", colGroup.getField() );

  }
}
