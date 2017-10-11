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

package org.pentaho.reporting.libraries.designtime.swing.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Locale;
import java.util.TimeZone;

public class TimeCellEditor extends JPanel implements TableCellEditor {
  private class TextComponentEditHandler implements Runnable, DocumentListener, ActionListener {
    private JTextComponent textComponent;
    private Color color;
    private boolean inProgress;

    public TextComponentEditHandler( final JTextComponent textComponent ) {
      this.textComponent = textComponent;
      this.color = this.textComponent.getBackground();
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      convertParameterValue();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      convertParameterValue();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      convertParameterValue();
    }

    private void convertParameterValue() {
      if ( inProgress ) {
        return;
      }
      inProgress = true;
      SwingUtilities.invokeLater( this );
    }

    public void run() {
      convert();
    }

    protected void convert() {
      try {
        final String text = textComponent.getText();
        textComponent.setBackground( color );

        final Date date = convertValue( text );
        TimeCellEditor.this.date = date;
      } catch ( Exception e ) {
        // ignore, do not update (yet).
        textComponent.setBackground( Color.RED );
      } finally {
        inProgress = false;
      }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      convert();
    }
  }

  private static final Log logger = LogFactory.getLog( TimeCellEditor.class );
  private JTextField dateField;
  private DateFormat sdf;
  private Class dateType;
  private EventListenerList listeners;
  private Date date;
  private static final String DEFAULT_FORMAT = "HH:mm:ss.SSS";

  /**
   * Constructs a new <code>DatePickerParameterComponent</code>.
   */
  public TimeCellEditor( final Class dateType ) {
    this.listeners = new EventListenerList();
    this.dateType = dateType;
    if ( this.dateType.isArray() ) {
      this.dateType = this.dateType.getComponentType();
    }

    dateField = new JTextField();
    dateField.setColumns( 20 );

    final TextComponentEditHandler listener = new TextComponentEditHandler( dateField );
    dateField.getDocument().addDocumentListener( listener );
    dateField.addActionListener( listener );

    setLayout( new BorderLayout() );
    dateField.setEditable( true );

    add( dateField, BorderLayout.CENTER );

    setDateFormat( createDateFormat( DEFAULT_FORMAT, Locale.getDefault(), TimeZone.getDefault() ) );
  }

  private DateFormat createDateFormat( final String parameterFormatString,
                                       final Locale locale,
                                       final TimeZone timeZone ) {
    if ( parameterFormatString != null ) {
      try {
        final SimpleDateFormat dateFormat =
          new SimpleDateFormat( parameterFormatString, new DateFormatSymbols( locale ) );
        dateFormat.setTimeZone( timeZone );
        dateFormat.setLenient( true );
        return dateFormat;
      } catch ( Exception e ) {
        // boo! Not a valid pattern ...
        // its not a show-stopper either, as the pattern is a mere hint, not a mandatory thing
        logger.warn( "Parameter format-string for date-parameter was not a valid date-format-string", e );
      }
    }

    final SimpleDateFormat dateFormat = new SimpleDateFormat( DEFAULT_FORMAT, new DateFormatSymbols( locale ) );
    dateFormat.setTimeZone( timeZone );
    dateFormat.setLenient( true );
    return dateFormat;
  }

  private void setDate( final Object value ) {
    if ( value == null ) {
      return;
    }
    if ( value instanceof String ) {
      // if its a string, then it must be in the normalized parameter format.
      try {
        final Date date = convertValue( (String) value );
        this.date = ( date );
        dateField.setText( sdf.format( date ) );
      } catch ( Exception e ) {
        logger.debug( "Unparsable date-string", e );
      }
    } else if ( value instanceof Date ) {
      final Date date = (Date) value;
      this.date = ( date );
      dateField.setText( sdf.format( date ) );
    } else {
      logger.debug( "Date-parameter must be set either as normalized date-string or as date-object: " +
        value + " [" + value.getClass() + "]" );
    }
  }

  protected Date convertValue( final String text ) {
    if ( StringUtils.isEmpty( text ) ) {
      return null;
    }

    try {
      final Date o = (Date) sdf.parseObject( text );
      // this magic converts the date or number value to the real type.
      // the formatter always returns doubles/bigdecimals or java.util.Dates
      // but we may need sql-dates, long-objects etc ..
      return DateConverter.convertToDateType( o, dateType );
    } catch ( ParseException e ) {
      throw new RuntimeException( "Failed to format object", e );
    }
  }

  /**
   * Sets an initial <code>value</code> for the editor.  This will cause the editor to <code>stopEditing</code> and lose
   * any partially edited value if the editor is editing when this method is called. <p>
   * <p/>
   * Returns the component that should be added to the client's <code>Component</code> hierarchy.  Once installed in the
   * client's hierarchy this component will then be able to draw and receive user input.
   *
   * @param table      the <code>JTable</code> that is asking the editor to edit; can be <code>null</code>
   * @param value      the value of the cell to be edited; it is up to the specific editor to interpret and draw the
   *                   value.  For example, if value is the string "true", it could be rendered as a string or it could
   *                   be rendered as a check box that is checked.  <code>null</code> is a valid value
   * @param isSelected true if the cell is to be rendered with highlighting
   * @param row        the row of the cell being edited
   * @param column     the column of the cell being edited
   * @return the component for editing
   */
  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    setDate( value );
    return this;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    return date;
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
   * @see #shouldSelectCell
   */
  public boolean isCellEditable( final EventObject anEvent ) {
    if ( anEvent instanceof MouseEvent ) {
      final MouseEvent mouseEvent = (MouseEvent) anEvent;
      return mouseEvent.getClickCount() >= 2 && mouseEvent.getButton() == MouseEvent.BUTTON1;
    }
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
   * @see #isCellEditable
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
    fireEditingStopped();
    return true;
  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    fireEditingCanceled();
  }


  protected void fireEditingCanceled() {
    final CellEditorListener[] listeners = this.listeners.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingCanceled( event );
    }
  }


  protected void fireEditingStopped() {
    final CellEditorListener[] listeners = this.listeners.getListeners( CellEditorListener.class );
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
    listeners.add( CellEditorListener.class, l );
  }

  /**
   * Removes a listener from the list that's notified
   *
   * @param l the CellEditorListener
   */
  public void removeCellEditorListener( final CellEditorListener l ) {
    listeners.remove( CellEditorListener.class, l );
  }

  public void setDateFormat( final DateFormat timeFormat ) {
    if ( timeFormat == null ) {
      throw new NullPointerException();
    }
    sdf = timeFormat;
    setDate( getCellEditorValue() );
    if ( timeFormat instanceof SimpleDateFormat ) {
      final SimpleDateFormat dateFormat = (SimpleDateFormat) timeFormat;
      setToolTipText( dateFormat.toLocalizedPattern() );
    } else {
      setToolTipText( null );
    }
  }

  public DateFormat getDateFormat() {
    return sdf;
  }
}
