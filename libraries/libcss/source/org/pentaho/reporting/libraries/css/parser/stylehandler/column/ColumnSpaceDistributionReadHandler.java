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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.parser.stylehandler.column;

import org.pentaho.reporting.libraries.css.keys.column.ColumnSpaceDistribution;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 08.12.2005, 17:36:20
 *
 * @author Thomas Morgner
 */
public class ColumnSpaceDistributionReadHandler extends OneOfConstantsReadHandler
{
  public ColumnSpaceDistributionReadHandler()
  {
    super(false);
    addValue(ColumnSpaceDistribution.BETWEEN);
    addValue(ColumnSpaceDistribution.END);
    addValue(ColumnSpaceDistribution.INNER);
    addValue(ColumnSpaceDistribution.OUTER);
    addValue(ColumnSpaceDistribution.START);
  }
}
