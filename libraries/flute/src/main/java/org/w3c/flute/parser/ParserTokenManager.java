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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.w3c.flute.parser;

public class ParserTokenManager implements ParserConstants {
  public java.io.PrintStream debugStream = System.out;

  public void setDebugStream( java.io.PrintStream ds ) {
    debugStream = ds;
  }

  private final int jjStopStringLiteralDfa_0( int pos, long active0, long active1 ) {
    switch( pos ) {
      case 0:
        if ( ( active0 & 0xf800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          return 48;
        }
        if ( ( active0 & 0x200000L ) != 0L ) {
          return 387;
        }
        if ( ( active0 & 0x2040L ) != 0L ) {
          return 388;
        }
        return -1;
      case 1:
        if ( ( active0 & 0xf800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 1;
          return 389;
        }
        if ( ( active0 & 0x40L ) != 0L ) {
          jjmatchedKind = 36;
          jjmatchedPos = 1;
          return 388;
        }
        return -1;
      case 2:
        if ( ( active0 & 0xf800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 2;
          return 389;
        }
        return -1;
      case 3:
        if ( ( active0 & 0xf800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 3;
          return 389;
        }
        return -1;
      case 4:
        if ( ( active0 & 0x4000000000000000L ) != 0L ) {
          return 389;
        }
        if ( ( active0 & 0xb800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 4;
          return 389;
        }
        return -1;
      case 5:
        if ( ( active0 & 0x1000000000000000L ) != 0L ) {
          return 389;
        }
        if ( ( active0 & 0xa800000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 5;
          return 389;
        }
        return -1;
      case 6:
        if ( ( active0 & 0x800000000000000L ) != 0L ) {
          return 389;
        }
        if ( ( active0 & 0xa000000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 6;
          return 389;
        }
        return -1;
      case 7:
        if ( ( active0 & 0x2000000000000000L ) != 0L ) {
          return 389;
        }
        if ( ( active0 & 0x8000000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 7;
          return 389;
        }
        return -1;
      case 8:
        if ( ( active0 & 0x8000000000000000L ) != 0L || ( active1 & 0x1L ) != 0L ) {
          jjmatchedKind = 65;
          jjmatchedPos = 8;
          return 389;
        }
        return -1;
      default:
        return -1;
    }
  }

  private final int jjStartNfa_0( int pos, long active0, long active1 ) {
    return jjMoveNfa_0( jjStopStringLiteralDfa_0( pos, active0, active1 ), pos + 1 );
  }

  private final int jjStopAtPos( int pos, int kind ) {
    jjmatchedKind = kind;
    jjmatchedPos = pos;
    return pos + 1;
  }

  private final int jjStartNfaWithStates_0( int pos, int kind, int state ) {
    jjmatchedKind = kind;
    jjmatchedPos = pos;
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      return pos + 1;
    }
    return jjMoveNfa_0( state, pos + 1 );
  }

  private final int jjMoveStringLiteralDfa0_0() {
    switch( curChar ) {
      case 40:
        return jjStopAtPos( 0, 23 );
      case 41:
        return jjStopAtPos( 0, 22 );
      case 42:
        return jjStopAtPos( 0, 20 );
      case 43:
        return jjStopAtPos( 0, 12 );
      case 44:
        return jjStopAtPos( 0, 14 );
      case 45:
        jjmatchedKind = 13;
        return jjMoveStringLiteralDfa1_0( 0x40L, 0x0L );
      case 46:
        return jjStartNfaWithStates_0( 0, 21, 387 );
      case 47:
        jjmatchedKind = 17;
        return jjMoveStringLiteralDfa1_0( 0x4L, 0x0L );
      case 58:
        return jjStopAtPos( 0, 24 );
      case 59:
        return jjStopAtPos( 0, 15 );
      case 60:
        return jjMoveStringLiteralDfa1_0( 0x20L, 0x0L );
      case 61:
        return jjStopAtPos( 0, 11 );
      case 62:
        return jjStopAtPos( 0, 16 );
      case 64:
        return jjMoveStringLiteralDfa1_0( 0xf800000000000000L, 0x1L );
      case 91:
        return jjStopAtPos( 0, 18 );
      case 93:
        return jjStopAtPos( 0, 19 );
      case 123:
        return jjStopAtPos( 0, 7 );
      case 124:
        return jjMoveStringLiteralDfa1_0( 0x200L, 0x0L );
      case 125:
        return jjStopAtPos( 0, 8 );
      case 126:
        return jjMoveStringLiteralDfa1_0( 0x400L, 0x0L );
      default:
        return jjMoveNfa_0( 1, 0 );
    }
  }

  private final int jjMoveStringLiteralDfa1_0( long active0, long active1 ) {
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 0, active0, active1 );
      return 1;
    }
    switch( curChar ) {
      case 33:
        return jjMoveStringLiteralDfa2_0( active0, 0x20L, active1, 0L );
      case 42:
        if ( ( active0 & 0x4L ) != 0L ) {
          return jjStopAtPos( 1, 2 );
        }
        break;
      case 45:
        return jjMoveStringLiteralDfa2_0( active0, 0x40L, active1, 0L );
      case 61:
        if ( ( active0 & 0x200L ) != 0L ) {
          return jjStopAtPos( 1, 9 );
        } else if ( ( active0 & 0x400L ) != 0L ) {
          return jjStopAtPos( 1, 10 );
        }
        break;
      case 67:
      case 99:
        return jjMoveStringLiteralDfa2_0( active0, 0x2000000000000000L, active1, 0L );
      case 70:
      case 102:
        return jjMoveStringLiteralDfa2_0( active0, 0L, active1, 0x1L );
      case 73:
      case 105:
        return jjMoveStringLiteralDfa2_0( active0, 0x800000000000000L, active1, 0L );
      case 77:
      case 109:
        return jjMoveStringLiteralDfa2_0( active0, 0x1000000000000000L, active1, 0L );
      case 78:
      case 110:
        return jjMoveStringLiteralDfa2_0( active0, 0x8000000000000000L, active1, 0L );
      case 80:
      case 112:
        return jjMoveStringLiteralDfa2_0( active0, 0x4000000000000000L, active1, 0L );
      default:
        break;
    }
    return jjStartNfa_0( 0, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa2_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 0, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 1, active0, active1 );
      return 2;
    }
    switch( curChar ) {
      case 45:
        return jjMoveStringLiteralDfa3_0( active0, 0x20L, active1, 0L );
      case 62:
        if ( ( active0 & 0x40L ) != 0L ) {
          return jjStopAtPos( 2, 6 );
        }
        break;
      case 65:
      case 97:
        return jjMoveStringLiteralDfa3_0( active0, 0xc000000000000000L, active1, 0L );
      case 69:
      case 101:
        return jjMoveStringLiteralDfa3_0( active0, 0x1000000000000000L, active1, 0L );
      case 72:
      case 104:
        return jjMoveStringLiteralDfa3_0( active0, 0x2000000000000000L, active1, 0L );
      case 77:
      case 109:
        return jjMoveStringLiteralDfa3_0( active0, 0x800000000000000L, active1, 0L );
      case 79:
      case 111:
        return jjMoveStringLiteralDfa3_0( active0, 0L, active1, 0x1L );
      default:
        break;
    }
    return jjStartNfa_0( 1, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa3_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 1, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 2, active0, active1 );
      return 3;
    }
    switch( curChar ) {
      case 45:
        if ( ( active0 & 0x20L ) != 0L ) {
          return jjStopAtPos( 3, 5 );
        }
        break;
      case 65:
      case 97:
        return jjMoveStringLiteralDfa4_0( active0, 0x2000000000000000L, active1, 0L );
      case 68:
      case 100:
        return jjMoveStringLiteralDfa4_0( active0, 0x1000000000000000L, active1, 0L );
      case 71:
      case 103:
        return jjMoveStringLiteralDfa4_0( active0, 0x4000000000000000L, active1, 0L );
      case 77:
      case 109:
        return jjMoveStringLiteralDfa4_0( active0, 0x8000000000000000L, active1, 0L );
      case 78:
      case 110:
        return jjMoveStringLiteralDfa4_0( active0, 0L, active1, 0x1L );
      case 80:
      case 112:
        return jjMoveStringLiteralDfa4_0( active0, 0x800000000000000L, active1, 0L );
      default:
        break;
    }
    return jjStartNfa_0( 2, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa4_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 2, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 3, active0, active1 );
      return 4;
    }
    switch( curChar ) {
      case 69:
      case 101:
        if ( ( active0 & 0x4000000000000000L ) != 0L ) {
          return jjStartNfaWithStates_0( 4, 62, 389 );
        }
        return jjMoveStringLiteralDfa5_0( active0, 0x8000000000000000L, active1, 0L );
      case 73:
      case 105:
        return jjMoveStringLiteralDfa5_0( active0, 0x1000000000000000L, active1, 0L );
      case 79:
      case 111:
        return jjMoveStringLiteralDfa5_0( active0, 0x800000000000000L, active1, 0L );
      case 82:
      case 114:
        return jjMoveStringLiteralDfa5_0( active0, 0x2000000000000000L, active1, 0L );
      case 84:
      case 116:
        return jjMoveStringLiteralDfa5_0( active0, 0L, active1, 0x1L );
      default:
        break;
    }
    return jjStartNfa_0( 3, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa5_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 3, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 4, active0, active1 );
      return 5;
    }
    switch( curChar ) {
      case 45:
        return jjMoveStringLiteralDfa6_0( active0, 0L, active1, 0x1L );
      case 65:
      case 97:
        if ( ( active0 & 0x1000000000000000L ) != 0L ) {
          return jjStartNfaWithStates_0( 5, 60, 389 );
        }
        break;
      case 82:
      case 114:
        return jjMoveStringLiteralDfa6_0( active0, 0x800000000000000L, active1, 0L );
      case 83:
      case 115:
        return jjMoveStringLiteralDfa6_0( active0, 0xa000000000000000L, active1, 0L );
      default:
        break;
    }
    return jjStartNfa_0( 4, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa6_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 4, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 5, active0, active1 );
      return 6;
    }
    switch( curChar ) {
      case 69:
      case 101:
        return jjMoveStringLiteralDfa7_0( active0, 0x2000000000000000L, active1, 0L );
      case 70:
      case 102:
        return jjMoveStringLiteralDfa7_0( active0, 0L, active1, 0x1L );
      case 80:
      case 112:
        return jjMoveStringLiteralDfa7_0( active0, 0x8000000000000000L, active1, 0L );
      case 84:
      case 116:
        if ( ( active0 & 0x800000000000000L ) != 0L ) {
          return jjStartNfaWithStates_0( 6, 59, 389 );
        }
        break;
      default:
        break;
    }
    return jjStartNfa_0( 5, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa7_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 5, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 6, active0, active1 );
      return 7;
    }
    switch( curChar ) {
      case 65:
      case 97:
        return jjMoveStringLiteralDfa8_0( active0, 0x8000000000000000L, active1, 0x1L );
      case 84:
      case 116:
        if ( ( active0 & 0x2000000000000000L ) != 0L ) {
          return jjStartNfaWithStates_0( 7, 61, 389 );
        }
        break;
      default:
        break;
    }
    return jjStartNfa_0( 6, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa8_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 6, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 7, active0, active1 );
      return 8;
    }
    switch( curChar ) {
      case 67:
      case 99:
        return jjMoveStringLiteralDfa9_0( active0, 0x8000000000000000L, active1, 0x1L );
      default:
        break;
    }
    return jjStartNfa_0( 7, active0, active1 );
  }

  private final int jjMoveStringLiteralDfa9_0( long old0, long active0, long old1, long active1 ) {
    if ( ( ( active0 &= old0 ) | ( active1 &= old1 ) ) == 0L ) {
      return jjStartNfa_0( 7, old0, old1 );
    }
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      jjStopStringLiteralDfa_0( 8, active0, active1 );
      return 9;
    }
    switch( curChar ) {
      case 69:
      case 101:
        if ( ( active0 & 0x8000000000000000L ) != 0L ) {
          return jjStartNfaWithStates_0( 9, 63, 389 );
        } else if ( ( active1 & 0x1L ) != 0L ) {
          return jjStartNfaWithStates_0( 9, 64, 389 );
        }
        break;
      default:
        break;
    }
    return jjStartNfa_0( 8, active0, active1 );
  }

  private final void jjCheckNAdd( int state ) {
    if ( jjrounds[ state ] != jjround ) {
      jjstateSet[ jjnewStateCnt++ ] = state;
      jjrounds[ state ] = jjround;
    }
  }

  private final void jjAddStates( int start, int end ) {
    do {
      jjstateSet[ jjnewStateCnt++ ] = jjnextStates[ start ];
    } while ( start++ != end );
  }

  private final void jjCheckNAddTwoStates( int state1, int state2 ) {
    jjCheckNAdd( state1 );
    jjCheckNAdd( state2 );
  }

  private final void jjCheckNAddStates( int start, int end ) {
    do {
      jjCheckNAdd( jjnextStates[ start ] );
    } while ( start++ != end );
  }

  private final void jjCheckNAddStates( int start ) {
    jjCheckNAdd( jjnextStates[ start ] );
    jjCheckNAdd( jjnextStates[ start + 1 ] );
  }

  static final long[] jjbitVec0 = {
    0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
  };

  private final int jjMoveNfa_0( int startState, int curPos ) {
    int[] nextStates;
    int startsAt = 0;
    jjnewStateCnt = 387;
    int i = 1;
    jjstateSet[ 0 ] = startState;
    int j, kind = 0x7fffffff;
    for (; ; ) {
      if ( ++jjround == 0x7fffffff ) {
        ReInitRounds();
      }
      if ( curChar < 64 ) {
        long l = 1L << curChar;
        MatchLoop:
        do {
          switch( jjstateSet[ --i ] ) {
            case 388:
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              } else if ( curChar == 40 ) {
                if ( kind > 77 ) {
                  kind = 77;
                }
              }
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddTwoStates( 78, 79 );
              }
              break;
            case 1:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                if ( kind > 37 ) {
                  kind = 37;
                }
                jjCheckNAddStates( 9, 80 );
              } else if ( ( 0x100003600L & l ) != 0L ) {
                if ( kind > 1 ) {
                  kind = 1;
                }
                jjCheckNAdd( 0 );
              } else if ( curChar == 46 ) {
                jjCheckNAddStates( 81, 98 );
              } else if ( curChar == 45 ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddStates( 99, 109 );
              } else if ( curChar == 33 ) {
                jjCheckNAddTwoStates( 67, 76 );
              } else if ( curChar == 35 ) {
                jjCheckNAddTwoStates( 37, 38 );
              } else if ( curChar == 39 ) {
                jjCheckNAddStates( 110, 113 );
              } else if ( curChar == 34 ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 387:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 118, 120 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 188, 191 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 185, 187 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 183, 184 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 180, 182 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 175, 179 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 171, 174 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 167, 170 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 164, 166 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 161, 163 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 158, 160 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 155, 157 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 152, 154 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 149, 151 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 146, 148 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 143, 145 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 141, 142 );
              }
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                if ( kind > 37 ) {
                  kind = 37;
                }
                jjCheckNAdd( 140 );
              }
              break;
            case 389:
            case 49:
              if ( ( 0x3ff200000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 48:
              if ( curChar != 45 ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 0:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 1 ) {
                kind = 1;
              }
              jjCheckNAdd( 0 );
              break;
            case 2:
              if ( curChar == 34 ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 3:
              if ( ( 0xfffffffb00000200L & l ) != 0L ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 4:
              if ( curChar == 34 && kind > 35 ) {
                kind = 35;
              }
              break;
            case 6:
              if ( curChar == 12 ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 8:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 9:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 121, 126 );
              }
              break;
            case 10:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 11:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 127, 135 );
              }
              break;
            case 12:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 136, 140 );
              }
              break;
            case 13:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 141, 146 );
              }
              break;
            case 14:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 147, 153 );
              }
              break;
            case 15:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 154, 161 );
              }
              break;
            case 16:
              if ( curChar == 13 ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 17:
              if ( curChar == 10 ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 18:
              if ( curChar == 13 ) {
                jjstateSet[ jjnewStateCnt++ ] = 17;
              }
              break;
            case 19:
              if ( curChar == 39 ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 20:
              if ( ( 0xffffff7f00000200L & l ) != 0L ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 21:
              if ( curChar == 39 && kind > 35 ) {
                kind = 35;
              }
              break;
            case 23:
              if ( curChar == 12 ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 25:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 26:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 162, 167 );
              }
              break;
            case 27:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 28:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 168, 176 );
              }
              break;
            case 29:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 177, 181 );
              }
              break;
            case 30:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 182, 187 );
              }
              break;
            case 31:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 188, 194 );
              }
              break;
            case 32:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 195, 202 );
              }
              break;
            case 33:
              if ( curChar == 13 ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 34:
              if ( curChar == 10 ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 35:
              if ( curChar == 13 ) {
                jjstateSet[ jjnewStateCnt++ ] = 34;
              }
              break;
            case 36:
              if ( curChar == 35 ) {
                jjCheckNAddTwoStates( 37, 38 );
              }
              break;
            case 37:
              if ( ( 0x3ff200000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 39:
              if ( ( 0xffffffff00000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 40:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 203, 206 );
              break;
            case 41:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 42:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 207, 213 );
              break;
            case 43:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 214, 216 );
              break;
            case 44:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 217, 220 );
              break;
            case 45:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 221, 225 );
              break;
            case 46:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 226, 231 );
              break;
            case 51:
              if ( ( 0xffffffff00000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 52:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 232, 235 );
              break;
            case 53:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 54:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 236, 242 );
              break;
            case 55:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 243, 245 );
              break;
            case 56:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 246, 249 );
              break;
            case 57:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 250, 254 );
              break;
            case 58:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 255, 260 );
              break;
            case 60:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 261, 264 );
              break;
            case 61:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 265, 271 );
              break;
            case 62:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 272, 274 );
              break;
            case 63:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 275, 278 );
              break;
            case 64:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 279, 283 );
              break;
            case 65:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 284, 289 );
              break;
            case 66:
              if ( curChar == 33 ) {
                jjCheckNAddTwoStates( 67, 76 );
              }
              break;
            case 67:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddTwoStates( 67, 76 );
              }
              break;
            case 77:
              if ( curChar != 45 ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 99, 109 );
              break;
            case 78:
              if ( ( 0x3ff200000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 80:
              if ( ( 0xffffffff00000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 81:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 290, 293 );
              break;
            case 82:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 83:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 294, 300 );
              break;
            case 84:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 301, 303 );
              break;
            case 85:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 304, 307 );
              break;
            case 86:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 308, 312 );
              break;
            case 87:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 313, 318 );
              break;
            case 88:
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 91:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 92:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 319, 323 );
              }
              break;
            case 93:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 94:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 324, 331 );
              }
              break;
            case 95:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 332, 335 );
              }
              break;
            case 96:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 336, 340 );
              }
              break;
            case 97:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 341, 346 );
              }
              break;
            case 98:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 347, 353 );
              }
              break;
            case 99:
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 101:
              if ( curChar != 45 ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 102:
              if ( ( 0x3ff200000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 104:
              if ( ( 0xffffffff00000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 105:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 354, 357 );
              break;
            case 106:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 107:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 358, 364 );
              break;
            case 108:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 365, 367 );
              break;
            case 109:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 368, 371 );
              break;
            case 110:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 372, 376 );
              break;
            case 111:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 377, 382 );
              break;
            case 113:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 383, 386 );
              break;
            case 114:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 387, 393 );
              break;
            case 115:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 394, 396 );
              break;
            case 116:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 397, 400 );
              break;
            case 117:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 401, 405 );
              break;
            case 118:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 406, 411 );
              break;
            case 120:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 121:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 412, 416 );
              }
              break;
            case 122:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 123:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 417, 424 );
              }
              break;
            case 124:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 425, 428 );
              }
              break;
            case 125:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 429, 433 );
              }
              break;
            case 126:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 434, 439 );
              }
              break;
            case 127:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 440, 446 );
              }
              break;
            case 128:
              if ( ( 0x3ff200000000000L & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 129:
              if ( curChar == 40 && kind > 77 ) {
                kind = 77;
              }
              break;
            case 131:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 132:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 447, 451 );
              }
              break;
            case 133:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 134:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 452, 459 );
              }
              break;
            case 135:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 460, 463 );
              }
              break;
            case 136:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 464, 468 );
              }
              break;
            case 137:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 469, 474 );
              }
              break;
            case 138:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 475, 481 );
              }
              break;
            case 139:
              if ( curChar == 46 ) {
                jjCheckNAddStates( 81, 98 );
              }
              break;
            case 140:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 37 ) {
                kind = 37;
              }
              jjCheckNAdd( 140 );
              break;
            case 141:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 141, 142 );
              }
              break;
            case 142:
              if ( curChar == 37 && kind > 41 ) {
                kind = 41;
              }
              break;
            case 143:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 143, 145 );
              }
              break;
            case 146:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 146, 148 );
              }
              break;
            case 149:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 149, 151 );
              }
              break;
            case 152:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 152, 154 );
              }
              break;
            case 155:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 155, 157 );
              }
              break;
            case 158:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 158, 160 );
              }
              break;
            case 161:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 161, 163 );
              }
              break;
            case 164:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 164, 166 );
              }
              break;
            case 167:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 167, 170 );
              }
              break;
            case 171:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 171, 174 );
              }
              break;
            case 175:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 175, 179 );
              }
              break;
            case 180:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 180, 182 );
              }
              break;
            case 183:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 183, 184 );
              }
              break;
            case 185:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 185, 187 );
              }
              break;
            case 188:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 188, 191 );
              }
              break;
            case 192:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 118, 120 );
              }
              break;
            case 193:
              if ( curChar != 45 ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 194:
              if ( ( 0x3ff200000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 196:
              if ( ( 0xffffffff00000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 197:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 482, 485 );
              break;
            case 198:
              if ( ( 0x100003600L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 199:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 486, 492 );
              break;
            case 200:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 493, 495 );
              break;
            case 201:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 496, 499 );
              break;
            case 202:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 500, 504 );
              break;
            case 203:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 505, 510 );
              break;
            case 205:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 511, 514 );
              break;
            case 206:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 515, 521 );
              break;
            case 207:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 522, 524 );
              break;
            case 208:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 525, 528 );
              break;
            case 209:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 529, 533 );
              break;
            case 210:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 534, 539 );
              break;
            case 212:
              if ( curChar == 40 ) {
                jjCheckNAddStates( 540, 545 );
              }
              break;
            case 213:
              if ( ( 0xfffffc7a00000000L & l ) != 0L ) {
                jjCheckNAddStates( 546, 549 );
              }
              break;
            case 214:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddTwoStates( 214, 215 );
              }
              break;
            case 215:
              if ( curChar == 41 && kind > 39 ) {
                kind = 39;
              }
              break;
            case 217:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 546, 549 );
              }
              break;
            case 218:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 550, 554 );
              }
              break;
            case 219:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 546, 549 );
              }
              break;
            case 220:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 555, 562 );
              }
              break;
            case 221:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 563, 566 );
              }
              break;
            case 222:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 567, 571 );
              }
              break;
            case 223:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 572, 577 );
              }
              break;
            case 224:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 578, 584 );
              }
              break;
            case 225:
              if ( curChar == 39 ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 226:
              if ( ( 0xffffff7f00000200L & l ) != 0L ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 227:
              if ( curChar == 39 ) {
                jjCheckNAddTwoStates( 214, 215 );
              }
              break;
            case 229:
              if ( curChar == 12 ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 231:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 232:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 589, 594 );
              }
              break;
            case 233:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 234:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 595, 603 );
              }
              break;
            case 235:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 604, 608 );
              }
              break;
            case 236:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 609, 614 );
              }
              break;
            case 237:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 615, 621 );
              }
              break;
            case 238:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 622, 629 );
              }
              break;
            case 239:
              if ( curChar == 13 ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 240:
              if ( curChar == 10 ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 241:
              if ( curChar == 13 ) {
                jjstateSet[ jjnewStateCnt++ ] = 240;
              }
              break;
            case 242:
              if ( curChar == 34 ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 243:
              if ( ( 0xfffffffb00000200L & l ) != 0L ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 244:
              if ( curChar == 34 ) {
                jjCheckNAddTwoStates( 214, 215 );
              }
              break;
            case 246:
              if ( curChar == 12 ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 248:
              if ( ( 0xffffffff00000000L & l ) != 0L ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 249:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 634, 639 );
              }
              break;
            case 250:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 251:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 640, 648 );
              }
              break;
            case 252:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 649, 653 );
              }
              break;
            case 253:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 654, 659 );
              }
              break;
            case 254:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 660, 666 );
              }
              break;
            case 255:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 667, 674 );
              }
              break;
            case 256:
              if ( curChar == 13 ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 257:
              if ( curChar == 10 ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 258:
              if ( curChar == 13 ) {
                jjstateSet[ jjnewStateCnt++ ] = 257;
              }
              break;
            case 259:
              if ( ( 0x100003600L & l ) != 0L ) {
                jjCheckNAddStates( 675, 681 );
              }
              break;
            case 262:
              if ( curChar == 43 ) {
                jjAddStates( 682, 683 );
              }
              break;
            case 263:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 264;
              break;
            case 264:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 684, 687 );
              break;
            case 265:
              if ( curChar == 63 && kind > 76 ) {
                kind = 76;
              }
              break;
            case 266:
            case 281:
            case 285:
            case 288:
            case 291:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAdd( 265 );
              break;
            case 267:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 265, 266 );
              break;
            case 268:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 688, 690 );
              break;
            case 269:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjAddStates( 691, 696 );
              break;
            case 270:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 271;
              }
              break;
            case 271:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 272;
              }
              break;
            case 272:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAdd( 273 );
              }
              break;
            case 273:
              if ( ( 0x3ff000000000000L & l ) != 0L && kind > 76 ) {
                kind = 76;
              }
              break;
            case 274:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 275;
              }
              break;
            case 275:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 276;
              }
              break;
            case 276:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 277;
              }
              break;
            case 277:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAdd( 265 );
              break;
            case 278:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 279;
              }
              break;
            case 279:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 280;
              }
              break;
            case 280:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 281;
              break;
            case 282:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 283;
              }
              break;
            case 283:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 284;
              break;
            case 284:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 265, 285 );
              break;
            case 286:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 287;
              break;
            case 287:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 697, 699 );
              break;
            case 289:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 265, 288 );
              break;
            case 290:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 700, 703 );
              break;
            case 292:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 265, 291 );
              break;
            case 293:
              if ( curChar != 63 ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 704, 706 );
              break;
            case 294:
              if ( curChar == 43 ) {
                jjstateSet[ jjnewStateCnt++ ] = 295;
              }
              break;
            case 295:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 296, 302 );
              }
              break;
            case 296:
              if ( curChar == 45 ) {
                jjstateSet[ jjnewStateCnt++ ] = 297;
              }
              break;
            case 297:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 298;
              break;
            case 298:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 707, 710 );
              break;
            case 299:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAdd( 273 );
              break;
            case 300:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 273, 299 );
              break;
            case 301:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 711, 713 );
              break;
            case 302:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 714, 718 );
              }
              break;
            case 303:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAdd( 296 );
              }
              break;
            case 304:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 303, 296 );
              }
              break;
            case 305:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 719, 721 );
              }
              break;
            case 306:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 722, 725 );
              }
              break;
            case 308:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 726, 729 );
              break;
            case 309:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 730, 736 );
              break;
            case 310:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 737, 739 );
              break;
            case 311:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 740, 743 );
              break;
            case 312:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 744, 748 );
              break;
            case 313:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 749, 754 );
              break;
            case 314:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 755, 759 );
              }
              break;
            case 315:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 760, 767 );
              }
              break;
            case 316:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 768, 771 );
              }
              break;
            case 317:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 772, 776 );
              }
              break;
            case 318:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 777, 782 );
              }
              break;
            case 319:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 783, 789 );
              }
              break;
            case 320:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 790, 794 );
              }
              break;
            case 321:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 795, 802 );
              }
              break;
            case 322:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 803, 806 );
              }
              break;
            case 323:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 807, 811 );
              }
              break;
            case 324:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 812, 817 );
              }
              break;
            case 325:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 818, 824 );
              }
              break;
            case 326:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 825, 829 );
              }
              break;
            case 327:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 830, 837 );
              }
              break;
            case 328:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 838, 841 );
              }
              break;
            case 329:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 842, 846 );
              }
              break;
            case 330:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 847, 852 );
              }
              break;
            case 331:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 853, 859 );
              }
              break;
            case 332:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 37 ) {
                kind = 37;
              }
              jjCheckNAddStates( 9, 80 );
              break;
            case 333:
              if ( ( 0x3ff000000000000L & l ) == 0L ) {
                break;
              }
              if ( kind > 37 ) {
                kind = 37;
              }
              jjCheckNAdd( 333 );
              break;
            case 334:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 334, 335 );
              }
              break;
            case 335:
              if ( curChar == 46 ) {
                jjCheckNAdd( 140 );
              }
              break;
            case 336:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 336, 142 );
              }
              break;
            case 337:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 337, 338 );
              }
              break;
            case 338:
              if ( curChar == 46 ) {
                jjCheckNAdd( 141 );
              }
              break;
            case 339:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 339, 145 );
              }
              break;
            case 340:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 340, 341 );
              }
              break;
            case 341:
              if ( curChar == 46 ) {
                jjCheckNAdd( 143 );
              }
              break;
            case 342:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 342, 148 );
              }
              break;
            case 343:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 343, 344 );
              }
              break;
            case 344:
              if ( curChar == 46 ) {
                jjCheckNAdd( 146 );
              }
              break;
            case 345:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 345, 151 );
              }
              break;
            case 346:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 346, 347 );
              }
              break;
            case 347:
              if ( curChar == 46 ) {
                jjCheckNAdd( 149 );
              }
              break;
            case 348:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 348, 154 );
              }
              break;
            case 349:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 349, 350 );
              }
              break;
            case 350:
              if ( curChar == 46 ) {
                jjCheckNAdd( 152 );
              }
              break;
            case 351:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 351, 157 );
              }
              break;
            case 352:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 352, 353 );
              }
              break;
            case 353:
              if ( curChar == 46 ) {
                jjCheckNAdd( 155 );
              }
              break;
            case 354:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 354, 160 );
              }
              break;
            case 355:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 355, 356 );
              }
              break;
            case 356:
              if ( curChar == 46 ) {
                jjCheckNAdd( 158 );
              }
              break;
            case 357:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 357, 163 );
              }
              break;
            case 358:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 358, 359 );
              }
              break;
            case 359:
              if ( curChar == 46 ) {
                jjCheckNAdd( 161 );
              }
              break;
            case 360:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 360, 166 );
              }
              break;
            case 361:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 361, 362 );
              }
              break;
            case 362:
              if ( curChar == 46 ) {
                jjCheckNAdd( 164 );
              }
              break;
            case 363:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 363, 170 );
              }
              break;
            case 364:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 364, 365 );
              }
              break;
            case 365:
              if ( curChar == 46 ) {
                jjCheckNAdd( 167 );
              }
              break;
            case 366:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 366, 174 );
              }
              break;
            case 367:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 367, 368 );
              }
              break;
            case 368:
              if ( curChar == 46 ) {
                jjCheckNAdd( 171 );
              }
              break;
            case 369:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 369, 179 );
              }
              break;
            case 370:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 370, 371 );
              }
              break;
            case 371:
              if ( curChar == 46 ) {
                jjCheckNAdd( 175 );
              }
              break;
            case 372:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 372, 182 );
              }
              break;
            case 373:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 373, 374 );
              }
              break;
            case 374:
              if ( curChar == 46 ) {
                jjCheckNAdd( 180 );
              }
              break;
            case 375:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 375, 184 );
              }
              break;
            case 376:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 376, 377 );
              }
              break;
            case 377:
              if ( curChar == 46 ) {
                jjCheckNAdd( 183 );
              }
              break;
            case 378:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 378, 187 );
              }
              break;
            case 379:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 379, 380 );
              }
              break;
            case 380:
              if ( curChar == 46 ) {
                jjCheckNAdd( 185 );
              }
              break;
            case 381:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 381, 191 );
              }
              break;
            case 382:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 382, 383 );
              }
              break;
            case 383:
              if ( curChar == 46 ) {
                jjCheckNAdd( 188 );
              }
              break;
            case 384:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddStates( 860, 862 );
              }
              break;
            case 385:
              if ( ( 0x3ff000000000000L & l ) != 0L ) {
                jjCheckNAddTwoStates( 385, 386 );
              }
              break;
            case 386:
              if ( curChar == 46 ) {
                jjCheckNAdd( 192 );
              }
              break;
            default:
              break;
          }
        } while ( i != startsAt );
      } else if ( curChar < 128 ) {
        long l = 1L << ( curChar & 077 );
        MatchLoop:
        do {
          switch( jjstateSet[ --i ] ) {
            case 388:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 80, 81 );
              } else if ( curChar == 124 ) {
                jjAddStates( 863, 864 );
              }
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 91, 92 );
              } else if ( curChar == 124 ) {
                if ( kind > 40 ) {
                  kind = 40;
                }
              }
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 120, 121 );
              }
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddTwoStates( 78, 79 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 131, 132 );
              }
              break;
            case 1:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddStates( 99, 109 );
              } else if ( curChar == 92 ) {
                jjCheckNAddStates( 865, 872 );
              } else if ( curChar == 64 ) {
                jjAddStates( 873, 874 );
              }
              if ( ( 0x20000000200000L & l ) != 0L ) {
                jjAddStates( 875, 877 );
              }
              break;
            case 389:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                if ( kind > 65 ) {
                  kind = 65;
                }
                jjCheckNAddTwoStates( 49, 50 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 51, 52 );
              }
              break;
            case 48:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                if ( kind > 65 ) {
                  kind = 65;
                }
                jjCheckNAddTwoStates( 49, 50 );
              } else if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 51, 60 );
              }
              break;
            case 3:
            case 8:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 5:
              if ( curChar == 92 ) {
                jjAddStates( 878, 881 );
              }
              break;
            case 7:
              if ( curChar == 92 ) {
                jjAddStates( 882, 883 );
              }
              break;
            case 9:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 121, 126 );
              }
              break;
            case 11:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 127, 135 );
              }
              break;
            case 12:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 136, 140 );
              }
              break;
            case 13:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 141, 146 );
              }
              break;
            case 14:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 147, 153 );
              }
              break;
            case 15:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 154, 161 );
              }
              break;
            case 20:
            case 25:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 22:
              if ( curChar == 92 ) {
                jjAddStates( 884, 887 );
              }
              break;
            case 24:
              if ( curChar == 92 ) {
                jjAddStates( 888, 889 );
              }
              break;
            case 26:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 162, 167 );
              }
              break;
            case 28:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 168, 176 );
              }
              break;
            case 29:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 177, 181 );
              }
              break;
            case 30:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 182, 187 );
              }
              break;
            case 31:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 188, 194 );
              }
              break;
            case 32:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 195, 202 );
              }
              break;
            case 37:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 38:
              if ( curChar == 92 ) {
                jjAddStates( 890, 891 );
              }
              break;
            case 39:
              if ( ( 0x7fffffffffffffffL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 40:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 203, 206 );
              break;
            case 42:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 207, 213 );
              break;
            case 43:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 214, 216 );
              break;
            case 44:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 217, 220 );
              break;
            case 45:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 221, 225 );
              break;
            case 46:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddStates( 226, 231 );
              break;
            case 47:
              if ( curChar == 64 ) {
                jjAddStates( 873, 874 );
              }
              break;
            case 49:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 50:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 51, 52 );
              }
              break;
            case 51:
              if ( ( 0x7fffffffffffffffL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 52:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 232, 235 );
              break;
            case 54:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 236, 242 );
              break;
            case 55:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 243, 245 );
              break;
            case 56:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 246, 249 );
              break;
            case 57:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 250, 254 );
              break;
            case 58:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 255, 260 );
              break;
            case 59:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 51, 60 );
              }
              break;
            case 60:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 261, 264 );
              break;
            case 61:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 265, 271 );
              break;
            case 62:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 272, 274 );
              break;
            case 63:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 275, 278 );
              break;
            case 64:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 279, 283 );
              break;
            case 65:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddStates( 284, 289 );
              break;
            case 68:
              if ( ( 0x10000000100000L & l ) != 0L && kind > 66 ) {
                kind = 66;
              }
              break;
            case 69:
              if ( ( 0x400000004000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 68;
              }
              break;
            case 70:
              if ( ( 0x200000002L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 69;
              }
              break;
            case 71:
              if ( ( 0x10000000100000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 70;
              }
              break;
            case 72:
              if ( ( 0x4000000040000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 71;
              }
              break;
            case 73:
              if ( ( 0x800000008000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 72;
              }
              break;
            case 74:
              if ( ( 0x1000000010000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 73;
              }
              break;
            case 75:
              if ( ( 0x200000002000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 74;
              }
              break;
            case 76:
              if ( ( 0x20000000200L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 75;
              }
              break;
            case 77:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 99, 109 );
              break;
            case 78:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 79:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 80, 81 );
              }
              break;
            case 80:
              if ( ( 0x7fffffffffffffffL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 81:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 290, 293 );
              break;
            case 83:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 294, 300 );
              break;
            case 84:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 301, 303 );
              break;
            case 85:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 304, 307 );
              break;
            case 86:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 308, 312 );
              break;
            case 87:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 313, 318 );
              break;
            case 88:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 89:
              if ( curChar == 124 && kind > 40 ) {
                kind = 40;
              }
              break;
            case 90:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 91, 92 );
              }
              break;
            case 91:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 92:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 319, 323 );
              }
              break;
            case 94:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 324, 331 );
              }
              break;
            case 95:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 332, 335 );
              }
              break;
            case 96:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 336, 340 );
              }
              break;
            case 97:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 341, 346 );
              }
              break;
            case 98:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 347, 353 );
              }
              break;
            case 99:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 100:
              if ( curChar == 124 ) {
                jjAddStates( 863, 864 );
              }
              break;
            case 101:
            case 102:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 103:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 104, 105 );
              }
              break;
            case 104:
              if ( ( 0x7fffffffffffffffL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 105:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 354, 357 );
              break;
            case 107:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 358, 364 );
              break;
            case 108:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 365, 367 );
              break;
            case 109:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 368, 371 );
              break;
            case 110:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 372, 376 );
              break;
            case 111:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 377, 382 );
              break;
            case 112:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 104, 113 );
              }
              break;
            case 113:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 383, 386 );
              break;
            case 114:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 387, 393 );
              break;
            case 115:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 394, 396 );
              break;
            case 116:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 397, 400 );
              break;
            case 117:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 401, 405 );
              break;
            case 118:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddStates( 406, 411 );
              break;
            case 119:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 120, 121 );
              }
              break;
            case 120:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 121:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 412, 416 );
              }
              break;
            case 123:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 417, 424 );
              }
              break;
            case 124:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 425, 428 );
              }
              break;
            case 125:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 429, 433 );
              }
              break;
            case 126:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 434, 439 );
              }
              break;
            case 127:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 440, 446 );
              }
              break;
            case 128:
              if ( ( 0x7fffffe87fffffeL & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 130:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 131, 132 );
              }
              break;
            case 131:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 132:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 447, 451 );
              }
              break;
            case 134:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 452, 459 );
              }
              break;
            case 135:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 460, 463 );
              }
              break;
            case 136:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 464, 468 );
              }
              break;
            case 137:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 469, 474 );
              }
              break;
            case 138:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 475, 481 );
              }
              break;
            case 144:
              if ( ( 0x10000000100000L & l ) != 0L && kind > 42 ) {
                kind = 42;
              }
              break;
            case 145:
              if ( ( 0x1000000010000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 144;
              }
              break;
            case 147:
              if ( ( 0x200000002000L & l ) != 0L && kind > 43 ) {
                kind = 43;
              }
              break;
            case 148:
              if ( ( 0x200000002000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 147;
              }
              break;
            case 150:
              if ( ( 0x200000002000L & l ) != 0L && kind > 44 ) {
                kind = 44;
              }
              break;
            case 151:
              if ( ( 0x800000008L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 150;
              }
              break;
            case 153:
              if ( ( 0x800000008L & l ) != 0L && kind > 45 ) {
                kind = 45;
              }
              break;
            case 154:
              if ( ( 0x1000000010000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 153;
              }
              break;
            case 156:
              if ( ( 0x400000004000L & l ) != 0L && kind > 46 ) {
                kind = 46;
              }
              break;
            case 157:
              if ( ( 0x20000000200L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 156;
              }
              break;
            case 159:
              if ( ( 0x100000001000000L & l ) != 0L && kind > 47 ) {
                kind = 47;
              }
              break;
            case 160:
              if ( ( 0x1000000010000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 159;
              }
              break;
            case 162:
              if ( ( 0x200000002000L & l ) != 0L && kind > 48 ) {
                kind = 48;
              }
              break;
            case 163:
              if ( ( 0x2000000020L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 162;
              }
              break;
            case 165:
              if ( ( 0x100000001000000L & l ) != 0L && kind > 49 ) {
                kind = 49;
              }
              break;
            case 166:
              if ( ( 0x2000000020L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 165;
              }
              break;
            case 168:
              if ( ( 0x8000000080L & l ) != 0L && kind > 50 ) {
                kind = 50;
              }
              break;
            case 169:
              if ( ( 0x2000000020L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 168;
              }
              break;
            case 170:
              if ( ( 0x1000000010L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 169;
              }
              break;
            case 172:
              if ( ( 0x1000000010L & l ) != 0L && kind > 51 ) {
                kind = 51;
              }
              break;
            case 173:
              if ( ( 0x200000002L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 172;
              }
              break;
            case 174:
              if ( ( 0x4000000040000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 173;
              }
              break;
            case 176:
              if ( ( 0x1000000010L & l ) != 0L && kind > 52 ) {
                kind = 52;
              }
              break;
            case 177:
              if ( ( 0x200000002L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 176;
              }
              break;
            case 178:
              if ( ( 0x4000000040000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 177;
              }
              break;
            case 179:
              if ( ( 0x8000000080L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 178;
              }
              break;
            case 181:
              if ( ( 0x8000000080000L & l ) != 0L && kind > 53 ) {
                kind = 53;
              }
              break;
            case 182:
              if ( ( 0x200000002000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 181;
              }
              break;
            case 184:
              if ( ( 0x8000000080000L & l ) != 0L && kind > 54 ) {
                kind = 54;
              }
              break;
            case 186:
              if ( ( 0x400000004000000L & l ) != 0L && kind > 55 ) {
                kind = 55;
              }
              break;
            case 187:
              if ( ( 0x10000000100L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 186;
              }
              break;
            case 189:
              if ( ( 0x400000004000000L & l ) != 0L && kind > 56 ) {
                kind = 56;
              }
              break;
            case 190:
              if ( ( 0x10000000100L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 189;
              }
              break;
            case 191:
              if ( ( 0x80000000800L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 190;
              }
              break;
            case 193:
            case 194:
              if ( ( 0x7fffffe87fffffeL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 195:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 196, 197 );
              }
              break;
            case 196:
              if ( ( 0x7fffffffffffffffL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 197:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 482, 485 );
              break;
            case 199:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 486, 492 );
              break;
            case 200:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 493, 495 );
              break;
            case 201:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 496, 499 );
              break;
            case 202:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 500, 504 );
              break;
            case 203:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 505, 510 );
              break;
            case 204:
              if ( curChar == 92 ) {
                jjCheckNAddTwoStates( 196, 205 );
              }
              break;
            case 205:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 511, 514 );
              break;
            case 206:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 515, 521 );
              break;
            case 207:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 522, 524 );
              break;
            case 208:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 525, 528 );
              break;
            case 209:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 529, 533 );
              break;
            case 210:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddStates( 534, 539 );
              break;
            case 211:
              if ( ( 0x20000000200000L & l ) != 0L ) {
                jjAddStates( 875, 877 );
              }
              break;
            case 213:
            case 217:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 546, 549 );
              }
              break;
            case 216:
              if ( curChar == 92 ) {
                jjAddStates( 892, 893 );
              }
              break;
            case 218:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 550, 554 );
              }
              break;
            case 220:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 555, 562 );
              }
              break;
            case 221:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 563, 566 );
              }
              break;
            case 222:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 567, 571 );
              }
              break;
            case 223:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 572, 577 );
              }
              break;
            case 224:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 578, 584 );
              }
              break;
            case 226:
            case 231:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 228:
              if ( curChar == 92 ) {
                jjAddStates( 894, 897 );
              }
              break;
            case 230:
              if ( curChar == 92 ) {
                jjAddStates( 898, 899 );
              }
              break;
            case 232:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 589, 594 );
              }
              break;
            case 234:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 595, 603 );
              }
              break;
            case 235:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 604, 608 );
              }
              break;
            case 236:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 609, 614 );
              }
              break;
            case 237:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 615, 621 );
              }
              break;
            case 238:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 622, 629 );
              }
              break;
            case 243:
            case 248:
              if ( ( 0x7fffffffffffffffL & l ) != 0L ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            case 245:
              if ( curChar == 92 ) {
                jjAddStates( 900, 903 );
              }
              break;
            case 247:
              if ( curChar == 92 ) {
                jjAddStates( 904, 905 );
              }
              break;
            case 249:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 634, 639 );
              }
              break;
            case 251:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 640, 648 );
              }
              break;
            case 252:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 649, 653 );
              }
              break;
            case 253:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 654, 659 );
              }
              break;
            case 254:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 660, 666 );
              }
              break;
            case 255:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 667, 674 );
              }
              break;
            case 260:
              if ( ( 0x100000001000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 212;
              }
              break;
            case 261:
              if ( ( 0x4000000040000L & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 260;
              }
              break;
            case 269:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjAddStates( 691, 696 );
              break;
            case 270:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 271;
              }
              break;
            case 271:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 272;
              }
              break;
            case 272:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAdd( 273 );
              }
              break;
            case 273:
              if ( ( 0x7e0000007eL & l ) != 0L && kind > 76 ) {
                kind = 76;
              }
              break;
            case 274:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 275;
              }
              break;
            case 275:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 276;
              }
              break;
            case 276:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 277;
              }
              break;
            case 277:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 265;
              break;
            case 278:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 279;
              }
              break;
            case 279:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 280;
              }
              break;
            case 280:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 281;
              break;
            case 282:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjstateSet[ jjnewStateCnt++ ] = 283;
              }
              break;
            case 283:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 284;
              break;
            case 286:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 287;
              break;
            case 295:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddTwoStates( 296, 302 );
              }
              break;
            case 297:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjstateSet[ jjnewStateCnt++ ] = 298;
              break;
            case 298:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 707, 710 );
              break;
            case 299:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAdd( 273 );
              break;
            case 300:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddTwoStates( 273, 299 );
              break;
            case 301:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 76 ) {
                kind = 76;
              }
              jjCheckNAddStates( 711, 713 );
              break;
            case 302:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 714, 718 );
              }
              break;
            case 303:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAdd( 296 );
              }
              break;
            case 304:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddTwoStates( 303, 296 );
              }
              break;
            case 305:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 719, 721 );
              }
              break;
            case 306:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 722, 725 );
              }
              break;
            case 307:
              if ( curChar == 92 ) {
                jjCheckNAddStates( 865, 872 );
              }
              break;
            case 308:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 726, 729 );
              break;
            case 309:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 730, 736 );
              break;
            case 310:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 737, 739 );
              break;
            case 311:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 740, 743 );
              break;
            case 312:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 744, 748 );
              break;
            case 313:
              if ( ( 0x7e0000007eL & l ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 749, 754 );
              break;
            case 314:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 755, 759 );
              }
              break;
            case 315:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 760, 767 );
              }
              break;
            case 316:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 768, 771 );
              }
              break;
            case 317:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 772, 776 );
              }
              break;
            case 318:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 777, 782 );
              }
              break;
            case 319:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 783, 789 );
              }
              break;
            case 320:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 790, 794 );
              }
              break;
            case 321:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 795, 802 );
              }
              break;
            case 322:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 803, 806 );
              }
              break;
            case 323:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 807, 811 );
              }
              break;
            case 324:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 812, 817 );
              }
              break;
            case 325:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 818, 824 );
              }
              break;
            case 326:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 825, 829 );
              }
              break;
            case 327:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 830, 837 );
              }
              break;
            case 328:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 838, 841 );
              }
              break;
            case 329:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 842, 846 );
              }
              break;
            case 330:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 847, 852 );
              }
              break;
            case 331:
              if ( ( 0x7e0000007eL & l ) != 0L ) {
                jjCheckNAddStates( 853, 859 );
              }
              break;
            default:
              break;
          }
        } while ( i != startsAt );
      } else {
        int i2 = ( curChar & 0xff ) >> 6;
        long l2 = 1L << ( curChar & 077 );
        MatchLoop:
        do {
          switch( jjstateSet[ --i ] ) {
            case 388:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddTwoStates( 78, 79 );
              }
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 1:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                if ( kind > 25 ) {
                  kind = 25;
                }
              }
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                if ( kind > 36 ) {
                  kind = 36;
                }
                jjCheckNAddStates( 99, 109 );
              }
              break;
            case 389:
            case 49:
            case 51:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 48:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 65 ) {
                kind = 65;
              }
              jjCheckNAddTwoStates( 49, 50 );
              break;
            case 3:
            case 8:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 114, 117 );
              }
              break;
            case 20:
            case 25:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 110, 113 );
              }
              break;
            case 37:
            case 39:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 58 ) {
                kind = 58;
              }
              jjCheckNAddTwoStates( 37, 38 );
              break;
            case 77:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddStates( 99, 109 );
              break;
            case 78:
            case 80:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 36 ) {
                kind = 36;
              }
              jjCheckNAddTwoStates( 78, 79 );
              break;
            case 88:
            case 91:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 6, 8 );
              }
              break;
            case 99:
            case 120:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 3, 5 );
              }
              break;
            case 101:
            case 102:
            case 104:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 40 ) {
                kind = 40;
              }
              jjCheckNAddTwoStates( 102, 103 );
              break;
            case 128:
            case 131:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 0, 2 );
              }
              break;
            case 193:
            case 194:
            case 196:
              if ( ( jjbitVec0[ i2 ] & l2 ) == 0L ) {
                break;
              }
              if ( kind > 57 ) {
                kind = 57;
              }
              jjCheckNAddTwoStates( 194, 195 );
              break;
            case 213:
            case 217:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 546, 549 );
              }
              break;
            case 226:
            case 231:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 585, 588 );
              }
              break;
            case 243:
            case 248:
              if ( ( jjbitVec0[ i2 ] & l2 ) != 0L ) {
                jjCheckNAddStates( 630, 633 );
              }
              break;
            default:
              break;
          }
        } while ( i != startsAt );
      }
      if ( kind != 0x7fffffff ) {
        jjmatchedKind = kind;
        jjmatchedPos = curPos;
        kind = 0x7fffffff;
      }
      ++curPos;
      if ( ( i = jjnewStateCnt ) == ( startsAt = 387 - ( jjnewStateCnt = startsAt ) ) ) {
        return curPos;
      }
      try {
        curChar = input_stream.readChar();
      } catch ( java.io.IOException e ) {
        return curPos;
      }
    }
  }

  private final int jjMoveStringLiteralDfa0_1() {
    switch( curChar ) {
      case 42:
        return jjMoveStringLiteralDfa1_1( 0x8L );
      default:
        return 1;
    }
  }

  private final int jjMoveStringLiteralDfa1_1( long active0 ) {
    try {
      curChar = input_stream.readChar();
    } catch ( java.io.IOException e ) {
      return 1;
    }
    switch( curChar ) {
      case 47:
        if ( ( active0 & 0x8L ) != 0L ) {
          return jjStopAtPos( 1, 3 );
        }
        break;
      default:
        return 2;
    }
    return 2;
  }

  static final int[] jjnextStates = {
    128, 129, 130, 99, 100, 119, 88, 89, 90, 333, 334, 335, 336, 337, 338, 142,
    339, 340, 341, 145, 342, 343, 344, 148, 345, 346, 347, 151, 348, 349, 350, 154,
    351, 352, 353, 157, 354, 355, 356, 160, 357, 358, 359, 163, 360, 361, 362, 166,
    363, 364, 365, 170, 366, 367, 368, 174, 369, 370, 371, 179, 372, 373, 374, 182,
    375, 376, 377, 184, 378, 379, 380, 187, 381, 382, 383, 191, 384, 385, 386, 193,
    204, 140, 141, 143, 146, 149, 152, 155, 158, 161, 164, 167, 171, 175, 180, 183,
    185, 188, 192, 78, 88, 89, 99, 100, 128, 129, 130, 119, 90, 79, 20, 21,
    22, 24, 3, 4, 5, 7, 192, 193, 204, 3, 10, 4, 5, 7, 11, 3,
    12, 10, 4, 5, 7, 13, 14, 15, 3, 10, 4, 5, 7, 3, 12, 10,
    4, 5, 7, 3, 12, 10, 4, 5, 7, 13, 3, 12, 10, 4, 5, 7,
    13, 14, 20, 27, 21, 22, 24, 28, 20, 29, 27, 21, 22, 24, 30, 31,
    32, 20, 27, 21, 22, 24, 20, 29, 27, 21, 22, 24, 20, 29, 27, 21,
    22, 24, 30, 20, 29, 27, 21, 22, 24, 30, 31, 37, 41, 38, 42, 37,
    43, 41, 38, 44, 45, 46, 37, 41, 38, 37, 43, 41, 38, 37, 43, 41,
    38, 44, 37, 43, 41, 38, 44, 45, 49, 53, 50, 54, 49, 55, 53, 50,
    56, 57, 58, 49, 53, 50, 49, 55, 53, 50, 49, 55, 53, 50, 56, 49,
    55, 53, 50, 56, 57, 53, 49, 50, 61, 62, 53, 49, 50, 63, 64, 65,
    53, 49, 50, 62, 53, 49, 50, 62, 53, 49, 50, 63, 62, 53, 49, 50,
    63, 64, 78, 82, 79, 83, 78, 84, 82, 79, 85, 86, 87, 78, 82, 79,
    78, 84, 82, 79, 78, 84, 82, 79, 85, 78, 84, 82, 79, 85, 86, 88,
    93, 89, 90, 94, 88, 95, 93, 89, 90, 96, 97, 98, 88, 93, 89, 90,
    88, 95, 93, 89, 90, 88, 95, 93, 89, 90, 96, 88, 95, 93, 89, 90,
    96, 97, 102, 106, 103, 107, 102, 108, 106, 103, 109, 110, 111, 102, 106, 103,
    102, 108, 106, 103, 102, 108, 106, 103, 109, 102, 108, 106, 103, 109, 110, 106,
    102, 103, 114, 115, 106, 102, 103, 116, 117, 118, 106, 102, 103, 115, 106, 102,
    103, 115, 106, 102, 103, 116, 115, 106, 102, 103, 116, 117, 99, 122, 100, 119,
    123, 99, 124, 122, 100, 119, 125, 126, 127, 99, 122, 100, 119, 99, 124, 122,
    100, 119, 99, 124, 122, 100, 119, 125, 99, 124, 122, 100, 119, 125, 126, 128,
    133, 129, 130, 134, 128, 135, 133, 129, 130, 136, 137, 138, 128, 133, 129, 130,
    128, 135, 133, 129, 130, 128, 135, 133, 129, 130, 136, 128, 135, 133, 129, 130,
    136, 137, 194, 198, 195, 199, 194, 200, 198, 195, 201, 202, 203, 194, 198, 195,
    194, 200, 198, 195, 194, 200, 198, 195, 201, 194, 200, 198, 195, 201, 202, 198,
    194, 195, 206, 207, 198, 194, 195, 208, 209, 210, 198, 194, 195, 207, 198, 194,
    195, 207, 198, 194, 195, 208, 207, 198, 194, 195, 208, 209, 213, 225, 242, 215,
    216, 259, 213, 214, 215, 216, 213, 215, 216, 219, 220, 213, 221, 215, 216, 219,
    222, 223, 224, 213, 215, 216, 219, 213, 221, 215, 216, 219, 213, 221, 215, 216,
    219, 222, 213, 221, 215, 216, 219, 222, 223, 226, 227, 228, 230, 226, 233, 227,
    228, 230, 234, 226, 235, 233, 227, 228, 230, 236, 237, 238, 226, 233, 227, 228,
    230, 226, 235, 233, 227, 228, 230, 226, 235, 233, 227, 228, 230, 236, 226, 235,
    233, 227, 228, 230, 236, 237, 243, 244, 245, 247, 243, 250, 244, 245, 247, 251,
    243, 252, 250, 244, 245, 247, 253, 254, 255, 243, 250, 244, 245, 247, 243, 252,
    250, 244, 245, 247, 243, 252, 250, 244, 245, 247, 253, 243, 252, 250, 244, 245,
    247, 253, 254, 213, 225, 242, 214, 215, 216, 259, 263, 269, 265, 266, 267, 268,
    265, 266, 267, 270, 274, 278, 282, 286, 290, 265, 288, 289, 265, 291, 292, 293,
    265, 291, 292, 273, 299, 300, 301, 273, 299, 300, 303, 296, 304, 305, 306, 303,
    296, 304, 303, 296, 304, 305, 82, 78, 79, 309, 310, 82, 78, 79, 311, 312,
    313, 82, 78, 79, 310, 82, 78, 79, 310, 82, 78, 79, 311, 310, 82, 78,
    79, 311, 312, 93, 88, 89, 90, 315, 316, 93, 88, 89, 90, 317, 318, 319,
    93, 88, 89, 90, 316, 93, 88, 89, 90, 316, 93, 88, 89, 90, 317, 316,
    93, 88, 89, 90, 317, 318, 122, 99, 100, 119, 321, 322, 122, 99, 100, 119,
    323, 324, 325, 122, 99, 100, 119, 322, 122, 99, 100, 119, 322, 122, 99, 100,
    119, 323, 322, 122, 99, 100, 119, 323, 324, 133, 128, 129, 130, 327, 328, 133,
    128, 129, 130, 329, 330, 331, 133, 128, 129, 130, 328, 133, 128, 129, 130, 328,
    133, 128, 129, 130, 329, 328, 133, 128, 129, 130, 329, 330, 384, 193, 204, 101,
    112, 80, 308, 91, 314, 120, 320, 131, 326, 48, 59, 261, 262, 294, 6, 16,
    18, 17, 8, 9, 23, 33, 35, 34, 25, 26, 39, 40, 217, 218, 229, 239,
    241, 240, 231, 232, 246, 256, 258, 257, 248, 249,
  };
  public static final String[] jjstrLiteralImages = {
    "", null, null, null, null, "\74\41\55\55", "\55\55\76", "\173", "\175",
    "\174\75", "\176\75", "\75", "\53", "\55", "\54", "\73", "\76", "\57", "\133", "\135",
    "\52", "\56", "\51", "\50", "\72", null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, };
  public static final String[] lexStateNames = {
    "DEFAULT",
    "IN_COMMENT",
  };
  public static final int[] jjnewLexState = {
    -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1,
  };
  static final long[] jjtoToken = {
    0xffffffb803ffffe3L, 0x7007L,
  };
  static final long[] jjtoSkip = {
    0x8L, 0x0L,
  };
  static final long[] jjtoMore = {
    0x14L, 0x0L,
  };
  protected CharStream input_stream;
  private final int[] jjrounds = new int[ 387 ];
  private final int[] jjstateSet = new int[ 774 ];
  StringBuffer image;
  int jjimageLen;
  int lengthOfMatch;
  protected char curChar;

  public ParserTokenManager( CharStream stream ) {
    input_stream = stream;
  }

  public ParserTokenManager( CharStream stream, int lexState ) {
    this( stream );
    SwitchTo( lexState );
  }

  public void ReInit( CharStream stream ) {
    jjmatchedPos = jjnewStateCnt = 0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private final void ReInitRounds() {
    int i;
    jjround = 0x80000001;
    for ( i = 387; i-- > 0; ) {
      jjrounds[ i ] = 0x80000000;
    }
  }

  public void ReInit( CharStream stream, int lexState ) {
    ReInit( stream );
    SwitchTo( lexState );
  }

  public void SwitchTo( int lexState ) {
    if ( lexState >= 2 || lexState < 0 ) {
      throw new TokenMgrError( "Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",
        TokenMgrError.INVALID_LEXICAL_STATE );
    } else {
      curLexState = lexState;
    }
  }

  protected Token jjFillToken() {
    Token t = Token.newToken( jjmatchedKind );
    t.kind = jjmatchedKind;
    String im = jjstrLiteralImages[ jjmatchedKind ];
    t.image = ( im == null ) ? input_stream.GetImage() : im;
    t.beginLine = input_stream.getBeginLine();
    t.beginColumn = input_stream.getBeginColumn();
    t.endLine = input_stream.getEndLine();
    t.endColumn = input_stream.getEndColumn();
    return t;
  }

  int curLexState = 0;
  int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;

  public Token getNextToken() {
    int kind;
    Token specialToken = null;
    Token matchedToken;
    int curPos = 0;

    EOFLoop:
    for (; ; ) {
      try {
        curChar = input_stream.BeginToken();
      } catch ( java.io.IOException e ) {
        jjmatchedKind = 0;
        matchedToken = jjFillToken();
        return matchedToken;
      }
      image = null;
      jjimageLen = 0;

      for (; ; ) {
        switch( curLexState ) {
          case 0:
            jjmatchedKind = 0x7fffffff;
            jjmatchedPos = 0;
            curPos = jjMoveStringLiteralDfa0_0();
            if ( jjmatchedPos == 0 && jjmatchedKind > 78 ) {
              jjmatchedKind = 78;
            }
            break;
          case 1:
            jjmatchedKind = 0x7fffffff;
            jjmatchedPos = 0;
            curPos = jjMoveStringLiteralDfa0_1();
            if ( jjmatchedPos == 0 && jjmatchedKind > 4 ) {
              jjmatchedKind = 4;
            }
            break;
        }
        if ( jjmatchedKind != 0x7fffffff ) {
          if ( jjmatchedPos + 1 < curPos ) {
            input_stream.backup( curPos - jjmatchedPos - 1 );
          }
          if ( ( jjtoToken[ jjmatchedKind >> 6 ] & ( 1L << ( jjmatchedKind & 077 ) ) ) != 0L ) {
            matchedToken = jjFillToken();
            TokenLexicalActions( matchedToken );
            if ( jjnewLexState[ jjmatchedKind ] != -1 ) {
              curLexState = jjnewLexState[ jjmatchedKind ];
            }
            return matchedToken;
          } else if ( ( jjtoSkip[ jjmatchedKind >> 6 ] & ( 1L << ( jjmatchedKind & 077 ) ) ) != 0L ) {
            if ( jjnewLexState[ jjmatchedKind ] != -1 ) {
              curLexState = jjnewLexState[ jjmatchedKind ];
            }
            continue EOFLoop;
          }
          jjimageLen += jjmatchedPos + 1;
          if ( jjnewLexState[ jjmatchedKind ] != -1 ) {
            curLexState = jjnewLexState[ jjmatchedKind ];
          }
          curPos = 0;
          jjmatchedKind = 0x7fffffff;
          try {
            curChar = input_stream.readChar();
            continue;
          } catch ( java.io.IOException e1 ) {
          }
        }
        int error_line = input_stream.getEndLine();
        int error_column = input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;
        try {
          input_stream.readChar();
          input_stream.backup( 1 );
        } catch ( java.io.IOException e1 ) {
          EOFSeen = true;
          error_after = curPos <= 1 ? "" : input_stream.GetImage();
          if ( curChar == '\n' || curChar == '\r' ) {
            error_line++;
            error_column = 0;
          } else {
            error_column++;
          }
        }
        if ( !EOFSeen ) {
          input_stream.backup( 1 );
          error_after = curPos <= 1 ? "" : input_stream.GetImage();
        }
        throw new TokenMgrError( EOFSeen, curLexState, error_line, error_column, error_after, curChar,
          TokenMgrError.LEXICAL_ERROR );
      }
    }
  }

  void TokenLexicalActions( Token matchedToken ) {
    switch( jjmatchedKind ) {
      case 1:
        if ( image == null ) {
          image = new StringBuffer(
            new String( input_stream.GetSuffix( jjimageLen + ( lengthOfMatch = jjmatchedPos + 1 ) ) ) );
        } else {
          image.append( input_stream.GetSuffix( jjimageLen + ( lengthOfMatch = jjmatchedPos + 1 ) ) );
        }
        image = Parser.SPACE;
        break;
      default:
        break;
    }
  }
}
