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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.GoldSaveLoadIT;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;

/**
 * Created by Dmitriy Stepanov on 12.02.18.
 */
public class ExpressionWriterUtilityIT extends GoldSaveLoadIT {

  @Test
  public void testFailOnError() throws Exception {
    FormulaExpression fe = new FormulaExpression();
    fe.setFormula( "TRUE()" );
    String name = "expression";
    fe.setName( name );

    MasterReport r = new MasterReport();
    r.addExpression( fe );

    MasterReport r1 = postProcess( r );
    FormulaExpression e = (FormulaExpression) r1.getExpressions().get( name );
    Assert.assertEquals( fe.getFailOnError(), e.getFailOnError() );

    r.removeExpression( fe );
    fe.setFailOnError( true );
    r.addExpression( fe );

    r1 = postProcess( r );
    e = (FormulaExpression) r1.getExpressions().get( name );
    Assert.assertEquals( fe.getFailOnError(), e.getFailOnError() );

    r.removeExpression( fe );
    fe.setFailOnError( false );
    r.addExpression( fe );

    r1 = postProcess( r );
    e = (FormulaExpression) r1.getExpressions().get( name );
    Assert.assertEquals( fe.getFailOnError(), e.getFailOnError() );
  }
}
