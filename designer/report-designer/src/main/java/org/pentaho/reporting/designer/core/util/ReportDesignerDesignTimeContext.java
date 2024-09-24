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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import java.awt.*;

public class ReportDesignerDesignTimeContext implements DesignTimeContext {
  private ReportDesignerContext designerContext;
  private ReportDocumentContext activeContext;

  public ReportDesignerDesignTimeContext( final ReportDesignerContext designerContext ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    this.designerContext = designerContext;
    this.activeContext = this.designerContext.getActiveContext();
    if ( activeContext == null ) {
      throw new NullPointerException();
    }
  }

  public AbstractReportDefinition getReport() {
    return activeContext.getReportDefinition();
  }

  public Window getParentWindow() {
    final Component component = designerContext.getView().getParent();
    if ( component instanceof Window ) {
      return (Window) component;
    }
    return LibSwingUtil.getWindowAncestor( component );
  }

  public DataSchemaModel getDataSchemaModel() {
    return activeContext.getReportDataSchemaModel();
  }

  public void error( final Exception e ) {
    UncaughtExceptionsModel.getInstance().addException( e );
  }

  public void userError( final Exception e ) {
    UncaughtExceptionsModel.getInstance().addException( e );
  }

  public LocaleSettings getLocaleSettings() {
    return WorkspaceSettings.getInstance();
  }

  public boolean isShowExpertItems() {
    return WorkspaceSettings.getInstance().isShowExpertItems();
  }

  public boolean isShowDeprecatedItems() {
    return WorkspaceSettings.getInstance().isShowDeprecatedItems();
  }

  public MaturityLevel getMaturityLevel() {
    return WorkspaceSettings.getInstance().getMaturityLevel();
  }

  public DataFactoryContext getDataFactoryContext() {
    return new DesignTimeDataFactoryContext( activeContext.getContextRoot() );
  }
}
