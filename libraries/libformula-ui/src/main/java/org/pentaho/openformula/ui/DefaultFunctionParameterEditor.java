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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.model2.FormulaElement;
import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.openformula.ui.util.InlineEditTextField;
import org.pentaho.openformula.ui.util.SelectFieldAction;
import org.pentaho.openformula.ui.util.TooltipLabel;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

public class DefaultFunctionParameterEditor extends JPanel implements FunctionParameterEditor, FieldDefinitionSource {
  private class FieldSelectorUpdateHandler implements PropertyChangeListener {
    private int paramIndex;

    private FieldSelectorUpdateHandler( final int paramIndex ) {
      this.paramIndex = paramIndex;
    }

    @Override
    public void propertyChange( final PropertyChangeEvent evt ) {
      final FieldDefinition value = (FieldDefinition) evt.getNewValue();
      //noinspection MagicCharacter,StringConcatenation
      if ( value != null ) {
        final String text = FormulaUtil.quoteReference( value.getName() );
        final String parameterValue = getParameterValue( paramIndex );
        final TextFieldHolderStruct fieldStruct = getParameterField( paramIndex );
        final InlineEditTextField field = fieldStruct.getTextFields();

        final StringBuilder b = new StringBuilder( parameterValue );
        // remove the selected content, if any
        b.delete( field.getSelectionStart(), field.getSelectionEnd() );
        // then insert the new content at the cursor position
        final int caretPosition = field.getCaretPosition();
        b.insert( caretPosition, text );
        fieldStruct.setText( b.toString() );
      }
    }
  }

  private class FocusListenerHandler extends FocusAdapter {
    private InlineEditTextField paramTextField;
    private int parameterIndex;

    private FocusListenerHandler( final InlineEditTextField paramTextField, final int parameterIndex ) {
      this.paramTextField = paramTextField;
      this.parameterIndex = parameterIndex;
    }

    public void focusLost( final FocusEvent e ) {
      handleFocusChange();
    }

    @Override
    public void focusGained( final FocusEvent e ) {
      handleFocusChange();
    }

    private void handleFocusChange() {
      if ( inSetupUpdate ) {
        return;
      }

      final String s = paramTextField.getText();
      fireParameterUpdate( parameterIndex, s );
    }
  }

  private static class TextFieldHolderStruct {
    private InlineEditTextField textFields;
    private SelectFieldAction selectFieldAction;
    private FocusListenerHandler focusHandler;
    private Component[] extraComponents;

    protected TextFieldHolderStruct( final InlineEditTextField textFields,
                                     final SelectFieldAction selectFieldAction,
                                     final FocusListenerHandler focusHandler,
                                     final Component... extraComponents ) {
      this.textFields = textFields;
      this.selectFieldAction = selectFieldAction;
      this.focusHandler = focusHandler;
      this.extraComponents = extraComponents;
    }

    protected InlineEditTextField getTextFields() {
      return textFields;
    }

    public void setText( final String text ) {
      textFields.setText( text );
      if ( text != null ) {
        textFields.setCaretPosition( text.length() );
      }
    }

    public String getText() {
      return textFields.getText();
    }

    public void dispose() {
      selectFieldAction.dispose();
      textFields.getParent().remove( textFields );
      for ( final Component c : extraComponents ) {
        c.getParent().remove( c );
      }
    }
  }

  public static final int FIELDS_ADD = 2;

  private static final TextFieldHolderStruct[] EMPTY_FIELDS = new TextFieldHolderStruct[ 0 ];
  private static final FieldDefinition[] EMPTY_FIELDDEF = new FieldDefinition[ 0 ];

  private FunctionDescription selectedFunction;
  private JPanel parameterPane;
  private FieldDefinition[] fields;
  private TextFieldHolderStruct[] textFields;

  private boolean inSetupUpdate;
  private int parameterUpdatedCount;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public DefaultFunctionParameterEditor() {
    parameterPane = new JPanel();
    parameterPane.setLayout( new GridBagLayout() );

    this.inSetupUpdate = false;
    this.parameterUpdatedCount = -1;

    this.textFields = EMPTY_FIELDS;
    this.fields = EMPTY_FIELDDEF;

    final JPanel parameterPaneCarrier = new JPanel();
    parameterPaneCarrier.setLayout( new BorderLayout() );
    parameterPaneCarrier.add( parameterPane, BorderLayout.NORTH );

    final JScrollPane comp = new JScrollPane( parameterPaneCarrier );
    comp.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    comp.setViewportBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    setLayout( new CardLayout() );
    add( "2", comp );
    add( "1", Box.createRigidArea( new Dimension( 650, 250 ) ) );
  }

  public FunctionDescription getSelectedFunction() {
    return selectedFunction;
  }

  @Override
  public void clearSelectedFunction() {
    setSelectedFunction( new FunctionParameterContext() );
  }


  /**
   * Determines whether the current context formula is the main one (the first formula following the '=').  So
   * '=COUNT(1;SUM(1;2;3))', COUNT would be the main formula.  If context points to SUM then we return false.
   *
   * @param context
   * @return - true if the context points to the left most outer formula.
   */
  public boolean isMainFormula( final FunctionParameterContext context ) {
    final FormulaEditorModel editorModel = context.getEditorModel();
    if ( ( editorModel == null ) || ( editorModel.getLength() < 1 ) ) {
      return true;
    }

    final FormulaElement mainFormulaElement = editorModel.getFormulaElementAt( 1 );
    final FunctionInformation currentFunction = editorModel.getCurrentFunction();
    if ( ( mainFormulaElement != null ) && ( currentFunction != null ) && ( currentFunction.getFunctionOffset() == 1 )
      &&
      ( mainFormulaElement.getText().equals( currentFunction.getCanonicalName() ) ) ) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * If user is typing in formula text-area, this method updates the appropriate parameter field.  Note that the
   * parameter fields are not always visible so if they are not visible then return false.  Note that when user is
   * typing in formula text-area and they are typing an embedded formula, the parameter fields for that embedded formula
   * don't get displayed. They get displayed if user points cursor over the formula or arrows over the formula - just
   * not when typing.
   *
   * @param context
   * @return
   */
  private boolean updateCurrentParameterField( final FunctionParameterContext context ) {
    final FunctionDescription selectedFunction = context.getFunction();
    final String[] parameterValues = context.getParameterValues();

    // Iterate over each parameter field looking to find the field associated with
    // the embedded formula.  If we find it, build up the formula in parameter field
    // to reflect what was typed into the formula text-area
    for ( int i = 0; i < textFields.length; i++ ) {
      final String parameterValue = textFields[ i ].getText();
      if ( ( parameterValue != null ) && ( parameterValue.startsWith( selectedFunction.getCanonicalName() )
        == true ) ) {
        String updatedFormula = selectedFunction.getCanonicalName() + "(";
        for ( int paramIndex = 0; paramIndex < parameterValues.length; paramIndex++ ) {
          if ( parameterValues[ paramIndex ] != null ) {
            updatedFormula = updatedFormula + parameterValues[ paramIndex ];
            updatedFormula += ";";
          }
        }

        // Remove the trailing semicolon
        if ( updatedFormula.endsWith( ";" ) ) {
          updatedFormula = updatedFormula.substring( 0, updatedFormula.length() - 1 );
        }

        if ( parameterValue.endsWith( ")" ) ) {
          updatedFormula += ")";
        }

        textFields[ i ].setText( updatedFormula );
        return true;
      }
    }

    // We did not find the corresponding parameter field as it is not being displayed
    return false;
  }

  private void updateParameterFields( final String[] parameterValues ) {
    if ( parameterValues != null && parameterValues.length <= textFields.length ) {
      for ( int i = 0; i < parameterValues.length; i++ ) {
        final String string = parameterValues[ i ];
        if ( textFields[ i ] != null ) {
          textFields[ i ].setText( string );
        }
      }
    }
  }

  @Override
  public void setSelectedFunction( final FunctionParameterContext context ) {
    try {
      inSetupUpdate = true;

      final FunctionDescription fnDesc = context.getFunction();

      //this is empty function?
      if ( fnDesc == null ) {
        for ( int i = 0; i < textFields.length; i++ ) {
          textFields[ i ].dispose();
        }

        this.textFields = EMPTY_FIELDS;
        return;
      }

      final boolean functionChanged = ( selectedFunction != fnDesc );
      this.selectedFunction = fnDesc;

      //currently editing one
      final String[] parameterValues = context.getParameterValues();
      final String[] parameterFieldValues =
        getParametersValues( context.getFunctionInformation(), context.getFunction() );

      //recreate whole text fields
      if ( functionChanged ) {
        parameterPane.removeAll();

        this.textFields = new TextFieldHolderStruct[ parameterFieldValues.length ];
        final int fieldFocus = Math.max( 0, parameterUpdatedCount );
        for ( int i = 0; i < parameterFieldValues.length; i++ ) {
          this.textFields[ i ] = addTextField( parameterFieldValues[ i ], i, ( i == fieldFocus ) );
        }
      } else if ( textFields.length != parameterFieldValues.length ) {
        final TextFieldHolderStruct[] oldTextFields = this.textFields;
        this.textFields = new TextFieldHolderStruct[ parameterFieldValues.length ];
        System.arraycopy( oldTextFields, 0, textFields, 0, Math.min( oldTextFields.length, textFields.length ) );
        final int fieldFocus = Math.max( 0, parameterUpdatedCount );
        for ( int i = parameterFieldValues.length; i < oldTextFields.length; i++ ) {
          oldTextFields[ i ].dispose();
        }
        for ( int i = oldTextFields.length; i < parameterFieldValues.length; i++ ) {
          this.textFields[ i ] = addTextField( parameterFieldValues[ i ], i, ( i == fieldFocus ) );
        }
      }

      if ( isMainFormula( context ) == true ) {
        updateParameterFields( parameterValues );
        //return;
      } else {
        // If we are in an embedded formula, update the main
        // formula's parameter field that is associated with
        // this embedded formula.
        if ( updateCurrentParameterField( context ) == false ) {
          // The parameter field is pointing to the embedded
          // formula - update it
          updateParameterFields( parameterValues );
        }
      }
    } finally {
      inSetupUpdate = false;
      invalidate();
      revalidate();
      repaint();
    }
  }

  private TextFieldHolderStruct addTextField( final String parameterValue,
                                              final int parameterPosition,
                                              final boolean requestFocus ) {
    //this value is used to compute field hints.
    final int paramPos = Math.max( 0, Math.min( selectedFunction.getParameterCount() - 1, parameterPosition ) );
    final String displayName = selectedFunction.getParameterDisplayName( paramPos, Locale.getDefault() );
    final String description = selectedFunction.getParameterDescription( paramPos, Locale.getDefault() );

    final JLabel paramNameLabel = new JLabel( displayName );
    final InlineEditTextField paramTextField = new InlineEditTextField();

    paramTextField.setText( parameterValue );
    if ( parameterValue != null ) {
      paramTextField.setCaretPosition( parameterValue.length() );
    }
    paramTextField.setFont
      ( new Font( Font.MONOSPACED, paramTextField.getFont().getStyle(), paramTextField.getFont().getSize() ) );

    final FocusListenerHandler handler = new FocusListenerHandler( paramTextField, parameterPosition );
    paramTextField.addFocusListener( handler );

    if ( requestFocus ) {
      paramTextField.setFocusable( true );
      paramTextField.requestFocusInWindow();
    }

    final SelectFieldAction selectFieldAction =
      new SelectFieldAction( this, new FieldSelectorUpdateHandler( parameterPosition ), this );
    // treat insert field as parameter edit
    selectFieldAction.setFocusReturn( paramTextField );

    final BorderlessButton button = new BorderlessButton( selectFieldAction );
    final TooltipLabel tooltipLabel = new TooltipLabel( description );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    this.parameterPane.add( paramNameLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    this.parameterPane.add( paramTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    this.parameterPane.add( button, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = parameterPosition;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 3, 5, 3, 5 );
    this.parameterPane.add( tooltipLabel, gbc );

    return new TextFieldHolderStruct( paramTextField, selectFieldAction, handler,
      paramNameLabel, button, tooltipLabel );
  }

  //returns expected number of fields for formula editor
  private static int computeFunctionParameterCount( final FunctionInformation info, final FunctionDescription desc ) {
    if ( !desc.isInfiniteParameterCount() ) {
      return desc.getParameterCount();
    }

    final String[] parameters = info.getParameters();
    int lastNonEmpty = 0;
    for ( int i = 0; i < parameters.length; i += 1 ) {
      final String p = parameters[ i ];
      if ( StringUtils.isEmpty( p ) ) {
        continue;
      }
      lastNonEmpty = i;
    }

    return Math.max( lastNonEmpty + 1, desc.getParameterCount() ) + FIELDS_ADD;
  }

  public String[] getParametersValues( final FunctionInformation fnInfo, final FunctionDescription fnDesc ) {
    final int paramCount = computeFunctionParameterCount( fnInfo, fnDesc );

    final String[] parameterValues = new String[ paramCount ];
    final int definedParameterCount = Math.min( fnInfo.getParameterCount(), paramCount );
    for ( int i = 0; i < definedParameterCount; i++ ) {
      final String text = fnInfo.getParameterText( i );
      parameterValues[ i ] = text;
    }

    //if there is more than FIELDS_MAX_NUMBER parameters -
    if ( definedParameterCount > 0 && fnInfo.getParameterCount() > paramCount ) {
      final StringBuilder lastParamEatsAllBuffer = new StringBuilder( 100 );
      final int lastParamIdx = definedParameterCount - 1;
      for ( int i = lastParamIdx; i < fnInfo.getParameterCount(); i++ ) {
        if ( i > lastParamIdx ) {
          lastParamEatsAllBuffer.append( ';' );
        }
        lastParamEatsAllBuffer.append( fnInfo.getParameterText( i ) );
      }
      parameterValues[ lastParamIdx ] = lastParamEatsAllBuffer.toString();
    }

    return parameterValues;
  }

  protected TextFieldHolderStruct getParameterField( final int field ) {
    return textFields[ field ];
  }

  public String getParameterValue( final int param ) {
    return textFields[ param ].getText();
  }

  @Override
  public void addParameterUpdateListener( final ParameterUpdateListener listener ) {
    if ( listenerList.getListenerCount( ParameterUpdateListener.class ) == 0 ) {
      listenerList.add( ParameterUpdateListener.class, listener );
    }
  }

  @Override
  public void removeParameterUpdateListener( final ParameterUpdateListener listener ) {
    listenerList.remove( ParameterUpdateListener.class, listener );
  }

  protected void fireParameterUpdate( final int param, final String text ) {
    final boolean catchAllParameter =
      selectedFunction.isInfiniteParameterCount() && ( param >= selectedFunction.getParameterCount() );
    final ParameterUpdateListener[] updateListeners = listenerList.getListeners( ParameterUpdateListener.class );
    for ( int i = 0; i < updateListeners.length; i++ ) {
      final ParameterUpdateListener listener = updateListeners[ i ];
      listener.parameterUpdated( new ParameterUpdateEvent( this, param, text, catchAllParameter ) );
    }
  }

  @Override
  public void setFields( final FieldDefinition[] fields ) {
    this.fields = fields.clone();
  }

  @Override
  public FieldDefinition[] getFields() {
    if ( fields == null ) {
      return new FieldDefinition[ 0 ];
    }
    return fields.clone();
  }

  @Override
  public Component getEditorComponent() {
    return this;
  }
}
