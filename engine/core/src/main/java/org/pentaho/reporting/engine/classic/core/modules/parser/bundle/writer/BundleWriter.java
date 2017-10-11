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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.repository.ContentIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * The bundle-frontend. This class orchestrates the writing process.
 * <p/>
 * In the absence of template files, it will simply serialize the whole report into a XML file and will fail if there
 * are non-serializable or unrecognized elements. Unlike the old ext-writer, this class does not try to write all
 * report-definitions. The report definitions fed into this writer must use ElementType implementation to be written
 * correctly.
 * <p/>
 * In this very first implementation, we ignore the global bundle and write everything into the target bundle. To make
 * selective writing work, we have to tag each shared element as shared.
 *
 * @author Thomas Morgner
 */
public class BundleWriter {
  private static final String MASTER_HANDLER_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.handler.master.";
  private static final String SUBREPORT_HANDLER_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.handler.subreport.";

  private BundleWriterHandler[] masterWriter;
  private BundleWriterHandler[] subreportWriter;

  public BundleWriter() {

    masterWriter = BundleWriterHandlerRegistry.getInstance().getWriteHandlers( true );
    if ( masterWriter.length == 0 ) {
      throw new IllegalStateException( "Bundle-Writer configuration is invalid." );
    }

    // Sort the list of BundleWriterHandlers so the processing occurs in a better defined order
    sortBundleWriterHandlers( masterWriter );

    subreportWriter = BundleWriterHandlerRegistry.getInstance().getWriteHandlers( false );
    if ( subreportWriter.length == 0 ) {
      throw new IllegalStateException( "Bundle-Writer configuration is invalid." );
    }
    // Sort the list of BundleWriterHandlers so the processing occurs in a better defined order
    sortBundleWriterHandlers( subreportWriter );
  }

  public void writeReport( final WriteableDocumentBundle bundle, final MasterReport report ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( report == null ) {
      throw new NullPointerException();
    }

    if ( bundle == report.getBundle() ) {
      throw new IllegalArgumentException( "Cannot write to the originating bundle." );
    }

    final DocumentBundle reportBundle = report.getBundle();
    if ( reportBundle == null ) {
      this.writeReport( bundle, report, new MemoryDocumentBundle() );
    } else {
      this.writeReport( bundle, report, reportBundle );
    }

  }

  public void writeReport( final WriteableDocumentBundle bundle, final MasterReport report,
      final DocumentBundle globalBundle ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( globalBundle == null ) {
      throw new NullPointerException();
    }

    final WriteableDocumentMetaData data = bundle.getWriteableDocumentMetaData();
    data.setBundleType( ClassicEngineBoot.BUNDLE_TYPE );

    final MasterReport clone = (MasterReport) report.derive();
    final BundleWriterState state = new BundleWriterState( clone, globalBundle, this );
    for ( int i = 0; i < masterWriter.length; i++ ) {
      final BundleWriterHandler handler = masterWriter[i];
      handler.writeReport( bundle, state );
    }
  }

  public void writeSubReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    for ( int i = 0; i < subreportWriter.length; i++ ) {
      final BundleWriterHandler handler = subreportWriter[i];
      handler.writeReport( bundle, state );
    }
  }

  public static void writeReportToZipFile( final MasterReport report, final String file ) throws BundleWriterException,
    ContentIOException, IOException {
    writeReportToZipFile( report, new File( file ) );
  }

  public static void writeReportToZipFile( final MasterReport report, final File file ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( file == null ) {
      throw new NullPointerException();
    }

    final MemoryDocumentBundle outputBundle = new MemoryDocumentBundle();
    final BundleWriter writer = new BundleWriter();
    writer.writeReport( outputBundle, report );
    BundleUtilities.writeAsZip( file, outputBundle );
  }

  public static void writeReportToZipStream( final MasterReport report, final OutputStream out ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final BundleWriter writer = new BundleWriter();
    writer.writeReport( documentBundle, report );
    BundleUtilities.writeAsZip( out, documentBundle );
  }

  public static void writeReportToZipStream( final MasterReport report, final OutputStream out,
      final DocumentMetaData metaData ) throws IOException, BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final BundleWriter writer = new BundleWriter();
    writer.writeReport( documentBundle, report );

    // restore the metadata to match the metadata of the original bundle.
    final WriteableDocumentMetaData targetMetaData = (WriteableDocumentMetaData) documentBundle.getMetaData();
    for ( final String namespace : metaData.getMetaDataNamespaces() ) {
      for ( final String name : metaData.getMetaDataNames( namespace ) ) {
        targetMetaData.setBundleAttribute( namespace, name, metaData.getBundleAttribute( namespace, name ) );
      }
    }

    BundleUtilities.writeAsZip( out, documentBundle );
  }

  public static void writeReportToDirectory( final MasterReport report, final File file ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( file == null ) {
      throw new NullPointerException();
    }
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final BundleWriter writer = new BundleWriter();
    writer.writeReport( documentBundle, report );
    BundleUtilities.writeToDirectory( file, documentBundle );
  }

  /**
   * Sorts the BundleWriterHandlers so that they are processed in the correct order
   *
   * @param masterWriter
   *          the array of BundleWriterHandlers to sort
   */
  private static void sortBundleWriterHandlers( final BundleWriterHandler[] masterWriter ) {
    Arrays.sort( masterWriter, new BundleWriterHandlerComparator() );
  }

  /**
   * Simple comparator class which is used to compare BundleWriterHandlers for sorting purposes
   */
  private static class BundleWriterHandlerComparator implements Comparator<BundleWriterHandler> {
    private BundleWriterHandlerComparator() {
    }

    public int compare( final BundleWriterHandler o1, final BundleWriterHandler o2 ) {
      // We should not have to worry about type checking here
      return o1.getProcessingOrder() - o2.getProcessingOrder();
    }
  }
}
