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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * An ItemSum function, that is reset to zero on every new page.
 *
 * @author Thomas Morgner
 */
public class PageItemSumFunction extends ItemSumFunction
    implements PageEventListener
{
  /**
   * Default Constructor.
   */
  public PageItemSumFunction()
  {
  }

  /**
   * Handles the pageStartedEvent.
   *
   * @param event the report event.
   */
  public void pageStarted(final ReportEvent event)
  {
    clear();
  }

  /**
   * Handles the pageFinishedEvent. This method is emtpy and only here as implementation side effect.
   *
   * @param event the report event.
   */
  public void pageFinished(final ReportEvent event)
  {
  }
}
