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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

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
