/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.referencedoc;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.net.URL;

/**
 * An application that generates a report that provides style key reference information.
 *
 * @author Thomas Morgner.
 */
public final class DataSourceReferenceGenerator {
  /**
   * The report definition file.
   */
  private static final String REFERENCE_REPORT = "DataSourceReferenceReport.xml"; //$NON-NLS-1$

  /**
   * DefaultConstructor.
   */
  private DataSourceReferenceGenerator() {
  }

  /**
   * Returns the DataSourceReferenceTableModel.
   *
   * @return the tablemodel for the reference documentation.
   */
  public static TableModel createData() {
    final DataSourceCollector cc = new DataSourceCollector();
    cc.addFactory( new DefaultDataSourceFactory() );

    return new DataSourceReferenceTableModel( cc );
  }

  /**
   * The starting point for the application.
   *
   * @param args
   *          ignored.
   */
  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ReportGenerator gen = ReportGenerator.getInstance();
    final URL reportURL = ObjectUtilities.getResourceRelative( REFERENCE_REPORT, DataSourceReferenceGenerator.class );
    if ( reportURL == null ) {
      System.err.println( "The report was not found in the classpath" ); //$NON-NLS-1$
      System.err.println( "File: " + REFERENCE_REPORT ); //$NON-NLS-1$
      System.exit( 1 );
      return;
    }

    final MasterReport report;
    try {
      report = gen.parseReport( reportURL );
    } catch ( Exception e ) {
      System.err.println( "The report could not be parsed." ); //$NON-NLS-1$
      System.err.println( "File: " + REFERENCE_REPORT ); //$NON-NLS-1$
      e.printStackTrace( System.err );
      System.exit( 1 );
      return;
    }
    report.setDataFactory( new TableDataFactory( "default", createData() ) ); //$NON-NLS-1$
    try {
      HtmlReportUtil.createStreamHTML( report, System.getProperty( "user.home" ) //$NON-NLS-1$
          + "/datasource-reference.html" ); //$NON-NLS-1$
      PdfReportUtil.createPDF( report, System.getProperty( "user.home" ) //$NON-NLS-1$
          + "/datasource-reference.pdf" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      System.err.println( "The report processing failed." ); //$NON-NLS-1$
      System.err.println( "File: " + REFERENCE_REPORT ); //$NON-NLS-1$
      e.printStackTrace( System.err );
      System.exit( 1 );
    }
  }

}
