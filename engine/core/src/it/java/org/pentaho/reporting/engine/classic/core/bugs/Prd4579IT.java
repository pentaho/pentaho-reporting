/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
