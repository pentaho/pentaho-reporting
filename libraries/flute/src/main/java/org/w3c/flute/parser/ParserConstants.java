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


package org.w3c.flute.parser;

public interface ParserConstants {

  int EOF = 0;
  int S = 1;
  int CDO = 5;
  int CDC = 6;
  int LBRACE = 7;
  int RBRACE = 8;
  int DASHMATCH = 9;
  int INCLUDES = 10;
  int EQ = 11;
  int PLUS = 12;
  int MINUS = 13;
  int COMMA = 14;
  int SEMICOLON = 15;
  int PRECEDES = 16;
  int DIV = 17;
  int LBRACKET = 18;
  int RBRACKET = 19;
  int ANY = 20;
  int DOT = 21;
  int LPARAN = 22;
  int RPARAN = 23;
  int COLON = 24;
  int NONASCII = 25;
  int H = 26;
  int UNICODE = 27;
  int ESCAPE = 28;
  int NMSTART = 29;
  int NMCHAR = 30;
  int STRINGCHAR = 31;
  int D = 32;
  int NAME = 33;
  int NNAME = 34;
  int STRING = 35;
  int IDENT = 36;
  int NUMBER = 37;
  int _URL = 38;
  int URL = 39;
  int NAMESPACE_IDENT = 40;
  int PERCENTAGE = 41;
  int PT = 42;
  int MM = 43;
  int CM = 44;
  int PC = 45;
  int IN = 46;
  int PX = 47;
  int EMS = 48;
  int EXS = 49;
  int DEG = 50;
  int RAD = 51;
  int GRAD = 52;
  int MS = 53;
  int SECOND = 54;
  int HZ = 55;
  int KHZ = 56;
  int DIMEN = 57;
  int HASH = 58;
  int IMPORT_SYM = 59;
  int MEDIA_SYM = 60;
  int CHARSET_SYM = 61;
  int PAGE_SYM = 62;
  int NAMESPACE_SYM = 63;
  int FONT_FACE_SYM = 64;
  int ATKEYWORD = 65;
  int IMPORTANT_SYM = 66;
  int RANGE0 = 67;
  int RANGE1 = 68;
  int RANGE2 = 69;
  int RANGE3 = 70;
  int RANGE4 = 71;
  int RANGE5 = 72;
  int RANGE6 = 73;
  int RANGE = 74;
  int UNI = 75;
  int UNICODERANGE = 76;
  int FUNCTION = 77;
  int UNKNOWN = 78;

  int DEFAULT = 0;
  int IN_COMMENT = 1;

  String[] tokenImage = {
    "<EOF>",
    "<S>",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 4>",
    "\"<!--\"",
    "\"-->\"",
    "\"{\"",
    "\"}\"",
    "\"|=\"",
    "\"~=\"",
    "\"=\"",
    "\"+\"",
    "\"-\"",
    "\",\"",
    "\";\"",
    "\">\"",
    "\"/\"",
    "\"[\"",
    "\"]\"",
    "\"*\"",
    "\".\"",
    "\")\"",
    "\"(\"",
    "\":\"",
    "<NONASCII>",
    "<H>",
    "<UNICODE>",
    "<ESCAPE>",
    "<NMSTART>",
    "<NMCHAR>",
    "<STRINGCHAR>",
    "<D>",
    "<NAME>",
    "<NNAME>",
    "<STRING>",
    "<IDENT>",
    "<NUMBER>",
    "<_URL>",
    "<URL>",
    "<NAMESPACE_IDENT>",
    "<PERCENTAGE>",
    "<PT>",
    "<MM>",
    "<CM>",
    "<PC>",
    "<IN>",
    "<PX>",
    "<EMS>",
    "<EXS>",
    "<DEG>",
    "<RAD>",
    "<GRAD>",
    "<MS>",
    "<SECOND>",
    "<HZ>",
    "<KHZ>",
    "<DIMEN>",
    "<HASH>",
    "\"@import\"",
    "\"@media\"",
    "\"@charset\"",
    "\"@page\"",
    "\"@namespace\"",
    "\"@font-face\"",
    "<ATKEYWORD>",
    "<IMPORTANT_SYM>",
    "<RANGE0>",
    "<RANGE1>",
    "<RANGE2>",
    "<RANGE3>",
    "<RANGE4>",
    "<RANGE5>",
    "<RANGE6>",
    "<RANGE>",
    "<UNI>",
    "<UNICODERANGE>",
    "<FUNCTION>",
    "<UNKNOWN>",
  };

}
