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

package org.pentaho.reporting.engine.classic.extensions.drilldown.devtools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownModule;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.FormulaLinkCustomizer;
import org.pentaho.reporting.engine.classic.extensions.drilldown.PatternLinkCustomizer;
import org.pentaho.reporting.engine.classic.extensions.drilldown.parser.DrillDownProfileCollection;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class DrillDownProfileEditor extends CommonDialog {
  private static final Log logger = LogFactory.getLog( DrillDownProfileEditor.class );

  private class NewAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private NewAction() {
      putValue( NAME, "New" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      create();
    }
  }

  private class RemoveAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemoveAction() {
      putValue( NAME, "Remove" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DrillDownProfile o = (DrillDownProfile) profileList.getSelectedValue();
      if ( o == null ) {
        return;
      }
      drillDownProfiles.removeElement( o );

    }
  }

  private class EditAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditAction() {
      putValue( NAME, "Edit" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DrillDownProfile o = (DrillDownProfile) profileList.getSelectedValue();
      if ( o == null ) {
        return;
      }

      final DrillDownProfile drillDownProfile = edit( o );
      if ( drillDownProfile == o ) {
        return;
      }

      final int i = drillDownProfiles.indexOf( o );
      drillDownProfiles.removeElement( o );
      drillDownProfiles.add( i, drillDownProfile );
    }
  }

  private class LoadAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private LoadAction() {
      putValue( NAME, "Load" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final CommonFileChooser chooser = FileChooserService.getInstance().getFileChooser( "drilldown-profiles" );
      if ( chooser.showDialog( DrillDownProfileEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }
      final File selectedFile = chooser.getSelectedFile();
      if ( selectedFile != null ) {
        load( selectedFile );
      }
    }
  }

  private class SaveAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private SaveAction() {
      putValue( NAME, "Save" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final CommonFileChooser chooser = FileChooserService.getInstance().getFileChooser( "drilldown-profiles" );
      if ( chooser.showDialog( DrillDownProfileEditor.this, JFileChooser.SAVE_DIALOG ) == false ) {
        return;
      }
      final File selectedFile = chooser.getSelectedFile();
      if ( selectedFile != null ) {
        save( selectedFile );
      }
    }
  }

  private static class DrillDownProfileListRenderer extends DefaultListCellRenderer {
    private DrillDownProfileListRenderer() {
    }

    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      if ( value instanceof DrillDownProfile ) {
        final DrillDownProfile profile = (DrillDownProfile) value;
        final String valueText = String.format( "%s", profile.getName() );
        return super.getListCellRendererComponent( list, valueText, index, isSelected, cellHasFocus );
      }
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }
  }

  private DefaultListModel drillDownProfiles;
  private JList profileList;

  /**
   * Creates a new modal dialog.
   */
  public DrillDownProfileEditor() {
    super();
    init();
  }

  public DrillDownProfileEditor( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public DrillDownProfileEditor( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected String getDialogId() {
    return "extensions.drilldown.DrillDownProfileEditor";
  }

  protected Component createContentPane() {
    drillDownProfiles = new DefaultListModel();
    profileList = new JList( drillDownProfiles );
    profileList.setCellRenderer( new DrillDownProfileListRenderer() );

    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    buttonPane.add( new JButton( new NewAction() ) );
    buttonPane.add( new JButton( new EditAction() ) );
    buttonPane.add( new JButton( new RemoveAction() ) );
    buttonPane.add( Box.createHorizontalStrut( 10 ) );
    buttonPane.add( new JButton( new LoadAction() ) );
    buttonPane.add( new JButton( new SaveAction() ) );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( buttonPane, BorderLayout.NORTH );
    contentPane.add( new JScrollPane( profileList ), BorderLayout.CENTER );
    return contentPane;
  }

  public void load( final File file ) {
    try {
      final ResourceManager resourceManager = new ResourceManager();
      final Resource resource = resourceManager.createDirectly( file, DrillDownProfileCollection.class );
      final DrillDownProfileCollection typeCollection = (DrillDownProfileCollection) resource.getResource();
      final DrillDownProfile[] types = typeCollection.getData();
      for ( int i = 0; i < types.length; i++ ) {
        final DrillDownProfile metaData = types[ i ];
        if ( metaData != null ) {
          drillDownProfiles.addElement( metaData );
        }
      }
    } catch ( Exception e ) {
      DrillDownProfileEditor.logger.error( "Failed:", e );
    }
  }

  public DrillDownProfile edit( final DrillDownProfile profile ) {
    final AbstractDrillDownEditor drillDownEditor;
    if ( PatternLinkCustomizer.class.isAssignableFrom( profile.getLinkCustomizerType() ) ) {
      drillDownEditor = new PatternDrillDownEditor( this );
    } else if ( FormulaLinkCustomizer.class.isAssignableFrom( profile.getLinkCustomizerType() ) ) {
      drillDownEditor = new FormulaDrillDownEditor( this );
    } else {
      return profile;
    }

    return drillDownEditor.performEdit( profile );
  }

  public void create() {
    final int retval = JOptionPane.showOptionDialog( this, "Select link customizer type", "Select Link Customizer Type",
      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
      new Class[] { FormulaLinkCustomizer.class, PatternLinkCustomizer.class }, FormulaLinkCustomizer.class );
    if ( retval == 0 ) {
      drillDownProfiles.addElement( new DrillDownProfile( FormulaLinkCustomizer.class ) );
    } else if ( retval == 1 ) {
      drillDownProfiles.addElement( new DrillDownProfile( PatternLinkCustomizer.class ) );
    }
  }

  public void save( final File file ) {
    try {
      final DefaultTagDescription tags = new DefaultTagDescription();
      tags.setDefaultNamespace( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE );
      tags.setNamespaceHasCData( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, false );
      tags.setElementHasCData( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "attribute", true );

      final XmlWriter w = new XmlWriter( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" ), tags );
      w.writeXmlDeclaration( "UTF-8" );

      final AttributeList rootList = new AttributeList();
      rootList.addNamespaceDeclaration( "", DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE );
      w.writeTag( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "drilldown-profiles", rootList, XmlWriter.OPEN );

      final Object[] objects = drillDownProfiles.toArray();
      for ( int i = 0; i < objects.length; i++ ) {
        final DrillDownProfile object = (DrillDownProfile) objects[ i ];

        final AttributeList profileAttrs = new AttributeList();
        profileAttrs.setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "name", object.getName() );
        profileAttrs.setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "class",
          object.getLinkCustomizerType().getName() );
        profileAttrs
          .setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "bundle-name", object.getBundleLocation() );
        profileAttrs
          .setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "expert", String.valueOf( object.isExpert() ) );
        profileAttrs
          .setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "hidden", String.valueOf( object.isHidden() ) );
        profileAttrs.setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "deprecated",
          String.valueOf( object.isDeprecated() ) );
        profileAttrs.setAttribute( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "preferred",
          String.valueOf( object.isPreferred() ) );

        w.writeTag( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "drilldown-profile", profileAttrs, XmlWriter.OPEN );

        final String[] attrNames = object.getAttributes();
        for ( int j = 0; j < attrNames.length; j++ ) {
          final String attrName = attrNames[ j ];
          final String attrValue = object.getAttribute( attrName );

          w.writeTag( DrillDownModule.DRILLDOWN_PROFILE_NAMESPACE, "attribute", "name", attrName, XmlWriter.OPEN );
          w.writeTextNormalized( attrValue, false );
          w.writeCloseTag();
        }

        w.writeCloseTag();
      }

      w.writeCloseTag();
      w.close();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();

    DrillDownProfileEditor editor = new DrillDownProfileEditor();
    editor.pack();
    LibSwingUtil.centerFrameOnScreen( editor );
    editor.setVisible( true );

  }
}
