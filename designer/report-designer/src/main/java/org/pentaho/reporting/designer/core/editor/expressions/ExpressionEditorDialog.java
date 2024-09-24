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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.ExpressionListCellRenderer;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

public class ExpressionEditorDialog extends CommonDialog {
  private static final String PREFIX = "org.pentaho.reporting.designer.core.editor.expressions.plugins.";

  private class ExpressionSelectionHandler implements ListDataListener {
    private ExpressionSelectionHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {

    }

    public void intervalRemoved( final ListDataEvent e ) {

    }

    public void contentsChanged( final ListDataEvent e ) {
      final ExpressionMetaData selectedItem = (ExpressionMetaData) expressionEditor.getSelectedItem();
      final Expression expression = getExpression();

      if ( selectedItem == null ) {
        if ( expression != null ) {
          setExpression( null );
        }
        return;
      }

      if ( expression == null ||
        ObjectUtilities.equal( selectedItem.getExpressionType(), expression.getClass() ) == false ) {
        setExpression( selectedItem.create() );
      }
    }
  }

  private static class ExpressionEditorWrapper extends JComponent {
    private JComponent disabledPanel;
    private CardLayout cardLayout;
    private JPanel enabledPanel;
    private ExpressionEditor editor;

    private ExpressionEditorWrapper() {
      disabledPanel = new JPanel();
      disabledPanel.setLayout( new BorderLayout() );
      disabledPanel
        .add( new JLabel( EditorExpressionsMessages.getString( "ExpressionEditorDialog.NoExtendedEditor" ) ) );

      cardLayout = new CardLayout();

      enabledPanel = new JPanel();
      enabledPanel.setLayout( new BorderLayout() );

      setLayout( cardLayout );
      add( disabledPanel, "disabled" ); // NON-NLS
      add( enabledPanel, "enabled" ); // NON-NLS

      cardLayout.first( this );
    }

    public ExpressionEditor getEditor() {
      return editor;
    }

    public void setEditor( final ExpressionEditor editor ) {
      if ( this.editor != null ) {
        this.editor.stopEditing();
      }

      this.editor = editor;

      if ( editor == null ) {
        cardLayout.first( this );
      } else {
        final JComponent editorComponent = editor.getEditorComponent();
        enabledPanel.removeAll();
        enabledPanel.add( editorComponent );
        enabledPanel.revalidate();

        cardLayout.last( this );
      }
    }

    public void stopEditing() {
      if ( this.editor != null ) {
        this.editor.stopEditing();
      }
    }
  }

  private class TabSelectionListener implements ChangeListener {
    private TabSelectionListener() {
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( viewPane.getSelectedIndex() == 0 ) {
        if ( viewPane.getTabCount() == 2 ) {
          wrapper.getEditor().stopEditing();
        }
      } else {
        expressionEditorPane.stopEditing();
        wrapper.getEditor().initialize( expression, designerContext );
      }
    }
  }

  private JTabbedPane viewPane;
  private ExpressionPropertiesEditorPanel expressionEditorPane;
  private JComboBox expressionEditor;
  private Expression expression;
  private ExpressionEditorWrapper wrapper;
  private HashMap<String, Class> editorPlugins;
  private ReportDesignerContext designerContext;
  private DefaultComboBoxModel model;
  private boolean expressionsOnly;
  private boolean showStandaloneProperties;


  public ExpressionEditorDialog() {
    init();
  }

  public ExpressionEditorDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public ExpressionEditorDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    editorPlugins = new HashMap<String, Class>();
    loadPlugins();

    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    setTitle( EditorExpressionsMessages.getString( "ExpressionEditorDialog.Title" ) );
    setModal( true );
    setResizable( true );

    expressionEditorPane = new ExpressionPropertiesEditorPanel();
    wrapper = new ExpressionEditorWrapper();

    viewPane = new JTabbedPane();
    viewPane.addTab( EditorExpressionsMessages.getString( "ExpressionEditorDialog.Properties" ),
      new JScrollPane( expressionEditorPane ) );
    viewPane.setEnabledAt( 0, false );
    viewPane.addChangeListener( new TabSelectionListener() );

    final ExpressionMetaData[] knownExpressions = ExpressionUtil.getInstance().getKnownExpressions();
    model = new DefaultComboBoxModel( knownExpressions );
    model.addListDataListener( new ExpressionSelectionHandler() );

    expressionEditor = new JComboBox( model );
    expressionEditor.setEditable( false );
    expressionEditor.setRenderer( new ExpressionListCellRenderer() );

    super.init();

    setExpressionsOnly( true );
    setExpression( null );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ExpressionEditor";
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression( final Expression expression ) {
    if ( viewPane.getTabCount() == 2 ) {
      viewPane.removeTabAt( 1 );
    }
    this.expression = expression;
    if ( expression == null ) {
      this.expressionEditor.setSelectedItem( null );
      this.wrapper.setEditor( null );

      this.expressionEditorPane.setData( new Expression[ 0 ] );

      viewPane.setEnabledAt( 0, false );
    } else {
      try {

        viewPane.setEnabledAt( 0, true );

        this.expressionEditor.setSelectedItem
          ( ExpressionRegistry.getInstance().getExpressionMetaData( expression.getClass().getName() ) );

        this.expressionEditorPane.setData( new Expression[] { this.expression } );
        final ExpressionEditor plugin = createEditorForClass( expression );
        if ( plugin != null ) {
          wrapper.setEditor( plugin );

          viewPane.addTab( plugin.getTitle(), wrapper );
          viewPane.setSelectedIndex( 1 );
        } else {
          wrapper.setEditor( null );
        }
      } catch ( MetaDataLookupException e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
        this.expressionEditor.setSelectedItem( null );
      }
    }
  }

  private ExpressionEditor createEditorForClass( final Expression expression ) {
    final Class plugin = editorPlugins.get( expression.getClass().getName() );
    if ( plugin == null ) {
      return null;
    }
    try {
      final ExpressionEditor ed = (ExpressionEditor) plugin.newInstance();
      ed.initialize( expression, designerContext );
      return ed;
    } catch ( Throwable e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      return null;
    }
  }

  public boolean isExpressionsOnly() {
    return expressionsOnly;
  }

  public void setExpressionsOnly( final boolean expressionsOnly ) {
    if ( this.expressionsOnly == expressionsOnly ) {
      return;
    }

    this.expressionsOnly = expressionsOnly;
    this.model.removeAllElements();
    this.model.addElement( null );
    final ExpressionMetaData[] knownExpressions;
    if ( expressionsOnly ) {
      knownExpressions = ExpressionUtil.getInstance().getKnownExpressions();
    } else {
      knownExpressions = ExpressionUtil.getInstance().getKnownFunctions();
    }
    for ( final ExpressionMetaData knownExpression : knownExpressions ) {
      this.model.addElement( knownExpression );
    }
  }

  private void loadPlugins() {
    final ClassLoader classLoader = ObjectUtilities.getClassLoader( ExpressionEditorDialog.class );
    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator<String> keys = config.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = keys.next();
      final String expressionClass = key.substring( PREFIX.length() );
      try {
        final String editorClass = config.getConfigProperty( key );
        final Class c = Class.forName( editorClass, false, classLoader );
        if ( c != null ) {
          editorPlugins.put( expressionClass, c );
        }
      } catch ( Throwable e ) {
        e.printStackTrace();
      }
    }
  }

  protected Component createContentPane() {
    final JPanel headerPanel = new JPanel( new VerticalLayout( 5, VerticalLayout.LEFT, VerticalLayout.TOP ) );
    headerPanel.add( new JLabel( EditorExpressionsMessages.getString( "ExpressionEditorDialog.SelectedExpression" ) ) );
    headerPanel.add( expressionEditor );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( viewPane, BorderLayout.CENTER );
    panel.add( headerPanel, BorderLayout.NORTH );
    return panel;
  }

  public Expression performEditExpression( final ReportDesignerContext context, final Expression expression ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    setShowStandaloneProperties( false );
    setExpressionsOnly( true );
    designerContext = context;
    if ( expression == null ) {
      setExpression( new FormulaExpression() );
    } else {
      setExpression( expression.getInstance() );
    }
    if ( super.performEdit() == false ) {
      return null;
    }

    this.wrapper.stopEditing();
    if ( this.expression instanceof FormulaExpression ) {
      final FormulaExpression formulaExpression = (FormulaExpression) this.expression;
      if ( StringUtils.isEmpty( formulaExpression.getFormula() ) ) {
        return null;
      }
    }
    return this.expression;
  }

  public void setShowStandaloneProperties( final boolean showStandaloneProperties ) {
    this.showStandaloneProperties = showStandaloneProperties;
  }

  public boolean isShowStandaloneProperties() {
    return showStandaloneProperties;
  }


}
