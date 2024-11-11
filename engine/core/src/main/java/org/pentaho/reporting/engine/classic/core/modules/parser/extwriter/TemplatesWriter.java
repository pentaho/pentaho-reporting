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


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * The templates writer is responsible to write the templates section.
 *
 * @author Thomas Morgner
 * @deprecated No longer used.
 */
public class TemplatesWriter extends AbstractXMLDefinitionWriter {
  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param writer
   *          the current indention level.
   */
  public TemplatesWriter( final ReportWriter reportWriter, final XmlWriter writer ) {
    super( reportWriter, writer );
  }

  /**
   * Writes the templates (not yet supported).
   *
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void write() throws IOException, ReportWriterException {
    // templates are no longer written here. The templates are written as fully
    // resolved template declarations in the elements instead.
  }
}
