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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.DisplayRole;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 27.11.2005, 20:46:54
 *
 * @author Thomas Morgner
 */
public class DisplayRoleReadHandler extends OneOfConstantsReadHandler {
  public DisplayRoleReadHandler() {
    super( false );
    addValue( DisplayRole.BLOCK );
    addValue( DisplayRole.COMPACT );
    addValue( DisplayRole.INLINE );
    addValue( DisplayRole.LIST_ITEM );
    addValue( DisplayRole.NONE );
    addValue( DisplayRole.RUBY_BASE );
    addValue( DisplayRole.RUBY_BASE_GROUP );
    addValue( DisplayRole.RUBY_TEXT );
    addValue( DisplayRole.RUBY_TEXT_GROUP );
    addValue( DisplayRole.RUN_IN );
    addValue( DisplayRole.TABLE_CAPTION );
    addValue( DisplayRole.TABLE_CELL );
    addValue( DisplayRole.TABLE_COLUMN );
    addValue( DisplayRole.TABLE_COLUMN_GROUP );
    addValue( DisplayRole.TABLE_FOOTER_GROUP );
    addValue( DisplayRole.TABLE_HEADER_GROUP );
    addValue( DisplayRole.TABLE_ROW );
    addValue( DisplayRole.TABLE_ROW_GROUP );
    addValue( DisplayRole.CANVAS );
  }
}
