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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

public class LegacyChartsUiPlugin extends AbstractReportDesignerUiPlugin {
  public LegacyChartsUiPlugin() {
  }

  public String[] getOverlaySources() {
    return new String[] { "org/pentaho/reporting/designer/extensions/legacycharts/ui-overlay.xul" }; // NON-NLS
  }
}
