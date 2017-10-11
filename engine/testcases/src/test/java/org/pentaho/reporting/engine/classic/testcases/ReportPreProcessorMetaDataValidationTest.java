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

package org.pentaho.reporting.engine.classic.testcases;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.base.MetaDataValidationTestBase;

import java.util.ArrayList;
import java.util.List;

public class ReportPreProcessorMetaDataValidationTest extends MetaDataValidationTestBase<ReportPreProcessorMetaData> {
  public ReportPreProcessorMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    ReportPreProcessorMetaData[] m = ReportPreProcessorRegistry.getInstance().getAllReportPreProcessorMetaDatas();
    List list = super.performTest( m );
    Assert.assertEquals( new ArrayList(), list );
  }

  protected void performTestOnElement( final ReportPreProcessorMetaData metaData ) {
    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );

    try {
      final Object type = metaData.create();
    } catch ( Exception e ) {
      Assert.fail( "metadata creation failed" );

    }

    validate( metaData );

    for ( ReportPreProcessorPropertyMetaData propertyDescription : metaData.getPropertyDescriptions() ) {
      validate( propertyDescription );
    }
  }
}
