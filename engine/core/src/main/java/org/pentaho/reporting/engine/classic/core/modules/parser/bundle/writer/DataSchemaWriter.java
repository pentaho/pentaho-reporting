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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.writer.StandaloneDataSchemaWriter;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DataSchemaWriter implements BundleWriterHandler {
  public DataSchemaWriter() {
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
   * @return the name of the newly generated file or null if no file was created.
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

    final BundleWriterState contentState = new BundleWriterState( state, state.getReport(), "dataschema.xml" );

    final OutputStream outputStream =
        new BufferedOutputStream( bundle.createEntry( contentState.getFileName(), "text/xml" ) );
    final MasterReport report = state.getMasterReport();
    final DataSchemaDefinition definition = report.getDataSchemaDefinition();

    final StandaloneDataSchemaWriter dataSchemaWriter = new StandaloneDataSchemaWriter();
    dataSchemaWriter.write( definition, outputStream, "UTF-8" );
    outputStream.close();
    return contentState.getFileName();
  }
}
