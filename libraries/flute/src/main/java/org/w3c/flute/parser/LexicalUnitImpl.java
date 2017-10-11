/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: LexicalUnitImpl.java 1830 2006-04-23 14:51:03Z taqua $
 */
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
 * Copyright (c) 1999 - 2017 Hitachi Vantara, World Wide Web Consortium.  All rights reserved.
 */

package org.w3c.flute.parser;

import org.w3c.css.sac.LexicalUnit;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
class LexicalUnitImpl implements LexicalUnit {

  LexicalUnit prev;
  LexicalUnit next;

  short type;
  int line;
  int column;

  int i;
  float f;
  short dimension;
  String sdimension;
  String s;
  String fname;
  LexicalUnitImpl params;

  LexicalUnitImpl( short type, int line, int column, LexicalUnitImpl p ) {
    if ( p != null ) {
      prev = p;
      p.next = this;
    }
    this.line = line;
    this.column = column - 1;
    this.type = type;
  }

  LexicalUnitImpl( int line, int column, LexicalUnitImpl previous, int i ) {
    this( SAC_INTEGER, line, column, previous );
    this.i = i;
  }

  LexicalUnitImpl( int line, int column, LexicalUnitImpl previous,
                   short dimension, String sdimension, float f ) {
    this( dimension, line, column, previous );
    this.f = f;
    this.dimension = dimension;
    this.sdimension = sdimension;
  }

  LexicalUnitImpl( int line, int column, LexicalUnitImpl previous,
                   short type, String s ) {
    this( type, line, column, previous );
    this.s = s;
  }

  LexicalUnitImpl( short type, int line, int column,
                   LexicalUnitImpl previous, String fname,
                   LexicalUnitImpl params ) {
    this( type, line, column, previous );
    this.fname = fname;
    this.params = params;
  }


  public int getLineNumber() {
    return line;
  }

  public int getColumnNumber() {
    return column;
  }

  public short getLexicalUnitType() {
    return type;
  }

  public LexicalUnit getNextLexicalUnit() {
    return next;
  }

  public LexicalUnit getPreviousLexicalUnit() {
    return prev;
  }

  public int getIntegerValue() {
    return i;
  }

  void setIntegerValue( int i ) {
    this.i = i;
  }

  public float getFloatValue() {
    return f;
  }

  void setFloatValue( float f ) {
    this.f = f;
  }

  public String getDimensionUnitText() {
    switch( type ) {
      case SAC_PERCENTAGE:
        return "%";
      case SAC_EM:
        return "em";
      case SAC_EX:
        return "ex";
      case SAC_PIXEL:
        return "px";
      case SAC_CENTIMETER:
        return "cm";
      case SAC_MILLIMETER:
        return "mm";
      case SAC_INCH:
        return "in";
      case SAC_POINT:
        return "pt";
      case SAC_PICA:
        return "pc";
      case SAC_DEGREE:
        return "deg";
      case SAC_RADIAN:
        return "rad";
      case SAC_GRADIAN:
        return "grad";
      case SAC_MILLISECOND:
        return "ms";
      case SAC_SECOND:
        return "s";
      case SAC_HERTZ:
        return "Hz";
      case SAC_KILOHERTZ:
        return "kHz";
      case SAC_DIMENSION:
        return sdimension;
      default:
        throw new IllegalStateException( "invalid dimension " + type );
    }
  }

  public String getStringValue() {
    return s;
  }

  public String getFunctionName() {
    return fname;
  }

  public org.w3c.css.sac.LexicalUnit getParameters() {
    return params;
  }

  public org.w3c.css.sac.LexicalUnit getSubValues() {
    return params;
  }

  public String toString() {
    String text;
    switch( type ) {
      case SAC_OPERATOR_COMMA:
        text = ",";
        break;
      case SAC_OPERATOR_PLUS:
        text = "+";
        break;
      case SAC_OPERATOR_MINUS:
        text = "-";
        break;
      case SAC_OPERATOR_MULTIPLY:
        text = "*";
        break;
      case SAC_OPERATOR_SLASH:
        text = "/";
        break;
      case SAC_OPERATOR_MOD:
        text = "%";
        break;
      case SAC_OPERATOR_EXP:
        text = "^";
        break;
      case SAC_OPERATOR_LT:
        text = "<";
        break;
      case SAC_OPERATOR_GT:
        text = ">";
        break;
      case SAC_OPERATOR_LE:
        text = "<=";
        break;
      case SAC_OPERATOR_GE:
        text = "=>";
        break;
      case SAC_OPERATOR_TILDE:
        text = "~";
        break;
      case SAC_INHERIT:
        text = "inherit";
        break;
      case SAC_INTEGER:
        text = Integer.toString( i, 10 );
        break;
      case SAC_REAL:
        text = f + "";
        break;
      case SAC_EM:
      case SAC_EX:
      case SAC_PIXEL:
      case SAC_INCH:
      case SAC_CENTIMETER:
      case SAC_MILLIMETER:
      case SAC_POINT:
      case SAC_PICA:
      case SAC_PERCENTAGE:
      case SAC_DEGREE:
      case SAC_GRADIAN:
      case SAC_RADIAN:
      case SAC_MILLISECOND:
      case SAC_SECOND:
      case SAC_HERTZ:
      case SAC_KILOHERTZ:
      case SAC_DIMENSION:
        String fs = null;
        int i = (int) f;
        if ( ( (float) i ) == f ) {
          text = i + getDimensionUnitText();
        } else {
          text = f + getDimensionUnitText();
        }
        break;
      case SAC_URI:
        text = "uri(" + s + ")";
        break;
      case SAC_COUNTER_FUNCTION:
      case SAC_COUNTERS_FUNCTION:
      case SAC_RGBCOLOR:
      case SAC_RECT_FUNCTION:
      case SAC_FUNCTION:
        text = getFunctionName() + "(" + getParameters() + ")";
        break;
      case SAC_IDENT:
        text = getStringValue();
        break;
      case SAC_STRING_VALUE:
        // @@SEEME. not exact
        text = "\"" + getStringValue() + "\"";
        break;
      case SAC_ATTR:
        text = "attr(" + getStringValue() + ")";
        break;
      case SAC_UNICODERANGE:
        text = "@@TODO";
        break;
      case SAC_SUB_EXPRESSION:
        text = getSubValues().toString();
        break;
      default:
        text = "@unknown";
        break;
    }
    if ( next != null ) {
      return text + ' ' + next;
    } else {
      return text;
    }
  }

  // here some useful function for creation
  static LexicalUnitImpl createNumber( int line, int column,
                                       LexicalUnitImpl previous, float v ) {
    int i = (int) v;
    if ( v == ( (float) i ) ) {
      return new LexicalUnitImpl( line, column, previous, i );
    } else {
      return new LexicalUnitImpl( line, column, previous, SAC_REAL, "", v );
    }
  }

  static LexicalUnitImpl createInteger( int line, int column,
                                        LexicalUnitImpl previous, int i ) {
    return new LexicalUnitImpl( line, column, previous, i );
  }

  static LexicalUnitImpl createPercentage( int line, int column,
                                           LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_PERCENTAGE, null, v );
  }

  static LexicalUnitImpl createEMS( int line, int column,
                                    LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_EM, null, v );
  }

  static LexicalUnitImpl createEXS( int line, int column,
                                    LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_EX, null, v );
  }

  static LexicalUnitImpl createPX( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_PIXEL,
      null, v );
  }

  static LexicalUnitImpl createCM( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_CENTIMETER, null, v );
  }

  static LexicalUnitImpl createMM( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_MILLIMETER, null, v );
  }

  static LexicalUnitImpl createIN( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_INCH,
      null, v );
  }

  static LexicalUnitImpl createPT( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_POINT,
      null, v );
  }

  static LexicalUnitImpl createPC( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_PICA,
      null, v );
  }

  static LexicalUnitImpl createDEG( int line, int column,
                                    LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_DEGREE,
      null, v );
  }

  static LexicalUnitImpl createRAD( int line, int column,
                                    LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_RADIAN,
      null, v );
  }

  static LexicalUnitImpl createGRAD( int line, int column,
                                     LexicalUnitImpl previous, float v ) {
    return new LexicalUnitImpl( line, column, previous, SAC_GRADIAN,
      null, v );
  }

  static LexicalUnitImpl createMS( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    if ( v < 0 ) {
      throw new ParseException( "Time values may not be negative" );
    }
    return new LexicalUnitImpl( line, column, previous,
      SAC_MILLISECOND, null, v );
  }

  static LexicalUnitImpl createS( int line, int column,
                                  LexicalUnitImpl previous, float v ) {
    if ( v < 0 ) {
      throw new ParseException( "Time values may not be negative" );
    }
    return new LexicalUnitImpl( line, column, previous, SAC_SECOND,
      null, v );
  }

  static LexicalUnitImpl createHZ( int line, int column,
                                   LexicalUnitImpl previous, float v ) {
    if ( v < 0 ) {
      throw new ParseException( "Frequency values may not be negative" );
    }
    return new LexicalUnitImpl( line, column, previous, SAC_HERTZ,
      null, v );
  }

  static LexicalUnitImpl createKHZ( int line, int column,
                                    LexicalUnitImpl previous, float v ) {
    if ( v < 0 ) {
      throw new ParseException( "Frequency values may not be negative" );
    }
    return new LexicalUnitImpl( line, column, previous,
      SAC_KILOHERTZ, null, v );
  }

  static LexicalUnitImpl createDimen( int line, int column,
                                      LexicalUnitImpl previous,
                                      float v, String s ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_DIMENSION, s, v );
  }

  static LexicalUnitImpl createInherit( int line, int column,
                                        LexicalUnitImpl previous ) {
    return new LexicalUnitImpl( line, column, previous, SAC_INHERIT, "inherit" );
  }

  static LexicalUnitImpl createIdent( int line, int column,
                                      LexicalUnitImpl previous, String s ) {
    return new LexicalUnitImpl( line, column, previous, SAC_IDENT, s );
  }

  static LexicalUnitImpl createString( int line, int column,
                                       LexicalUnitImpl previous, String s ) {
    return new LexicalUnitImpl( line, column, previous,
      SAC_STRING_VALUE, s );
  }

  static LexicalUnitImpl createURL( int line, int column,
                                    LexicalUnitImpl previous, String s ) {
    return new LexicalUnitImpl( line, column, previous, SAC_URI, s );
  }

  static LexicalUnitImpl createAttr( int line, int column,
                                     LexicalUnitImpl previous, String s ) {
    return new LexicalUnitImpl( line, column, previous, SAC_ATTR, s );
  }

  static LexicalUnitImpl createCounter( int line, int column,
                                        LexicalUnitImpl previous,
                                        LexicalUnit params ) {
    return new LexicalUnitImpl( SAC_COUNTER_FUNCTION, line,
      column, previous, "counter",
      (LexicalUnitImpl) params );
  }

  static LexicalUnitImpl createCounters( int line, int column,
                                         LexicalUnitImpl previous,
                                         LexicalUnit params ) {
    return new LexicalUnitImpl( SAC_COUNTERS_FUNCTION, line,
      column, previous, "counters",
      (LexicalUnitImpl) params );
  }

  static LexicalUnitImpl createRGBColor( int line, int column,
                                         LexicalUnitImpl previous,
                                         LexicalUnit params ) {
    return new LexicalUnitImpl( SAC_RGBCOLOR, line, column,
      previous, "color",
      (LexicalUnitImpl) params );
  }

  static LexicalUnitImpl createRect( int line, int column,
                                     LexicalUnitImpl previous,
                                     LexicalUnit params ) {
    return new LexicalUnitImpl( SAC_RECT_FUNCTION, line, column,
      previous, "rect",
      (LexicalUnitImpl) params );
  }

  static LexicalUnitImpl createFunction( int line, int column,
                                         LexicalUnitImpl previous,
                                         String fname,
                                         LexicalUnit params ) {
    return new LexicalUnitImpl( SAC_FUNCTION, line, column, previous,
      fname,
      (LexicalUnitImpl) params );
  }

  static LexicalUnitImpl createUnicodeRange( int line, int column,
                                             LexicalUnit previous,
                                             LexicalUnit params ) {
    // @@ return new LexicalUnitImpl(line, column, previous, null, SAC_UNICODERANGE, params);
    return null;
  }

  static LexicalUnitImpl createComma( int line, int column,
                                      LexicalUnitImpl previous ) {
    return new LexicalUnitImpl( SAC_OPERATOR_COMMA, line, column, previous );
  }

  static LexicalUnitImpl createSlash( int line, int column,
                                      LexicalUnitImpl previous ) {
    return new LexicalUnitImpl( SAC_OPERATOR_SLASH, line, column, previous );
  }
}
