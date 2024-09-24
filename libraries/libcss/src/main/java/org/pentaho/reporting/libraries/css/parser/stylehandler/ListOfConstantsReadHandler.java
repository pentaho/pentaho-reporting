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
public abstract class ListOfConstantsReadHandler extends ListOfValuesReadHandler {
  private HashMap constants;
  private boolean autoAllowed;

  protected ListOfConstantsReadHandler( final boolean auto ) {
    this( Integer.MAX_VALUE, auto, false );
  }

  protected ListOfConstantsReadHandler( final int maxCount,
                                        final boolean auto,
                                        final boolean distinct ) {
    super( maxCount, distinct );
    constants = new HashMap();
    this.autoAllowed = auto;
    if ( autoAllowed ) {
      constants.put( "auto", CSSAutoValue.getInstance() );
    }
  }

  public void addValue( CSSConstant constant ) {
    constants.put( constant.getCSSText().toLowerCase(), constant );
  }

  protected CSSValue parseValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    return (CSSValue) constants.get( value.getStringValue().toLowerCase() );
  }

  public boolean isAutoAllowed() {
    return autoAllowed;
  }
}
