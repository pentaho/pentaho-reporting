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

package org.pentaho.reporting.engine.classic.samples;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Generates a report using a paginated Swing Preview Dialog. The parameters for this report
 * can be modified while previewing the dialog and the changes can be seen instantly.
 * <p/>
 * The report generated in this scenario will be the same as created in Sample1:
 * <ol>
 * <li>The report definition file is a .prpt file which will be loaded and parsed
 * <li>The data factory is a simple JDBC data factory using HSQLDB
 * <li>There are no runtime report parameters used
 * </ol>
 */
public class Sample3 {

  /**
   * @param args
   */
  public static void main( String[] args ) {
    // initialize the Reporting Engine
    ClassicEngineBoot.getInstance().start();

    // Get the complete report definition (the report definition with the data factory and
    // parameters already applied)
    Sample3 sample = new Sample3();
    final MasterReport report = sample.getCompleteReportDefinition();

    // Generate the swing preview dialog
    final PreviewDialog dialog = new PreviewDialog();
    dialog.setReportJob( report );
    dialog.setSize( 500, 500 );
    dialog.setModal( true );
    dialog.setVisible( true );
    System.exit( 0 );
  }

  /**
   * Generates the report definition that has the data factory and 
   * parameters already applied.
   * @return the completed report definition
   */
  public MasterReport getCompleteReportDefinition() {
    final MasterReport report = getReportDefinition();

    // Add any parameters to the report
    final Map<String, Object> reportParameters = getReportParameters();
    if ( null != reportParameters ) {
      for ( String key : reportParameters.keySet() ) {
        report.getParameterValues().put( key, reportParameters.get( key ) );
      }
    }

    // Set the data factory for the report
    final DataFactory dataFactory = getDataFactory();
    if ( dataFactory != null ) {
      report.setDataFactory( dataFactory );
    }

    // Return the completed report
    return report;
  }

  /**
   * Returns the report definition which will be used to generate the report. In this case, the report will be
   * loaded and parsed from a file contained in this package.
   *
   * @return the loaded and parsed report definition to be used in report generation.
   */
  private MasterReport getReportDefinition() {
    try {
      // Using the classloader, get the URL to the reportDefinition file
      // NOTE: We will re-use the report definition from SAMPLE1
      final ClassLoader classloader = this.getClass().getClassLoader();
      final URL reportDefinitionURL = classloader
          .getResource( "org/pentaho/reporting/engine/classic/samples/Sample1.prpt" );

      // Parse the report file
      final ResourceManager resourceManager = new ResourceManager();
      final Resource directly = resourceManager.createDirectly( reportDefinitionURL, MasterReport.class );
      return (MasterReport) directly.getResource();
    } catch ( ResourceException e ) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the set of runtime report parameters. This sample report uses the following three parameters:
   * <ul>
   * <li><b>Report Title</b> - The title text on the top of the report</li>
   * <li><b>Customer Names</b> - an array of customer names to show in the report</li>
   * <li><b>Col Headers BG Color</b> - the background color for the column headers</li>
   * </ul>
   *
   * @return <code>null</code> indicating the report generator does not use any report parameters
   */
  private Map<String, Object> getReportParameters() {
    final Map parameters = new HashMap<String, Object>();
    parameters.put( "Report Title", "Simple Embedded Report Example with Parameters" );
    parameters.put( "Col Headers BG Color", "yellow" );
    parameters.put( "Customer Names", new String[] { "American Souvenirs Inc", "Toys4GrownUps.com", "giftsbymail.co.uk", "BG&E Collectables", "Classic Gift Ideas, Inc" } );
    return parameters;
  }

  /**
   * Returns the data factory which will be used to generate the data used during report generation. In this example,
   * we will return null since the data factory has been defined in the report definition.
   *
   * @return the data factory used with the report generator
   */
  private DataFactory getDataFactory() {
    return null;
  }
}
