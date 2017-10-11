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
