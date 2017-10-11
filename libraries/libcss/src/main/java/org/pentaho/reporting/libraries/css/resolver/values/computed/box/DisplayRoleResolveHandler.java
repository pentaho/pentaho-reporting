/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
