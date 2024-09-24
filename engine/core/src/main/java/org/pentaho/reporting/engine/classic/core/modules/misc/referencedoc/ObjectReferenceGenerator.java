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
