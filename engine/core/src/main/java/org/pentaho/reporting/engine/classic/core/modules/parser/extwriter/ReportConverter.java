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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ExtraShapesClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

/**
 * A utility class for converting XML report definitions from the old format to the new format.
 *
 * @author Thomas Morgner
 */
public class ReportConverter {
  private static final Log logger = LogFactory.getLog( ReportConverter.class );

  /**
   * Default constructor.
   */
  public ReportConverter() {
  }

  /**
   * Writes a report in the new XML format.
   *
   * @param report
   *          the report.
   * @param w
   *          a character stream writer.
   * @param contentBase
   *          the content base for creating relative URLs.
   * @param encoding
   *          the encoding of the generated file.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there were problems while serializing the report definition.
   */
  public void write( final MasterReport report, final Writer w, final URL contentBase, final String encoding )
    throws IOException, ReportWriterException {
    if ( contentBase == null ) {
      throw new NullPointerException( "ContentBase is null" );
    }
    final ModifiableConfiguration config = new HierarchicalConfiguration( report.getReportConfiguration() );
    config.setConfigProperty( AbstractXmlResourceFactory.CONTENTBASE_KEY, contentBase.toExternalForm() );

    final ReportWriter writer = new ReportWriter( report, encoding, config );
    writer.addClassFactoryFactory( new URLClassFactory() );
    writer.addClassFactoryFactory( new DefaultClassFactory() );
    writer.addClassFactoryFactory( new BandLayoutClassFactory() );
    writer.addClassFactoryFactory( new ArrayClassFactory() );
    writer.addClassFactoryFactory( new ExtraShapesClassFactory() );

    writer.addStyleKeyFactory( new DefaultStyleKeyFactory() );
    writer.addStyleKeyFactory( new PageableLayoutStyleKeyFactory() );
    writer.addTemplateCollection( new DefaultTemplateCollection() );
    writer.addElementFactory( new DefaultElementFactory() );
    writer.addDataSourceFactory( new DefaultDataSourceFactory() );
    writer.write( w );
  }

  /**
   * Returns the URL of a report.
   *
   * @param name
   *          the report name.
   * @return The URL (or <code>null</code>).
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  public URL findReport( final String name ) throws IOException {
    final URL in = ObjectUtilities.getResource( name, ReportConverter.class );
    if ( in != null ) {
      return in;
    }
    final File f = new File( name );
    if ( f.canRead() ) {
      return f.toURL();
    }
    return null;
  }

  /**
   * Parses a report from the specified template file.
   *
   * @param templateURL
   *          the template location.
   * @return The report.
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  private MasterReport parseReport( final URL templateURL ) throws IOException {
    try {
      final ReportGenerator generator = ReportGenerator.getInstance();
      return generator.parseReport( templateURL );
    } catch ( Exception e ) {
      ReportConverter.logger.info( "ParseReport failed; Cause: ", e );
      throw new IOException( "Failed to parse the report" );
    }
  }

  /**
   * Parses a report from the old version of the XML report format, and writes a file in the new XML report format.
   *
   * @param inName
   *          the input report file.
   * @param outFile
   *          the output report file.
   * @param encoding
   *          the encoding of the generated file.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void convertReport( final String inName, final String outFile, final String encoding ) throws IOException,
    ReportWriterException {
    final URL reportURL = findReport( inName );
    if ( reportURL == null ) {
      throw new IOException( "The specified report definition was not found" );
    }
    final File out = new File( outFile );
    final OutputStream base = new FileOutputStream( out );
    final Writer w = new BufferedWriter( new OutputStreamWriter( base, encoding ) );
    try {
      convertReport( reportURL, out.toURL(), w, encoding );
    } finally {
      w.close();
    }
  }

  /**
   * Parses a report from the old version of the XML report format, and writes a file in the new XML report format.
   *
   * @param in
   *          the input report file.
   * @param out
   *          the output report file.
   * @param encoding
   *          the encoding of the generated file.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void convertReport( final File in, final File out, final String encoding ) throws IOException,
    ReportWriterException {
    final OutputStream base = new FileOutputStream( out );
    final Writer w = new BufferedWriter( new OutputStreamWriter( base, encoding ) );
    try {
      convertReport( in.toURL(), out.toURL(), w, encoding );
    } finally {
      w.close();
    }
  }

  /**
   * Parses a report from the old version of the XML report format, and writes a file in the new XML report format.
   *
   * @param in
   *          the input resource from where to read the report
   * @param contentBase
   *          the contentbase where the new report will be stored.
   * @param w
   *          the report writer
   * @param encoding
   *          the encoding of the generated file.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void convertReport( final URL in, final URL contentBase, final Writer w, final String encoding )
    throws IOException, ReportWriterException {
    if ( in == null ) {
      throw new NullPointerException( "Input URL is null" );
    }
    if ( contentBase == null ) {
      throw new NullPointerException( "ContentBase is null" );
    }
    if ( w == null ) {
      throw new NullPointerException( "Writer is null" );
    }
    if ( encoding == null ) {
      throw new NullPointerException( "Encoding is null." );
    }
    final MasterReport report = parseReport( in );
    write( report, w, contentBase, encoding );
    w.flush();
  }

  /**
   * The starting point for the conversion utility. The utility accepts two command line arguments, the first is the
   * name of the input file (a report in the old format) and the second is the name of the output file (a report in the
   * new format will be written to this file).
   *
   * @param args
   *          command line arguments.
   * @throws Exception
   *           if there is any problem.
   */
  public static void main( final String[] args ) throws IOException, ReportWriterException {
    if ( args.length != 2 ) {
      System.err.println( "Usage: ReportConverter <InFile> <OutFile>" );
      System.exit( 1 );
    }
    final ReportConverter converter = new ReportConverter();
    converter.convertReport( args[0], args[1], "UTF-16" );
  }
}
