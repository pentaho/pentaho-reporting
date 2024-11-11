/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.parser;

import org.pentaho.reporting.libraries.formula.lvalues.ParsePosition;

public class FormulaParseException extends ParseException {
  private ParsePosition parsePosition;
  private TokenMgrError error;
  private Token currentTokenVal;
  private Throwable parent;

  public FormulaParseException( final String message ) {
    super( message );
  }

  public FormulaParseException( final ParseException pe ) {
    this( pe.currentToken, pe.expectedTokenSequences, pe.tokenImage );
    this.parent = pe;
  }

  public FormulaParseException( final Token currentTokenVal,
                                final int[][] expectedTokenSequencesVal,
                                final String[] tokenImageVal ) {
    super( currentTokenVal, expectedTokenSequencesVal, tokenImageVal );
    this.currentTokenVal = currentTokenVal;
    parsePosition = new ParsePosition
      ( currentTokenVal.beginLine, currentTokenVal.beginColumn,
        currentTokenVal.endLine, currentTokenVal.endColumn );
  }

  public Token getCurrentTokenVal() {
    return currentTokenVal;
  }

  public Throwable getParent() {
    return parent;
  }

  public FormulaParseException( final TokenMgrError error ) {
    super( error.getMessage() );
    this.error = error;
    this.parent = error;
    this.parsePosition = new ParsePosition
      ( error.getErrorLine(), error.getErrorColumn(), error.getErrorLine(), error.getErrorColumn() );
  }

  public TokenMgrError getError() {
    return error;
  }

  public ParsePosition getParsePosition() {
    return parsePosition;
  }
}
