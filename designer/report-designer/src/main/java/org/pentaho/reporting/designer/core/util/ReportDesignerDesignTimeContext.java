/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
