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

import bsh.ParseException;
import bsh.Parser;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.misc.beanshell.BSHExpression;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.StringReader;

public class BeanShellScriptingExpressionEditor extends ScriptingExpressionEditor {
  private class ValidateAction extends AbstractAction {
    private ValidateAction() {
      putValue( Action.NAME, EditorExpressionsMessages.getString( "BeanShellScriptingExpressionEditor.Validation" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final Parser parser = new Parser( new StringReader( getText() ) );
      parser.setRetainComments( true );
      setStatus( " " );
      try {
        //noinspection StatementWithEmptyBody
        while ( !parser.Line() ) {
          ;
        }
        setStatus( EditorExpressionsMessages.getString( "BeanShellScriptingExpressionEditor.ValidationComplete" ) );
      } catch ( ParseException e1 ) {
        setStatus( e1.getMessage() );
      }
    }
  }

  private BSHExpression bshExpression;

  public BeanShellScriptingExpressionEditor() {
    setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_JAVA );
    addValidateButton( new ValidateAction() );
  }

  public void initialize( final Expression expression, final ReportDesignerContext context ) {
    bshExpression = (BSHExpression) expression;
    setText( bshExpression.getExpression() );
  }

  public void stopEditing() {
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
