/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
