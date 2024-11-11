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


package org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VariableReadHandler extends AbstractXmlReadHandler {
  private String dataRowName;
  private String variableName;

  public VariableReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  @Override
  protected void startParsing( final Attributes attrs ) throws SAXException {
    dataRowName = attrs.getValue( getUri(), "datarow-name" );
    if ( dataRowName == null ) {
      throw new ParseException( "Required attribute 'datarow-name' is not defined" );
    }

    variableName = attrs.getValue( getUri(), "variable-name" );
    if ( variableName == null ) {
      variableName = dataRowName;
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  @Override
  public Object getObject() throws SAXException {
    return new ParameterMapping( dataRowName, variableName );
  }

  public String getVariableName() {
    return variableName;
  }

  public String getDataRowName() {
    return dataRowName;
  }
}
