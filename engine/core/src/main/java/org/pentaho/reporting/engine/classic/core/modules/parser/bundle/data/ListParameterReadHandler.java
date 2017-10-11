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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.StaticListParameter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ListParameterReadHandler extends AbstractParameterReadHandler {
  private static final Log logger = LogFactory.getLog( ListParameterReadHandler.class );

  private String query;
  private String keyColumnName;
  private String valueColumnName;
  private ListParameter result;
  private boolean strictValueCheck;
  private boolean allowMultiSelection;

  public ListParameterReadHandler() {
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    query = attrs.getValue( getUri(), "query" );
    keyColumnName = attrs.getValue( getUri(), "key-column" );

    if ( query != null && keyColumnName == null ) {
      Locator locator = getLocator();
      logger.warn( String.format( "Required parameter 'key-column' is missing for parameter '%s'. [%d:%d]", getName(),
          locator.getLineNumber(), locator.getColumnNumber() ) );
      keyColumnName = "";
    }

    valueColumnName = attrs.getValue( getUri(), "value-column" );
    if ( valueColumnName == null ) {
      valueColumnName = keyColumnName;
    }

    strictValueCheck = "true".equals( attrs.getValue( getUri(), "strict-values" ) );
    allowMultiSelection = "true".equals( attrs.getValue( getUri(), "allow-multi-selection" ) );
  }

  /**
   * Sets result to a DefaultListParameter if associated with a query, StaticListParameter otherwise.
   */
  protected void doneParsing() {
    final AbstractParameter result;
    if ( query != null ) {
      result =
          new DefaultListParameter( query, keyColumnName, valueColumnName, getName(), allowMultiSelection,
              strictValueCheck, getType() );
    } else {
      result = new StaticListParameter( getName(), allowMultiSelection, strictValueCheck, getType() );
    }

    result.setMandatory( isMandatory() );
    result.setDefaultValue( getDefaultValue() );
    applyAttributes( result );
    this.result = (ListParameter) result;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
