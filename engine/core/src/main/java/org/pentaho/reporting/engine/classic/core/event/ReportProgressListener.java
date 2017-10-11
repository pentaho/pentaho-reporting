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
 * A report progress listener receives status events about the report processing status. This is mainly used to display
 * progress dialogs.
 *
 * @author Thomas Morgner
 */
public interface ReportProgressListener extends EventListener {
  /**
   * Receives a notification that the report processing has started.
   *
   * @param event
   *          the start event.
   */
  public void reportProcessingStarted( ReportProgressEvent event );

  /**
   * Receives a notification that the report processing made some progress.
   *
   * @param event
   *          the update event.
   */
  public void reportProcessingUpdate( ReportProgressEvent event );

  /**
   * Receives a notification that the report processing was finished.
   *
   * @param event
   *          the finish event.
   */
  public void reportProcessingFinished( ReportProgressEvent event );
}
