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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner;

import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class ReportDesignerSubReportXmlFactoryModule implements XmlFactoryModule {
  public ReportDesignerSubReportXmlFactoryModule() {
  }

  /**
   * Checks the given document data to compute the propability of whether this factory module would be able to handle
   * the given data.
   *
   * @param documentInfo the document information collection.
   * @return an integer value indicating how good the document matches the factories requirements.
   */
  public int getDocumentSupport( final XmlDocumentInfo documentInfo ) {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if ( rootNamespace != null && rootNamespace.length() > 0 ) {
      // this cannot be a report-designer file, as the report designer does not use namespaces or DTDs.
      return XmlFactoryModule.NOT_RECOGNIZED;
    } else if ( "subreport".equals( documentInfo.getRootElement() ) ) {
      // make this module a lower priority module than the simple-xml module.
      if ( documentInfo.getRootElementAttributes() == null ||
        documentInfo.getRootElementAttributes().getLength() == 0 ) {
        // make yourself a little bit more important than the plain recognized by tagname, as the
        // empty root tag is a good hint already.
        return XmlFactoryModule.RECOGNIZED_BY_TAGNAME + 1;
      }
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  /**
   * Creates an XmlReadHandler for the root-tag based on the given document information.
   *
   * @param documentInfo the document information that has been extracted from the parser.
   * @return the root handler or null.
   */
  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new ReportDesignerSubReportRootHandler();
  }

  /**
   * Returns the default namespace for a document with the characteristics given in the XmlDocumentInfo.
   *
   * @param documentInfo the document information.
   * @return the default namespace uri for the document.
   */
  public String getDefaultNamespace( final XmlDocumentInfo documentInfo ) {
    return ReportDesignerParserModule.NAMESPACE;
  }
}
