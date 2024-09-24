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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.openformula.ui.util.FieldDefinitionCellRenderer;
import org.pentaho.openformula.ui.util.FieldSelectorDialog;
import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.DateFormatModel;
import org.pentaho.reporting.designer.core.settings.NumberFormatModel;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.designer.core.util.GroupSelectorDialog;
import org.pentaho.reporting.designer.core.util.QuerySelectorDialog;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;
import org.pentaho.reporting.libraries.designtime.swing.EmptyValueListCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.NonFilteringPlainDocument;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.EventObject;

public abstract class AbstractStringValueCellEditor extends JPanel implements CellEditor {

  protected class ExtendedEditorAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    protected ExtendedEditorAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final String valueRole = getValueRole();
      final JComboBox comboBox = getComboBox();
      if ( RESOURCE_VALUE_ROLE.equals( valueRole ) ) {
        final FileFilter[] filters = {
          new FilesystemFilter( ".properties", // NON-NLS
            Messages.getString( "BundledResourceEditor.PropertiesTranslations" ) ),
          new FilesystemFilter( new String[] { ".xml", ".report", ".prpt", ".prpti", ".prptstyle" }, // NON-NLS
            Messages.getString( "BundledResourceEditor.Resources" ), true ),
          new FilesystemFilter( new String[] { ".gif", ".jpg", ".jpeg", ".png", ".svg", ".wmf" }, // NON-NLS
            Messages.getString( "BundledResourceEditor.Images" ), true ),
        };

        final CommonFileChooser chooser = FileChooserService.getInstance().getFileChooser( "resources" );
        chooser.setFilters( filters );
        final String text = getTextField().getText();
        if ( StringUtils.isEmpty( text ) == false ) {
          chooser.setSelectedFile( new File( text ) );
        }

        if ( chooser.showDialog( AbstractStringValueCellEditor.this, JFileChooser.OPEN_DIALOG ) ) {
          final File file = chooser.getSelectedFile();
          if ( file == null ) {
            return;
          }

          final ReportRenderContext reportRenderContext = getReportContext();
          if ( reportRenderContext == null ) {
            getTextField().setText( file.getPath() );
          } else {
            final File reportContextFile = DesignTimeUtil.getContextAsFile( reportRenderContext.getReportDefinition() );
            final String path;
            if ( reportContextFile != null ) {
              path = IOUtils.getInstance().createRelativePath( file.getPath(), reportContextFile.getAbsolutePath() );
            } else {
              path = file.getPath();
            }
            getTextField().setText( path );
          }
        }
      } else if ( FIELD_VALUE_ROLE.equals( valueRole ) ) {
        final Window window = LibSwingUtil.getWindowAncestor( AbstractStringValueCellEditor.this );
        final FieldSelectorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new FieldSelectorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new FieldSelectorDialog( (Dialog) window );
        } else {
          editorDialog = new FieldSelectorDialog();
        }

        final FieldDefinition[] fields = getFields();
        final String selectedItem = (String) comboBox.getSelectedItem();
        FieldDefinition selected = null;
        if ( selectedItem != null ) {
          for ( int i = 0; i < fields.length; i++ ) {
            final FieldDefinition field = fields[ i ];
            if ( selectedItem.equals( field.getName() ) ) {
              selected = field;
              break;
            }
          }
        }
        final FieldDefinition fieldDefinition = editorDialog.performEdit( fields, selected );
        if ( fieldDefinition != null ) {
          comboBox.setSelectedItem( fieldDefinition.getName() );
        }
      } else if ( QUERY_VALUE_ROLE.equals( valueRole ) ) {
        final Window window = LibSwingUtil.getWindowAncestor( AbstractStringValueCellEditor.this );
        final QuerySelectorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new QuerySelectorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new QuerySelectorDialog( (Dialog) window );
        } else {
          editorDialog = new QuerySelectorDialog();
        }

        final String selectedQuery =
          editorDialog.performEdit( getQueryNames(), (String) comboBox.getSelectedItem() );
        if ( editorDialog.isConfirmed() ) {
          comboBox.setSelectedItem( selectedQuery );
        }
      } else if ( GROUP_VALUE_ROLE.equals( valueRole ) ) {
        final Window window = LibSwingUtil.getWindowAncestor( AbstractStringValueCellEditor.this );
        final GroupSelectorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new GroupSelectorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new GroupSelectorDialog( (Dialog) window );
        } else {
          editorDialog = new GroupSelectorDialog();
        }

        final String originalGroup = (String) comboBox.getSelectedItem();
        final String selectedGroup = editorDialog.performEdit( getGroups(), originalGroup );
        if ( editorDialog.isConfirmed() ) {
          comboBox.setSelectedItem( selectedGroup );
        }
      } else if ( FORMULA_VALUE_ROLE.equals( valueRole ) ) {
        final FormulaEditorDialog editorDialog =
          GUIUtils.createFormulaEditorDialog( getReportDesignerContext(), AbstractStringValueCellEditor.this );

        final String originalFormula = (String) comboBox.getSelectedItem();
        final String formula = editorDialog.editFormula( originalFormula, getFields() );
        if ( formula != null ) {
          comboBox.setSelectedItem( formula );
        }
      } else if ( NUMBER_FORMAT_VALUE_ROLE.equals( valueRole ) || DATE_FORMAT_VALUE_ROLE.equals( valueRole ) ) {
        final Window window = LibSwingUtil.getWindowAncestor( AbstractStringValueCellEditor.this );
        final TextAreaPropertyEditorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new TextAreaPropertyEditorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new TextAreaPropertyEditorDialog( (Dialog) window );
        } else {
          editorDialog = new TextAreaPropertyEditorDialog();
        }
        final String originalFormula = (String) comboBox.getSelectedItem();
        final String text = editorDialog.performEdit( originalFormula );
        if ( editorDialog.isConfirmed() ) {
          comboBox.setSelectedItem( text );
        }
      } else {
        final Window window = LibSwingUtil.getWindowAncestor( AbstractStringValueCellEditor.this );
        final TextAreaPropertyEditorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new TextAreaPropertyEditorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new TextAreaPropertyEditorDialog( (Dialog) window );
        } else {
          editorDialog = new TextAreaPropertyEditorDialog();
        }

        final JTextComponent textField = getTextField();
        final String originalValue = textField.getText();
        final String text = editorDialog.performEdit( originalValue );
        if ( editorDialog.isConfirmed() ) {
          textField.setText( text );
        }
      }

      stopCellEditing();
    }
  }

  protected class SelectionAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SelectionAction() {
      putValue( Action.NAME, UtilMessages.getInstance().getString( "AbstractStringValueCellEditor.SelectValue" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( filterEvents ) {
        return;
      }
      stopCellEditing();
    }
  }

  protected class CancelAction extends AbstractAction {
    public CancelAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      cancelCellEditing();
    }
  }

  private class InsertNewLineAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private InsertNewLineAction() {
      putValue( Action.NAME, UtilMessages.getInstance().getString( "AbstractStringValueCellEditor.InsertNewLine" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final JTextComponent textComponent = getTextField();
      final int position = textComponent.getCaretPosition();
      try {
        textComponent.getDocument().insertString( position, "\n", null );
      } catch ( BadLocationException e1 ) {
        // yeah, whatever
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  private class ReportModelChangeHandler implements ReportModelListener {
    private ReportModelChangeHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      // any node change must cause the editing to stop
      cancelCellEditing();
    }
  }

  protected static final String CONFIRM_EDITOR = "confirmEditor";
  protected static final String POPUP_EDITOR = "popupEditor";
  protected static final String CANCEL_EDITOR = "cancelEditor";
  protected static final String NEWLINE_EDITOR = "newlineEditor";

  private static final String RESOURCE_VALUE_ROLE = "Resource";
  private static final String FIELD_VALUE_ROLE = "Field";
  private static final String QUERY_VALUE_ROLE = "Query";
  private static final String GROUP_VALUE_ROLE = "Group";
  private static final String FORMULA_VALUE_ROLE = "Formula";
  private static final String NUMBER_FORMAT_VALUE_ROLE = "NumberFormat";
  private static final String DATE_FORMAT_VALUE_ROLE = "DateFormat";

  protected static final String[] EMPTY_EXTRA_FIELDS = new String[ 0 ];
  protected static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[ 0 ];
  private JTextArea textField;
  private JButton ellipsisButton;
  private EventListenerList eventListenerList;
  private boolean nullable;
  private String valueRole;
  private JComboBox comboBox;
  private DefaultDataAttributeContext dataAttributeContext;
  private transient Object originalValue;
  private volatile boolean filterEvents;
  private boolean comboBoxActive;
  private boolean formulaFragment;
  private String[] extraFields;
  private ReportDesignerContext designerContext;
  private MasterReport currentMasterReport;
  private ReportModelChangeHandler modelChangeHandler;

  public AbstractStringValueCellEditor() {
    setLayout( new BorderLayout() );

    modelChangeHandler = new ReportModelChangeHandler();

    final Action action = createExtendedEditorAction();

    this.eventListenerList = new EventListenerList();
    this.dataAttributeContext = new DefaultDataAttributeContext();
    this.extraFields = EMPTY_EXTRA_FIELDS;

    ellipsisButton = new EllipsisButton( "..." );
    ellipsisButton.addActionListener( action );

    textField = new JTextArea();
    textField.setLineWrap( true );
    textField.setDocument( new NonFilteringPlainDocument() );
    textField.getInputMap().put( UtilMessages.getInstance().getKeyStroke
      ( "AbstractStringValueCellEditor.Popup.Accelerator" ), POPUP_EDITOR );
    textField.getActionMap().put( POPUP_EDITOR, action );
    textField.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), CANCEL_EDITOR );
    textField.getActionMap().put( CANCEL_EDITOR, new CancelAction() );
    textField.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK ), NEWLINE_EDITOR );
    textField.getActionMap().put( NEWLINE_EDITOR, new InsertNewLineAction() );
    textField.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), CONFIRM_EDITOR );
    textField.getActionMap().put( CONFIRM_EDITOR, new SelectionAction() );
    textField.setBorder( BorderFactory.createEmptyBorder() );

    comboBox = new JComboBox();
    final ComboBoxEditor boxEditor = comboBox.getEditor();
    if ( boxEditor instanceof BasicComboBoxEditor ) {
      final BasicComboBoxEditor basicComboBoxEditor = (BasicComboBoxEditor) boxEditor;
      final Object editorComponent = basicComboBoxEditor.getEditorComponent();
      if ( editorComponent instanceof JTextField ) {
        final JTextField editorTextField = (JTextField) editorComponent;
        editorTextField.setDocument( new NonFilteringPlainDocument() );
      }
    }
    comboBox.setRenderer( new EmptyValueListCellRenderer() );
    comboBox.addActionListener( new SelectionAction() );
    comboBox.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), new CancelAction() );
    comboBox.getInputMap().put( UtilMessages.getInstance().getKeyStroke
      ( "AbstractStringValueCellEditor.Popup.Accelerator" ), POPUP_EDITOR );
    comboBox.setBorder( BorderFactory.createEmptyBorder() );
    comboBox.setEditable( true );

    add( textField, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );

    nullable = false;
  }

  public boolean isFormulaFragment() {
    return formulaFragment;
  }

  public void setFormulaFragment( final boolean formulaFragment ) {
    this.formulaFragment = formulaFragment;
  }

  protected Action createExtendedEditorAction() {
    return new ExtendedEditorAction();
  }

  protected Component create( final String valueRole, final String[] extraFields, final Object value ) {
    final ReportRenderContext reportContext = getReportContext();
    if ( reportContext != null ) {
      currentMasterReport = reportContext.getMasterReportElement();
      currentMasterReport.addReportModelListener( modelChangeHandler );
    }

    try {
      filterEvents = true;
      setExtraFields( extraFields );

      if ( QUERY_VALUE_ROLE.equals( valueRole ) ) {
        comboBox.setModel( new DefaultComboBoxModel( getQueryNames() ) );
        comboBox.setRenderer( new EmptyValueListCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else if ( FIELD_VALUE_ROLE.equals( valueRole ) ) {
        comboBox.setModel( new DefaultComboBoxModel( getFields() ) );
        comboBox.setRenderer( new FieldDefinitionCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else if ( GROUP_VALUE_ROLE.equals( valueRole ) ) {
        comboBox.setModel( new DefaultComboBoxModel( getGroups() ) );
        comboBox.setRenderer( new EmptyValueListCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else if ( NUMBER_FORMAT_VALUE_ROLE.equals( valueRole ) ) {
        comboBox.setModel( new DefaultComboBoxModel( new NumberFormatModel().getNumberFormats() ) );
        comboBox.setRenderer( new EmptyValueListCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else if ( DATE_FORMAT_VALUE_ROLE.equals( valueRole ) ) {
        comboBox.setModel( new DefaultComboBoxModel( new DateFormatModel().getNumberFormats() ) );
        comboBox.setRenderer( new EmptyValueListCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else if ( FORMULA_VALUE_ROLE.equals( valueRole ) ) {
        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        final FieldDefinition[] definitions = getFields();
        for ( int i = 0; i < definitions.length; i++ ) {
          final FieldDefinition fieldDefinition = definitions[ i ];
          comboBoxModel.addElement( "=" + FormulaUtil.quoteReference( fieldDefinition.getName() ) );
        }
        comboBox.setModel( comboBoxModel );
        comboBox.setRenderer( new EmptyValueListCellRenderer() );
        comboBox.setEditable( true );
        add( comboBox, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        comboBox.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = true;
      } else {
        add( textField, BorderLayout.CENTER );
        add( ellipsisButton, BorderLayout.EAST );
        textField.requestFocus();
        setValueRole( valueRole );
        comboBoxActive = false;
      }

      if ( value == null ) {
        comboBox.setSelectedItem( null );
        textField.setText( null );
      } else {
        if ( FORMULA_VALUE_ROLE.equals( valueRole ) && isFormulaFragment() ) {
          final GenericExpressionRuntime expressionRuntime = new GenericExpressionRuntime
            ( new StaticDataRow(), new DefaultTableModel(), -1, new DefaultProcessingContext() );
          final String formulaText = FormulaUtil.createEditorTextFromFormula
            ( String.valueOf( value ), new ReportFormulaContext( new DefaultFormulaContext(), expressionRuntime ) );
          textField.setText( formulaText );
          comboBox.setSelectedItem( formulaText );
        } else {
          comboBox.setSelectedItem( value );
          textField.setText( String.valueOf( value ) );
        }
      }

      originalValue = value;
      return this;
    } finally {
      filterEvents = false;
    }
  }

  protected void configureEditorStyle( final Font font, final Color foreground, final Color background ) {
    comboBox.setFont( font );
    comboBox.setForeground( foreground );
    comboBox.setBackground( background );

    textField.setFont( font );
    textField.setForeground( foreground );
    textField.setBackground( background );
  }

  protected JComboBox getComboBox() {
    return comboBox;
  }

  protected boolean isNullable() {
    return nullable;
  }

  protected void setNullable( final boolean nullable ) {
    this.nullable = nullable;
  }

  public void requestFocus() {
    textField.requestFocus();
  }

  protected JTextComponent getTextField() {
    return textField;
  }

  protected JButton getEllipsisButton() {
    return ellipsisButton;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    if ( comboBoxActive ) {
      final Object selectedItem = comboBox.getSelectedItem();
      if ( selectedItem instanceof FieldDefinition ) {
        final FieldDefinition fieldDefinition = (FieldDefinition) selectedItem;
        return fieldDefinition.getName();
      }
      if ( "".equals( selectedItem ) ) {
        return null;
      }

      if ( selectedItem instanceof String && FORMULA_VALUE_ROLE.equals( getValueRole() ) ) {
        if ( isFormulaFragment() ) {
          return FormulaUtil.createFormulaFromUIText( (String) selectedItem );
        }
      }
      return selectedItem;
    }
    final String s = textField.getText();
    if ( "".equals( s ) ) {
      return null;
    }

    if ( FORMULA_VALUE_ROLE.equals( getValueRole() ) ) {
      if ( isFormulaFragment() ) {
        return FormulaUtil.createFormulaFromUIText( s );
      }
    }
    return s;
  }

  /**
   * Asks the editor if it can start editing using <code>anEvent</code>. <code>anEvent</code> is in the invoking
   * component coordinate system. The editor can not assume the Component returned by
   * <code>getCellEditorComponent</code> is installed.  This method is intended for the use of client to avoid the cost
   * of setting up and installing the editor component if editing is not possible. If editing can be started this method
   * returns true.
   *
   * @param anEvent the event the editor should use to consider whether to begin editing or not
   * @return true if editing can be started
   */
  public boolean isCellEditable( final EventObject anEvent ) {
    return true;
  }

  /**
   * Returns true if the editing cell should be selected, false otherwise. Typically, the return value is true, because
   * is most cases the editing cell should be selected.  However, it is useful to return false to keep the selection
   * from changing for some types of edits. eg. A table that contains a column of check boxes, the user might want to be
   * able to change those checkboxes without altering the selection.  (See Netscape Communicator for just such an
   * example) Of course, it is up to the client of the editor to use the return value, but it doesn't need to if it
   * doesn't want to.
   *
   * @param anEvent the event the editor should use to start editing
   * @return true if the editor would like the editing cell to be selected; otherwise returns false
   */
  public boolean shouldSelectCell( final EventObject anEvent ) {
    return true;
  }

  /**
   * Tells the editor to stop editing and accept any partially edited value as the value of the editor.  The editor
   * returns false if editing was not stopped; this is useful for editors that validate and can not accept invalid
   * entries.
   *
   * @return true if editing was stopped; false otherwise
   */
  public boolean stopCellEditing() {
    try {
      if ( comboBoxActive ) {
        // ugly hack to make the combobox editor commit any changes before we go out of focus.
        comboBox.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, comboBox.getActionCommand() ) );
      }
      fireEditingStopped();
      return true;
    } catch ( final Exception e ) {
      DebugLog.log( "Exception caught while editing cell-value", e ); // NON-NLS
      // exception ignored
      fireEditingCanceled();
      if ( originalValue != null ) {
        textField.setText( String.valueOf( originalValue ) );
      } else {
        textField.setText( null );
      }
      return true;
    } finally {
      unregisterListener();
    }

  }

  protected void unregisterListener() {
    if ( currentMasterReport != null ) {
      currentMasterReport.removeReportModelListener( modelChangeHandler );
    }
  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    try {
      if ( originalValue != null ) {
        textField.setText( String.valueOf( originalValue ) );
      } else {
        textField.setText( null );
      }
      try {
        filterEvents = true;
        comboBox.setSelectedItem( originalValue );
      } finally {
        filterEvents = false;
      }
      fireEditingCanceled();
    } finally {
      unregisterListener();
    }
  }

  protected void fireEditingCanceled() {
    final CellEditorListener[] listeners = eventListenerList.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingCanceled( event );
    }
  }

  protected void fireEditingStopped() {
    final CellEditorListener[] listeners = eventListenerList.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingStopped( event );
    }
  }

  protected String getValueRole() {
    return valueRole;
  }

  protected void setValueRole( final String valueRole ) {
    this.valueRole = valueRole;
  }

  protected String[] getExtraFields() {
    return extraFields;
  }

  protected void setExtraFields( final String[] extraFields ) {
    this.extraFields = extraFields.clone();
  }

  /**
   * Adds a listener to the list that's notified when the editor stops, or cancels editing.
   *
   * @param l the CellEditorListener
   */
  public void addCellEditorListener( final CellEditorListener l ) {
    eventListenerList.add( CellEditorListener.class, l );
  }

  /**
   * Removes a listener from the list that's notified
   *
   * @param l the CellEditorListener
   */
  public void removeCellEditorListener( final CellEditorListener l ) {
    eventListenerList.remove( CellEditorListener.class, l );
  }

  protected String[] getQueryNames() {
    return CellEditorUtility.getQueryNames( getReportDesignerContext() );
  }

  public ReportRenderContext getReportContext() {
    if ( designerContext == null ) {
      return null;
    }
    ReportDesignerDocumentContext documentContext = designerContext.getActiveContext();
    if ( documentContext instanceof ReportRenderContext ) {
      return (ReportRenderContext) documentContext;
    }
    return null;
  }

  public void setReportDesignerContext( final ReportDesignerContext designerContext ) {
    this.designerContext = designerContext;
  }

  public ReportDesignerContext getReportDesignerContext() {
    return designerContext;
  }

  protected FieldDefinition[] getFields() {
    return CellEditorUtility.getFields( designerContext, getExtraFields() );
  }

  protected String[] getGroups() {
    final ReportRenderContext reportContext = getReportContext();
    if ( reportContext == null ) {
      return new String[ 0 ];
    }

    return ModelUtility.getGroups( reportContext.getReportDefinition() );
  }
}
