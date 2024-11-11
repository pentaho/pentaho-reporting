/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009 Time: 19:19:01
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProviderWriteHandler {
  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final CubeFileProvider cubeFileProvider )
    throws IOException, ReportWriterException;

}
