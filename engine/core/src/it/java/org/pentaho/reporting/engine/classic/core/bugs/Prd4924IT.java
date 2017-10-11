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
