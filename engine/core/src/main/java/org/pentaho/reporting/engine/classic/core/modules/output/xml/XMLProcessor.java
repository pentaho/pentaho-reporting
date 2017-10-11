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

package org.pentaho.reporting.engine.classic.core.modules.output.xml;

import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Writer;

/**
 * The XMLProcessor coordinates the report processing for the XML-Output. This class is responsible to initialize and
 * maintain the XMLWriter, which performs the output process.
 * <p/>
 * The XMLProcessor is not intended to produce complex output, it is an educational example. If you want valid xml data
 * enriched with layouting information, then have a look at the HTML-OutputTarget, this target is also able to write
 * XHTMl code.
 *
 * @author Thomas Morgner
 * @deprecated The whole basic XML output is deprecated as it cannot handle inline subreports.
 */
public class XMLProcessor extends AbstractReportProcessor {
  private static class XMLDataOutputProcessor extends AbstractOutputProcessor {
    private OutputProcessorMetaData metaData;

    private XMLDataOutputProcessor( final Configuration config ) {
      metaData = new GenericOutputProcessorMetaData( EXPORT_DESCRIPTOR );
    }

    protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
      throws ContentProcessingException {

    }

    public OutputProcessorMetaData getMetaData() {
      return metaData;
    }
  }

  /**
   * the target writer.
   */
  private Writer writer;

  private static final String EXPORT_DESCRIPTOR = "document/xml";

  /**
   * Creates a new XMLProcessor. The processor will output the report as simple xml stream.
   *
   * @param report
   *          the report that should be processed
   * @throws ReportProcessingException
   *           if the report could not be initialized
   */
  public XMLProcessor( final MasterReport report ) throws ReportProcessingException {
    super( report, new XMLDataOutputProcessor( report.getConfiguration() ) );
  }

  /**
   * Returns the writer, which will receive the generated output.
   *
   * @return the writer
   */
  public Writer getWriter() {
    return writer;
  }

  /**
   * Sets the writer, which will receive the generated output. The writer should have the proper encoding set.
   *
   * @param writer
   *          that should receive the generated output.
   */
  public void setWriter( final Writer writer ) {
    this.writer = writer;
  }

  /**
   * Checks whether report processing should be aborted when an exception occurs.
   *
   * @param config
   *          the configuration.
   * @return if strict error handling is enabled.
   */
  protected static boolean isStrictErrorHandling( final Configuration config ) {
    final String strictError = config.getConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY );
    return "true".equals( strictError );
  }

  protected OutputFunction createLayoutManager() {
    final XMLWriter xmlWriter = new XMLWriter();
    xmlWriter.setWriter( writer );
    return xmlWriter;
  }
}
