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

package org.pentaho.reporting.libraries.formula.lvalues;

import java.io.Serializable;

public class ParsePosition implements Serializable {
  private int startColumn;
  private int startLine;
  private int endColumn;
  private int endLine;

  public ParsePosition( final int startLine,
                        final int startColumn,
                        final int endLine,
                        final int endColumn ) {
    this.startLine = startLine;
    this.startColumn = startColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
  }

  public int getEndColumn() {
    return endColumn;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getStartColumn() {
    return startColumn;
  }

  public int getStartLine() {
    return startLine;
  }

  public String toString() {
    return "ParsePosition={startLine=" + startLine +
      "; startColumn=" + startColumn +
      "; endLine=" + endLine +
      "; endColumn=" + endColumn + "}";

  }
}
