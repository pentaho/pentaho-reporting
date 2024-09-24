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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * A report definition writer.
 *
 * @author Thomas Morgner.
 */
public class ReportDefinitionWriter extends AbstractXMLDefinitionWriter {
  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the writer context holding the global configuration for this write-operation.
   * @param xmlWriter
   *          the report writer.
   */
  public ReportDefinitionWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    super( reportWriter, xmlWriter );
  }

  /**
   * Writes a report definition to a character stream writer. After the standard XML header and the opening tag is
   * written, this class delegates work to:
   * <p/>
   * <ul>
   * <li>{@link ParserConfigWriter} to write the parser configuration;</li>
   * <li>{@link ReportConfigWriter} to write the report configuration;</li>
   * <li>{@link StylesWriter} to write the templates;</li>
   * <li>{@link ReportDescriptionWriter} to write the report description;</li>
   * <li>{@link FunctionsWriter} to write the function definitions;</li>
   * </ul>
   *
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void write() throws IOException, ReportWriterException {
    final MasterReport report = (MasterReport) getReport();
    final String reportName = report.getTitle();
    final XmlWriter xmlWriter = getXmlWriter();

    final AttributeList attList = new AttributeList();
    attList.addNamespaceDeclaration( "", ExtParserModule.NAMESPACE );
    if ( reportName != null ) {
      attList.setAttribute( ExtParserModule.NAMESPACE, "name", reportName );
    }

    final String query = report.getQuery();
    if ( query != null ) {
      attList.setAttribute( ExtParserModule.NAMESPACE, "query", query );
    }
    attList.setAttribute( ExtParserModule.NAMESPACE, "engine-version", ClassicEngineInfo.getInstance().getVersion() );

    xmlWriter.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.REPORT_DEFINITION_TAG, attList,
        XmlWriterSupport.OPEN );

    final ParserConfigWriter parserConfigWriter = new ParserConfigWriter( getReportWriter(), xmlWriter );
    parserConfigWriter.write();

    final ReportConfigWriter reportConfigWriter = new ReportConfigWriter( getReportWriter(), xmlWriter );
    reportConfigWriter.write();

    final ReportDescriptionWriter reportDescriptionWriter = new ReportDescriptionWriter( getReportWriter(), xmlWriter );
    reportDescriptionWriter.write();

    final FunctionsWriter functionsWriter = new FunctionsWriter( getReportWriter(), xmlWriter );
    functionsWriter.write();
    xmlWriter.writeCloseTag();
  }

}
