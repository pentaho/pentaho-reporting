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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import org.pentaho.reporting.engine.classic.core.MasterReport;

/**
 * An export plug-in is a class that can work with the
 * {@link org.pentaho.reporting.engine.classic.core.modules.gui .base.actions.ExportAction} class to implement an export
 * function for reports.
 *
 * @author Thomas Morgner.
 */
public interface ExportActionPlugin extends ActionPlugin {
  /**
   * Exports a report.
   *
   * @param report
   *          the report.
   * @return A boolean.
   */
  public boolean performExport( MasterReport report );

}
