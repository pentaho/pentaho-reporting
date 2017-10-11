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

package org.pentaho.reporting.engine.classic.core.modules.misc.bsf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.base.PreProcessorTestBase;

public class BSFReportPreProcessorTest extends PreProcessorTestBase {
  private static final String SCRIPT =
      "org.pentaho.reporting.engine.classic.core.modules.misc.bsf.BSFReportPreProcessorTest.run(definition, \"script\")"
          + "; return definition;";
  private static final String PRESCRIPT =
      "org.pentaho.reporting.engine.classic.core.modules.misc.bsf.BSFReportPreProcessorTest.run(definition, "
          + "\"pre-data-script\"); return definition;";

  public static void run( AbstractReportDefinition def, String value ) {
    def.setAttribute( "test", "test", value );
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  protected ReportPreProcessor create() {
    BSFReportPreProcessor p = new BSFReportPreProcessor();
    p.setLanguage( "beanshell" );
    p.setPreDataScript( PRESCRIPT );
    p.setScript( SCRIPT );
    return p;
  }

  @Test
  public void testUnconfiguredDoesNotCrash() throws ReportProcessingException {
    BSFReportPreProcessor p = new BSFReportPreProcessor();
    MasterReport preData = materializePreData( new MasterReport(), p );
    MasterReport postData = materialize( new MasterReport(), p );

    Assert.assertNull( preData.getAttribute( "test", "test" ) );
    Assert.assertNull( postData.getAttribute( "test", "test" ) );

  }

  @Test
  public void testReportIsConfigured() throws ReportProcessingException {
    ReportPreProcessor p = create();
    MasterReport preData = materializePreData( new MasterReport(), p );
    MasterReport postData = materialize( new MasterReport(), p );

    Assert.assertEquals( "pre-data-script", preData.getAttribute( "test", "test" ) );
    Assert.assertEquals( "script", postData.getAttribute( "test", "test" ) );

  }
}
