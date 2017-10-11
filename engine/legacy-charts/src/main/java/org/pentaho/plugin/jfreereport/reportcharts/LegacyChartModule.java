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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
