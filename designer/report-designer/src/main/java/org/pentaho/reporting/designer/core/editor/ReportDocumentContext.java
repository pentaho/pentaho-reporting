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


package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.ReportDesignerDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.SharedElementRenderer;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.HashMap;

public interface ReportDocumentContext extends ReportDesignerDocumentContext<MasterReport> {
  void addReportDataChangeListener( ReportDataChangeListener l );

  void removeReportDataChangeListener( ReportDataChangeListener l );

  AbstractReportDefinition getReportDefinition();

  ZoomModel getZoomModel();

  ContextAwareDataSchemaModel getReportDataSchemaModel();

  SharedElementRenderer getSharedRenderer();

  // todo might be able to remove that one
  ResourceManager getResourceManager();

  HashMap<String, Object> getProperties();

  // todo codesmell
  boolean isBandedContext();

  // todo CodeSmell
  void resetChangeTracker();
}
