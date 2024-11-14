/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.event.async;

/**
 * Execution status.
 * Final statuses must not change according to the business logic.
 */
public enum AsyncExecutionStatus {

  QUEUED( Boolean.FALSE ),
  WORKING( Boolean.FALSE ),
  CONTENT_AVAILABLE( Boolean.FALSE ),
  //Can be overriden by SCHEDULED status
  FINISHED( Boolean.FALSE ),
  FAILED( Boolean.TRUE ),
  CANCELED( Boolean.TRUE ),
  PRE_SCHEDULED( Boolean.FALSE ),
  SCHEDULED( Boolean.TRUE );

  private final boolean isFinal;

  public boolean isFinal() {
    return isFinal;
  }

  AsyncExecutionStatus( final boolean isFinal ) {
    this.isFinal = isFinal;
  }


}
