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
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class Prd3912IT {
  private static class ValidateExpression extends AbstractExpression {
    private ValidateExpression() {
      setName( "Validate" );
    }

    public Object getValue() {
      try {

        for ( String col : getDataRow().getColumnNames() ) {
          if ( "Validate".equals( col ) ) {
            // we cannot self-validate a result that we are currently computing.
            break;
          }
          getDataRow().isChanged( col );
        }
        return true;
      } catch ( Exception e ) {
        throw new AssertionError();
      }
    }
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testReport() throws ResourceException {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3912.prpt" );
    report.addExpression( new ValidateExpression() );
    DebugReportRunner.execGraphics2D( report );
  }
}
