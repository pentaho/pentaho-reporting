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
* Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
* Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.openerp.parser;

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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 * Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
 */

import com.debortoliwines.openerp.reporting.di.OpenERPConfiguration;
import com.debortoliwines.openerp.reporting.di.OpenERPFieldInfo;
import com.debortoliwines.openerp.reporting.di.OpenERPFilterInfo;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.openerp.OpenERPDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @author Pieter van der Merwe
 */
public class OpenERPDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ConfigReadHandler configReadHandler;
  private ArrayList<FilterReadHandler> filters = new ArrayList<FilterReadHandler>();
  private OpenERPDataFactory dataFactory;

  private ArrayList<SelectedFieldReadHandler> selectedFieldHandlers = new ArrayList<SelectedFieldReadHandler>();
  private ArrayList<OpenERPFieldInfo> allFields = new ArrayList<OpenERPFieldInfo>();

  public OpenERPDataSourceReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "config".equals( tagName ) ) {
      configReadHandler = new ConfigReadHandler();
      return configReadHandler;
    }

    if ( "filter".equals( tagName ) ) {
      final FilterReadHandler filterReadHandler = new FilterReadHandler();
      filters.add( filterReadHandler );
      return filterReadHandler;
    }

    if ( "selectedField".equals( tagName ) ) {
      final SelectedFieldReadHandler selectedFieldReadHandler = new SelectedFieldReadHandler( allFields );
      selectedFieldHandlers.add( selectedFieldReadHandler );
      return selectedFieldReadHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final OpenERPDataFactory srdf = new OpenERPDataFactory();
    if ( configReadHandler == null ) {
      throw new ParseException( "Required element 'config' is missing.", getLocator() );
    }

    srdf.setQueryName( configReadHandler.getQueryName() );

    OpenERPConfiguration config = configReadHandler.getConfig();
    srdf.setConfig( config );

    ArrayList<OpenERPFilterInfo> filterRows = new ArrayList<OpenERPFilterInfo>();
    for ( FilterReadHandler handler : filters ) {
      filterRows.add( handler.getFilter() );
    }
    config.setFilters( filterRows );

    ArrayList<OpenERPFieldInfo> selectedFields = new ArrayList<OpenERPFieldInfo>();
    for ( SelectedFieldReadHandler handler : selectedFieldHandlers ) {
      selectedFields.add( handler.getField() );
    }
    config.setSelectedFields( selectedFields );

    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
