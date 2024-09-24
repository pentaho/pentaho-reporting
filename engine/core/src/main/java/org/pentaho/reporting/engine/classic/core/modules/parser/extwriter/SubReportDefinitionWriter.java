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

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 22, 2007, 3:05:02 PM
 *
 * @author Thomas Morgner
 */
public class SubReportDefinitionWriter extends AbstractXMLDefinitionWriter {
  public SubReportDefinitionWriter( final ReportWriterContext reportWriter, final XmlWriter xmlWriter ) {
    super( reportWriter, xmlWriter );
  }

  /**
   * Writes the report definition portion. Every DefinitionWriter handles one or more elements of the JFreeReport object
   * tree, DefinitionWriter traverse the object tree and write the known objects or forward objects to other definition
   * writers.
   *
   * @throws java.io.IOException
   *           if there is an I/O problem.
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException
   *           if the report serialisation failed.
   */
  public void write() throws IOException, ReportWriterException {
    final SubReport report = (SubReport) getReport();
    final XmlWriter xmlWriter = getXmlWriter();

    final AttributeList attList = new AttributeList();
    if ( getReportWriter().hasParent() == false ) {
      attList.addNamespaceDeclaration( "", ExtParserModule.NAMESPACE );
    }

    final String query = report.getQuery();
    if ( query != null ) {
      attList.setAttribute( ExtParserModule.NAMESPACE, "query", query );
    }
    xmlWriter.writeTag( ExtParserModule.NAMESPACE, "sub-report", attList, XmlWriterSupport.OPEN );

    writeParameterDeclaration();

    // no need to write the parser config, if this subreport is inlined.
    if ( getReportWriter().hasParent() == false ) {
      final ParserConfigWriter parserConfigWriter = new ParserConfigWriter( getReportWriter(), xmlWriter );
      parserConfigWriter.write();
    }

    final ReportConfigWriter reportConfigWriter = new ReportConfigWriter( getReportWriter(), xmlWriter );
    reportConfigWriter.write();

    final ReportDescriptionWriter reportDescriptionWriter = new ReportDescriptionWriter( getReportWriter(), xmlWriter );
    reportDescriptionWriter.write();

    final FunctionsWriter functionsWriter = new FunctionsWriter( getReportWriter(), xmlWriter );
    functionsWriter.write();
    xmlWriter.writeCloseTag();
  }

  private void writeParameterDeclaration() throws IOException {
    final SubReport report = (SubReport) getReport();
    final ParameterMapping[] exportMappings = report.getExportMappings();
    for ( int i = 0; i < exportMappings.length; i++ ) {
      final ParameterMapping mapping = exportMappings[i];
      final AttributeList attList = new AttributeList();
      attList.setAttribute( ExtParserModule.NAMESPACE, "name", mapping.getName() );
      if ( mapping.getAlias().equals( mapping.getName() ) == false ) {
        attList.setAttribute( ExtParserModule.NAMESPACE, "alias", mapping.getAlias() );
      }

      getXmlWriter().writeTag( ExtParserModule.NAMESPACE, "export-parameter", attList, XmlWriterSupport.CLOSE );
    }

    final ParameterMapping[] importMappings = report.getInputMappings();
    for ( int i = 0; i < importMappings.length; i++ ) {
      final ParameterMapping mapping = importMappings[i];
      final AttributeList attList = new AttributeList();
      attList.setAttribute( ExtParserModule.NAMESPACE, "name", mapping.getName() );
      if ( mapping.getAlias().equals( mapping.getName() ) == false ) {
        attList.setAttribute( ExtParserModule.NAMESPACE, "alias", mapping.getAlias() );
      }

      getXmlWriter().writeTag( ExtParserModule.NAMESPACE, "import-parameter", attList, XmlWriterSupport.CLOSE );
    }
  }
}
