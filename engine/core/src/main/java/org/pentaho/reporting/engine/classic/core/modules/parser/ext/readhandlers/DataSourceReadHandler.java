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

import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.DataTarget;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class DataSourceReadHandler extends CompoundObjectReadHandler {
  private DataSourceReadHandler childHandler;

  public DataSourceReadHandler() {
    super( null );
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
    final String typeName = attrs.getValue( getUri(), "type" );
    if ( typeName == null ) {
      throw new ParseException( "The datasource type must be specified", getRootHandler().getDocumentLocator() );
    }

    final DataSourceCollector fc =
        (DataSourceCollector) getRootHandler().getHelperObject( ReportDefinitionReadHandler.DATASOURCE_FACTORY_KEY );
    final ObjectDescription od = fc.getDataSourceDescription( typeName );
    if ( od == null ) {
      throw new ParseException( "The specified DataSource type is not defined", getLocator() );
    }
    setObjectDescription( od );
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

    if ( "datasource".equals( tagName ) ) {
      childHandler = new DataSourceReadHandler();
      return childHandler;
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    final DataSource ds = (DataSource) super.getObject();
    if ( childHandler != null && ds instanceof DataTarget ) {
      final DataTarget dt = (DataTarget) ds;
      dt.setDataSource( (DataSource) childHandler.getObject() );
    }
    return ds;
  }
}
