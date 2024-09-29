/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
