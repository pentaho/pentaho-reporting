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
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class DataSourceFactoryReadHandler extends AbstractPropertyXmlReadHandler {
  public DataSourceFactoryReadHandler() {
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
    final String className = CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), "class" ) );
    if ( className == null ) {
      throw new ParseException( "Attribute 'class' is missing.", getRootHandler().getDocumentLocator() );
    }
    final DataSourceCollector fc =
        (DataSourceCollector) getRootHandler().getHelperObject( ReportDefinitionReadHandler.DATASOURCE_FACTORY_KEY );

    final DataSourceFactory factory =
        (DataSourceFactory) ObjectUtilities.loadAndInstantiate( className, getClass(), DataSourceFactory.class );
    if ( factory != null ) {
      factory.configure( getRootHandler().getParserConfiguration() );
      fc.addFactory( factory );
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
