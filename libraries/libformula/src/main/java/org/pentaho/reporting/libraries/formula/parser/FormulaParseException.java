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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
