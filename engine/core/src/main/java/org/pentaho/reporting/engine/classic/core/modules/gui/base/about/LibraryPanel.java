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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.about;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * A panel containing a table that lists the libraries used in a project.
 * <p/>
 * Used in the AboutFrame class.
 *
 * @author David Gilbert
 */
public class LibraryPanel extends JPanel {

  /**
   * The table.
   */
  private JTable table;

  /**
   * Constructs a LibraryPanel.
   *
   * @param libraries
   *          a list of libraries (represented by Library objects).
   */
  public LibraryPanel( final List libraries ) {

    setLayout( new BorderLayout() );

    final ResourceBundle resources = ResourceBundle.getBundle( SwingPreviewModule.BUNDLE_NAME );

    final String[] names =
        new String[] { resources.getString( "libraries-table.column.name" ),
          resources.getString( "libraries-table.column.version" ),
          resources.getString( "libraries-table.column.licence" ), resources.getString( "libraries-table.column.info" ) };
    final DefaultTableModel model = new DefaultTableModel( names, libraries.size() );
    for ( int i = 0; i < libraries.size(); i++ ) {
      final DependencyInformation depInfo = (DependencyInformation) libraries.get( i );
      model.setValueAt( depInfo.getName(), i, 0 );
      model.setValueAt( depInfo.getVersion(), i, 1 );
      model.setValueAt( depInfo.getLicenseName(), i, 2 );
      model.setValueAt( depInfo.getInfo(), i, 3 );
    }

    this.table = new JTable( model );
    add( new JScrollPane( this.table ) );

  }

  public LibraryPanel( final ProjectInformation projectInfo ) {
    this( LibraryPanel.getLibraries( projectInfo ) );
  }

  private static List getLibraries( final ProjectInformation info ) {
    if ( info == null ) {
      return new ArrayList();
    }
    final ArrayList libs = new ArrayList();
    LibraryPanel.collectLibraries( info, libs );
    return libs;
  }

  private static void collectLibraries( final ProjectInformation info, final List list ) {
    DependencyInformation[] libs = info.getLibraries();
    for ( int i = 0; i < libs.length; i++ ) {
      final DependencyInformation lib = libs[i];
      if ( list.contains( lib ) == false ) {
        // prevent duplicates, they look ugly ..
        list.add( lib );
        if ( lib instanceof ProjectInformation ) {
          LibraryPanel.collectLibraries( (ProjectInformation) lib, list );
        }
      }
    }

    libs = info.getOptionalLibraries();
    for ( int i = 0; i < libs.length; i++ ) {
      final DependencyInformation lib = libs[i];
      if ( list.contains( lib ) == false ) {
        // prevent duplicates, they look ugly ..
        list.add( lib );
        if ( lib instanceof ProjectInformation ) {
          LibraryPanel.collectLibraries( (ProjectInformation) lib, list );
        }
      }
    }
  }

  protected JTable getTable() {
    return table;
  }
}
