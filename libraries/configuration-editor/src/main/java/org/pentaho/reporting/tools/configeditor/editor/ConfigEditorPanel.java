/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.editor;

import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.util.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

/**
 * The container component that is responsible for creating and managing the module editor for the currently selected
 * module.
 *
 * @author Thomas Morgner
 */
public class ConfigEditorPanel extends JPanel {
  /**
   * A component holding the module description.
   */
  private final JTextArea descriptionArea;
  /**
   * A component holding the name of the module.
   */
  private final JTextArea moduleNameField;
  /**
   * A component holding the producer of the current module.
   */
  private final JTextArea producerField;

  /**
   * The message format used to create the module name and version.
   */
  private final MessageFormat moduleNameFormat;
  /**
   * The container to hold the editor.
   */
  private final JPanel editorArea;

  /**
   * The current module editor (may be null).
   */
  private ModuleEditor moduleEditor;

  /**
   * Creates a new ConfigEditorPanel.
   */
  public ConfigEditorPanel() {
    moduleNameFormat = new MessageFormat( "{0} - Version {1}.{2}-{3}" ); //$NON-NLS-1$

    moduleNameField = new JTextArea();
    moduleNameField.setName( "ModuleNameField" ); //$NON-NLS-1$
    moduleNameField.setMinimumSize( new Dimension( 100, 10 ) );
    moduleNameField.setEditable( false );
    moduleNameField.setLineWrap( false );
    moduleNameField.setFont( new Font( "SansSerif", //$NON-NLS-1$
      Font.BOLD, moduleNameField.getFont().getSize() + 4 ) );

    producerField = new JTextArea();
    producerField.setName( "ProducerField" ); //$NON-NLS-1$
    producerField.setMinimumSize( new Dimension( 100, 10 ) );
    producerField.setEditable( false );
    producerField.setLineWrap( false );
    producerField.setWrapStyleWord( true );
    producerField.setFont( producerField.getFont().deriveFont( Font.ITALIC ) );
    producerField.setBackground( UIManager.getColor( "controlLtHighlight" ) ); //$NON-NLS-1$

    descriptionArea = new JTextArea();
    descriptionArea.setName( "DescriptionArea" ); //$NON-NLS-1$
    descriptionArea.setMinimumSize( new Dimension( 100, 10 ) );
    descriptionArea.setEditable( false );
    descriptionArea.setLineWrap( true );
    descriptionArea.setWrapStyleWord( true );
    descriptionArea.setBackground( UIManager.getColor( "controlShadow" ) ); //$NON-NLS-1$

    editorArea = new JPanel();
    editorArea.setLayout( new BorderLayout() );

    final JPanel contentArea = new JPanel();
    contentArea.setLayout( new VerticalLayout() );//this, BoxLayout.Y_AXIS));
    contentArea.add( moduleNameField );
    contentArea.add( producerField );
    contentArea.add( descriptionArea );

    setLayout( new BorderLayout() );
    add( contentArea, BorderLayout.NORTH );
    add( editorArea, BorderLayout.CENTER );
  }

  /**
   * Defines the currently edited module and initializes an module editor for that module.
   *
   * @param module  the module that should be edited.
   * @param config  the report configuration that supplies the values for the module.
   * @param entries a list of entries which should be edited.
   */
  public void editModule( final Module module,
                          final HierarchicalConfiguration config,
                          final ConfigDescriptionEntry[] entries ) {
    final Object[] params = new Object[ 4 ];
    params[ 0 ] = module.getName();
    params[ 1 ] = module.getMajorVersion();
    params[ 2 ] = module.getMinorVersion();
    params[ 3 ] = module.getPatchLevel();
    moduleNameField.setText( moduleNameFormat.format( params ) );
    producerField.setText( module.getProducer() );
    descriptionArea.setText( module.getDescription() );

    editorArea.removeAll();

    moduleEditor = EditorFactory.getInstance().getModule( module, config, entries );
    if ( moduleEditor != null ) {
      editorArea.add( moduleEditor.getComponent() );
      moduleEditor.reset();
    }
    invalidate();
  }

  /**
   * Resets the currently edited module to the default values from the report configuration.
   */
  public void reset() {
    if ( moduleEditor != null ) {
      moduleEditor.reset();
    }
  }

  /**
   * Stores all values from the module editor into the report configuration.
   */
  public void store() {
    if ( moduleEditor != null ) {
      moduleEditor.store();
    }
  }

}
