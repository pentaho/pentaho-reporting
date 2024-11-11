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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ConfigurationReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class ReportConfigReadHandler extends AbstractPropertyXmlReadHandler {
  private DataFactoryReadHandler dataFactoryReadHandler;

  public ReportConfigReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    final DataFactoryReadHandlerFactory factory = DataFactoryReadHandlerFactory.getInstance();
    final DataFactoryReadHandler handler = (DataFactoryReadHandler) factory.getHandler( uri, tagName );
    if ( handler != null ) {
      dataFactoryReadHandler = handler;
      return handler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    final AbstractReportDefinition report =
        (AbstractReportDefinition) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( report instanceof MasterReport ) {
      final MasterReport masterReport = (MasterReport) report;
      if ( "page-definition".equals( tagName ) ) {
        return new PageDefinitionReadHandler();
      } else if ( "simple-page-definition".equals( tagName ) ) {
        return new SimplePageDefinitionReadHandler();
      } else if ( "configuration".equals( tagName ) ) {
        return new ConfigurationReadHandler( masterReport.getReportConfiguration() );
      }
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
    if ( dataFactoryReadHandler == null ) {
      return;
    }

    final Object maybeReport = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( maybeReport instanceof MasterReport == false ) {
      return;
    }

    final MasterReport report = (MasterReport) maybeReport;
    final DataFactory dataFactory = dataFactoryReadHandler.getDataFactory();
    if ( dataFactory != null ) {
      report.setDataFactory( dataFactory );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
