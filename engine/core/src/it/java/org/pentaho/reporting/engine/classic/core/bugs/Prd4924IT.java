/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;

import static org.junit.Assert.assertNotNull;

public class Prd4924IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testGetPropertyType() {
    ReportPreProcessorMetaData[] metas = ReportPreProcessorRegistry.getInstance().getAllReportPreProcessorMetaDatas();
    for ( ReportPreProcessorMetaData meta : metas ) {
      ReportPreProcessorPropertyMetaData[] propertyDescriptions = meta.getPropertyDescriptions();
      for ( ReportPreProcessorPropertyMetaData propertyDescription : propertyDescriptions ) {
        assertNotNull( propertyDescription.getPropertyType() );
      }
    }
  }

  @Test
  public void testGetBeanDescriptor() {
    ReportPreProcessorMetaData[] metas = ReportPreProcessorRegistry.getInstance().getAllReportPreProcessorMetaDatas();
    for ( ReportPreProcessorMetaData meta : metas ) {
      ReportPreProcessorPropertyMetaData[] propertyDescriptions = meta.getPropertyDescriptions();
      for ( ReportPreProcessorPropertyMetaData propertyDescription : propertyDescriptions ) {
        assertNotNull( propertyDescription.getBeanDescriptor() );
      }
    }
  }
}
