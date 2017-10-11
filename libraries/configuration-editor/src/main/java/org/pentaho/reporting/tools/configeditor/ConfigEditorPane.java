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

package org.pentaho.reporting.tools.configeditor;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.editor.ConfigEditorPanel;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeModel;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeModelException;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeModuleNode;
import org.pentaho.reporting.tools.configeditor.util.ConfigTreeRenderer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class ConfigEditorPane extends JPanel {

  /**
   * This class handles the tree selection events and activates the detail editors.
   */
  private class ModuleTreeSelectionHandler implements TreeSelectionListener {
    /**
     * DefaultConstructor.
     */
    protected ModuleTreeSelectionHandler() {
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final TreeSelectionEvent e ) {
      if ( configuration == null ) {
        throw new IllegalStateException( "Need a configuration" );
      }
      final TreePath path = e.getPath();
      final Object lastPathElement = path.getLastPathComponent();
      if ( lastPathElement instanceof ConfigTreeModuleNode ) {
        final ConfigTreeModuleNode node = (ConfigTreeModuleNode) lastPathElement;
        final ConfigEditorPanel detailEditorPane = getDetailEditorPane();
        detailEditorPane.store();
        detailEditorPane.editModule( node.getModule(), configuration, node.getAssignedKeys() );
      }
    }
  }


  /**
   * The detail editor for the currently selected tree node.
   */
  private ConfigEditorPanel detailEditorPane;

  /**
   * The tree model used to display the structure of the report configuration.
   */
  private ConfigTreeModel treeModel;

  /**
   * Need to keep a hold of this so that we can manipulate it when the tree is loaded.
   */
  private JTree tree;

  /**
   * The currently used report configuration.
   */
  private HierarchicalConfiguration configuration;
  private AbstractBoot packageManager;

  public ConfigEditorPane( final AbstractBoot packageManager, final boolean includeGlobals ) {
    this.packageManager = packageManager;
    detailEditorPane = new ConfigEditorPanel();

    setLayout( new BorderLayout() );
    final JSplitPane splitPane = new JSplitPane
      ( JSplitPane.HORIZONTAL_SPLIT, createEntryTree( includeGlobals ), detailEditorPane );
    splitPane.setDividerLocation( 250 );
    add( splitPane, BorderLayout.CENTER );
  }


  /**
   * Creates the JTree for the report configuration.
   *
   * @return the tree component.
   * @throws ConfigTreeModelException if the model could not be built.
   */
  private JComponent createEntryTree( final boolean includeGlobals ) {
    treeModel = new ConfigTreeModel( packageManager, includeGlobals );

    final TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
    selectionModel.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

    tree = new JTree( treeModel );
    tree.setSelectionModel( selectionModel );
    tree.setCellRenderer( new ConfigTreeRenderer() );
    tree.setRootVisible( false );
    tree.setShowsRootHandles( true );
    tree.addTreeSelectionListener( new ModuleTreeSelectionHandler() );

    return new JScrollPane
      ( tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
  }

  public void updateConfiguration( final HierarchicalConfiguration configuration ) {
    this.configuration = configuration;
    treeModel.updateConfiguration();

    // expand out the tree and select the first node.
    for ( int i = 0; i < tree.getRowCount(); i++ ) {
      tree.expandRow( i );
    }
    tree.setSelectionRow( 1 );
  }

  /**
   * Resets all values.
   */
  public void reset() {
    if ( configuration == null ) {
      return;
    }

    // clear all previously set configuration settings ...
    final Enumeration defaults = configuration.getConfigProperties();
    while ( defaults.hasMoreElements() ) {
      final String key = (String) defaults.nextElement();
      configuration.setConfigProperty( key, null );
    }
  }

  public String getDescriptionForKey( final String key ) {
    final ConfigDescriptionEntry entry = treeModel.getEntryForKey( key );
    if ( entry != null ) {
      return entry.getDescription();
    }
    return null;
  }

  public void load( final boolean append ) throws IOException {
    treeModel.load( append );
  }

  public void loadModel( final InputStream in, final boolean append ) throws IOException {
    treeModel.load( in, append );
  }

  /**
   * Returns the detail editor pane.
   *
   * @return the detail editor.
   */
  protected ConfigEditorPanel getDetailEditorPane() {
    return detailEditorPane;
  }

  public void commit() {
    detailEditorPane.store();
  }
}
