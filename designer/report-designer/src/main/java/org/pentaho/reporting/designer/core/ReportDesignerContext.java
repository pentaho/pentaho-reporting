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

package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.RecentFilesModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * A model that holds the current active render-context. A report-designer frame can have more than one render-context.
 * It also has a welcome and a preview pane.
 *
 * @author Thomas Morgner
 */
public interface ReportDesignerContext {
  public static final String REPORT_RENDER_CONTEXT_PROPERTY = "reportRenderContext";
  public static final String ACTIVE_CONTEXT_PROPERTY = "activeContext";
  public static final String STATUS_TEXT_PROPERTY = "statusText";
  public static final String SELECTION_WAITING_PROPERTY = "selectionWaiting";
  public static final String PAGE_PROPERTY = "page";
  public static final String PAGE_TOTAL_PROPERTY = "pageTotal";

  public RecentFilesModel getRecentFilesModel();

  public ReportDocumentContext getActiveContext();

  public ReportDesignerDocumentContext<?> getActiveDocument();

  public void setActiveDocument( ReportDesignerDocumentContext<?> context );

  public void setStatusText( String text );

  @Deprecated
  public void setPageNumbers( int page, int pageTotal );

  @Deprecated
  public int getPage();

  @Deprecated
  public int getPageTotal();

  @Deprecated
  public Component getParent();

  @Deprecated
  public JPopupMenu getPopupMenu( final String id );

  @Deprecated
  public JComponent getToolBar( final String id );

  public int addMasterReport( final MasterReport masterReportElement ) throws ReportDataFactoryException;

  public int addSubReport( final ReportDocumentContext parentReportContext,
                           final SubReport subReportElement ) throws ReportDataFactoryException;

  public ReportDesignerDocumentContext getDocumentContext( int index );

  public ReportRenderContext getReportRenderContext( int index );

  public int getReportRenderContextCount();

  public void removeReportRenderContext( int index );

  public void addPropertyChangeListener( final PropertyChangeListener listener );

  public void addPropertyChangeListener( final String property, final PropertyChangeListener listener );

  public void removePropertyChangeListener( final PropertyChangeListener listener );

  public void removePropertyChangeListener( final String property, final PropertyChangeListener listener );

  public ReportDesignerView getView();

  public int findActiveContextIndex();

  public boolean isSelectionWaiting();

  public void setSelectionWaiting( final boolean selectionWaiting );

  public GlobalAuthenticationStore getGlobalAuthenticationStore();
}
