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
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009 Time: 19:19:37
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProviderBundleWriteHandler {
  public void write( final WriteableDocumentBundle bundle,
                     final BundleWriterState state,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider )
    throws IOException, BundleWriterException;

}
