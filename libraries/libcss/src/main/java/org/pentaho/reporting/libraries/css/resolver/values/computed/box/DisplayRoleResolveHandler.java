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

package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.keys.box.DisplayRole;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class DisplayRoleResolveHandler extends ConstantsResolveHandler {
  public DisplayRoleResolveHandler() {
    addNormalizeValue( DisplayRole.BLOCK );
    addNormalizeValue( DisplayRole.COMPACT );
    addNormalizeValue( DisplayRole.INLINE );
    addNormalizeValue( DisplayRole.LIST_ITEM );
    addNormalizeValue( DisplayRole.NONE );
    addNormalizeValue( DisplayRole.RUBY_BASE );
    addNormalizeValue( DisplayRole.RUBY_BASE_GROUP );
    addNormalizeValue( DisplayRole.RUBY_TEXT );
    addNormalizeValue( DisplayRole.RUBY_TEXT_GROUP );
    addNormalizeValue( DisplayRole.RUN_IN );
    addNormalizeValue( DisplayRole.TABLE_CAPTION );
    addNormalizeValue( DisplayRole.TABLE_CELL );
    addNormalizeValue( DisplayRole.TABLE_COLUMN );
    addNormalizeValue( DisplayRole.TABLE_COLUMN_GROUP );
    addNormalizeValue( DisplayRole.TABLE_FOOTER_GROUP );
    addNormalizeValue( DisplayRole.TABLE_HEADER_GROUP );
    addNormalizeValue( DisplayRole.TABLE_ROW );
    addNormalizeValue( DisplayRole.TABLE_ROW_GROUP );

    setFallback( DisplayRole.INLINE );
  }

}
