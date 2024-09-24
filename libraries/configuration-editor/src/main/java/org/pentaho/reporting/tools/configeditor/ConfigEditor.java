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

package org.pentaho.reporting.tools.configeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeModelException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

/**
 * The ConfigEditor can be used to edit the global jfreereport.properties files. These files provide global settings for
 * all reports and contain the system level configuration of JFreeReport.
 *
 * @author Thomas Morgner
 */
public class ConfigEditor extends JFrame {
  private static final Log logger = LogFactory.getLog( ConfigEditor.class );

  /**
   * An Action to handle close requests.
   */
  private class CloseAction extends AbstractAction {
    /**
     * DefaultConstructor.
     */
    protected CloseAction() {
      putValue( Action.NAME, getResources().getString( "action.exit.name" ) ); //$NON-NLS-1$
    }

    /**
     * Invoked when an action occurs. The action invokes System.exit(0).
     *
     * @param e the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      attempClose();
    }
  }

  /**
   * An action to handle save requests.
   */
  private class SaveAction extends AbstractAction {
    /**
     * DefaultConstructor.
     */
    protected SaveAction() {
      putValue( Action.NAME, getResources().getString( "action.save.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.save.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Saves the configuration.
     *
     * @param e the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      save();
    }
  }

  /**
   * An action to handle load requests.
   */
  private class LoadAction extends AbstractAction {
    /**
     * DefaultConstructor.
     */
    protected LoadAction() {
      putValue( Action.NAME, getResources().getString( "action.load.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.load.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Loads the configuration.
     *
     * @param e the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      load();
    }
  }

  /**
   * An action to handle new requests, which reset the report configuration.
   */
  private class NewAction extends AbstractAction {
    /**
     * DefaultConstructor.
     */
    protected NewAction() {
      putValue( Action.NAME, getResources().getString( "action.new.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.new.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Reset the configuration.
     *
     * @param e the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      reset();
    }
  }

  private class CloseHandler extends WindowAdapter {

    private CloseHandler() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      attempClose();
    }
  }

  private static final String PROPERTIES_FILE_EXTENSION = ".properties";
  /**
   * A constant defining that text should be escaped in a way which is suitable for property keys.
   */
  private static final int ESCAPE_KEY = 0;
  /**
   * A constant defining that text should be escaped in a way which is suitable for property values.
   */
  private static final int ESCAPE_VALUE = 1;
  /**
   * A constant defining that text should be escaped in a way which is suitable for property comments.
   */
  private static final int ESCAPE_COMMENT = 2;

  /**
   * A label that serves as status bar.
   */
  private JLabel statusHolder;
  /**
   * The resource bundle instance of this dialog.
   */
  private final ResourceBundleSupport resources;

  /**
   * The file chooser used to load and save the report configuration.
   */
  private JFileChooser fileChooser;

  private HierarchicalConfiguration configuration;
  private ConfigEditorPane editorPane;

  public ConfigEditor() throws ConfigTreeModelException {
    this( new HierarchicalConfiguration( ConfigEditorBoot.getInstance().getGlobalConfig() ),
      ConfigEditorBoot.getInstance() );
  }

  public ConfigEditor( final HierarchicalConfiguration configuration,
                       final AbstractBoot packageManager ) throws ConfigTreeModelException {
    this.configuration = configuration;
    resources = new ResourceBundleSupport( getLocale(), ConfigEditorBoot.BUNDLE_NAME,
      ObjectUtilities.getClassLoader( ConfigEditor.class ) );
    editorPane = new ConfigEditorPane( packageManager, true );
    editorPane.updateConfiguration( configuration );

    setTitle( resources.getString( "config-editor.title" ) ); //$NON-NLS-1$


    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( editorPane, BorderLayout.CENTER );
    contentPane.add( createButtonPane(), BorderLayout.SOUTH );

    final JPanel cPaneStatus = new JPanel();
    cPaneStatus.setLayout( new BorderLayout() );
    cPaneStatus.add( contentPane, BorderLayout.CENTER );
    cPaneStatus.add( createStatusBar(), BorderLayout.SOUTH );

    setContentPane( cPaneStatus );

    addWindowListener( new CloseHandler() );

  }

  /**
   * Returns the resource bundle of this editor for translating strings.
   *
   * @return the resource bundle.
   */
  protected ResourceBundleSupport getResources() {
    return resources;
  }

  /**
   * Creates the button pane to hold all control buttons.
   *
   * @return the created panel with all control buttons.
   */
  private JPanel createButtonPane() {
    final Action closeAction = new CloseAction();
    final Action saveAction = new SaveAction();
    final Action loadAction = new LoadAction();
    final Action newAction = new NewAction();

    final JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout( new GridLayout( 1, 4, 5, 5 ) );
    buttonHolder.add( new JButton( newAction ) );
    buttonHolder.add( new JButton( loadAction ) );
    buttonHolder.add( new JButton( saveAction ) );
    buttonHolder.add( new JButton( closeAction ) );

    final JPanel panel = new JPanel();
    panel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    panel.add( buttonHolder );
    return panel;
  }

  /**
   * Creates the statusbar for this frame. Use setStatus() to display text on the status bar.
   *
   * @return the status bar.
   */
  protected JPanel createStatusBar() {
    final JPanel statusPane = new JPanel();
    statusPane.setLayout( new BorderLayout() );
    statusPane.setBorder( BorderFactory.createLineBorder( UIManager.getDefaults().getColor(
      "controlShadow" ) ) ); //$NON-NLS-1$
    statusHolder = new JLabel( " " ); //$NON-NLS-1$
    statusPane.setMinimumSize( statusHolder.getPreferredSize() );
    statusPane.add( statusHolder, BorderLayout.WEST );

    return statusPane;
  }

  /**
   * Defines the text to be displayed on the status bar. Setting text will replace any other previously defined text.
   *
   * @param text the new statul bar text.
   */
  private void setStatusText( final String text ) {
    statusHolder.setText( text );
  }

  //  private String getStatusText ()
  //  {
  //    return statusHolder.getText();
  //  }

  /**
   * Loads the report configuration from a user selectable report properties file.
   */
  protected void load() {
    setStatusText( resources.getString( "ConfigEditor.USER_LOADING_FILE" ) ); //$NON-NLS-1$
    if ( fileChooser == null ) {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter = new FilesystemFilter
        ( ConfigEditor.PROPERTIES_FILE_EXTENSION, resources.getString(
          "config-editor.file-description.properties" ) ); //$NON-NLS-1$
      fileChooser.addChoosableFileFilter( filter );
      fileChooser.setMultiSelectionEnabled( false );
    }

    final int option = fileChooser.showOpenDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on .properties
      if ( StringUtils.endsWithIgnoreCase( selFileName, ConfigEditor.PROPERTIES_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + ConfigEditor.PROPERTIES_FILE_EXTENSION;
      }
      final Properties prop = new Properties();
      try {
        final InputStream in = new BufferedInputStream( new FileInputStream( selFileName ) );
        try {
          prop.load( in );
        } finally {
          in.close();
        }
      } catch ( IOException ioe ) {
        ConfigEditor.logger.debug( resources.getString( "ConfigEditor.ERROR_0003_FAILED_TO_LOAD_PROPERTIES",
          ioe.toString() ), ioe ); //$NON-NLS-1$
        setStatusText( resources.getString( "ConfigEditor.ERROR_0003_FAILED_TO_LOAD_PROPERTIES",
          ioe.getMessage() ) ); //$NON-NLS-1$
        return;
      }

      reset();

      final Enumeration keys = prop.keys();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        final String value = prop.getProperty( key );
        configuration.setConfigProperty( key, value );
      }

      editorPane.updateConfiguration( configuration );
      setStatusText( resources.getString( "ConfigEditor.USER_LOAD_PROPS_COMPLETE" ) ); //$NON-NLS-1$
    }
  }

  protected void reset() {
    editorPane.reset();
  }

  /**
   * Saves the report configuration to a user selectable report properties file.
   */
  protected void save() {
    setStatusText( resources.getString( "ConfigEditor.USER_SAVING" ) ); //$NON-NLS-1$
    editorPane.commit();

    if ( fileChooser == null ) {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter = new FilesystemFilter
        ( ConfigEditor.PROPERTIES_FILE_EXTENSION, resources.getString(
          "config-editor.file-description.properties" ) ); //$NON-NLS-1$
      fileChooser.addChoosableFileFilter( filter );
      fileChooser.setMultiSelectionEnabled( false );
    }

    final int option = fileChooser.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on xls
      if ( StringUtils.endsWithIgnoreCase( selFileName, ConfigEditor.PROPERTIES_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + ConfigEditor.PROPERTIES_FILE_EXTENSION;
      }
      write( selFileName );
    }
  }

  /**
   * Writes the configuration into the file specified by the given file name.
   *
   * @param filename the target file name
   */
  private void write( final String filename ) {
    final Properties prop = new Properties();
    final ArrayList<String> names = new ArrayList<String>();
    // clear all previously set configuration settings ...
    final Enumeration defaults = configuration.getConfigProperties();
    while ( defaults.hasMoreElements() ) {
      final String key = (String) defaults.nextElement();
      names.add( key );
      prop.setProperty( key, configuration.getConfigProperty( key ) );
    }

    Collections.sort( names );

    PrintWriter out = null;
    try {
      out = new PrintWriter( new OutputStreamWriter( new BufferedOutputStream( new FileOutputStream( filename ) ) ) );

      for ( int i = 0; i < names.size(); i++ ) {
        final String key = names.get( i );
        final String value = prop.getProperty( key );

        final String description = editorPane.getDescriptionForKey( key );
        if ( description != null ) {
          writeDescription( description, out );
        }
        saveConvert( key, ConfigEditor.ESCAPE_KEY, out );
        out.print( "=" ); //$NON-NLS-1$
        saveConvert( value, ConfigEditor.ESCAPE_VALUE, out );
        out.println();
      }
      out.close();
      setStatusText( resources.getString( "ConfigEditor.USER_SAVING_COMPLETE" ) ); //$NON-NLS-1$
    } catch ( IOException ioe ) {
      ConfigEditor.logger.debug( resources.getString( "ConfigEditor.ERROR_0004_FAILED_PROPERTIES_SAVE",
        ioe.toString() ), ioe ); //$NON-NLS-1$
      setStatusText( resources.getString( "ConfigEditor.ERROR_0004_FAILED_PROPERTIES_SAVE",
        ioe.getMessage() ) ); //$NON-NLS-1$
    } finally {
      if ( out != null ) {
        out.close();
      }
    }
  }

  /**
   * Writes a descriptive comment into the given print writer.
   *
   * @param text   the text to be written. If it contains more than one line, every line will be prepended by the
   *               comment character.
   * @param writer the writer that should receive the content.
   * @noinspection NestedAssignment
   */
  private void writeDescription( final String text, final PrintWriter writer ) {
    // check if empty content ... this case is easy ...
    if ( text.length() == 0 ) {
      return;
    }

    writer.println( "# " ); //$NON-NLS-1$

    try {
      final BufferedReader br = new BufferedReader( new StringReader( text ) );
      String s;
      while ( ( s = br.readLine() ) != null ) {
        writer.print( "# " ); //$NON-NLS-1$
        saveConvert( s, ConfigEditor.ESCAPE_COMMENT, writer );
        writer.println();
      }
      br.close();
    } catch ( IOException e ) {
      // does not happen, this is a string-reader
    }
  }

  /**
   * Performs the necessary conversion of an java string into a property escaped string.
   *
   * @param text       the text to be escaped
   * @param escapeMode the mode that should be applied.
   * @param writer     the writer that should receive the content.
   */
  private void saveConvert( final String text, final int escapeMode,
                            final PrintWriter writer ) {
    final char[] string = text.toCharArray();
    final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    for ( int x = 0; x < string.length; x++ ) {
      final char aChar = string[ x ];
      switch( aChar ) {
        case ' ': {
          if ( ( escapeMode != ConfigEditor.ESCAPE_COMMENT ) &&
            ( x == 0 || escapeMode == ConfigEditor.ESCAPE_KEY ) ) {
            writer.print( '\\' );
          }
          writer.print( ' ' );
          break;
        }
        case '\\': {
          writer.print( '\\' );
          writer.print( '\\' );
          break;
        }
        case '\t': {
          if ( escapeMode == ConfigEditor.ESCAPE_COMMENT ) {
            writer.print( aChar );
          } else {
            writer.print( '\\' );
            writer.print( 't' );
          }
          break;
        }
        case '\n': {
          writer.print( '\\' );
          writer.print( 'n' );
          break;
        }
        case '\r': {
          writer.print( '\\' );
          writer.print( 'r' );
          break;
        }
        case '\f': {
          if ( escapeMode == ConfigEditor.ESCAPE_COMMENT ) {
            writer.print( aChar );
          } else {
            writer.print( '\\' );
            writer.print( 'f' );
          }
          break;
        }
        case '#':
        case '"':
        case '!':
        case '=':
        case ':': {
          if ( escapeMode == ConfigEditor.ESCAPE_COMMENT ) {
            writer.print( aChar );
          } else {
            writer.print( '\\' );
            writer.print( aChar );
          }
          break;
        }
        default:
          if ( ( aChar < 0x0020 ) || ( aChar > 0x007e ) ) {
            writer.print( '\\' );
            writer.print( 'u' );
            writer.print( hexChars[ ( aChar >> 12 ) & 0xF ] );
            writer.print( hexChars[ ( aChar >> 8 ) & 0xF ] );
            writer.print( hexChars[ ( aChar >> 4 ) & 0xF ] );
            writer.print( hexChars[ aChar & 0xF ] );
          } else {
            writer.print( aChar );
          }
      }
    }
  }

  /**
   * Closes this frame and exits the JavaVM.
   */
  protected void attempClose() {
    System.exit( 0 );
  }

  /**
   * main Method to start the editor.
   *
   * @param args not used.
   */
  public static void main( final String[] args ) {
    try {
      ConfigEditorBoot.getInstance().start();
      final ConfigEditor ed = new ConfigEditor();
      ed.pack();
      ed.setVisible( true );
    } catch ( Exception e ) {
      final Messages messages = new Messages
        ( Locale.getDefault(), ConfigEditorBoot.BUNDLE_NAME, ObjectUtilities.getClassLoader( ConfigEditorBoot.class ) );
      final String message = messages.getString(
        "ConfigEditor.ERROR_0001_FAILED_TO_INITIALIZE" ); //$NON-NLS-1$
      logger.debug( message, e );
      JOptionPane.showMessageDialog( null, message );
    }
  }
}
