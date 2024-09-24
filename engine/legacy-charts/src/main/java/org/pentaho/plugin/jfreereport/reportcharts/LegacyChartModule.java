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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class LegacyChartModule extends AbstractModule {
  public LegacyChartModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
      ( "org/pentaho/plugin/jfreereport/reportcharts/meta-xy-chart-expressions.xml" );
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
      ( "org/pentaho/plugin/jfreereport/reportcharts/meta-other-chart-expressions.xml" );
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
      ( "org/pentaho/plugin/jfreereport/reportcharts/meta-categorical-chart-expressions.xml" );
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
      ( "org/pentaho/plugin/jfreereport/reportcharts/meta-collector-expressions.xml" );


    // Set the ChartFactory to the Legacy Theme
    ChartFactory.setChartTheme( StandardChartTheme.createLegacyTheme() );

  }
}
