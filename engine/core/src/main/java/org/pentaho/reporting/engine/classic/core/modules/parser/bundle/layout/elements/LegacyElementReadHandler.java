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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LegacyElementReadHandler extends AbstractElementReadHandler {
  private LegacyDataSourceReadHandler dataSourceReadHandler;
  private LegacyTemplateReadHandler templateReadHandler;

  public LegacyElementReadHandler() throws ParseException {
    super( LegacyType.INSTANCE );
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
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( ExtParserModule.NAMESPACE.equals( uri ) ) {
      if ( "template".equals( tagName ) ) {
        templateReadHandler = new LegacyTemplateReadHandler();
        return templateReadHandler;
      } else if ( "datasource".equals( tagName ) ) {
        dataSourceReadHandler = new LegacyDataSourceReadHandler();
        return dataSourceReadHandler;
      }
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  @SuppressWarnings( "deprecation" )
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( dataSourceReadHandler != null ) {
      getElement().setDataSource( (DataSource) dataSourceReadHandler.getObject() );
    } else if ( templateReadHandler != null ) {
      getElement().setDataSource( (DataSource) templateReadHandler.getObject() );
    }

  }
}
