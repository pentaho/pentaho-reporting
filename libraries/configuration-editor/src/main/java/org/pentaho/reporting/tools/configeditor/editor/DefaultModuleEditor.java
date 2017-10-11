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
import org.pentaho.reporting.tools.configeditor.Messages;
import org.pentaho.reporting.tools.configeditor.model.ClassConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.EnumConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.ModuleNodeFactory;
import org.pentaho.reporting.tools.configeditor.util.ConfigDescriptionEntryComparator;
import org.pentaho.reporting.tools.configeditor.util.VerticalLayout;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The default module editor provides a simple default implementation to edit all configuration keys for a given
 * module.
 *
 * @author Thomas Morgner
 */
public class DefaultModuleEditor implements ModuleEditor {
  /**
   * Handles the selection of an checkbox and enables the assigned editor component.
   */
  private static class EnableAction implements ActionListener {
    /**
     * The key editor that is assigned to the checkbox.
     */
    private final KeyEditor editor;
    /**
     * The source checkbox, to which this action is assigned.
     */
    private final JCheckBox source;

    /**
     * Creates a new enable action for the given checkbox.
     *
     * @param ed     the key editor that is assigned to the checkbox
     * @param source the checkbox on which this action is registered-
     */
    private EnableAction( final KeyEditor ed, final JCheckBox source ) {
      this.editor = ed;
      this.source = source;
    }

    /**
     * Enables the key editor if the checkbox is selected.
     *
     * @param e not used
     */
    public void actionPerformed( final ActionEvent e ) {
      editor.setEnabled( source.isSelected() );
    }
  }

  /**
   * A editor carrier implementation used to collect all active editor components and their assigned checkboxes.
   */
  private static class EditorCarrier {
    /**
     * The editor component.
     */
    private final KeyEditor editor;
    /**
     * The checkbox that enabled the editor.
     */
    private final JCheckBox enableBox;

    /**
     * Creates a new carrier for the given editor and checkbox.
     *
     * @param editor    the editor component to which the checkbox is assigned
     * @param enableBox the checkbox that enabled the editor.
     */
    private EditorCarrier( final KeyEditor editor, final JCheckBox enableBox ) {
      this.editor = editor;
      this.enableBox = enableBox;
    }

    /**
     * Return the key editor.
     *
     * @return the editor.
     */
    public KeyEditor getEditor() {
      return editor;
    }

    /**
     * Resets the keyeditor and the checkbox to the default value.
     */
    public void reset() {
      enableBox.setSelected( editor.isDefined() );
      editor.setEnabled( editor.isDefined() );
    }
  }

  /**
   * The contentpane that holds all other components.
   */
  private final JPanel contentpane;
  /**
   * The rootpane holds the editor and the help area.
   */
  private final JSplitPane rootpane;
  /**
   * The rootpane holds the editor and the help area.
   */
  private final JEditorPane helpPane;
  /**
   * Externalized string access
   */
  private final Messages messages;
  /**
   * The report configuration used in this module editor.
   */
  private HierarchicalConfiguration config;
  /**
   * The list of keynames used in the editor.
   */
  private ConfigDescriptionEntry[] keyNames;
  /**
   * all active key editors as array.
   */
  private EditorCarrier[] activeEditors;
  /**
   * The module which we edit.
   */
  private Module module;
  /**
   * The package of the module implementation.
   */
  private String modulePackage;

  /**
   * Creates a new, uninitialized module editor.
   */
  public DefaultModuleEditor() {
    messages = Messages.getInstance();
    contentpane = new JPanel();
    contentpane.setLayout( new VerticalLayout() );

    helpPane = new JEditorPane();
    helpPane.setEditable( false );
    helpPane.setEditorKit( new HTMLEditorKit() );
    helpPane.setPreferredSize( new Dimension( 600, 100 ) );

    final JPanel toolbar = new JPanel();
    toolbar.setLayout( new BorderLayout() );
    toolbar.add( new JScrollPane( helpPane ) );
    toolbar.setMinimumSize( new Dimension( 100, 150 ) );

    rootpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
    try {
      // An ugly way of calling
      //   rootpane.setResizeWeight(1);
      final Method m = rootpane.getClass().getMethod
        ( "setResizeWeight", new Class[] { Double.TYPE } ); //$NON-NLS-1$
      m.invoke( rootpane, new Object[] { new Double( 1 ) } );
    } catch ( Exception e ) {
      // ignored ...
    }
    rootpane.setBottomComponent( toolbar );
    rootpane.setTopComponent( new JScrollPane( contentpane ) );
  }

  /**
   * Creates a new, initialized instance of the default module editor.
   *
   * @param module   the module that should be edited.
   * @param config   the report configuration used to fill the values of the editors.
   * @param keyNames the list of keynames this module editor should handle.
   * @return the created new editor instance.
   * @see ModuleEditor#createInstance(Module, HierarchicalConfiguration, ConfigDescriptionEntry[])
   */
  public ModuleEditor createInstance
  ( final Module module, final HierarchicalConfiguration config,
    final ConfigDescriptionEntry[] keyNames ) {
    final DefaultModuleEditor ed = new DefaultModuleEditor();
    ed.setConfig( config );
    ed.setKeyNames( keyNames );
    ed.setModule( module );
    ed.build();
    return ed;
  }

  /**
   * Returns the currently edited module.
   *
   * @return the module of this editor.
   */
  protected Module getModule() {
    return module;
  }

  /**
   * Defines the module for this editor.
   *
   * @param module the module, which should be handled by this editor.
   */
  protected void setModule( final Module module ) {
    if ( module == null ) {
      throw new NullPointerException();
    }
    this.module = module;
    this.modulePackage = ModuleNodeFactory.getPackage( module.getClass() );
  }

  /**
   * Checks whether this module editor can handle the given module.
   *
   * @param module the module to be edited.
   * @return true, if this editor may be used to edit the module, false otherwise.
   * @see ModuleEditor#canHandle(Module)
   */
  public boolean canHandle( final Module module ) {
    return true;
  }

  /**
   * Returns the report configuration used when loading values for this editor.
   *
   * @return the report configuration.
   */
  protected HierarchicalConfiguration getConfig() {
    return config;
  }

  /**
   * Defines the report configuration for this editor.
   *
   * @param config the report configuration.
   */
  protected void setConfig( final HierarchicalConfiguration config ) {
    this.config = config;
  }

  /**
   * Returns the key names used in this editor.
   *
   * @return the keynames.
   */
  protected ConfigDescriptionEntry[] getKeyNames() {
    return keyNames.clone();
  }

  /**
   * Defines the suggested key names for the module editor. This implementation will use these keys to build the key
   * editors.
   *
   * @param keyNames the key names for the editor.
   */
  protected void setKeyNames( final ConfigDescriptionEntry[] keyNames ) {
    this.keyNames = keyNames.clone();
    Arrays.sort( this.keyNames, new ConfigDescriptionEntryComparator() );
  }

  /**
   * Returns the editor component of the module. Calling this method is only valid on instances created with
   * createInstance.
   *
   * @return the editor component for the GUI.
   */
  public JComponent getComponent() {
    return rootpane;
  }

  /**
   * Creates a cut down display name for the given key. The display name will replace the module package with '~'.
   *
   * @param keyName the keyname which should be shortend.
   * @return the modified keyname suitable to be displayed as label.
   */
  private String createDisplayName( final String keyName ) {
    if ( keyName.startsWith( modulePackage ) ) {
      return '~' + keyName.substring( modulePackage.length() );
    }
    return keyName;
  }

  /**
   * Initializes all component for the module editor and creates and layouts all keyeditors.
   */
  protected void build() {
    final StringWriter writer = new StringWriter();
    writer.write( "<html><head><title></title></head><body>" ); //$NON-NLS-1$

    final JTextArea mangleInfo = new JTextArea();
    mangleInfo.setText( messages.getString( "DefaultModuleEditor.USER_GUIDE", modulePackage ) ); //$NON-NLS-1$
    mangleInfo.setName( "DescriptionArea" ); //$NON-NLS-1$
    mangleInfo.setMinimumSize( new Dimension( 100, 10 ) );
    mangleInfo.setEditable( false );
    mangleInfo.setLineWrap( true );
    mangleInfo.setWrapStyleWord( true );
    mangleInfo.setOpaque( false );
    contentpane.add( mangleInfo );

    final ConfigDescriptionEntry[] keyNames = getKeyNames();
    if ( keyNames == null ) {
      throw new IllegalStateException(
        messages.getString( "DefaultModuleEditor.ERROR_0001_NO_KEYS_DEFINED" ) ); //$NON-NLS-1$
    }

    activeEditors = new EditorCarrier[ keyNames.length ];
    for ( int i = 0; i < keyNames.length; i++ ) {
      final KeyEditor editor;
      final String displayName = createDisplayName( keyNames[ i ].getKeyName() );

      if ( keyNames[ i ] instanceof EnumConfigDescriptionEntry ) {
        final EnumConfigDescriptionEntry entry = (EnumConfigDescriptionEntry) keyNames[ i ];
        editor = new EnumKeyEditor( getConfig(), entry, displayName );
      } else if ( keyNames[ i ] instanceof ClassConfigDescriptionEntry ) {
        final ClassConfigDescriptionEntry entry = (ClassConfigDescriptionEntry) keyNames[ i ];
        editor = new ClassKeyEditor( getConfig(), entry, displayName );
      } else {
        editor = new TextKeyEditor( getConfig(), keyNames[ i ], displayName );
      }

      final JCheckBox enableCB = new JCheckBox();
      enableCB.addActionListener( new EnableAction( editor, enableCB ) );
      final JPanel panel = new JPanel();
      panel.setLayout( new BorderLayout() );
      panel.add( enableCB, BorderLayout.WEST );
      panel.add( editor.getComponent(), BorderLayout.CENTER );

      contentpane.add( panel );
      activeEditors[ i ] = new EditorCarrier( editor, enableCB );

      writer.write( "<h3><b>" ); //$NON-NLS-1$
      writer.write( keyNames[ i ].getKeyName() );
      writer.write( "</b></h3>" ); //$NON-NLS-1$
      writer.write( "<p>" ); //$NON-NLS-1$
      writer.write( keyNames[ i ].getDescription() );
      writer.write( "</p><hr>" ); //$NON-NLS-1$
    }

    int width = 0;
    for ( int i = 0; i < activeEditors.length; i++ ) {
      width = Math.max( width, activeEditors[ i ].getEditor().getLabelWidth() );
    }
    for ( int i = 0; i < activeEditors.length; i++ ) {
      activeEditors[ i ].getEditor().setLabelWidth( width );
    }
    writer.write( "</body></html>" ); //$NON-NLS-1$

    helpPane.setText( writer.toString() );

  }

  /**
   * Resets all keys to the values from the report configuration.
   */
  public void reset() {
    for ( int i = 0; i < activeEditors.length; i++ ) {
      activeEditors[ i ].reset();
    }
  }

  /**
   * Stores all values for the editor's keys into the report configuration.
   */
  public void store() {
    for ( int i = 0; i < activeEditors.length; i++ ) {
      activeEditors[ i ].getEditor().store();
    }
  }
}
