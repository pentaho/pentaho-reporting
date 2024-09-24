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

package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.editor.ElementPropertiesPanel;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.structuretree.AbstractReportTree;
import org.pentaho.reporting.designer.core.editor.structuretree.StructureTreePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class TreeSidePanel extends JComponent {
  private ReportDesignerContext context;
  private ElementPropertiesPanel attributeEditorPanel;
  private StructureAndDataTabChangeHandler structureAndDataTabChangeHandler;
  private JTabbedPane tabs;

  public TreeSidePanel( final ReportDesignerContext context,
                        final ElementPropertiesPanel attributeEditorPanel ) {
    this.context = context;
    this.attributeEditorPanel = attributeEditorPanel;
    this.init();
  }

  private void init() {
    // report structure
    final StructureTreePanel reportTree = new StructureTreePanel( AbstractReportTree.RenderType.REPORT );
    reportTree.setReportDesignerContext( context );
    final JPanel structurePanel = new JPanel( new BorderLayout() );
    final JComponent structureToolBar = context.getToolBar( "report-structure-toolbar" );// NON-NLS
    structurePanel.add( structureToolBar, BorderLayout.NORTH );
    structurePanel.add( reportTree, BorderLayout.CENTER );

    final JPanel dataPanel = new JPanel( new BorderLayout() );

    final JComponent dataToolBar = context.getToolBar( "report-fields-toolbar" );// NON-NLS
    dataPanel.add( dataToolBar, BorderLayout.NORTH );
    final StructureTreePanel dataTree = new StructureTreePanel( AbstractReportTree.RenderType.DATA );
    dataTree.setReportDesignerContext( context );
    dataPanel.add( dataTree, BorderLayout.CENTER );

    tabs = new JTabbedPane( JTabbedPane.TOP );
    structureAndDataTabChangeHandler = new StructureAndDataTabChangeHandler();
    tabs.addChangeListener( structureAndDataTabChangeHandler );
    tabs.add( Messages.getString( "StructureView.Structure" ), structurePanel );// NON-NLS
    tabs.add( Messages.getString( "StructureView.Data" ), dataPanel );// NON-NLS

    setLayout( new BorderLayout() );
    add( tabs, BorderLayout.CENTER );
  }

  public void showDataTab() {
    tabs.setSelectedIndex( 1 );
  }

  public void refreshTabPanel( final ElementPropertiesPanel attributeEditorPanel ) {
    structureAndDataTabChangeHandler
      .refreshTabPanel( attributeEditorPanel, context.getActiveContext(), false, true, true );
  }

  protected ElementPropertiesPanel getAttributeEditorPanel() {
    return attributeEditorPanel;
  }

  private class StructureAndDataTabChangeHandler implements ChangeListener {
    private StructureAndDataTabChangeHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      final ElementPropertiesPanel attributeEditorPanel = getAttributeEditorPanel();
      if ( attributeEditorPanel == null ) {
        return;
      }
      final ReportDocumentContext activeContext = context.getActiveContext();
      if ( activeContext == null ) {
        return;
      }
      final JTabbedPane tabs = (JTabbedPane) e.getSource();
      if ( tabs.getSelectedIndex() == 0 ) {
        refreshTabPanel( attributeEditorPanel, activeContext, true, false, false );
      } else {
        refreshTabPanel( attributeEditorPanel, activeContext, false, true, true );
      }
    }


    protected void refreshTabPanel( final ElementPropertiesPanel attributeEditorPanel,
                                    final ReportDocumentContext activeContext,
                                    final boolean attributeCard,
                                    final boolean datasourceCard,
                                    final boolean expressionCard ) {
      attributeEditorPanel.setAllowAttributeCard( attributeCard );
      attributeEditorPanel.setAllowDataSourceCard( datasourceCard );
      attributeEditorPanel.setAllowExpressionCard( expressionCard );
      attributeEditorPanel.reset( activeContext.getSelectionModel() );
    }
  }

}
