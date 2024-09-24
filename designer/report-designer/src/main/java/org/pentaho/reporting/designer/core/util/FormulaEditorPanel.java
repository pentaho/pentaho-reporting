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

package org.pentaho.reporting.designer.core.util;

import org.apache.commons.lang.ObjectUtils;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * A text field that acts as a simple input for formulas with a button to invoke the formula editor if needed.
 *
 * @author Thomas Morgner.
 */
public class FormulaEditorPanel extends JPanel {
  private class OpenFormulaEditorAction extends AbstractAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final FormulaEditorDialog dialog =
        GUIUtils.createFormulaEditorDialog( getReportDesignerContext(), FormulaEditorPanel.this );
      final String formula = dialog.editFormula( formulaField.getText(), computeFields() );
      if ( formula == null ) {
        // cancel pressed ... do nothing ...
        return;
      }

      formulaField.setText( formula );

    }
  }

  private class KeyEventForwarder implements KeyListener {
    private KeyEventForwarder() {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link java.awt.event.KeyEvent} for a definition
     * of a key typed event.
     */
    public void keyTyped( final KeyEvent e ) {
      final KeyListener[] keyListeners = listenerList.getListeners( KeyListener.class );
      for ( int i = 0; i < keyListeners.length; i++ ) {
        final KeyListener keyListener = keyListeners[ i ];
        keyListener.keyTyped( e );
      }
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link java.awt.event.KeyEvent} for a
     * definition of a key pressed event.
     */
    public void keyPressed( final KeyEvent e ) {
      final KeyListener[] keyListeners = listenerList.getListeners( KeyListener.class );
      for ( int i = 0; i < keyListeners.length; i++ ) {
        final KeyListener keyListener = keyListeners[ i ];
        keyListener.keyPressed( e );
      }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link java.awt.event.KeyEvent} for a
     * definition of a key released event.
     */
    public void keyReleased( final KeyEvent e ) {
      final KeyListener[] keyListeners = listenerList.getListeners( KeyListener.class );
      for ( int i = 0; i < keyListeners.length; i++ ) {
        final KeyListener keyListener = keyListeners[ i ];
        keyListener.keyReleased( e );
      }
    }
  }

  private class ValueChangeEventGenerator extends DocumentChangeHandler implements ListDataListener, Runnable {
    private String lastValue;

    private ValueChangeEventGenerator() {
    }

    protected void handleChange( final DocumentEvent e ) {
      SwingUtilities.invokeLater( this );
    }

    /**
     * Sent after the indices in the index0,index1 interval have been inserted in the data model. The new interval
     * includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    public void intervalAdded( final ListDataEvent e ) {

    }

    /**
     * Sent after the indices in the index0,index1 interval have been removed from the data model.  The interval
     * includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    public void intervalRemoved( final ListDataEvent e ) {

    }

    /**
     * Sent when the contents of the list has changed in a way that's too complex to characterize with the previous
     * methods. For example, this is sent when an item has been replaced. Index0 and index1 bracket the change.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    public void contentsChanged( final ListDataEvent e ) {
      run();
    }

    public void run() {
      final String formula = getFormula();
      if ( ObjectUtilities.equal( lastValue, formula ) ) {
        return;
      }
      final Object oldValue = lastValue;
      lastValue = formula;
      firePropertyChange( "formula", oldValue, lastValue );
    }
  }

  private static class GenericDataFieldDefinition implements FieldDefinition {
    private String name;

    private GenericDataFieldDefinition( final String name ) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String getDisplayName() {
      return name;
    }

    public Icon getIcon() {
      return IconLoader.getInstance().getDataSetsIcon();
    }

    public Class getFieldType() {
      return Object.class;
    }
  }

  private static final FieldDefinition[] EMPTY_FIELDS = new FieldDefinition[ 0 ];
  private DefaultDataAttributeContext dataAttributeContext;
  private ReportDesignerContext reportDesignerContext;
  private boolean formulaFragment;
  private EllipsisButton ellipsisButton;
  private EventListenerList listenerList;

  private JTextField formulaField;
  private JComboBox formulaBox;
  private DefaultComboBoxModel tagModel;
  private boolean comboBoxActive;
  private boolean limitFields;
  private ValueChangeEventGenerator changeEventGenerator;
  private FormulaEditorDataModel editorDataModel;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public FormulaEditorPanel() {
    this.listenerList = new EventListenerList();
    this.dataAttributeContext = new DefaultDataAttributeContext();
    setLayout( new BorderLayout() );

    ellipsisButton = new EllipsisButton( "..." );
    ellipsisButton.setDefaultCapable( false );
    ellipsisButton.setMargin( new Insets( 0, 0, 0, 0 ) );
    ellipsisButton.addActionListener( new OpenFormulaEditorAction() );

    changeEventGenerator = new ValueChangeEventGenerator();

    tagModel = new DefaultComboBoxModel();
    tagModel.addListDataListener( changeEventGenerator );

    formulaBox = new JComboBox( tagModel );
    formulaBox.setEditable( true );
    formulaBox.addKeyListener( new KeyEventForwarder() );

    formulaField = new JTextField();
    formulaField.getDocument().addDocumentListener( changeEventGenerator );
    formulaBox.addKeyListener( new KeyEventForwarder() );

    add( formulaField, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );
  }

  private void activateComboBox() {
    if ( comboBoxActive ) {
      return;
    }
    final String formula = getFormula();
    remove( formulaField );
    add( formulaBox, BorderLayout.CENTER );
    formulaBox.setSelectedItem( formula );
    comboBoxActive = true;
    revalidate();
  }

  private void activateTextField() {
    if ( comboBoxActive == false ) {
      return;
    }

    final String formula = getFormula();
    remove( formulaBox );
    add( formulaField, BorderLayout.CENTER );
    formulaField.setText( formula );
    comboBoxActive = false;
    revalidate();
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }

  protected FieldDefinition[] computeFields() {
    if ( reportDesignerContext == null ) {
      return EMPTY_FIELDS;
    }

    final ReportDocumentContext renderContext = reportDesignerContext.getActiveContext();
    if ( renderContext == null ) {
      return EMPTY_FIELDS;
    }

    final ContextAwareDataSchemaModel model = renderContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>( columnNames.length );
    final DataSchema dataSchema = model.getDataSchema();
    final DefaultDataAttributeContext attributeContext = new DefaultDataAttributeContext();
    final String parameter;
    if ( editorDataModel != null ) {
      parameter = editorDataModel.getParameter();
    } else {
      parameter = null;
    }

    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[ i ];
      final DataAttributes attributes = dataSchema.getAttributes( columnName );
      if ( attributes == null ) {
        throw new IllegalStateException( "No data-schema for expression with name '" + columnName + '\'' );
      }
      final Object source = attributes.getMetaAttribute
        ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class, attributeContext );
      if ( limitFields == false ||
        MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( source ) ||
        MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( source ) ) {
        if ( limitFields && "report.date".equals( columnName ) ) {
          // this is a magical field
          continue;
        }
        fields.add( new DataSchemaFieldDefinition( columnName, attributes, dataAttributeContext ) );
        if ( ObjectUtils.equals( parameter, columnName ) ) {
          break;
        }
      }
    }

    if ( editorDataModel != null ) {
      final String[] dataFields = editorDataModel.getDataFields();
      for ( final String field : dataFields ) {
        fields.add( new GenericDataFieldDefinition( field ) );
      }
    }
    return fields.toArray( new FieldDefinition[ fields.size() ] );
  }

  public boolean isLimitFields() {
    return limitFields;
  }

  public void setLimitFields( final boolean limitFields ) {
    this.limitFields = limitFields;
  }

  public boolean isFormulaFragment() {
    return formulaFragment;
  }

  public void setFormulaFragment( final boolean formulaFragment ) {
    this.formulaFragment = formulaFragment;
  }

  public String getFormula() {
    final String s;
    if ( comboBoxActive ) {
      s = (String) formulaBox.getSelectedItem();
    } else {
      s = formulaField.getText();
    }

    if ( StringUtils.isEmpty( s, true ) ) {
      return null;
    }
    if ( isFormulaFragment() ) {
      return FormulaUtil.createFormulaFromUIText( s );
    }
    return s;
  }

  public void setFormula( final String text ) {
    if ( text == null ) {
      formulaField.setText( null );
      formulaBox.setSelectedItem( null );
      return;
    }

    if ( isFormulaFragment() ) {
      final GenericExpressionRuntime expressionRuntime = new GenericExpressionRuntime
        ( new StaticDataRow(), new DefaultTableModel(), -1, new DefaultProcessingContext() );
      final String formulaText = FormulaUtil.createEditorTextFromFormula
        ( text, new ReportFormulaContext( new DefaultFormulaContext(), expressionRuntime ) );
      formulaField.setText( formulaText );
      formulaBox.setSelectedItem( formulaText );
    } else {
      formulaField.setText( text );
      formulaBox.setSelectedItem( text );
    }
  }

  /**
   * Sets whether or not this component is enabled. A component that is enabled may respond to user input, while a
   * component that is not enabled cannot respond to user input.  Some components may alter their visual representation
   * when they are disabled in order to provide feedback to the user that they cannot take input. <p>Note: Disabling a
   * component does not disable its children.
   * <p/>
   * <p>Note: Disabling a lightweight component does not prevent it from receiving MouseEvents.
   *
   * @param enabled true if this component should be enabled, false otherwise
   * @beaninfo preferred: true bound: true attribute: visualUpdate true description: The enabled state of the
   * component.
   * @see java.awt.Component#isEnabled
   * @see java.awt.Component#isLightweight
   */
  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    formulaField.setEnabled( enabled );
    ellipsisButton.setEnabled( enabled );
  }

  public void selectAll() {
    formulaField.selectAll();
  }

  public void setEditable( final boolean editable ) {
    formulaField.setEditable( editable );
    ellipsisButton.setEnabled( editable );
  }

  public boolean isEditable() {
    return formulaField.isEditable();
  }

  public void addFormulaKeyListener( final KeyListener k ) {
    this.listenerList.add( KeyListener.class, k );
  }

  public void removeFormulaKeyListener( final KeyListener k ) {
    this.listenerList.remove( KeyListener.class, k );
  }

  public String[] getTags() {
    final int size = tagModel.getSize();
    final String[] retval = new String[ size ];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[ i ] = (String) tagModel.getElementAt( i );
    }
    return retval;
  }

  public void setTags( final String[] tags ) {
    if ( tags == null ) {
      throw new NullPointerException();
    }

    tagModel.removeAllElements();
    for ( int i = 0; i < tags.length; i++ ) {
      tagModel.addElement( tags[ i ] );
    }
    if ( tags.length == 0 ) {
      activateTextField();
    } else {
      activateComboBox();
    }
  }

  public FormulaEditorDataModel getEditorDataModel() {
    return editorDataModel;
  }

  public void setEditorDataModel( final FormulaEditorDataModel editorDataModel ) {
    this.editorDataModel = editorDataModel;
  }
}
