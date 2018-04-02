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
 *  Copyright (c) 2006 - 2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.drilldown.swing;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUi;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownUiException;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.DrillDownModelWrapper;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.swing.TooltipAndTargetPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Swing analog for sugar-xaction-drilldown.xul dialog.
 *
 * @author Aleksandr Kozlov
 */
public class SwingRemoteDrillDownUi implements DrillDownUi {

  /** Form component identifiers for lookup. */
  enum ComponentLookup {
    SERVER_URL_CHECKBOX, LOGIN_BUTTON, SERVER_URL_FIELD, BROWSE_BUTTON, PATH_FIELD, PARAMETER_TABLE;
  };

  /** Component map to perform lookup by name. */
  private HashMap<ComponentLookup, Component> componentMap;

  /** DrillDown form controller. */
  private SwingRemoteDrillDownController controller;

  /** Top=level components container. */
  private JPanel editor;

  /** Server URL input field. */
  private JTextField serverUrlField;

  /** Path input field. */
  private JTextField pathField;

  /** Tooltip and Target panel. */
  private TooltipAndTargetPanel tatPanel;

  /** Wrapper for drill down model. */
  private DrillDownModelWrapper wrapper;

  /** Current active render-context holder. */
  private ReportDesignerContext reportDesignerContext;

  /**
   * Create Swing version of the sugar-xaction-drilldown.xul dialog.
   */
  public SwingRemoteDrillDownUi() {
    editor = createEditor();
    initComponentMap();
  }

  /**
   * Create the main panel of the dialog with all Swing component and layout them.
   *
   * @return the main panel of the dialog.
   */
  private JPanel createEditor() {
    final int layoutIndent = 2;

    JPanel editor = new JPanel();
    SpringLayout layout = new SpringLayout();
    editor.setLayout( layout );

    // "Include server URL in path" section
    JCheckBox serverUrlCheckBox = createServerUrlCheckBox();
    editor.add( serverUrlCheckBox );
    layout.putConstraint( SpringLayout.NORTH, serverUrlCheckBox, layoutIndent, SpringLayout.NORTH, editor );
    layout.putConstraint( SpringLayout.EAST, serverUrlCheckBox, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, serverUrlCheckBox, layoutIndent, SpringLayout.WEST, editor );

    // "Server URL" section
    JLabel serverUrlLabel = createServerUrlLabel();
    editor.add( serverUrlLabel );
    layout.putConstraint( SpringLayout.NORTH, serverUrlLabel, layoutIndent, SpringLayout.SOUTH, serverUrlCheckBox );
    layout.putConstraint( SpringLayout.EAST, serverUrlLabel, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, serverUrlLabel, layoutIndent, SpringLayout.WEST, editor );

    JButton loginButton = createLoginButton();
    editor.add( loginButton );
    layout.putConstraint( SpringLayout.NORTH, loginButton, layoutIndent, SpringLayout.SOUTH, serverUrlLabel );
    layout.putConstraint( SpringLayout.EAST, loginButton, 0, SpringLayout.EAST, editor );

    serverUrlField = createServerUrlField();
    editor.add( serverUrlField );
    layout.putConstraint( SpringLayout.NORTH, serverUrlField, layoutIndent, SpringLayout.SOUTH, serverUrlLabel );
    layout.putConstraint( SpringLayout.EAST, serverUrlField, -layoutIndent, SpringLayout.WEST, loginButton );
    layout.putConstraint( SpringLayout.SOUTH, serverUrlField, 0, SpringLayout.SOUTH, loginButton );
    layout.putConstraint( SpringLayout.WEST, serverUrlField, layoutIndent, SpringLayout.WEST, editor );

    // "Path" section
    JLabel pathLabel = createPathLabel();
    editor.add( pathLabel );
    layout.putConstraint( SpringLayout.NORTH, pathLabel, layoutIndent, SpringLayout.SOUTH, serverUrlField );
    layout.putConstraint( SpringLayout.EAST, pathLabel, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, pathLabel, layoutIndent, SpringLayout.WEST, editor );

    JButton browseButton = createBrowseButton();
    editor.add( browseButton );
    layout.putConstraint( SpringLayout.NORTH, browseButton, layoutIndent, SpringLayout.SOUTH, pathLabel );
    layout.putConstraint( SpringLayout.EAST, browseButton, 0, SpringLayout.EAST, editor );

    pathField = createPathField();
    editor.add( pathField );
    layout.putConstraint( SpringLayout.NORTH, pathField, layoutIndent, SpringLayout.SOUTH, pathLabel );
    layout.putConstraint( SpringLayout.EAST, pathField, -layoutIndent, SpringLayout.WEST, browseButton );
    layout.putConstraint( SpringLayout.SOUTH, pathField, 0, SpringLayout.SOUTH, browseButton );
    layout.putConstraint( SpringLayout.WEST, pathField, layoutIndent, SpringLayout.WEST, editor );

    /** "Target:" and "Tooltip:" input fields */
    tatPanel = new TooltipAndTargetPanel();
    tatPanel.getTargetComboBox().addItemListener( new ItemListener() {
      public void itemStateChanged( final ItemEvent e ) {
        getModel().setTargetFormula( e.getItem().toString() );
      }
    } );
    tatPanel.getTooltipPanel().addPropertyChangeListener( "formula", new PropertyChangeListener() {
      public void propertyChange( final PropertyChangeEvent evt ) {
        getModel().setTooltipFormula( evt.getNewValue().toString() );
      }
    } );
    editor.add( tatPanel );
    layout.putConstraint( SpringLayout.NORTH, tatPanel, layoutIndent, SpringLayout.SOUTH, pathField );
    layout.putConstraint( SpringLayout.EAST, tatPanel, 0, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.WEST, tatPanel, layoutIndent, SpringLayout.WEST, editor );

    // "Parameter table" section
    DrillDownParameterTable table = createParameterTable();
    editor.add( table );
    layout.putConstraint( SpringLayout.NORTH, table, layoutIndent, SpringLayout.SOUTH, tatPanel );
    layout.putConstraint( SpringLayout.EAST, table, layoutIndent, SpringLayout.EAST, editor );
    layout.putConstraint( SpringLayout.SOUTH, table, layoutIndent, SpringLayout.SOUTH, editor );
    layout.putConstraint( SpringLayout.WEST, table, layoutIndent, SpringLayout.WEST, editor );

    return editor;
  }

  /**
   * Create top checkbox with "Include server URL in path" label.
   *
   * @return created checkbox.
   */
  private JCheckBox createServerUrlCheckBox() {
    JCheckBox checkBox = new JCheckBox();
    checkBox.setName( ComponentLookup.SERVER_URL_CHECKBOX.name() );
    checkBox.setText( Messages.getString( "DrillDownDialog.ServerUrlCheckBox.Text" ) );
    return checkBox;
  }

  /**
   * Create a label of the "server URL" text field.
   *
   * @return created label.
   */
  private JLabel createServerUrlLabel() {
    JLabel label = new JLabel();
    label.setText( Messages.getString( "DrillDownDialog.ServerUrlInput.Label" ) );
    label.setMinimumSize( new Dimension( 0, 20 ) );
    return label;
  }

  /**
   * Create a "Login" button.
   *
   * @return created button.
   */
  private JButton createLoginButton() {
    JButton button = new JButton();
    button.setName( ComponentLookup.LOGIN_BUTTON.name() );
    button.setText( Messages.getString( "DrillDownDialog.LoginButton.Text" ) );
    return button;
  }

  /**
   * Create "server URL" text field.
   *
   * @return created text field.
   */
  private JTextField createServerUrlField() {
    JTextField field = new JTextField();
    field.setName( ComponentLookup.SERVER_URL_FIELD.name() );
    return field;
  }

  /**
   * Create a label for the path input.
   *
   * @return created label.
   */
  private JLabel createPathLabel() {
    JLabel label = new JLabel();
    label.setText( Messages.getString( "DrillDownDialog.PathInput.Label" ) );
    label.setMinimumSize( new Dimension( 0, 20 ) );
    return label;
  }

  /**
   * Create a "browse" button.
   *
   * @return created button.
   */
  private JButton createBrowseButton() {
    JButton button = new JButton();
    button.setName( ComponentLookup.BROWSE_BUTTON.name() );
    button.setText( Messages.getString( "DrillDownDialog.BrowseButton.Text" ) );
    return button;
  }

  /**
   * Create "path" test field.
   *
   * @return created text field.
   */
  private JTextField createPathField() {
    JTextField field = new JTextField();
    field.setName( ComponentLookup.PATH_FIELD.name() );
    return field;
  }

  /**
   * Create drill down parameter table.
   *
   * @return created table.
   */
  private DrillDownParameterTable createParameterTable() {
    DrillDownParameterTable table = new DrillDownParameterTable();
    table.setName( ComponentLookup.PARAMETER_TABLE.name() );
    table.setShowRefreshButton( true );
    table.setAllowCustomParameter( true );
    return table;
  }

  /**
   * Initialize component map for the lookup.
   */
  private void initComponentMap() {
    componentMap = new HashMap<>();
    for ( Component component : editor.getComponents() ) {
      try {
        String name = component.getName();
        if ( name != null ) {
          ComponentLookup lookupName = ComponentLookup.valueOf( name );
          componentMap.put( lookupName, component );
        }
      } catch ( IllegalArgumentException ex ) {
        // Simply skip component, do not handle the exception --Kaa
      }
    }
  }

  /**
   * Component lookup.
   *
   * @param lookupName name of the component.
   * @param <C> class of the component.
   * @return found component.
   * @throws IllegalStateException of component not found by provided name.
   */
  public <C extends Component> C getComponent( ComponentLookup lookupName ) {
    if ( componentMap.containsKey( lookupName ) ) {
      return (C) componentMap.get( lookupName );
    } else {
      throw new IllegalStateException( "Element " + lookupName + " wasn't initialized properly" );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Component getEditorPanel() {
    return editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DrillDownModel getModel() {
    return wrapper.getModel();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init( Component parent,
                    ReportDesignerContext reportDesignerContext,
                    DrillDownModel model,
                    String[] extraFields
  ) throws DrillDownUiException {
    this.reportDesignerContext = reportDesignerContext;
    this.wrapper = new DrillDownModelWrapper( model );
    model.setDrillDownConfig( SwingRemoteDrillDownUiProfile.DEFAULT_PROFILE );

    // Check model and init default values
    if ( getModel().isLimitedEditor() ) {
      tatPanel.hideContent();
    }
    if ( getModel().getTooltipFormula() != null ) {
      tatPanel.getTooltipPanel().setFormula( getModel().getTooltipFormula() );
    }
    if ( getModel().getTargetFormula() != null ) {
      tatPanel.getTargetComboBox().setSelectedItem( getModel().getTargetFormula() );
    }
    if ( getModel().getDrillDownPath() != null ) {
      pathField.setText( getModel().getDrillDownPath() );
    }

    controller = new SwingRemoteDrillDownController( this, reportDesignerContext, wrapper );
    controller.init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deactivate() {
    controller.deactivate();
  }
}
