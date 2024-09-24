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

package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.tools.configeditor.ConfigEditorPane;

import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;

public class ConfigurationEditorDialog extends CommonDialog {
  private ConfigEditorPane editorPane;

  public ConfigurationEditorDialog() {
    init();
  }

  public ConfigurationEditorDialog( final Frame owner ) {
    super( owner );
    init();
  }

  public ConfigurationEditorDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  protected Component createContentPane() {
    setTitle( Messages.getString( "ConfigurationEditorDialog.Title" ) );

    editorPane = new ConfigEditorPane( ClassicEngineBoot.getInstance(), false );
    editorPane.setPreferredSize( new Dimension( 825, 425 ) );
    try {
      editorPane.load( false );
    } catch ( final IOException ioe ) {
      UncaughtExceptionsModel.getInstance().addException( ioe );
    }
    return editorPane;
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.Configuration";
  }

  public boolean performEdit( final ModifiableConfiguration config ) {
    final HashSet<String> existingKeys = new HashSet<String>();
    final HierarchicalConfiguration hconf = new HierarchicalConfiguration( config );
    if ( config instanceof HierarchicalConfiguration ) {
      final HierarchicalConfiguration oconf = (HierarchicalConfiguration) config;
      final Enumeration configProperties = oconf.getConfigProperties();
      while ( configProperties.hasMoreElements() ) {
        // mark all manually set properties as defined ..
        final String key = (String) configProperties.nextElement();
        hconf.setConfigProperty( key, oconf.getConfigProperty( key ) );
        existingKeys.add( key );
      }
    }
    editorPane.updateConfiguration( hconf );

    if ( performEdit() == false ) {
      return false;
    }

    editorPane.commit();

    final Enumeration configProperties = hconf.getConfigProperties();
    while ( configProperties.hasMoreElements() ) {
      final String key = (String) configProperties.nextElement();
      config.setConfigProperty( key, hconf.getConfigProperty( key ) );
      existingKeys.remove( key );
    }

    final String[] keys = existingKeys.toArray( new String[ existingKeys.size() ] );
    for ( int i = 0; i < keys.length; i++ ) {
      final String key = keys[ i ];
      config.setConfigProperty( key, null );
    }
    return true;
  }

  protected void performInitialResize() {
    setSize( 825, 425 );
    LibSwingUtil.centerDialogInParent( this );
  }

  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ConfigurationEditorDialog dialog = new ConfigurationEditorDialog();
    dialog.performEdit( ClassicEngineBoot.getInstance().getEditableConfig() );
  }


}
