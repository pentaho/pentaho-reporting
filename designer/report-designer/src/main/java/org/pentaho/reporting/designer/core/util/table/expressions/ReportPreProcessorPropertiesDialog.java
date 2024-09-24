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

package org.pentaho.reporting.designer.core.util.table.expressions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class ReportPreProcessorPropertiesDialog extends CommonDialog {
  private static class ReportPreProcessorPropertiesEditorPanel extends JPanel {
    private ReportPreProcessorPropertiesTableModel dataModel;
    private ElementMetaDataTable table;
    private SortHeaderPanel headerPanel;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     */
    public ReportPreProcessorPropertiesEditorPanel() {
      setLayout( new BorderLayout() );

      dataModel = new ReportPreProcessorPropertiesTableModel();

      table = new ElementMetaDataTable();
      table.setModel( dataModel );

      headerPanel = new SortHeaderPanel( dataModel );

      add( headerPanel, BorderLayout.NORTH );
      add( new JScrollPane( table ), BorderLayout.CENTER );
    }

    public ReportPreProcessor getData() {
      return dataModel.getData();
    }

    public void setData( final ReportPreProcessor elements ) {
      dataModel.setData( elements );
    }

    protected void updateDesignerContext( final ReportDesignerContext newContext ) {
      table.setReportDesignerContext( newContext );
    }

    protected void updateSelection( final ReportPreProcessor model ) {
      if ( model == null ) {
        dataModel.setData( null );
      } else {
        dataModel.setData( model );
      }
    }

    protected void updateActiveContext( final ReportRenderContext newContext ) {
      if ( newContext == null ) {
        dataModel.setData( null );
      }
    }

    /**
     * Sets whether or not this component is enabled. A component that is enabled may respond to user input, while a
     * component that is not enabled cannot respond to user input.  Some components may alter their visual
     * representation when they are disabled in order to provide feedback to the user that they cannot take input.
     * <p>Note: Disabling a component does not disable it's children.
     * <p/>
     * <p>Note: Disabling a lightweight component does not prevent it from receiving MouseEvents.
     *
     * @param enabled true if this component should be enabled, false otherwise
     * @see java.awt.Component#isEnabled
     * @see java.awt.Component#isLightweight
     */
    public void setEnabled( final boolean enabled ) {
      super.setEnabled( enabled );
      table.setEnabled( enabled );
      headerPanel.setEnabled( enabled );
    }
  }

  private ReportPreProcessorPropertiesEditorPanel editorPanel;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner.  A shared, hidden
   * frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public ReportPreProcessorPropertiesDialog()
    throws HeadlessException {
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner.  If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the <code>Frame</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public ReportPreProcessorPropertiesDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public ReportPreProcessorPropertiesDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ReportPreProcessorProperties";
  }

  protected Component createContentPane() {
    editorPanel = new ReportPreProcessorPropertiesEditorPanel();
    return editorPanel;
  }

  public ReportPreProcessor editExpression( final ReportPreProcessor input ) {
    editorPanel.setData( (ReportPreProcessor) input.clone() );

    if ( performEdit() ) {
      return editorPanel.getData();
    } else {
      return input;
    }
  }
}
