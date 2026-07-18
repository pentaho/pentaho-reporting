/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

public class TocUiPlugin extends AbstractReportDesignerUiPlugin {
  public TocUiPlugin() {
  }

  public String[] getOverlaySources() {
    return new String[] { "org/pentaho/reporting/designer/extensions/toc/ui-overlay.xul" };
  }
}
