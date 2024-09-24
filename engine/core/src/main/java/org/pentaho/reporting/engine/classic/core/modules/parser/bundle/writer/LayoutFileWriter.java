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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ReportDefinitionWriteHandler;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class LayoutFileWriter implements BundleWriterHandler {
  public LayoutFileWriter() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  /**
   * Writes a certain aspect into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the fully qualified filename inside the bundle of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final AbstractReportDefinition report = state.getReport();
    final BundleWriterState layoutFileState = new BundleWriterState( state, "layout.xml" );
    final OutputStream outputStream =
        new BufferedOutputStream( bundle.createEntry( layoutFileState.getFileName(), "text/xml" ) );
    final DefaultTagDescription tagDescription = BundleWriterHandlerRegistry.getInstance().createWriterTagDescription();
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.writeXmlDeclaration( "UTF-8" );

    final ReportDefinitionWriteHandler writeHandler = new ReportDefinitionWriteHandler();
    writeHandler.writeElement( bundle, layoutFileState, writer, report );

    writer.close();
    return layoutFileState.getFileName();
  }
}
