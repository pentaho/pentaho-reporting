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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportReadHandlerFactory;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

import java.util.Map;

/**
 * Creation-Date: Dec 18, 2006, 2:34:05 PM
 *
 * @author Thomas Morgner
 */
public class IncludeSubReportReadHandler extends AbstractPropertyXmlReadHandler {
  private SubReport report;
  private Object oldReport;
  private SubReportReadHandler subReportReadHandler;

  public IncludeSubReportReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String file = attrs.getValue( getUri(), "href" );
    if ( file != null ) {
      final Map parameters = deriveParseParameters();
      parameters.remove( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ) );
      parameters.put( new FactoryParameterKey( ReportParserUtil.INCLUDE_PARSING_KEY ),
          ReportParserUtil.INCLUDE_PARSING_VALUE );
      try {
        report = (SubReport) performExternalParsing( file, SubReport.class, parameters );
      } catch ( ResourceLoadingException e ) {
        throw new ParseException( "The specified subreport was not found or could not be loaded.", e, getLocator() );
      }
    } else {
      report = new SubReport();
    }

    final String query = attrs.getValue( getUri(), "query" );
    if ( query != null ) {
      report.setQuery( query );
    }

    final String name = attrs.getValue( getUri(), "name" );
    if ( name != null ) {
      report.setName( name );
    }

    final SubReportReadHandlerFactory factory = SubReportReadHandlerFactory.getInstance();
    final SubReportReadHandler handler = factory.getHandler( getUri(), getTagName() );
    if ( handler != null ) {
      handler.setDisableRootTagWarning( true );
      final RootXmlReadHandler rootHandler = getRootHandler();
      oldReport = rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
      rootHandler.setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, report );
      rootHandler.delegate( handler, getUri(), getTagName(), attrs );
      subReportReadHandler = handler;
    }
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( subReportReadHandler != null ) {
      report = subReportReadHandler.getSubReport();
    }

    if ( oldReport != null ) {
      getRootHandler().setHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME, oldReport );
      oldReport = null;
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() throws SAXException {
    return report;
  }
}
