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

package org.pentaho.reporting.engine.classic.core.designtime;

import java.awt.Window;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

public interface DesignTimeContext {
  /**
   * The currently active report (or subreport).
   *
   * @return the active report.
   */
  public AbstractReportDefinition getReport();

  /**
   * The parent window in the GUI for showing modal dialogs.
   *
   * @return the window or null, if there is no parent.
   */
  public Window getParentWindow();

  public DataSchemaModel getDataSchemaModel();

  public DataFactoryContext getDataFactoryContext();

  public void error( Exception e );

  public void userError( Exception e );

  public LocaleSettings getLocaleSettings();

  public boolean isShowExpertItems();

  public boolean isShowDeprecatedItems();

  public MaturityLevel getMaturityLevel();
}
