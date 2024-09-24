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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModelFactory;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.libraries.designtime.swing.settings.DefaultLocaleSettings;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

public class DefaultDesignTimeContext implements DesignTimeContext {
  private final DesignTimeDataFactoryContext dataFactoryContext;
  private final AbstractReportDefinition report;
  private final LocaleSettings localeSettings;
  private Window parentWindow;
  private ContextAwareDataSchemaModel dataSchemaModel;

  public DefaultDesignTimeContext( final AbstractReportDefinition report ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    this.report = report;
    this.localeSettings = new DefaultLocaleSettings();
    this.dataFactoryContext = new DesignTimeDataFactoryContext();
  }

  /**
   * The currently active report (or subreport).
   *
   * @return the active report.
   */
  public AbstractReportDefinition getReport() {
    return report;
  }

  public void setParentWindow( final Window parentWindow ) {
    this.parentWindow = parentWindow;
  }

  /**
   * The parent window in the GUI for showing modal dialogs.
   *
   * @return the window or null, if there is no parent.
   */
  public Window getParentWindow() {
    return parentWindow;
  }

  public DataSchemaModel getDataSchemaModel() {
    if ( dataSchemaModel == null ) {
      final ContextAwareDataSchemaModelFactory factory =
          ClassicEngineBoot.getInstance().getObjectFactory().get( ContextAwareDataSchemaModelFactory.class );
      dataSchemaModel = factory.create( report );
    }
    return dataSchemaModel;
  }

  public void error( final Exception e ) {
    e.printStackTrace();
  }

  public void userError( final Exception e ) {
    e.printStackTrace();
  }

  public LocaleSettings getLocaleSettings() {
    return localeSettings;
  }

  public boolean isShowExpertItems() {
    return true;
  }

  public boolean isShowDeprecatedItems() {
    return true;
  }

  public DesignTimeDataFactoryContext getDataFactoryContext() {
    return dataFactoryContext;
  }

  public MaturityLevel getMaturityLevel() {
    return MaturityLevel.Snapshot;
  }
}
