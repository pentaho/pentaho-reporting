/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
