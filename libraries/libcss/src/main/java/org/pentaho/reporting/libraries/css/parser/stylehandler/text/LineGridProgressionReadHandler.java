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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.LineGridProgression;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 03.12.2005, 19:50:46
 *
 * @author Thomas Morgner
 */
public class LineGridProgressionReadHandler extends OneOfConstantsReadHandler {
  public LineGridProgressionReadHandler() {
    super( false );
    addValue( LineGridProgression.LINE_HEIGHT );
    addValue( LineGridProgression.TEXT_HEIGHT );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue length = CSSValueFactory.createLengthValue( value );
    if ( length != null ) {
      return length;
    }
    return super.lookupValue( value );
  }
}
