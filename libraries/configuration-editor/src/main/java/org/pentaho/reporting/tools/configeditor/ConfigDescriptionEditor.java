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
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.tools.configeditor.model.ClassConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionModel;
import org.pentaho.reporting.tools.configeditor.model.EnumConfigDescriptionEntry;
import org.pentaho.reporting.tools.configeditor.model.TextConfigDescriptionEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * The config description editor is used to edit the configuration metadata used in the ConfigEditor to describe the
 * ReportConfiguration keys.
 *
 * @author Thomas Morgner
 */
public class ConfigDescriptionEditor extends JFrame {
  private static final Log logger = LogFactory.getLog( ConfigDescriptionEditor.class );

  private class CloseHandler extends WindowAdapter {

    private CloseHandler() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      attempExit();
    }
  }

  /**
   * A configuration key to define the Font used in the editor.
   */
  protected static final String EDITOR_FONT_KEY =
    "org.pentaho.reporting.engine.classic.core.modules.gui.config.EditorFont"; //$NON-NLS-1$
  /**
   * A configuration key to define the Font size used in the editor.
   */
  protected static final String EDITOR_FONT_SIZE_KEY =
    "org.pentaho.reporting.engine.classic.core.modules.gui.config.EditorFontSize"; //$NON-NLS-1$

  /**
   * An internal constant to activate the class detail editor.
   */
  private static final String CLASS_DETAIL_EDITOR_NAME = "Class"; //$NON-NLS-1$
  /**
   * An internal constant to activate the enumeration detail editor.
   */
  private static final String ENUM_DETAIL_EDITOR_NAME = "Enum"; //$NON-NLS-1$
  /**
   * An internal constant to activate the text detail editor.
   */
  private static final String TEXT_DETAIL_EDITOR_NAME = "Text"; //$NON-NLS-1$

  /**
   * Handles close requests in this editor.
   */
  private class CloseAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private CloseAction() {
      putValue( Action.NAME, getResources().getString( "action.exit.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the close request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      attempExit();
    }
  }

  /**
   * Handles save requests in this editor.
   */
  private class SaveAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private SaveAction() {
      putValue( Action.NAME, getResources().getString( "action.save.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.save.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the save request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      save();
    }
  }

  /**
   * Handles import requests in this editor. Imports try to build a new description model from a given report
   * configuration.
   */
  private class ImportAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private ImportAction() {
      putValue( Action.NAME, getResources().getString( "action.import.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.import.small-icon" ) ); //$NON-NLS-1$
      setEnabled( configurationToEdit != null );
    }

    /**
     * Handles the import request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( configurationToEdit == null ) {
        return;
      }

      final ConfigDescriptionModel model = getModel();
      model.importFromConfig( configurationToEdit );
      model.sort();
      setStatusText( getResources().getString( "config-description-editor.import-complete" ) ); //$NON-NLS-1$
    }
  }

  /**
   * Handles requests to add a new entry in this editor.
   */
  private class AddEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private AddEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.add-entry.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.add-entry.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the add-entry request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final TextConfigDescriptionEntry te =
        new TextConfigDescriptionEntry
          ( getResources().getString( "config-description-editor.unnamed-entry" ) ); //$NON-NLS-1$
      final ConfigDescriptionModel model = getModel();
      model.add( te );
      getEntryList().setSelectedIndex( model.getSize() - 1 );
    }
  }

  /**
   * Handles requests to remove an entry from this editor.
   */
  private class RemoveEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private RemoveEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.remove-entry.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.remove-entry.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the remove entry request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final int[] selectedEntries = getEntryList().getSelectedIndices();
      setSelectedEntry( null );
      final ConfigDescriptionModel model = getModel();
      model.removeAll( selectedEntries );
      getEntryList().clearSelection();
    }
  }

  /**
   * Handles load requests in this editor.
   */
  private class LoadAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private LoadAction() {
      putValue( Action.NAME, getResources().getString( "action.load.name" ) ); //$NON-NLS-1$
      putValue( Action.SMALL_ICON, getResources().getIcon( "action.load.small-icon" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the laod request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      load();
    }
  }

  /**
   * Handles update requests in the detail editor.
   */
  private class UpdateAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private UpdateAction() {
      putValue( Action.NAME, getResources().getString( "action.update.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the update request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      writeSelectedEntry();
    }
  }

  /**
   * Handles cancel requests in the detail editor.
   */
  private class CancelAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private CancelAction() {
      putValue( Action.NAME, getResources().getString( "action.cancel.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the cancel request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final ConfigDescriptionEntry ce = getSelectedEntry();
      setSelectedEntry( null );
      setSelectedEntry( ce );
    }
  }

  /**
   * Handles editor type selections within the detail editor.
   */
  private class SelectTypeAction extends AbstractAction {
    /**
     * the selected type.
     */
    private final int type;

    /**
     * Creates a new select type action for the given name and type.
     *
     * @param name the name of the action.
     * @param type the type that should be selected whenever this action gets called.
     */
    private SelectTypeAction( final String name, final int type ) {
      putValue( Action.NAME, name );
      this.type = type;
    }

    /**
     * Handles the select type request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      setEntryType( type );
    }
  }

  /**
   * Handles the list selection in the list of available config keys.
   */
  private class ConfigListSelectionListener implements ListSelectionListener {
    private boolean inUpdate;

    /**
     * Defaultconstructor.
     */
    private ConfigListSelectionListener() {
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      if ( inUpdate ) {
        return;
      }

      inUpdate = true;
      try {
        final ConfigDescriptionEntry newEntry;
        final int selectedIndex = getEntryList().getSelectedIndex();
        if ( selectedIndex == -1 ) {
          newEntry = null;
        } else {
          newEntry = getModel().get( selectedIndex );
        }

        final ConfigDescriptionEntry oldEntry = getSelectedEntry();
        if ( oldEntry != null ) {
          writeSelectedEntry();
        }

        if ( newEntry != null ) {
          final int index = getModel().indexOf( newEntry );
          if ( getEntryList().getSelectedIndex() != index ) {
            getEntryList().setSelectedIndex( index );
          }
          setSelectedEntry( newEntry );
        } else {
          getEntryList().setSelectedIndex( -1 );
          setSelectedEntry( null );
        }
      } finally {
        inUpdate = false;
      }

    }
  }

  /**
   * Handles list selections in the enumeration detail editor.
   */
  private class EnumerationListSelectionHandler implements ListSelectionListener {
    /**
     * Defaultconstructor.
     */
    private EnumerationListSelectionHandler() {
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      if ( getEnumEntryList().getSelectedIndex() == -1 ) {
        getEnumEntryEditField().setText( "" ); //$NON-NLS-1$
      } else {
        getEnumEntryEditField().setText( (String) getEnumEntryListModel().get
          ( getEnumEntryList().getSelectedIndex() ) );
      }
    }
  }

  /**
   * A ShortCut action to redefine the entries of the enumeration detail editor to represent a boolean value.
   */
  private class SetBooleanEnumEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private SetBooleanEnumEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.boolean.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the boolean redefinition request.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DefaultListModel enumEntryListModel = getEnumEntryListModel();
      enumEntryListModel.clear();
      getEnumEntryEditField().setText( "" ); //$NON-NLS-1$
      enumEntryListModel.addElement( "true" ); //$NON-NLS-1$
      enumEntryListModel.addElement( "false" ); //$NON-NLS-1$
    }
  }

  /**
   * Handles the request to add a new enumeration entry to the detail editor.
   */
  private class AddEnumEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private AddEnumEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.add-enum-entry.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the request to add a new enumeration entry to the detail editor.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      getEnumEntryListModel().addElement( getEnumEntryEditField().getText() );
    }
  }

  /**
   * Handles the request to remove an enumeration entry to the detail editor.
   */
  private class RemoveEnumEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private RemoveEnumEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.remove-enum-entry.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the request to remove an enumeration entry to the detail editor.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final JList enumEntryList = getEnumEntryList();
      final DefaultListModel enumEntryListModel = getEnumEntryListModel();
      final int[] selectedEntries = enumEntryList.getSelectedIndices();
      for ( int i = selectedEntries.length - 1; i >= 0; i-- ) {
        enumEntryListModel.remove( selectedEntries[ i ] );
      }
      enumEntryList.clearSelection();
    }
  }

  /**
   * Handles the request to update an enumeration entry to the detail editor.
   */
  private class UpdateEnumEntryAction extends AbstractAction {
    /**
     * Defaultconstructor.
     */
    private UpdateEnumEntryAction() {
      putValue( Action.NAME, getResources().getString( "action.update-enum-entry.name" ) ); //$NON-NLS-1$
    }

    /**
     * Handles the request to update an enumeration entry to the detail editor.
     *
     * @param e not used.
     */
    public void actionPerformed( final ActionEvent e ) {
      final int idx = getEnumEntryList().getSelectedIndex();
      if ( idx == -1 ) {
        getEnumEntryListModel().addElement( getEnumEntryEditField().getText() );
      } else {
        getEnumEntryListModel().setElementAt( getEnumEntryEditField().getText(), idx );
      }
    }
  }

  /**
   * An internal value to mark a text detail editor type.
   */
  private static final int TYPE_TEXT = 0;
  /**
   * An internal value to mark a class detail editor type.
   */
  private static final int TYPE_CLASS = 1;
  /**
   * An internal value to mark a enumeration detail editor type.
   */
  private static final int TYPE_ENUM = 2;

  /**
   * A radio button to select the text editor type for the current key.
   */
  private JRadioButton rbText;
  /**
   * A radio button to select the class editor type for the current key.
   */
  private JRadioButton rbClass;
  /**
   * A radio button to select the enumeration editor type for the current key.
   */
  private JRadioButton rbEnum;
  /**
   * The list model used to collect and manage all available keys.
   */
  private ConfigDescriptionModel model;
  /**
   * The name of the currently edited key.
   */
  private JTextField keyNameField;
  /**
   * The description field contains a short description of the current key.
   */
  private JTextArea descriptionField;
  /**
   * Allows to check, whether the key is a global (boot-time) key.
   */
  private JCheckBox globalField;
  /**
   * Allows to check, whether the key is hidden.
   */
  private JCheckBox hiddenField;
  /**
   * The name of the base class for the class detail editor.
   */
  private JTextField baseClassField;
  /**
   * contains the currently selected entry of the enumeration detail editor.
   */
  private JTextField enumEntryEditField;
  /**
   * contains all entries of the enumeration detail editor.
   */
  private DefaultListModel enumEntryListModel;
  /**
   * The current resource bundle used to translate the strings in this dialog.
   */
  private ResourceBundleSupport resources;
  /**
   * This cardlayout is used to display the currently selected detail editor.
   */
  private CardLayout detailManager;
  /**
   * Contains the detail editor manager.
   */
  private JPanel detailManagerPanel;
  /**
   * Contains the detail editor for the key.
   */
  private JPanel detailEditorPane;
  /**
   * The list is used to manage all available keys.
   */
  private JList entryList;
  /**
   * This list is used to manage the available entries of the enumeration detail editor.
   */
  private JList enumEntryList;
  /**
   * the currently selected description entry.
   */
  private ConfigDescriptionEntry selectedEntry;
  /**
   * The file chooser is used to select the file for the load/save operations.
   */
  private JFileChooser fileChooser;
  /**
   * Serves as statusline for the dialog.
   */
  private JLabel statusHolder;
  /**
   * The currently selected detail editor type.
   */
  private int type;

  private Configuration configurationToEdit;


  public ConfigDescriptionEditor() {
    this( ConfigEditorBoot.class.getName() );
  }

  /**
   * Constructs a ConfigDescriptionEditor that is initially invisible.
   */
  public ConfigDescriptionEditor( final AbstractBoot boot ) {
    if ( boot != null ) {
      boot.start();
      this.configurationToEdit = boot.getGlobalConfig();
    }

    init();
  }


  /**
   * Constructs a ConfigDescriptionEditor that is initially invisible.
   */
  public ConfigDescriptionEditor( final String booterClass ) {
    final AbstractBoot boot =
      ObjectUtilities.loadAndInstantiate( booterClass, ConfigDescriptionEditor.class, AbstractBoot.class );
    if ( boot != null ) {
      boot.start();
      this.configurationToEdit = boot.getGlobalConfig();
    }

    init();
  }

  private void init() {
    this.resources = Messages.getInstance();

    setTitle( resources.getString( "config-description-editor.title" ) ); //$NON-NLS-1$
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );

    detailEditorPane = createEditPane();
    final JSplitPane splitPane = new JSplitPane
      ( JSplitPane.HORIZONTAL_SPLIT, createEntryList(), detailEditorPane );

    contentPane.add( splitPane, BorderLayout.CENTER );
    contentPane.add( createButtonPane(), BorderLayout.SOUTH );

    final JPanel cPaneStatus = new JPanel();
    cPaneStatus.setLayout( new BorderLayout() );
    cPaneStatus.add( contentPane, BorderLayout.CENTER );
    cPaneStatus.add( createStatusBar(), BorderLayout.SOUTH );

    setContentPane( cPaneStatus );
    setEntryType( ConfigDescriptionEditor.TYPE_TEXT );
    setSelectedEntry( null );

    fileChooser = new JFileChooser();
    fileChooser.addChoosableFileFilter( new FilesystemFilter
      ( ".xml", resources.getString( "config-description-editor.xml-files" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    fileChooser.setMultiSelectionEnabled( false );

    setStatusText( resources.getString( "config-description-editor.welcome" ) ); //$NON-NLS-1$

    addWindowListener( new CloseHandler() );
  }

  /**
   * Creates and returns the entry list component that will hold all config description entries within a list.
   *
   * @return the created entry list.
   */
  private JPanel createEntryList() {
    final Action addEntryAction = new AddEntryAction();
    final Action removeEntryAction = new RemoveEntryAction();

    model = new ConfigDescriptionModel();
    entryList = new JList( model );
    entryList.addListSelectionListener( new ConfigListSelectionListener() );

    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable( false );
    toolbar.add( addEntryAction );
    toolbar.add( removeEntryAction );

    final JPanel panel = new JPanel();
    panel.setMinimumSize( new Dimension( 200, 0 ) );
    panel.setLayout( new BorderLayout() );
    panel.add( toolbar, BorderLayout.NORTH );
    panel.add( new JScrollPane
      ( entryList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED ), BorderLayout.CENTER );
    return panel;
  }

  /**
   * Returns the JList component containing all entries of the enumeration detail editor.
   *
   * @return the enumeration entry list.
   */
  protected JList getEnumEntryList() {
    return enumEntryList;
  }

  /**
   * Returns the text field containing the currently edited enumeration entry.
   *
   * @return the textfield containing the current entry.
   */
  protected JTextField getEnumEntryEditField() {
    return enumEntryEditField;
  }

  /**
   * Returns the List Model containing all entries of the current enumeration entry editor.
   *
   * @return the entry list.
   */
  protected DefaultListModel getEnumEntryListModel() {
    return enumEntryListModel;
  }

  /**
   * Returns the JList component containing all configuration entries.
   *
   * @return the entry list.
   */
  protected JList getEntryList() {
    return entryList;
  }

  /**
   * Creates a panel containing all dialog control buttons, like close, load, save and import.
   *
   * @return the button panel.
   */
  private JPanel createButtonPane() {
    final Action closeAction = new CloseAction();
    final Action saveAction = new SaveAction();
    final Action loadAction = new LoadAction();
    final Action importAction = new ImportAction();

    final JPanel panel = new JPanel();
    panel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

    final JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout( new GridLayout( 1, 4, 5, 5 ) );
    buttonHolder.add( new JButton( importAction ) );
    buttonHolder.add( new JButton( loadAction ) );
    buttonHolder.add( new JButton( saveAction ) );
    buttonHolder.add( new JButton( closeAction ) );

    panel.add( buttonHolder );
    return panel;
  }

  /**
   * Creates the detail editor panel. This panel will contain all specific editors for the keys.
   *
   * @return the detail editor panel.
   */
  private JPanel createEditPane() {

    final JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout( new GridLayout( 1, 4, 5, 5 ) );
    buttonHolder.add( new JButton( new CancelAction() ) );
    buttonHolder.add( new JButton( new UpdateAction() ) );

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    buttonPanel.add( buttonHolder );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( createDetailEditorPanel(), BorderLayout.CENTER );
    panel.add( buttonPanel, BorderLayout.SOUTH );
    return panel;
  }

  /**
   * Creates the enumeration detail editor.
   *
   * @return the enumeration detail editor.
   */
  private JPanel createEnumerationEditor() {
    enumEntryEditField = new JTextField();
    enumEntryListModel = new DefaultListModel();

    enumEntryList = new JList( enumEntryListModel );
    enumEntryList.addListSelectionListener( new EnumerationListSelectionHandler() );

    final JPanel listPanel = new JPanel();
    listPanel.setLayout( new BorderLayout() );
    listPanel.add( enumEntryEditField, BorderLayout.NORTH );
    listPanel.add( new JScrollPane( enumEntryList ), BorderLayout.CENTER );

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new GridLayout( 5, 1 ) );
    buttonPanel.add( new JButton( new AddEnumEntryAction() ) );
    buttonPanel.add( new JButton( new RemoveEnumEntryAction() ) );
    buttonPanel.add( new JButton( new UpdateEnumEntryAction() ) );
    buttonPanel.add( new JPanel() );
    buttonPanel.add( new JButton( new SetBooleanEnumEntryAction() ) );

    final JPanel buttonCarrier = new JPanel();
    buttonCarrier.setLayout( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
    buttonCarrier.add( buttonPanel );

    final JPanel editorPanel = new JPanel();
    editorPanel.setLayout( new BorderLayout() );
    editorPanel.add( listPanel, BorderLayout.CENTER );
    editorPanel.add( buttonCarrier, BorderLayout.EAST );
    return editorPanel;
  }

  /**
   * Creates the class detail editor.
   *
   * @return the class detail editor.
   */
  private JPanel createClassEditor() {
    baseClassField = new JTextField();
    final JLabel baseClassValidateMessage = new JLabel( " " );

    final JLabel textLabel = new JLabel
      ( resources.getString( "config-description-editor.baseclass" ) ); //$NON-NLS-1$
    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( textLabel, BorderLayout.WEST );
    panel.add( baseClassField, BorderLayout.CENTER );
    panel.add( baseClassValidateMessage, BorderLayout.SOUTH );

    final JPanel carrier = new JPanel();
    carrier.setLayout( new BorderLayout() );
    carrier.add( panel, BorderLayout.NORTH );
    return carrier;
  }

  /**
   * Creates the text detail editor.
   *
   * @return the text detail editor.
   */
  private JPanel createTextEditor() {
    final JLabel textLabel = new JLabel
      ( resources.getString( "config-description-editor.text-editor-message" ) ); //$NON-NLS-1$
    final JPanel panel = new JPanel();
    panel.setLayout( new FlowLayout() );
    panel.add( textLabel );
    return panel;
  }

  /**
   * Creates the common entry detail editor. This editor contains all shared properties.
   *
   * @return the common entry editor.
   */
  private JPanel createDetailEditorPanel() {
    final JLabel keyNameLabel = new JLabel
      ( resources.getString( "config-description-editor.keyname" ) ); //$NON-NLS-1$
    final JLabel descriptionLabel = new JLabel
      ( resources.getString( "config-description-editor.description" ) ); //$NON-NLS-1$
    final JLabel typeLabel = new JLabel( resources.getString( "config-description-editor.type" ) ); //$NON-NLS-1$
    final JLabel globalLabel = new JLabel( resources.getString( "config-description-editor.global" ) ); //$NON-NLS-1$
    final JLabel hiddenLabel = new JLabel( resources.getString( "config-description-editor.hidden" ) ); //$NON-NLS-1$

    hiddenField = new JCheckBox();
    globalField = new JCheckBox();
    final String font = ConfigEditorBoot.getInstance().getGlobalConfig().getConfigProperty
      ( ConfigDescriptionEditor.EDITOR_FONT_KEY, "Monospaced" ); //$NON-NLS-1$
    final int fontSize = ParserUtil.parseInt
      ( ConfigEditorBoot.getInstance().getGlobalConfig().getConfigProperty
        ( ConfigDescriptionEditor.EDITOR_FONT_SIZE_KEY ), 12 );
    descriptionField = new JTextArea();
    descriptionField.setFont( new Font( font, Font.PLAIN, fontSize ) );
    descriptionField.setLineWrap( true );
    descriptionField.setWrapStyleWord( true );
    keyNameField = new JTextField();

    final JPanel enumerationEditor = createEnumerationEditor();
    final JPanel textEditor = createTextEditor();
    final JPanel classEditor = createClassEditor();

    detailManagerPanel = new JPanel();
    detailManager = new CardLayout();
    detailManagerPanel.setLayout( detailManager );
    detailManagerPanel.add( classEditor, ConfigDescriptionEditor.CLASS_DETAIL_EDITOR_NAME );
    detailManagerPanel.add( textEditor, ConfigDescriptionEditor.TEXT_DETAIL_EDITOR_NAME );
    detailManagerPanel.add( enumerationEditor, ConfigDescriptionEditor.ENUM_DETAIL_EDITOR_NAME );

    final JPanel commonEntryEditorPanel = new JPanel();
    commonEntryEditorPanel.setLayout( new GridBagLayout() );
    commonEntryEditorPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 5 ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    commonEntryEditorPanel.add( keyNameLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    commonEntryEditorPanel.add( keyNameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    commonEntryEditorPanel.add( descriptionLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    gbc.ipady = 120;
    commonEntryEditorPanel.add( new JScrollPane
      ( descriptionField,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    commonEntryEditorPanel.add( globalLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    commonEntryEditorPanel.add( globalField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    commonEntryEditorPanel.add( hiddenLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    commonEntryEditorPanel.add( hiddenField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    commonEntryEditorPanel.add( typeLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    commonEntryEditorPanel.add( createTypeSelectionPane(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    gbc.ipadx = 120;
    commonEntryEditorPanel.add( detailManagerPanel, gbc );

    return commonEntryEditorPanel;
  }

  /**
   * Creates the type selection panel containing some radio buttons to define the detail editor type.
   *
   * @return the type selection panel.
   */
  private JPanel createTypeSelectionPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridLayout( 3, 1 ) );

    rbText = new JRadioButton( new SelectTypeAction
      ( resources.getString( "config-description-editor.type-text" ),
        ConfigDescriptionEditor.TYPE_TEXT ) ); //$NON-NLS-1$
    rbClass = new JRadioButton( new SelectTypeAction
      ( resources.getString( "config-description-editor.type-class" ),
        ConfigDescriptionEditor.TYPE_CLASS ) ); //$NON-NLS-1$
    rbEnum = new JRadioButton( new SelectTypeAction
      ( resources.getString( "config-description-editor.type-enum" ),
        ConfigDescriptionEditor.TYPE_ENUM ) ); //$NON-NLS-1$

    final ButtonGroup bg = new ButtonGroup();
    bg.add( rbText );
    bg.add( rbClass );
    bg.add( rbEnum );

    panel.add( rbText );
    panel.add( rbClass );
    panel.add( rbEnum );

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
   * Defines the status text for this dialog.
   *
   * @param text the new status text.
   */
  protected void setStatusText( final String text ) {
    statusHolder.setText( text );
  }

  /**
   * Returns the currently visible status text of this dialog.
   *
   * @return the status text.
   */
  protected String getStatusText() {
    return statusHolder.getText();
  }

  /**
   * Sets the entry type for the current config description entry. This also selects and activates the correct detail
   * editor for this type.
   *
   * @param type the type of the currently selected entry.
   */
  protected void setEntryType( final int type ) {
    this.type = type;
    if ( type == ConfigDescriptionEditor.TYPE_CLASS ) {
      detailManager.show( detailManagerPanel, ConfigDescriptionEditor.CLASS_DETAIL_EDITOR_NAME );
      rbClass.setSelected( true );
    } else if ( type == ConfigDescriptionEditor.TYPE_ENUM ) {
      detailManager.show( detailManagerPanel, ConfigDescriptionEditor.ENUM_DETAIL_EDITOR_NAME );
      rbEnum.setSelected( true );
    } else {
      detailManager.show( detailManagerPanel, ConfigDescriptionEditor.TEXT_DETAIL_EDITOR_NAME );
      rbText.setSelected( true );
    }
    invalidate();
  }

  /**
   * Returns the current entry type.
   *
   * @return the current entry type.
   */
  protected int getEntryType() {
    return type;
  }

  /**
   * Returns the currently select entry from the entry list model.
   *
   * @return the currently selected entry.
   */
  protected ConfigDescriptionEntry getSelectedEntry() {
    return selectedEntry;
  }

  /**
   * Defines the currently selected entry from the entry list model and updates the detail editor to reflect the data
   * from the entry.
   *
   * @param selectedEntry the selected entry.
   */
  protected void setSelectedEntry( final ConfigDescriptionEntry selectedEntry ) {
    this.selectedEntry = selectedEntry;

    enumEntryEditField.setText( "" ); //$NON-NLS-1$
    enumEntryListModel.clear();
    baseClassField.setText( "" ); //$NON-NLS-1$

    if ( this.selectedEntry == null ) {
      deepEnable( detailEditorPane, false );
    } else {
      deepEnable( detailEditorPane, true );
      keyNameField.setText( selectedEntry.getKeyName() );
      globalField.setSelected( selectedEntry.isGlobal() );
      hiddenField.setSelected( selectedEntry.isHidden() );
      descriptionField.setText( selectedEntry.getDescription() );
      if ( selectedEntry instanceof ClassConfigDescriptionEntry ) {
        final ClassConfigDescriptionEntry ce = (ClassConfigDescriptionEntry) selectedEntry;
        setEntryType( ConfigDescriptionEditor.TYPE_CLASS );
        if ( ce.getBaseClass() != null ) {
          baseClassField.setText( ce.getBaseClass().getName() );
        }
      } else if ( selectedEntry instanceof EnumConfigDescriptionEntry ) {
        final EnumConfigDescriptionEntry en = (EnumConfigDescriptionEntry) selectedEntry;
        final String[] enums = en.getOptions();
        for ( int i = 0; i < enums.length; i++ ) {
          enumEntryListModel.addElement( enums[ i ] );
        }
        setEntryType( ConfigDescriptionEditor.TYPE_ENUM );
      } else {
        setEntryType( ConfigDescriptionEditor.TYPE_TEXT );
      }
    }
  }

  /**
   * A utility method to enable or disable a component and all childs.
   *
   * @param comp  the component that should be enabled or disabled.
   * @param state the new enable state.
   */
  private void deepEnable( final Component comp, final boolean state ) {
    comp.setEnabled( state );
    if ( comp instanceof Container ) {
      final Container cont = (Container) comp;
      final Component[] childs = cont.getComponents();
      for ( int i = 0; i < childs.length; i++ ) {
        deepEnable( childs[ i ], state );
      }
    }
  }

  /**
   * Saves the config description model in a xml file.
   */
  protected void save() {
    fileChooser.setVisible( true );

    final int option = fileChooser.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      OutputStream out = null;
      try {
        out = new BufferedOutputStream( new FileOutputStream( fileChooser.getSelectedFile() ) );
        model.save( out, "UTF-8" ); //$NON-NLS-1$
        out.close();
        setStatusText( resources.getString( "config-description-editor.save-complete" ) ); //$NON-NLS-1$
      } catch ( Exception ioe ) {
        ConfigDescriptionEditor.logger.debug( "Failed", ioe ); //$NON-NLS-1$
        final String message = MessageFormat.format
          ( resources.getString( "config-description-editor.save-failed" ), //$NON-NLS-1$
            new Object[] { ioe.getMessage() } );
        setStatusText( message );
      } finally {
        if ( out != null ) {
          try {
            out.close();
          } catch ( IOException e ) {
            // ignored .. at least we tried it ..
          }
        }
      }
    }
  }

  /**
   * Loads the config description model from a xml file.
   */
  protected void load() {
    fileChooser.setVisible( true );

    final int option = fileChooser.showOpenDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      InputStream in = null;
      try {
        final FileInputStream fileIn = new FileInputStream( fileChooser.getSelectedFile() );
        in = new BufferedInputStream( fileIn );
        model.load( in );
        model.sort();
        setStatusText( resources.getString( "config-description-editor.load-complete" ) ); //$NON-NLS-1$
      } catch ( Exception ioe ) {
        ConfigDescriptionEditor.logger.debug( "Load Failed", ioe ); //$NON-NLS-1$
        final String message = MessageFormat.format
          ( resources.getString( "config-description-editor.load-failed" ), //$NON-NLS-1$
            new Object[] { ioe.getMessage() } );
        setStatusText( message );
      } finally {
        if ( in != null ) {
          try {
            in.close();
          } catch ( IOException e ) {
            // ignored .. at least we tried it ..
          }
        }
      }
    }
  }

  /**
   * Updates the currently selected entry from the values found in the detail editor.
   */
  protected void writeSelectedEntry() {
    final ConfigDescriptionEntry entry;
    switch( getEntryType() ) {
      case ConfigDescriptionEditor.TYPE_CLASS: {
        final ClassConfigDescriptionEntry ce = new ClassConfigDescriptionEntry( keyNameField.getText() );
        ce.setDescription( descriptionField.getText() );
        ce.setGlobal( globalField.isSelected() );
        ce.setHidden( hiddenField.isSelected() );
        try {
          final String className = baseClassField.getText();
          if ( className == null ) {
            ce.setBaseClass( Object.class );
          } else {
            final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
            final Class c = Class.forName( className, false, classLoader );
            ce.setBaseClass( c );
          }
        } catch ( Exception e ) {
          // invalid
          ConfigDescriptionEditor.logger.debug( "Class is invalid; defaulting to Object.class" ); //$NON-NLS-1$
          ce.setBaseClass( Object.class );
        }
        entry = ce;
        break;
      }
      case ConfigDescriptionEditor.TYPE_ENUM: {
        final EnumConfigDescriptionEntry ece = new EnumConfigDescriptionEntry( keyNameField.getText() );
        ece.setDescription( descriptionField.getText() );
        ece.setGlobal( globalField.isSelected() );
        ece.setHidden( hiddenField.isSelected() );
        final String[] enumEntries = new String[ enumEntryListModel.getSize() ];
        for ( int i = 0; i < enumEntryListModel.getSize(); i++ ) {
          enumEntries[ i ] = String.valueOf( enumEntryListModel.get( i ) );
        }
        ece.setOptions( enumEntries );
        entry = ece;
        break;
      }
      default: {
        final TextConfigDescriptionEntry te = new TextConfigDescriptionEntry( keyNameField.getText() );
        te.setDescription( descriptionField.getText() );
        te.setGlobal( globalField.isSelected() );
        te.setHidden( hiddenField.isSelected() );
        entry = te;
        break;
      }
    }

    final ConfigDescriptionEntry selectedEntry = getSelectedEntry();
    if ( selectedEntry == null ) {
      model.add( entry );
      return;
    }

    if ( ObjectUtilities.equal( selectedEntry.getKeyName(), entry.getKeyName() ) == false ) {
      model.remove( selectedEntry );
    }

    model.add( entry );
  }

  /**
   * Returns the config description model containing all metainformation about the configuration.
   *
   * @return the config description model.
   */
  protected ConfigDescriptionModel getModel() {
    return model;
  }

  /**
   * Handles the attemp to quit the program. This method shuts down the VM.
   */
  protected void attempExit() {
    System.exit( 0 );
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
   * The main entry point to start the detail editor.
   *
   * @param args ignored.
   */
  @SuppressWarnings( "UseOfSystemOutOrSystemErr" )
  public static void main( final String[] args ) {
    ConfigEditorBoot.getInstance().start();

    if ( args.length == 0 ) {
      final ConfigDescriptionEditor ed = new ConfigDescriptionEditor();
      ed.pack();
      ed.setVisible( true );
    } else {
      final AbstractBoot boot = AbstractBoot.loadBooter( args[ 0 ], ConfigDescriptionEditor.class );
      if ( boot == null ) {
        System.out.println( "Error: Unable to load the specified booter class: " + args[ 0 ] );
        System.exit( -1 );
      }
      final ConfigDescriptionEditor ed = new ConfigDescriptionEditor( boot );
      ed.pack();
      ed.setVisible( true );
    }
  }
}
