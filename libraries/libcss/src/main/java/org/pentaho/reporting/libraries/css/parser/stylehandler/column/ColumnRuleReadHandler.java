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
