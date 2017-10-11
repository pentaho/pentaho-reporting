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

package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.styles.Messages;
import org.pentaho.reporting.designer.core.editor.styles.SimpleStyleEditorPanel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.CSSParseException;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;
import org.pentaho.reporting.engine.classic.core.style.css.StyleSheetParserUtil;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.w3c.css.sac.SelectorList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class StyleDefinitionEditorDialog extends CommonDialog {
  private class SelectorUpdateHandler extends DocumentChangeHandler {
    private JTextField textField;
    private ElementStyleRule styleRule;
    private JXTaskPane pane;
    private Color color;
    private Color errorColor;

    private SelectorUpdateHandler( final JTextField textField,
                                   final ElementStyleRule styleRule,
                                   final JXTaskPane pane ) {
      this.textField = textField;
      this.styleRule = styleRule;
      this.pane = pane;
      this.color = textField.getBackground();
      this.errorColor = new Color( 1f, 0.75f, 0.75f );
    }

    protected void handleChange( final DocumentEvent e ) {
      try {
        final NamespaceCollection nc = StyleSheetParserUtil.getInstance().getNamespaceCollection();
        final String selectorText = textField.getText();
        final SelectorList list = StyleSheetParserUtil.getInstance().parseSelector( nc, selectorText );
        styleRule.clearSelectors();
        textField.setBackground( color );
        for ( int i = 0; i < list.getLength(); i++ ) {
          final CSSSelector selector = (CSSSelector) list.item( i );
          styleRule.addSelector( selector );
        }

        pane.setTitle( Messages.getString( "StyleDefinitionEditorDialog.RuleTitle", selectorText ) );
      } catch ( CSSParseException e1 ) {
        textField.setBackground( errorColor );
      }
    }
  }

  private class CloseAction extends AbstractAction {
    private CloseAction() {
      putValue( Action.NAME, Messages.getString( "StyleDefinitionEditorDialog.Close.Text" ) );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "StyleDefinitionEditorDialog.Close.Description" ) );
      putValue( Action.MNEMONIC_KEY, Messages.getOptionalMnemonic( "StyleDefinitionEditorDialog.Close.Mnemonic" ) );
      putValue( Action.ACCELERATOR_KEY,
        Messages.getOptionalKeyStroke( "StyleDefinitionEditorDialog.Close.Accelerator" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getEmptyIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      getCancelAction().actionPerformed( e );
    }
  }

  private class ElementStyleDefinitionChangeHandler implements ElementStyleDefinitionChangeListener {
    private ElementStyleDefinitionChangeHandler() {
    }

    public void styleRuleAdded( final ElementStyleDefinitionChangeEvent event ) {
      addRulePane( event.getStyleRule() );
    }

    public void styleRuleRemoved( final ElementStyleDefinitionChangeEvent event ) {
      removeRulePane( event.getStyleRule() );
    }

    public void styleRulesChanged( final ElementStyleDefinitionChangeEvent event ) {
      rebuildPanes();
    }
  }

  private class FileSourceChangeHandler implements PropertyChangeListener {
    private FileSourceChangeHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( editorContext.getSource() == null ) {
        setTitle( Messages.getString( "StyleDefinitionEditorDialog.Title" ) );
      } else {
        setTitle( Messages.getString( "StyleDefinitionEditorDialog.TitleFile", editorContext.getSource() ) );
      }
    }
  }

  private JXTaskPaneContainer taskPaneContainer;
  private HashMap<ElementStyleRule, JXTaskPane> taskPanes;
  private JXTaskPane taskPane;
  private boolean standalone;
  private JMenuBar standaloneMenuBar;
  private JMenuBar inlineMenuBar;
  private StyleDefinitionEditorContext editorContext;

  public StyleDefinitionEditorDialog( final ReportDesignerContext designerContext ) {
    init( designerContext );
  }

  public StyleDefinitionEditorDialog( final Frame owner,
                                      final ReportDesignerContext designerContext ) throws HeadlessException {
    super( owner );
    init( designerContext );
  }

  public StyleDefinitionEditorDialog( final Dialog owner,
                                      final ReportDesignerContext designerContext ) throws HeadlessException {
    super( owner );
    init( designerContext );
  }

  protected void init( final ReportDesignerContext designerContext ) {
    this.taskPanes = new HashMap<ElementStyleRule, JXTaskPane>();
    this.editorContext = new StyleDefinitionEditorContext( designerContext, this, new ElementStyleDefinition() );
    this.editorContext.addPropertyChangeListener( "source", new FileSourceChangeHandler() );

    setTitle( Messages.getString( "StyleDefinitionEditorDialog.InlineTitle" ) );

    final JMenu fileMenu = new JMenu( Messages.getString( "StyleDefinitionEditorDialog.Menu.File.Label" ) );
    fileMenu.setMnemonic( Messages.getMnemonic( "StyleDefinitionEditorDialog.Menu.File.Mnemonic" ) );
    fileMenu.add( new OpenAction( editorContext ) );
    fileMenu.add( new SaveAction( editorContext ) );
    fileMenu.add( new SaveAsAction( editorContext ) );
    fileMenu.addSeparator();
    fileMenu.add( new CloseAction() );

    standaloneMenuBar = new JMenuBar();
    standaloneMenuBar.add( fileMenu );

    final JMenu inlineMenu = new JMenu( Messages.getString( "StyleDefinitionEditorDialog.Menu.File.Label" ) );
    inlineMenu.setMnemonic( Messages.getMnemonic( "StyleDefinitionEditorDialog.Menu.File.Mnemonic" ) );
    inlineMenu.add( new OpenAction( editorContext ) );
    inlineMenu.add( new SaveAction( editorContext ) );
    inlineMenu.add( new SaveAsAction( editorContext ) );

    inlineMenuBar = new JMenuBar();
    inlineMenuBar.add( inlineMenu );

    setJMenuBar( inlineMenuBar );

    super.init();

    editorContext.addElementStyleDefinitionChangeListener( new ElementStyleDefinitionChangeHandler() );
  }

  protected void performInitialResize() {
    setSize( 800, 600 );
    LibSwingUtil.centerDialogInParent( this );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.StyleDefinitionEditor";
  }


  public boolean isStandalone() {
    return standalone;
  }

  public void setStandalone( final boolean standalone ) {
    this.standalone = standalone;
    if ( standalone ) {
      setJMenuBar( standaloneMenuBar );
      setButtonPaneVisible( false );
      setTitle( Messages.getString( "StyleDefinitionEditorDialog.Title" ) );
    } else {
      setJMenuBar( inlineMenuBar );
      setButtonPaneVisible( true );
      setTitle( Messages.getString( "StyleDefinitionEditorDialog.InlineTitle" ) );
    }
  }

  protected Component createContentPane() {
    taskPane = new JXTaskPane();
    taskPane.setSpecial( true );
    taskPane.setCollapsed( false );
    taskPane.setTitle( Messages.getString( "StyleDefinitionEditorDialog.TaskTitle" ) );
    taskPane.add( new AddStyleRuleAction( editorContext ) );

    taskPaneContainer = new JXTaskPaneContainer();
    taskPaneContainer.add( taskPane );
    return new JScrollPane( taskPaneContainer );
  }

  private void rebuildPanes() {
    this.taskPanes.clear();
    this.taskPaneContainer.removeAll();
    this.taskPaneContainer.add( taskPane );

    final ElementStyleDefinition existing = editorContext.getStyleDefinition();
    for ( int i = 0; i < existing.getRuleCount(); i += 1 ) {
      final ElementStyleSheet maybeRule = existing.getRule( i );
      if ( maybeRule instanceof ElementStyleRule == false ) {
        continue;
      }

      final ElementStyleRule rule = (ElementStyleRule) maybeRule;
      addRulePane( rule );
    }
  }

  private void addRulePane( final ElementStyleRule rule ) {
    final String selectorText = convertSelectorText( rule );
    final JTextField selector = new JTextField();
    selector.setText( selectorText );

    final JXTaskPane pane = new JXTaskPane();
    pane.setTitle( Messages.getString( "StyleDefinitionEditorDialog.RuleTitle", selectorText ) );
    pane.add( new RemoveStyleRuleAction( editorContext, rule ) );
    pane.add( selector );

    final SimpleStyleEditorPanel comp = new SimpleStyleEditorPanel( editorContext );
    comp.setReportDesignerContext( editorContext.getDesignerContext() );
    comp.setData( rule );
    pane.add( comp );

    selector.getDocument().addDocumentListener( new SelectorUpdateHandler( selector, rule, pane ) );

    taskPanes.put( rule, pane );
    taskPaneContainer.add( pane );

    taskPaneContainer.revalidate();
    taskPaneContainer.repaint();
  }

  private void removeRulePane( final ElementStyleRule rule ) {
    final JXTaskPane jxTaskPane = taskPanes.get( rule );
    if ( jxTaskPane != null ) {
      this.taskPaneContainer.remove( jxTaskPane );
      this.taskPanes.remove( rule );

      this.taskPaneContainer.revalidate();
      this.taskPaneContainer.repaint();
    }
  }

  private String convertSelectorText( final ElementStyleRule rule ) {
    final NamespaceCollection nc = StyleSheetParserUtil.getInstance().getNamespaceCollection();
    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < rule.getSelectorCount(); i += 1 ) {
      if ( i != 0 ) {
        b.append( ", " );
      }
      final CSSSelector selector = rule.getSelector( i );
      b.append( selector.print( nc ) );
    }
    return b.toString();
  }

  public ElementStyleDefinition performEdit( final ElementStyleDefinition existing ) {
    editorContext.setStyleDefinition( existing );
    if ( super.performEdit() == false ) {
      return existing;
    }

    return editorContext.getStyleDefinition().clone();
  }

  public static StyleDefinitionEditorDialog createDialog( final Component parent,
                                                          final ReportDesignerContext context ) {
    final StyleDefinitionEditorDialog exceptionDialog;
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof Dialog ) {
      exceptionDialog = new StyleDefinitionEditorDialog( (Dialog) window, context );
    } else if ( window instanceof Frame ) {
      exceptionDialog = new StyleDefinitionEditorDialog( (Frame) window, context );
    } else {
      exceptionDialog = new StyleDefinitionEditorDialog( context );
    }
    return exceptionDialog;
  }

  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    ReportDesignerBoot.getInstance().start();

    final StyleDefinitionEditorDialog dialog = new StyleDefinitionEditorDialog( null );
    dialog.setStandalone( true );
    dialog.setVisible( true );
  }
}
