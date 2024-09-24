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

import org.apache.bsf.BSFManager;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.misc.bsf.BSFExpression;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BSFScriptingExpressionEditor extends ScriptingExpressionEditor {
  private static class InternalBSFManager extends BSFManager {
    private InternalBSFManager() {
    }

    public static String[] getRegisteredLanguages() {
      final ArrayList<String> list = new ArrayList<String>();
      final Iterator iterator = registeredEngines.entrySet().iterator();
      while ( iterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) iterator.next();
        final String lang = (String) entry.getKey();
        final String className = (String) entry.getValue();

        try {
          // this is how BSH will load the class
          Class.forName( className, false, Thread.currentThread().getContextClassLoader() );
          list.add( lang );
        } catch ( Throwable t ) {
          // ignored.
        }

      }
      return list.toArray( new String[ list.size() ] );
    }
  }

  private class UpdateLanguageHandler implements ListDataListener {
    private UpdateLanguageHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {

    }

    public void intervalRemoved( final ListDataEvent e ) {

    }

    public void contentsChanged( final ListDataEvent e ) {
      updateComponents();
    }

    private void updateComponents() {
      setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting( (String) languageField.getSelectedItem() ) );
    }
  }

  private BSFExpression bshExpression;
  private JComboBox languageField;

  public BSFScriptingExpressionEditor() {
    setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_JAVA );

    languageField = new SmartComboBox( new DefaultComboBoxModel( InternalBSFManager.getRegisteredLanguages() ) );
    languageField.getModel().addListDataListener( new UpdateLanguageHandler() );

    final JPanel panel = new JPanel();
    panel.setLayout( new VerticalLayout( 5, VerticalLayout.BOTH ) );
    panel.add( new JLabel( EditorExpressionsMessages.getString( "BSFScriptingExpressionEditor.Language" ) ) );
    panel.add( languageField );

    getPanel().add( panel, BorderLayout.NORTH );
  }

  protected String getLanguage() {
    return (String) languageField.getSelectedItem();
  }

  public void initialize( final Expression expression, final ReportDesignerContext context ) {
    bshExpression = (BSFExpression) expression;
    setText( bshExpression.getExpression() );
    languageField.setSelectedItem( bshExpression.getLanguage() );
  }

  public void stopEditing() {
    bshExpression.setLanguage( (String) languageField.getSelectedItem() );
    bshExpression.setExpression( getText() );
  }

  private String mapLanguageToSyntaxHighlighting( final String language ) {
    if ( "beanshell".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }
    if ( "groovy".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_GROOVY;
    }
    if ( "javascript".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
    }
    if ( "jython".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_PYTHON;
    }
    if ( "xslt".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_XML;
    }

    return SyntaxConstants.SYNTAX_STYLE_NONE;
  }
}
