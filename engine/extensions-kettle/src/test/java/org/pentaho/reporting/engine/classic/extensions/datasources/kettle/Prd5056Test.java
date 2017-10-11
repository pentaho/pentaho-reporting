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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

public class Prd5056Test {
  
  private static final Log logger = LogFactory.getLog( Prd5056Test.class );
  
  private static final String QUERY =
    "test-src/org/pentaho/reporting/engine/classic/extensions/datasources/kettle/Prd-5056.ktr";
  
  private static final String STEP = "Abort";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test(expected = ReportDataFactoryException.class)
  public void testFailWithError() throws Exception {
      final KettleDataFactory kettleDataFactory = new KettleDataFactory();
      kettleDataFactory.setQuery( "test", new KettleTransFromFileProducer( QUERY, STEP ) );
      kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
      kettleDataFactory.queryData( "test", new ReportParameterValues() );
  }

  @Test
  public void testLoadSave() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    KettleTransFromFileProducer value = new KettleTransFromFileProducer( QUERY, STEP );
    Assert.assertTrue( value.isStopOnError() );

    kettleDataFactory.setQuery( "test", value );

    final KettleDataFactory e2 = (KettleDataFactory) DataSourceTestBase.loadAndSaveOnReport( kettleDataFactory );
    KettleTransFromFileProducer test = (KettleTransFromFileProducer) e2.getQuery( "test" );
    Assert.assertTrue( test.isStopOnError() );
  }

  @Test
  public void testLoadSaveFalse() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    KettleTransFromFileProducer value = new KettleTransFromFileProducer( QUERY, STEP );
    value.setStopOnError( false );
    kettleDataFactory.setQuery( "test", value );

    final KettleDataFactory e2 = (KettleDataFactory) DataSourceTestBase.loadAndSaveOnReport( kettleDataFactory );
    KettleTransFromFileProducer test = (KettleTransFromFileProducer) e2.getQuery( "test" );
    Assert.assertFalse( test.isStopOnError() );
  }
}
