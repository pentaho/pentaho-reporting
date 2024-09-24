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

package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class InlineSubreportMarker {
  private SubReport subreport;
  private InstanceID insertationPointId;
  private SubReportProcessType processType;

  public InlineSubreportMarker( final SubReport subreport, final InstanceID insertationPointId,
      final SubReportProcessType processType ) {
    if ( subreport == null ) {
      throw new NullPointerException();
    }
    if ( processType == null ) {
      throw new NullPointerException();
    }
    this.subreport = subreport;
    this.insertationPointId = insertationPointId;
    this.processType = processType;
  }

  public SubReport getSubreport() {
    return subreport;
  }

  public InstanceID getInsertationPointId() {
    return insertationPointId;
  }

  public SubReportProcessType getProcessType() {
    return processType;
  }
}
