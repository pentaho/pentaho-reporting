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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.StaticListParameter;
import org.pentaho.reporting.engine.classic.core.testsupport.ReportWritingUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Prd4579IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testParameterLoadSave() throws Exception {
    DefaultListParameter param =
        new DefaultListParameter( "dummy-query", "", "", "parameter", false, false, String.class );
    DefaultParameterDefinition pdef = new DefaultParameterDefinition();
    pdef.addParameterDefinition( param );

    MasterReport report = new MasterReport();
    report.setParameterDefinition( pdef );
    MasterReport result = ReportWritingUtil.saveAndLoad( report );
    ReportParameterDefinition parameterDefinition = result.getParameterDefinition();
    assertEquals( 1, parameterDefinition.getParameterCount() );

    ParameterDefinitionEntry resultParam = parameterDefinition.getParameterDefinition( 0 );
    assertTrue( resultParam instanceof DefaultListParameter );
    DefaultListParameter lp = (DefaultListParameter) resultParam;
    assertEquals( "", lp.getKeyColumn() );
    assertEquals( "", lp.getTextColumn() );
    assertEquals( "dummy-query", lp.getQueryName() );
  }

  @Test
  public void testStaticLoadSave() throws Exception {
    StaticListParameter param = new StaticListParameter( "parameter", false, false, String.class );
    DefaultParameterDefinition pdef = new DefaultParameterDefinition();
    pdef.addParameterDefinition( param );

    MasterReport report = new MasterReport();
    report.setParameterDefinition( pdef );
    MasterReport result = ReportWritingUtil.saveAndLoad( report );
    ReportParameterDefinition parameterDefinition = result.getParameterDefinition();
    assertEquals( 1, parameterDefinition.getParameterCount() );

    ParameterDefinitionEntry resultParam = parameterDefinition.getParameterDefinition( 0 );
    assertTrue( resultParam instanceof StaticListParameter );
  }

}
