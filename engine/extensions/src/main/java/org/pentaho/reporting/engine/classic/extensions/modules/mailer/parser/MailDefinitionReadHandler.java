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

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import java.util.ArrayList;
import java.util.Properties;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataSourceElementHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.MasterParameterDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailDefinition;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailHeader;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MailDefinitionReadHandler extends AbstractXmlReadHandler {
  private MailDefinition mailDefinition;
  private HeadersReadHandler headersReadHandler;
  private SessionPropertiesReadHandler sessionPropertiesReadHandler;
  private StringReadHandler burstQueryReadHandler;
  private StringReadHandler recipientsQueryReadHandler;
  private ReportReadHandler bodyReportReadHandler;
  private ArrayList attachmentReportReadHandlers;
  private MasterParameterDefinitionReadHandler parameterDefinitionReadHandler;
  private DataSourceElementHandler dataSourceElementHandler;

  public MailDefinitionReadHandler() {
    attachmentReportReadHandlers = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleNamespaces.DATADEFINITION.equals( uri ) ) {
      if ( "parameter-definition".equals( tagName ) ) {
        parameterDefinitionReadHandler = new MasterParameterDefinitionReadHandler();
        return parameterDefinitionReadHandler;
      }
      if ( "data-source".equals( tagName ) ) {
        dataSourceElementHandler = new DataSourceElementHandler();
        return dataSourceElementHandler;
      }
      return null;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "header".equals( tagName ) ) {
      headersReadHandler = new HeadersReadHandler();
      return headersReadHandler;
    }
    if ( "session".equals( tagName ) ) {
      sessionPropertiesReadHandler = new SessionPropertiesReadHandler();
      return sessionPropertiesReadHandler;
    }
    if ( "burst-query".equals( tagName ) ) {
      burstQueryReadHandler = new StringReadHandler();
      return burstQueryReadHandler;
    }
    if ( "recipients-query".equals( tagName ) ) {
      recipientsQueryReadHandler = new StringReadHandler();
      return recipientsQueryReadHandler;
    }
    if ( "body-report".equals( tagName ) ) {
      bodyReportReadHandler = new ReportReadHandler();
      return bodyReportReadHandler;
    }
    if ( "attachment-report".equals( tagName ) ) {
      final ReportReadHandler reportReadHandler = new ReportReadHandler();
      attachmentReportReadHandlers.add( reportReadHandler );
      return reportReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {

    mailDefinition = new MailDefinition();

    if ( bodyReportReadHandler != null ) {
      mailDefinition.setBodyReport( bodyReportReadHandler.getTargetType(), bodyReportReadHandler.getReport() );
    }
    for ( int i = 0; i < attachmentReportReadHandlers.size(); i++ ) {
      final ReportReadHandler handler = (ReportReadHandler) attachmentReportReadHandlers.get( i );
      mailDefinition.addAttachmentReport( handler.getTargetType(), handler.getReport() );
    }
    if ( sessionPropertiesReadHandler != null ) {
      mailDefinition.setSessionProperties( (Properties) sessionPropertiesReadHandler.getObject() );
    }
    if ( headersReadHandler != null ) {
      final MailHeader[] headers = headersReadHandler.getHeaders();
      for ( int i = 0; i < headers.length; i++ ) {
        mailDefinition.addHeader( headers[i] );
      }
    }
    if ( burstQueryReadHandler != null ) {
      mailDefinition.setBurstQuery( burstQueryReadHandler.getResult() );
    }
    if ( recipientsQueryReadHandler != null ) {
      mailDefinition.setBurstQuery( recipientsQueryReadHandler.getResult() );
    }
    if ( parameterDefinitionReadHandler != null ) {
      mailDefinition.setParameterDefinition( (ReportParameterDefinition) parameterDefinitionReadHandler.getObject() );
    }
    if ( dataSourceElementHandler != null ) {
      mailDefinition.getDataFactory().add( dataSourceElementHandler.getDataFactory() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occurred.
   */
  public Object getObject() throws SAXException {
    return mailDefinition;
  }
}
