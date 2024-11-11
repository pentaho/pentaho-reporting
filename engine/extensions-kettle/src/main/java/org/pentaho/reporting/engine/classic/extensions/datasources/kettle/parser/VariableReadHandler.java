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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VariableReadHandler extends AbstractXmlReadHandler {
  private String formula;
  private String variableName;

  public VariableReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    this.formula = attrs.getValue( getUri(), "formula" );
    if ( formula == null ) {
      String dataRowName = attrs.getValue( getUri(), "datarow-name" );
      if ( dataRowName == null ) {
        throw new ParseException( "Required attribute 'datarow-name' is not defined" );
      }

      this.formula = '=' + FormulaUtil.quoteReference( dataRowName );
    }

    variableName = attrs.getValue( getUri(), "variable-name" );
    if ( variableName == null ) {
      variableName = attrs.getValue( getUri(), "datarow-name" );
      if ( variableName == null ) {
        throw new ParseException
          ( "Required attribute 'variable-name' is not defined, and legacy 'data-row' name is also undefined." );
      }
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public FormulaParameter getObject() throws SAXException {
    return new FormulaParameter( variableName, formula );
  }
}
