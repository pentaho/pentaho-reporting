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
