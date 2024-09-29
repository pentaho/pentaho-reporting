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
