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
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateClassFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.net.URL;

/**
 * An application that creates a report documenting the object references.
 *
 * @author Thomas Morgner
 */
public final class ObjectReferenceGenerator {
  /**
   * The report definition.
   */
  private static final String REFERENCE_REPORT = "ObjectReferenceReport.xml"; //$NON-NLS-1$

  /**
   * DefaultConstructor.
   */
  private ObjectReferenceGenerator() {
  }

  /**
   * Creates the default tablemodel for the object reference generator.
   *
   * @return the tablemodel for the object reference generator.
   */
  public static TableModel createData() {
    final ClassFactoryCollector cc = new ClassFactoryCollector();
    cc.addFactory( new DefaultClassFactory() );
    cc.addFactory( new DefaultDataSourceFactory() );
    cc.addFactory( new TemplateClassFactory() );

    return new ObjectReferenceTableModel( cc );
  }

  /**
   * Starting point for the application.
   *
   * @param args
   *          ignored.
   */
  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ReportGenerator gen = ReportGenerator.getInstance();
    final URL reportURL = ObjectUtilities.getResourceRelative( REFERENCE_REPORT, ObjectReferenceGenerator.class );
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
          + "/object-reference.html" ); //$NON-NLS-1$
      PdfReportUtil.createPDF( report, System.getProperty( "user.home" ) + "/object-reference.pdf" ); //$NON-NLS-1$ //$NON-NLS-2$
    } catch ( Exception e ) {
      System.err.println( "The report processing failed." ); //$NON-NLS-1$
      System.err.println( "File: " + REFERENCE_REPORT ); //$NON-NLS-1$
      e.printStackTrace( System.err );
      System.exit( 1 );
    }
  }

}
