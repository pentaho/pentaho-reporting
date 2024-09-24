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

package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.output.GroupOutputHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.GroupOutputHandlerFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.RelationalGroupOutputHandler;

public class RenderedCrosstabOutputHandlerFactory implements GroupOutputHandlerFactory {
  public RenderedCrosstabOutputHandlerFactory() {
  }

  public GroupOutputHandler getOutputHandler( final ReportEvent event, final int beginOfRow ) {
    final int type = event.getType();
    if ( ( type & ReportEvent.CROSSTABBING_TABLE ) == ReportEvent.CROSSTABBING_TABLE ) {
      return new CrosstabOutputHandler();
    } else if ( ( type & ReportEvent.CROSSTABBING_OTHER ) == ReportEvent.CROSSTABBING_OTHER ) {
      return new CrosstabOtherOutputHandler();
    } else if ( ( type & ReportEvent.CROSSTABBING_ROW ) == ReportEvent.CROSSTABBING_ROW ) {
      return new CrosstabRowOutputHandler();
    } else if ( ( type & ReportEvent.CROSSTABBING_COL ) == ReportEvent.CROSSTABBING_COL ) {
      return new CrosstabColumnOutputHandler();
    } else {
      return new RelationalGroupOutputHandler();
    }
  }
}
