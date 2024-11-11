/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
