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
