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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.event;

import java.util.EventListener;

/**
 * The PageEventListener gets informed of PageEvents.
 * <p/>
 * This is an extracted interface of the original ReportEventListener. As page events are only fired by some (page
 * sensitive) report processors, there is no need to support page events in the ReportEventListener interface.
 * <p/>
 * Functions that should be informed of page events should implement this interface.
 * <p/>
 * Information: The pageCanceled method is called, if a empty page was created and was removed from the report
 * afterwards.
 *
 * @author Thomas Morgner
 */
public interface PageEventListener extends EventListener {

  /**
   * Receives notification that a new page is being started.
   *
   * @param event
   *          The event.
   */
  public void pageStarted( ReportEvent event );

  /**
   * Receives notification that a page is completed.
   *
   * @param event
   *          The event.
   */
  public void pageFinished( ReportEvent event );
}
