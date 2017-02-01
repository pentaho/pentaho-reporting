package org.pentaho.openformula.ui.table;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;
import org.pentaho.reporting.libraries.designtime.swing.EmptyValueListCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.NonFilteringPlainDocument;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

public class FormulaFragmentCellEditor extends JPanel implements TableCellEditor {
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
      final JComboBox comboBox = getComboBox();
      final FormulaEditorDialog editorDialog = FormulaFragmentCellEditor.this.createEditorDialog();
      final String originalFormula = (String) comboBox.getSelectedItem();
      final String formula = editorDialog.editFormula( originalFormula, getFields() );
      if ( formula != null ) {
        comboBox.setSelectedItem( formula );
      }
      stopCellEditing();
    }
  }

  protected FormulaEditorDialog createEditorDialog() {
    Window windowAncestor = SwingUtilities.getWindowAncestor( this );
    if ( windowAncestor instanceof Dialog ) {
      return new FormulaEditorDialog( (Dialog) windowAncestor );
    } else if ( windowAncestor instanceof Frame ) {
      return new FormulaEditorDialog( (Frame) windowAncestor );
    } else {
      return new FormulaEditorDialog();
    }
  }

  protected class SelectionAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SelectionAction() {
      putValue( Action.NAME, EditorMessages.getInstance().getString( "AbstractStringValueCellEditor.SelectValue" ) );
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

  protected static final String POPUP_EDITOR = "popupEditor";
  protected static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[ 0 ];

  private EventListenerList eventListenerList;
  private boolean nullable;

  private JButton ellipsisButton;
  private JComboBox comboBox;
  private transient Object originalValue;
  private volatile boolean filterEvents;
  private boolean formulaFragment;
  private FieldDefinition[] fields;
  private FormulaContext formulaContext;

  public FormulaFragmentCellEditor() {
    setLayout( new BorderLayout() );

    final Action action = createExtendedEditorAction();

    this.eventListenerList = new EventListenerList();

    ellipsisButton = new EllipsisButton( "..." );
    ellipsisButton.addActionListener( action );

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
    comboBox.getInputMap().put( EditorMessages.getInstance().getKeyStroke
      ( "AbstractStringValueCellEditor.Popup.Accelerator" ), POPUP_EDITOR );
    comboBox.setBorder( BorderFactory.createEmptyBorder() );
    comboBox.setEditable( true );

    add( comboBox, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );

    formulaContext = new DefaultFormulaContext();

    nullable = false;
  }

  public boolean isFormulaFragment() {
    return formulaFragment;
  }

  public void setFormulaFragment( final boolean formulaFragment ) {
    this.formulaFragment = formulaFragment;
  }

  public FormulaContext getFormulaContext() {
    return formulaContext;
  }

  public void setFormulaContext( final FormulaContext formulaContext ) {
    this.formulaContext = formulaContext;
  }

  protected Action createExtendedEditorAction() {
    return new ExtendedEditorAction();
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    return create( value );
  }

  protected Component create( final Object value ) {
    try {
      filterEvents = true;

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

      if ( value == null ) {
        comboBox.setSelectedItem( null );
      } else {
        String rawFormulaText;
        if ( isFormulaFragment() == false ) {
          rawFormulaText = FormulaUtil.extractFormula( String.valueOf( value ) );
        } else {
          rawFormulaText = String.valueOf( value );
        }

        String formulaText = FormulaUtil.createEditorTextFromFormula( rawFormulaText, formulaContext );
        comboBox.setSelectedItem( formulaText );
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
    comboBox.requestFocus();
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
    final Object selectedItem = comboBox.getSelectedItem();
    if ( "".equals( selectedItem ) || selectedItem == null ) {
      return null;
    }

    String text = String.valueOf( selectedItem );
    if ( text.startsWith( "'" ) ) {
      text = "=" + FormulaUtil.quoteString( text.substring( 1 ) );
    }

    if ( isFormulaFragment() ) {
      return FormulaUtil.createFormulaFromUIText( text );
    }
    return text;
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
      // ugly hack to make the combobox editor commit any changes before we go out of focus.
      comboBox.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, comboBox.getActionCommand() ) );
      fireEditingStopped();
      return true;
    } catch ( final Exception e ) {
      DebugLog.log( "Exception caught while editing cell-value", e ); // NON-NLS
      // exception ignored
      fireEditingCanceled();
      return true;
    }
  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    try {
      filterEvents = true;
      comboBox.setSelectedItem( originalValue );
    } finally {
      filterEvents = false;
    }
    fireEditingCanceled();
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

  public FieldDefinition[] getFields() {
    return fields;
  }

  public void setFields( final FieldDefinition[] fields ) {
    this.fields = fields;
  }
}
