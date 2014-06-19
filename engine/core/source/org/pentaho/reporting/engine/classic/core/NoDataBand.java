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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;

/**
 * The No-Data-Band is printed if the current report has no data in its main data-table. It replaces the itemband for
 * such reports.
 *
 * @author Thomas Morgner
 */
public class NoDataBand extends AbstractRootLevelBand
{
  /**
   * Constructs a new band.
   */
  public NoDataBand()
  {
    setElementType(new NoDataBandType());
  }

  /**
   * Constructs a new band with the given pagebreak attributes. Pagebreak attributes have no effect on subbands.
   *
   * @param pagebreakAfter  defines, whether a pagebreak should be done after that band was printed.
   * @param pagebreakBefore defines, whether a pagebreak should be done before that band gets printed.
   */
  public NoDataBand(final boolean pagebreakBefore, final boolean pagebreakAfter)
  {
    super(pagebreakBefore, pagebreakAfter);
  }
}
