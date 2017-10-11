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

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.openformula.ui.util.FunctionParameterEditHelper;
import org.pentaho.openformula.ui.util.InlineEditTextArea;
import org.pentaho.openformula.ui.util.SelectFieldAction;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.HorizontalLayout;
import org.pentaho.reporting.libraries.designtime.swing.ToolbarButton;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeUtil;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FormulaEditorPanel extends JComponent implements FieldDefinitionSource {
  private class CaretHandler implements CaretListener {
    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    public void caretUpdate( final CaretEvent e ) {
      if ( ignoreTextEvents ) {
        return;
      }

      editorModel.setCaretPosition( functionTextArea.getCaretPosition() );
      refreshInformationPanel();
      revalidateParameters( true );
    }

  }

  /**
   * A event handler that keeps the InformationPanel up to date.
   */
  private class FunctionDescriptionUpdateHandler implements PropertyChangeListener, ActionListener {
    private FunctionDescriptionUpdateHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      refreshInformationPanel();
    }

    /**
     * Invoked when an action occurs.
     *
     * @noinspection MagicCharacter
     */
    public void actionPerformed( final ActionEvent e ) {
      final FunctionDescription selectedFunction = functionSelectorPanel.getSelectedValue();
      final StringBuilder b = new StringBuilder( 100 );
      b.append( selectedFunction.getCanonicalName() );
      b.append( '(' );

      final int count;
      if ( selectedFunction.isInfiniteParameterCount() ) {
        count = Math.min( 1, selectedFunction.getParameterCount() );
      } else {
        count = selectedFunction.getParameterCount();
      }
      for ( int i = 0; i < count; i++ ) {
        if ( i > 0 ) {
          b.append( ";" );
        }
        final Type type = selectedFunction.getParameterType( i );
        b.append( TypeUtil.getParameterType( type, getLocale() ) );
      }
      b.append( ')' );

      try {
        final Document document = functionTextArea.getDocument();
        final int selectionStart = functionTextArea.getSelectionStart();
        document.remove( selectionStart, functionTextArea.getSelectionEnd() - selectionStart );
        document.insertString( functionTextArea.getCaretPosition(), b.toString(), null );
      } catch ( BadLocationException e1 ) {
        e1.printStackTrace();
      }
    }
  }

  private class DocumentSyncHandler implements PropertyChangeListener {
    private DocumentSyncHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( "text".equals( evt.getPropertyName() ) == false ) {
        return;
      }
      if ( ignoreTextEvents ) {
        return;
      }
      run();
    }

    public void run() {
      editorModel.setFormulaText( functionTextArea.getText() );
      editorModel.setCaretPosition( functionTextArea.getCaretPosition() );

      ignoreTextEvents = false;
      revalidateParameters( false );

      revalidateFormulaSyntax();
    }
  }

  public class ParameterUpdateHandler implements ParameterUpdateListener {
    private ParameterUpdateHandler() {
    }


    public boolean isEmbeddedFunction( final String parameterText ) {
      // Determine if the parameter is a function (i.e. has '(' and ')').  If so,
      // then figure if the
      if ( parameterText != null ) {
        if ( parameterText.contains( "(" ) && parameterText.contains( ")" ) ) {
          return true;
        }
      }

      return false;
    }

    /**
     * This method gets called after each parameter text has been entered in the parameter field.  If user is manually
     * entering text in formula text-area, then this method is called for each character entered.  If user is entering a
     * formula, the parameter field will not change to the corresponding embedded formula unless user puts their cursor
     * on the formula.
     *
     * @param event
     */
    public synchronized void parameterUpdated( final ParameterUpdateEvent event ) {
      if ( ignoreTextEvents == true ) {
        return;
      }

      final FunctionInformation fn = editorModel.getCurrentFunction();
      if ( fn == null ) {
        return;
      }

      final FunctionParameterEditHelper.EditResult formulaText =
        FunctionParameterEditHelper.buildFormulaText( event, fn, editorModel.getFormulaText() );

      ignoreTextEvents = true;
      // The formula in the formula text-area represents the correct and updated formula text.
      // Rebuild the element nodes based on this new representation.
      editorModel.setFormulaText( formulaText.text );

      // Update for formula text-area
      functionTextArea.setText( formulaText.text );
      functionTextArea.setCaretPosition( formulaText.caretPositionAfterEdit );
      editorModel.setCaretPosition( functionTextArea.getCaretPosition() );
      ignoreTextEvents = false;

      revalidateParameters( false );
      revalidateFormulaSyntax();
    }
  }

  private class FieldSelectorListener implements PropertyChangeListener {
    private FieldSelectorListener() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      final FieldDefinition value = (FieldDefinition) evt.getNewValue();
      final String text = FormulaUtil.quoteReference( value.getName() );
      insertText( text );
    }
  }

  private class InsertOperatorAction extends AbstractAction {
    private String symbol;
    private static final int IMAGE_SIZE = 16;

    private InsertOperatorAction( final String symbol,
                                  final String description ) {
      this.symbol = symbol;
      putValue( Action.SMALL_ICON, createImage( symbol ) );
      putValue( Action.SHORT_DESCRIPTION, description );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      insertText( symbol );
    }

    private ImageIcon createImage( final String symbol ) {
      final BufferedImage bi = new BufferedImage( IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB );
      final Graphics graphics = bi.getGraphics();
      final Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds( symbol, graphics );
      final int xspace = (int) Math.max( IMAGE_SIZE - stringBounds.getWidth(), 0 );
      final int yspace = (int) Math.max( IMAGE_SIZE - stringBounds.getHeight(), 0 );
      graphics.setColor( Color.BLACK );
      final double y2 = stringBounds.getY();
      final int y1 = (int) ( ( yspace / 2 ) - y2 );
      graphics.drawString( symbol, xspace / 2, y1 );
      graphics.dispose();
      return new ImageIcon( bi );
    }
  }

  private boolean ignoreTextEvents;

  private FunctionListPanel functionSelectorPanel;
  private MultiplexFunctionParameterEditor functionParameterEditor;
  private FunctionInformationPanel functionInformationPanel;

  private FormulaContext formulaContext;
  private InlineEditTextArea functionTextArea;
  private JLabel errorTextHolder;
  private JLabel errorIconHolder;
  private FieldDefinition[] fields;
  private FormulaEditorModel editorModel;
  private ImageIcon errorIcon;
  private SelectFieldAction selectFieldsAction;
  private JToolBar operatorPanel;
  private DocumentSyncHandler docSyncHandler;
  private ParameterUpdateHandler parameterUpdateHandler;

  public FormulaEditorPanel() {
    init();
  }

  public FormulaEditorModel getEditorModel() {
    return editorModel;
  }

  public DocumentSyncHandler getDocSyncHandler() {
    return docSyncHandler;
  }

  public void setDocSyncHandler( final DocumentSyncHandler docSyncHandler ) {
    this.docSyncHandler = docSyncHandler;
  }

  protected MultiplexFunctionParameterEditor getFunctionParameterEditor() {
    return functionParameterEditor;
  }

  protected void insertText( final String text ) {
    final int start = functionTextArea.getCaretPosition();
    final String formulaTextOriginal = editorModel.getFormulaText();
    final StringBuilder formulaText = new StringBuilder( formulaTextOriginal );

    // Ensure that only one equal sign in first cursor position exists.
    int textLength = text.length();
    if ( "=".equals( formulaTextOriginal ) ) {
      if ( text.startsWith( "=" ) ) {
        formulaText.append( text.substring( 1 ) );
        textLength--;
      } else {
        formulaText.append( text );
      }
    } else {
      String formulaFrag = text;
      if ( ( formulaTextOriginal.length() == 0 ) && ( start == 0 ) ) {
        formulaFrag = "=" + text;
      }

      formulaText.insert( start, formulaFrag );
    }


    ignoreTextEvents = true;
    editorModel.setFormulaText( formulaText.toString() );
    functionTextArea.setText( formulaText.toString() );
    ignoreTextEvents = false;

    functionTextArea.setCaretPosition( textLength + start );
    functionTextArea.requestFocus();
    revalidateParameters( false );
    revalidateFormulaSyntax();
  }


  public JToolBar getOperatorPanel() {
    return operatorPanel;
  }

  public void setEditor( final String function, final FunctionParameterEditor editor ) {
    functionParameterEditor.setEditor( function, editor );
  }

  public FunctionParameterEditor getEditor( final String function ) {
    return functionParameterEditor.getEditor( function );
  }

  public JTextArea getFunctionTextArea() {
    return functionTextArea;
  }

  protected void init() {
    editorModel = new FormulaEditorModel();

    functionInformationPanel = new FunctionInformationPanel();
    functionParameterEditor = new MultiplexFunctionParameterEditor();

    parameterUpdateHandler = new ParameterUpdateHandler();
    functionParameterEditor.addParameterUpdateListener( parameterUpdateHandler );

    functionTextArea = new InlineEditTextArea();
    this.setDocSyncHandler( new DocumentSyncHandler() );
    functionTextArea.addPropertyChangeListener( "text", getDocSyncHandler() );
    functionTextArea.setRows( 6 );
    functionTextArea.addCaretListener( new CaretHandler() );
    functionTextArea.setFont
      ( new Font( Font.MONOSPACED, functionTextArea.getFont().getStyle(), functionTextArea.getFont().getSize() ) );

    formulaContext = new DefaultFormulaContext();

    functionSelectorPanel = new FunctionListPanel();
    functionSelectorPanel
      .addPropertyChangeListener( "selectedValue", new FunctionDescriptionUpdateHandler() ); // NON-NLS
    functionSelectorPanel.addActionListener( new FunctionDescriptionUpdateHandler() );
    functionSelectorPanel.setFormulaContext( this.formulaContext );

    errorIcon = new ImageIcon( getClass().getResource( "/org/pentaho/openformula/ui/images/error.gif" ) ); // NON-NLS
    errorIconHolder = new JLabel();
    errorTextHolder = new JLabel();
    errorTextHolder.setName( "errorTextHolder" );

    selectFieldsAction = new SelectFieldAction( this, new FieldSelectorListener(), this );

    final JSplitPane functionPanel = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
    functionPanel.setTopComponent( functionParameterEditor.getEditorComponent() );
    functionPanel.setBottomComponent( buildFormulaTextPanel() );
    functionPanel.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    setLayout( new BorderLayout() );
    setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    add( functionSelectorPanel, BorderLayout.WEST );
    add( functionInformationPanel, BorderLayout.SOUTH );
    add( functionPanel, BorderLayout.CENTER );
  }

  private JComponent buildFormulaTextPanel() {
    operatorPanel = createOperatorPanel();

    final JPanel textPanel = new JPanel( new BorderLayout() );
    textPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets( 5, 0, 5, 0 );
    textPanel.add( operatorPanel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    textPanel.add( new JLabel( Messages.getInstance().getString( "FormulaEditorDialog.Formula" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    textPanel.add( errorIconHolder, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    textPanel.add( errorTextHolder, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 3;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    textPanel.add( new JScrollPane( functionTextArea ), gbc );
    return textPanel;
  }

  protected JToolBar createOperatorPanel() {
    final JToolBar operatorButtonPanel = new JToolBar();
    operatorButtonPanel.setFloatable( false );
    operatorButtonPanel.setOpaque( false );
    operatorButtonPanel.setLayout( new HorizontalLayout( 2 ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "+", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Add" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "-",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.Subtract" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "*",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.Multiply" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "/", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Divide" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "^", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Power" ) ) ) );
    operatorButtonPanel.add( Box.createRigidArea( new Dimension( 10, 1 ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "=", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Equal" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "<>",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.NotEqual" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "<", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Lesser" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( ">", Messages.getInstance().getString( "FormulaEditorDialog.Operator.Greater" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "<=",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.LesserEqual" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( ">=",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.GreaterEqual" ) ) ) );
    operatorButtonPanel.add( Box.createRigidArea( new Dimension( 10, 1 ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "%",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.Percentage" ) ) ) );
    operatorButtonPanel.add( new ToolbarButton
      ( new InsertOperatorAction( "&",
        Messages.getInstance().getString( "FormulaEditorDialog.Operator.Concatenation" ) ) ) );
    operatorButtonPanel.add( Box.createRigidArea( new Dimension( 10, 1 ) ) );
    operatorButtonPanel.add( new ToolbarButton( selectFieldsAction ) );
    return operatorButtonPanel;
  }

  public ParameterUpdateHandler getParameterUpdateHandler() {
    return parameterUpdateHandler;
  }

  public String getFormulaText() {
    return functionTextArea.getText();
  }

  public void setFormulaText( String formulaText ) {
    if ( ( formulaText == null ) || ( formulaText.length() == 0 ) ) {
      formulaText = "=";
    } else if ( formulaText.startsWith( "=" ) == false ) {
      formulaText = "=" + formulaText;
    }

    this.functionTextArea.setText( formulaText );
    this.functionTextArea.setCaretPosition( formulaText.length() );

    // Update model
    editorModel.setFormulaText( formulaText );
    editorModel.setCaretPosition( functionTextArea.getCaretPosition() );

    // Revalidate parameters and force refresh of parameter fields
    revalidateParameters( true );
  }

  public void setFields( final FieldDefinition[] fields ) {
    if ( fields == null ) {
      throw new NullPointerException();
    }
    this.fields = fields.clone();
    this.functionParameterEditor.setFields( fields );
  }

  public FieldDefinition[] getFields() {
    return fields.clone();
  }

  /**
   * Re-validate the parameters of the selected formula.
   *
   * @param switchParameterEditor - if true, then the parameter editor will adjust to correspond to formula in the
   *                              formula text-area.  This prevents parameter editor from changing while user is
   *                              entering an embedded formula.
   * @noinspection MagicCharacter
   */
  protected void revalidateParameters( final boolean switchParameterEditor ) {
    editorModel.revalidateStructure();
    if ( formulaContext == null ) {
      functionParameterEditor.clearSelectedFunction();
      return;
    }
    final FunctionInformation fnInfo = editorModel.getCurrentFunction();
    if ( fnInfo == null ) {
      functionParameterEditor.clearSelectedFunction();
      return;
    }
    final FunctionDescription fnDesc = formulaContext.getFunctionRegistry().getMetaData( fnInfo.getCanonicalName() );
    if ( fnDesc == null ) {
      functionParameterEditor.clearSelectedFunction();
      return;
    }

    functionInformationPanel.setSelectedFunction( fnDesc );

    try {
      ignoreTextEvents = true;
      functionParameterEditor.setSelectedFunction( new FunctionParameterContext
        ( fnDesc, fnInfo, switchParameterEditor, editorModel ) );
    } finally {
      ignoreTextEvents = false;
    }
  }

  private void refreshInformationPanel() {
    final FunctionInformation currentFunction = editorModel.getCurrentFunction();

    final FunctionDescription description;
    if ( currentFunction != null ) {
      description = formulaContext.getFunctionRegistry().getMetaData( currentFunction.getCanonicalName() );
    } else {
      description = functionSelectorPanel.getSelectedValue();
    }
    functionInformationPanel.setSelectedFunction( description );
  }


  protected void revalidateFormulaSyntax() {
    try {
      final String rawFormula = editorModel.getFormulaText();
      if ( StringUtils.isEmpty( rawFormula ) ) {
        errorTextHolder.setText( "" );
        errorTextHolder.setToolTipText( null );
        errorIconHolder.setIcon( null );
        return;
      }
      final String formulaText = FormulaUtil.extractFormula( rawFormula );
      if ( StringUtils.isEmpty( formulaText ) ) {
        errorTextHolder.setText( Messages.getInstance().getString( "FormulaEditorDialog.ShortErrorNoFormulaContext" ) );
        errorTextHolder
          .setToolTipText( Messages.getInstance().getString( "FormulaEditorDialog.ErrorNoFormulaContext" ) );
        return;
      }
      final Formula formula = new Formula( formulaText );
      formula.initialize( formulaContext );
      final TypeValuePair pair = formula.evaluateTyped();

      if ( pair.getValue() instanceof LibFormulaErrorValue ) {
        errorTextHolder.setText( Messages.getInstance().getString( "FormulaEditorDialog.ShortEvaluationError" ) );
        errorTextHolder.setToolTipText( Messages.getInstance().getString( "FormulaEditorDialog.EvaluationError" ) );
      } else {
        errorTextHolder.setToolTipText( null );
        errorTextHolder.setText( Messages.getInstance()
          .getString( "FormulaEditorDialog.EvaluationResult", String.valueOf( pair.getValue() ) ) );
      }
      errorIconHolder.setIcon( null );
    } catch ( ParseException pe ) {
      errorIconHolder.setIcon( errorIcon );
      if ( pe.currentToken == null ) {
        errorTextHolder.setText( Messages.getInstance().getString( "FormulaEditorDialog.ShortParseError" ) );
        errorTextHolder.setToolTipText(
          Messages.getInstance().getString( "FormulaEditorDialog.GenericParseError", pe.getLocalizedMessage() ) );
      } else {
        final String token = pe.currentToken.toString();
        final int line = pe.currentToken.beginLine;
        final int column = pe.currentToken.beginColumn;
        errorTextHolder.setText( Messages.getInstance().getString( "FormulaEditorDialog.ShortParseError" ) );
        errorTextHolder.setToolTipText( Messages.getInstance().getString( "FormulaEditorDialog.ParseError",
          new Object[] { token, line, column } ) );
      }
    } catch ( Exception e ) {
      errorIconHolder.setIcon( errorIcon );
      errorTextHolder.setText( Messages.getInstance().getString( "FormulaEditorDialog.ShortParseError" ) );
      errorTextHolder.setToolTipText(
        Messages.getInstance().getString( "FormulaEditorDialog.GenericParseError", e.getLocalizedMessage() ) );
    }
  }
}
