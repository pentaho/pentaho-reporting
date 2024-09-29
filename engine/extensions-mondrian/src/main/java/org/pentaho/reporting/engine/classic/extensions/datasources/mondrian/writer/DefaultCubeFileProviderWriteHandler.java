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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class DefaultCubeFileProviderWriteHandler
  implements CubeFileProviderBundleWriteHandler, CubeFileProviderWriteHandler {
  public DefaultCubeFileProviderWriteHandler() {
  }

  public void write( final WriteableDocumentBundle bundle,
                     final BundleWriterState state,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider ) throws IOException, BundleWriterException {
    write( xmlWriter, cubeFileProvider );
  }

  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider ) throws IOException, ReportWriterException {
    write( xmlWriter, cubeFileProvider );
  }

  protected void write( final XmlWriter writer, final CubeFileProvider provider ) throws IOException {
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-file", XmlWriter.OPEN );
    writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-filename", XmlWriter.OPEN );
    writer.writeTextNormalized( provider.getDesignTimeFile(), false );
    writer.writeCloseTag();
    if ( StringUtils.isEmpty( provider.getCubeConnectionName() ) == false ) {
      writer.writeTag( MondrianDataFactoryModule.NAMESPACE, "cube-connection-name", XmlWriter.OPEN );
      writer.writeTextNormalized( provider.getCubeConnectionName(), false );
      writer.writeCloseTag();
    }
    writer.writeCloseTag();
  }

}
