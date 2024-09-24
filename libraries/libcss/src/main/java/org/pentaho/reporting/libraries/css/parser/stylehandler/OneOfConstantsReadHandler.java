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

package org.pentaho.reporting.libraries.css.parser.stylehandler;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;

/**
 * Creation-Date: 26.11.2005, 19:16:43
 *
 * @author Thomas Morgner
 */
public abstract class OneOfConstantsReadHandler implements CSSValueReadHandler {
  private HashMap constants;
  private boolean autoAllowed;

  protected OneOfConstantsReadHandler( final boolean auto ) {
    constants = new HashMap();
    this.autoAllowed = auto;
    if ( autoAllowed ) {
      constants.put( "auto", CSSAutoValue.getInstance() );
    }
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    return lookupValue( value );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    return (CSSValue) constants.get( value.getStringValue().toLowerCase() );
  }

  protected void addValue( CSSConstant constant ) {
    constants.put( constant.getCSSText().toLowerCase(), constant );
  }

  public boolean isAutoAllowed() {
    return autoAllowed;
  }
}
