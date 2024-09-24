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

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailDefinition;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.repository.ContentIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MailDefinitionBundleWriter {
  public MailDefinitionBundleWriter() {
  }

  public void writeMailDefinition( final WriteableDocumentBundle documentBundle, final MailDefinition report,
      final DocumentBundle globalBundle ) {
    if ( documentBundle == null ) {
      throw new NullPointerException();
    }
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( globalBundle == null ) {
      throw new NullPointerException();
    }

    final WriteableDocumentMetaData data = documentBundle.getWriteableDocumentMetaData();
    data.setBundleType( "application/vnd.pentaho.reporting.mailer-definition" );

  }

  public static void writeReportToZipFile( final MailDefinition report, final File file ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( file == null ) {
      throw new NullPointerException();
    }

    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final MailDefinitionBundleWriter writer = new MailDefinitionBundleWriter();
    writer.writeMailDefinition( documentBundle, report, null );
    BundleUtilities.writeAsZip( file, documentBundle );
  }

  public static void writeReportToZipStream( final MailDefinition report, final OutputStream out ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final MailDefinitionBundleWriter writer = new MailDefinitionBundleWriter();
    writer.writeMailDefinition( documentBundle, report, null );
    BundleUtilities.writeAsZip( out, documentBundle );
  }

  public static void writeReportToDirectory( final MailDefinition report, final File file ) throws IOException,
    BundleWriterException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( file == null ) {
      throw new NullPointerException();
    }
    final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle();
    final MailDefinitionBundleWriter writer = new MailDefinitionBundleWriter();
    writer.writeMailDefinition( documentBundle, report, null );
    BundleUtilities.writeToDirectory( file, documentBundle );
  }
}
