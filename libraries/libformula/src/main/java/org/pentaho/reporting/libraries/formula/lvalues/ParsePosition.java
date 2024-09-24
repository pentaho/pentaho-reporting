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
