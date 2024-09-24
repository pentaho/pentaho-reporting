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

package org.pentaho.reporting.engine.classic.wizard.ui.xul;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.libraries.designtime.swing.settings.DefaultLocaleSettings;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import java.awt.*;

public class DefaultWizardDesignTimeContext implements DesignTimeContext {
  private static final Log logger = LogFactory.getLog( DefaultWizardDesignTimeContext.class );
  private WizardEditorModel editorModel;
  private Window parentWindow;
  private DesignTimeContext parentContext;
  private LocaleSettings localeSettings;

  public DefaultWizardDesignTimeContext( final WizardEditorModel editorModel, final Window parentWindow ) {
    this( editorModel, parentWindow, null );
  }

  public DefaultWizardDesignTimeContext( final WizardEditorModel editorModel,
                                         final Window parentWindow,
                                         final DesignTimeContext parentContext ) {
    this.editorModel = editorModel;
    this.parentWindow = parentWindow;
    this.parentContext = parentContext;
    if ( parentContext != null ) {
      this.localeSettings = parentContext.getLocaleSettings();
    } else {
      this.localeSettings = new DefaultLocaleSettings();
    }
  }

  public LocaleSettings getLocaleSettings() {
    return localeSettings;
  }

  public AbstractReportDefinition getReport() {
    return editorModel.getReportDefinition();
  }

  public Window getParentWindow() {
    return parentWindow;
  }

  public DataSchemaModel getDataSchemaModel() {
    return editorModel.getDataSchema();
  }

  public void error( final Exception e ) {
    ExceptionDialog.showExceptionDialog( parentWindow, "Error", e.getMessage(), e );
    logger.error( "Context received fatal error", e );
  }

  public void userError( final Exception e ) {
    ExceptionDialog.showExceptionDialog( parentWindow, "Non-Fatal Error", e.getMessage(), e );
    logger.info( "Context received user error", e );
  }

  public boolean isShowExpertItems() {
    if ( parentContext != null ) {
      return parentContext.isShowExpertItems();
    }
    return true;
  }

  public boolean isShowDeprecatedItems() {
    if ( parentContext != null ) {
      return parentContext.isShowDeprecatedItems();
    }
    return true;
  }

  public DataFactoryContext getDataFactoryContext() {
    if ( parentContext != null ) {
      return parentContext.getDataFactoryContext();
    }
    return new DesignTimeDataFactoryContext();
  }

  public MaturityLevel getMaturityLevel() {
    return parentContext.getMaturityLevel();
  }
}
