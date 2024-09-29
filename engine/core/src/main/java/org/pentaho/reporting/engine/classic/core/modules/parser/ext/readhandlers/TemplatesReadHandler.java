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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateDescription;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class TemplatesReadHandler extends AbstractPropertyXmlReadHandler {
  private ArrayList templateList;

  public TemplatesReadHandler() {
    templateList = new ArrayList();
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

    if ( "template".equals( tagName ) ) {
      final TemplateReadHandler readHandler = new TemplateReadHandler( true );
      templateList.add( readHandler );
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
    final TemplateCollection templateCollection =
        (TemplateCollection) getRootHandler().getHelperObject( ReportDefinitionReadHandler.TEMPLATE_FACTORY_KEY );
    for ( int i = 0; i < templateList.size(); i++ ) {
      final TemplateReadHandler readHandler = (TemplateReadHandler) templateList.get( i );
      templateCollection.addTemplate( (TemplateDescription) readHandler.getObjectDescription() );
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
