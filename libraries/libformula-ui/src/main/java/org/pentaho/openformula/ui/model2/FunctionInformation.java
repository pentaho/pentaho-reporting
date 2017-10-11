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

package org.pentaho.openformula.ui.model2;

import java.util.Arrays;

public class FunctionInformation {
  private String canonicalName;
  private int functionParameterStart;
  private int functionParameterEnd;
  private String[] parameterText;
  private int[] paramStart;
  private int[] paramEnd;
  private int functionOffset;
  private String functionText;

  public FunctionInformation( final String canonicalName,
                              final int functionOffset,
                              final int functionParameterStart,
                              final int functionParameterEnd,
                              final String functionText,
                              final String[] parameterText,
                              final int[] paramStart,
                              final int[] paramEnd ) {
    if ( canonicalName == null ) {
      throw new NullPointerException();
    }
    if ( functionParameterStart < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( functionParameterEnd < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( functionParameterEnd < functionParameterStart ) {
      throw new IndexOutOfBoundsException();
    }
    this.functionText = functionText;
    this.functionOffset = functionOffset;
    this.canonicalName = canonicalName;
    this.parameterText = parameterText;
    this.functionParameterStart = functionParameterStart;
    this.functionParameterEnd = functionParameterEnd;
    this.paramStart = paramStart;
    this.paramEnd = paramEnd;
  }

  public String getFunctionText() {
    return functionText;
  }

  public int getFunctionOffset() {
    return functionOffset;
  }

  public int getFunctionParameterEnd() {
    return functionParameterEnd;
  }

  public int getFunctionParameterStart() {
    return functionParameterStart;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public String getParameterText( final int i ) {
    return parameterText[ i ];
  }

  public int getParamStart( final int i ) {
    if ( i < paramStart.length ) {
      return paramStart[ i ];
    }

    return functionParameterStart;
  }

  public int getParamEnd( final int i ) {
    if ( i < 0 ) {
      return functionParameterStart;
    }
    return paramEnd[ i ];
  }

  public int getParameterCount() {
    return parameterText.length;
  }

  public String[] getParameters() {
    return parameterText.clone();
  }

  public String toString() {
    return "FunctionInformation{" +
      "canonicalName='" + canonicalName + '\'' +
      ", functionParameterStart=" + functionParameterStart +
      ", functionParameterEnd=" + functionParameterEnd +
      ", parameterText=" + Arrays.toString( parameterText ) +
      ", paramStart=" + Arrays.toString( paramStart ) +
      ", paramEnd=" + Arrays.toString( paramEnd ) +
      ", functionOffset=" + functionOffset +
      ", functionText='" + functionText + '\'' +
      '}';
  }
}
