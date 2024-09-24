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

package org.pentaho.reporting.libraries.css.parser.stylehandler.column;

import org.pentaho.reporting.libraries.css.keys.column.ColumnStyleKeys;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 03.12.2005, 21:56:57
 *
 * @author Thomas Morgner
 */
public class ColumnRuleReadHandler extends AbstractCompoundValueReadHandler {
  public ColumnRuleReadHandler() {
    addHandler( ColumnStyleKeys.COLUMN_RULE_COLOR, new ColumnRuleColorReadHandler() );
    addHandler( ColumnStyleKeys.COLUMN_RULE_STYLE, new ColumnRuleStyleReadHandler() );
    addHandler( ColumnStyleKeys.COLUMN_RULE_WIDTH, new ColumnRuleWidthReadHandler() );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    if ( unit.getLexicalUnitType() == LexicalUnit.SAC_INHERIT ) {
      Map map = new HashMap();
      map.put( ColumnStyleKeys.COLUMN_RULE_COLOR, CSSInheritValue.getInstance() );
      map.put( ColumnStyleKeys.COLUMN_RULE_STYLE, CSSInheritValue.getInstance() );
      map.put( ColumnStyleKeys.COLUMN_RULE_WIDTH, CSSInheritValue.getInstance() );
      return map;
    }
    return super.createValues( unit );
  }
}
