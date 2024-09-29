/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.CustomPageDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class PageDefinitionReadHandler extends AbstractPropertyXmlReadHandler {
  private ArrayList pageDefList;

  public PageDefinitionReadHandler() {
    pageDefList = new ArrayList();
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
      final PageReadHandler readHandler = new PageReadHandler();
      pageDefList.add( readHandler );
      return readHandler;
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
    if ( pageDefList.isEmpty() ) {
      throw new SAXException( "page-definition element needs at least one page definition." );
    }

    final CustomPageDefinition pageDefinition = new CustomPageDefinition();

    for ( int i = 0; i < pageDefList.size(); i++ ) {
      final PageReadHandler readHandler = (PageReadHandler) pageDefList.get( i );
      pageDefinition.addPageFormat( readHandler.getPageFormat(), readHandler.getX(), readHandler.getY() );
    }

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
