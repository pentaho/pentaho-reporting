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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class SimplePageDefinitionReadHandler extends AbstractPropertyXmlReadHandler {
  private PageReadHandler pageReadHandler;
  private int width;
  private int height;

  public SimplePageDefinitionReadHandler() {
  }

  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    width = ParserUtil.parseInt( attrs.getValue( getUri(), "width" ), 1 );
    height = ParserUtil.parseInt( attrs.getValue( getUri(), "height" ), 1 );
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "page".equals( tagName ) ) {
      pageReadHandler = new PageReadHandler();
      return pageReadHandler;
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
    if ( pageReadHandler == null ) {
      throw new SAXException( "simple-page-definition element needs one page definition." );
    }

    final SimplePageDefinition pageDefinition =
        new SimplePageDefinition( pageReadHandler.getPageFormat(), width, height );
    final MasterReport report =
        (MasterReport) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    report.setPageDefinition( pageDefinition );
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
