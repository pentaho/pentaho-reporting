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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.util;

import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;

import java.util.Properties;

/**
 * A collection of all character entites defined in the HTML4 standard. The key is the entity name, the property value
 * is the decoded string.
 *
 * @author Thomas Morgner
 */
public class HtmlCharacterEntities extends Properties {
  /**
   * The singleton instance for this entity-parser implementation.
   */
  private static CharacterEntityParser entityParser;

  /**
   * Gets the character entity parser for HTML content. The CharacterEntity parser translates known characters into
   * predefined entities.
   *
   * @return the character entity parser instance.
   */
  public static synchronized CharacterEntityParser getEntityParser() {
    if ( entityParser == null ) {
      entityParser = new CharacterEntityParser( new HtmlCharacterEntities() );
    }
    return entityParser;
  }

  private void addAccentedCharacters() {
    setProperty( "acute", "\u00b4" );
    setProperty( "cedil", "\u00b8" );
    setProperty( "circ", "\u02c6" );
    setProperty( "macr", "\u00af" );
    setProperty( "middot", "\u00b7" );
    setProperty( "tilde", "\u02dc" );
    setProperty( "uml", "\u00a8" );

    setProperty( "Aacute", "\u00c1" );
    setProperty( "aacute", "\u00e1" );
    setProperty( "Acirc", "\u00c2" );
    setProperty( "acirc", "\u00e2" );
    setProperty( "AElig", "\u00c6" );
    setProperty( "aelig", "\u00e6" );
    setProperty( "Agrave", "\u00c0" );
    setProperty( "agrave", "\u00e0" );
    setProperty( "Aring", "\u00c5" );
    setProperty( "aring", "\u00e5" );
    setProperty( "atilde", "\u00e3" );
    setProperty( "Atilde", "\u00c3" );
    setProperty( "Auml", "\u00c4" );
    setProperty( "auml", "\u00e4" );

    setProperty( "ccedil", "\u00e7" );
    setProperty( "Ccedil", "\u00c7" );

    setProperty( "Eacute", "\u00c9" );
    setProperty( "eacute", "\u00e9" );
    setProperty( "Ecirc", "\u00ca" );
    setProperty( "ecirc", "\u00ea" );
    setProperty( "Egrave", "\u00c8" );
    setProperty( "egrave", "\u00e8" );
    setProperty( "ETH", "\u00d0" );
    setProperty( "eth", "\u00f0" );
    setProperty( "Euml", "\u00cb" );
    setProperty( "euml", "\u00eb" );

    setProperty( "Iacute", "\u00cd" );
    setProperty( "iacute", "\u00ed" );
    setProperty( "Icirc", "\u00ce" );
    setProperty( "icirc", "\u00ee" );
    setProperty( "Igrave", "\u00cc" );
    setProperty( "igrave", "\u00ec" );
    setProperty( "Iuml", "\u00cf" );
    setProperty( "iuml", "\u00ef" );

    setProperty( "Ntilde", "\u00d1" );
    setProperty( "ntilde", "\u00f1" );

    setProperty( "Oacute", "\u00d3" );
    setProperty( "oacute", "\u00f3" );
    setProperty( "Ocirc", "\u00d4" );
    setProperty( "ocirc", "\u00f4" );
    setProperty( "Oelig", "\u0152" );
    setProperty( "oelig", "\u0153" );
    setProperty( "Ograve", "\u00d2" );
    setProperty( "ograve", "\u00f2" );
    setProperty( "Oslash", "\u00d8" );
    setProperty( "oslash", "\u00f8" );
    setProperty( "Otilde", "\u00d5" );
    setProperty( "otilde", "\u00f5" );
    setProperty( "Ouml", "\u00d6" );
    setProperty( "ouml", "\u00f6" );
    setProperty( "Scaron", "\u0160" );
    setProperty( "scaron", "\u0161" );
    setProperty( "szlig", "\u00df" );
    setProperty( "THORN", "\u00de" );
    setProperty( "thorn", "\u00fe" );

    setProperty( "Uacute", "\u00da" );
    setProperty( "uacute", "\u00fa" );
    setProperty( "Ucirc", "\u00db" );
    setProperty( "ucirc", "\u00fb" );
    setProperty( "Ugrave", "\u00d9" );
    setProperty( "ugrave", "\u00f9" );
    setProperty( "Uuml", "\u00dc" );
    setProperty( "uuml", "\u00fc" );
    setProperty( "Yacute", "\u00dd" );
    setProperty( "yacute", "\u00fd" );
    setProperty( "Yuml", "\u0178" );
    setProperty( "yuml", "\u00ff" );
  }

  /**
   * Creates an instance.
   * <p/>
   * Source: http://www.cookwood.com/html/extras/entities.html
   */
  public HtmlCharacterEntities() {
    // Characters with special meaning
    setProperty( "amp", "\u0026" );
    setProperty( "gt", "\u003e" );
    setProperty( "lt", "\u003c" );
    setProperty( "quot", "\"" );

    // Entities for accented characters, accents, and other diacritics from Western European Languages
    addAccentedCharacters();
    // Entities for punctuation characters
    addPunctuationCharacters();

    // Entities for mathematical and technical characters (including Greek)
    addMathCharacters();

    // Entities for shapes and arrows
    setProperty( "crarr", "\u21b5" );
    setProperty( "darr", "\u2193" );
    setProperty( "dArr", "\u21d3" );
    setProperty( "harr", "\u2194" );
    setProperty( "hArr", "\u21d4" );
    setProperty( "larr", "\u2190" );
    setProperty( "lArr", "\u21d0" );
    setProperty( "rarr", "\u2192" );
    setProperty( "rArr", "\u21d2" );
    setProperty( "uarr", "\u2191" );
    setProperty( "uArr", "\u21d1" );

    setProperty( "clubs", "\u2663" );
    setProperty( "diams", "\u2666" );
    setProperty( "hearts", "\u2665" );
    setProperty( "spades", "\u2660" );

    setProperty( "loz", "\u25ca" );

    setProperty( "rlm", "\u200f" );
    setProperty( "prop", "\u221d" );
  }

  private void addMathCharacters() {
    setProperty( "deg", "\u00b0" );
    setProperty( "divide", "\u00f7" );
    setProperty( "frac12", "\u00bd" );
    setProperty( "frac14", "\u00bc" );
    setProperty( "frac34", "\u00be" );
    setProperty( "ge", "\u2265" );
    setProperty( "le", "\u2264" );
    setProperty( "minus", "\u2212" );
    setProperty( "sup2", "\u00b2" );
    setProperty( "sup3", "\u00b3" );
    setProperty( "times", "\u00d7" );

    setProperty( "alefsym", "\u2135" );
    setProperty( "and", "\u2227" );
    setProperty( "ang", "\u2220" );
    setProperty( "asymp", "\u2248" );
    setProperty( "cap", "\u2229" );
    setProperty( "cong", "\u2245" );
    setProperty( "cup", "\u222a" );
    setProperty( "empty", "\u2205" );
    setProperty( "equiv", "\u2261" );
    setProperty( "exist", "\u2203" );
    setProperty( "fnof", "\u0192" );
    setProperty( "forall", "\u2200" );
    setProperty( "infin", "\u221e" );
    setProperty( "int", "\u222b" );
    setProperty( "isin", "\u2208" );
    setProperty( "lang", "\u2329" );
    setProperty( "lceil", "\u2308" );

    setProperty( "lfloor", "\u22a6" );
    setProperty( "lowast", "\u2217" );
    setProperty( "micro", "\u00b5" );
    setProperty( "nabla", "\u2207" );
    setProperty( "ne", "\u2260" );
    setProperty( "ni", "\u220b" );
    setProperty( "notin", "\u2209" );
    setProperty( "nsub", "\u2284" );
    setProperty( "oplus", "\u2295" );
    setProperty( "or", "\u2228" );
    setProperty( "otimes", "\u2297" );
    setProperty( "part", "\u2202" );
    setProperty( "perp", "\u22a5" );
    setProperty( "plusmn", "\u00b1" );
    setProperty( "prod", "\u220f" );
    setProperty( "radic", "\u221a" );
    setProperty( "rang", "\u232a" );
    setProperty( "rceil", "\u2309" );
    setProperty( "rfloor", "\u22a7" );
    setProperty( "sdot", "\u22c5" );
    setProperty( "sim", "\u223c" );
    setProperty( "sub", "\u2282" );
    setProperty( "sube", "\u2286" );
    setProperty( "sum", "\u2211" );
    setProperty( "sup", "\u2283" );
    setProperty( "supe", "\u2287" );
    setProperty( "there4", "\u2234" );

    setProperty( "Alpha", "\u0391" );
    setProperty( "alpha", "\u03b1" );
    setProperty( "Beta", "\u0392" );
    setProperty( "beta", "\u03b2" );
    setProperty( "Chi", "\u03a7" );
    setProperty( "chi", "\u03c7" );
    setProperty( "Delta", "\u0394" );
    setProperty( "delta", "\u03b4" );
    setProperty( "Epsilon", "\u0395" );
    setProperty( "epsilon", "\u03b5" );
    setProperty( "Eta", "\u0397" );
    setProperty( "eta", "\u03b7" );
    setProperty( "Gamma", "\u0393" );
    setProperty( "gamma", "\u03b3" );
    setProperty( "Iota", "\u0399" );
    setProperty( "iota", "\u03b9" );
    setProperty( "Kappa", "\u039a" );
    setProperty( "kappa", "\u03ba" );
    setProperty( "Lambda", "\u039b" );
    setProperty( "lambda", "\u03bb" );
    setProperty( "Mu", "\u039c" );
    setProperty( "mu", "\u03bc" );
    setProperty( "Nu", "\u039d" );
    setProperty( "nu", "\u03bd" );
    setProperty( "Omega", "\u03a9" );
    setProperty( "omega", "\u03c9" );
    setProperty( "Omicron", "\u039f" );
    setProperty( "omicron", "\u03bf" );
    setProperty( "Phi", "\u03a6" );
    setProperty( "phi", "\u03c6" );
    setProperty( "Pi", "\u03a0" );
    setProperty( "pi", "\u03c0" );
    setProperty( "piv", "\u03d6" );
    setProperty( "Psi", "\u03a8" );
    setProperty( "psi", "\u03c8" );
    setProperty( "Rho", "\u03a1" );
    setProperty( "rho", "\u03c1" );
    setProperty( "Sigma", "\u03a3" );
    setProperty( "sigma", "\u03c3" );
    setProperty( "sigmaf", "\u03c2" );
    setProperty( "Tau", "\u03a4" );
    setProperty( "tau", "\u03c4" );
    setProperty( "Theta", "\u0398" );
    setProperty( "theta", "\u03b8" );
    setProperty( "thetasym", "\u03d1" );
    setProperty( "upsih", "\u03d2" );
    setProperty( "Upsilon", "\u03a5" );
    setProperty( "upsilon", "\u03c5" );
    setProperty( "Xi", "\u039e" );
    setProperty( "xi", "\u03be" );
    setProperty( "Zeta", "\u0396" );
    setProperty( "zeta", "\u03b6" );
  }

  private void addPunctuationCharacters() {
    setProperty( "cent", "\u00a2" );
    setProperty( "curren", "\u00a4" );
    setProperty( "euro", "\u20ac" );
    setProperty( "pound", "\u00a3" );
    setProperty( "yen", "\u00a5" );

    setProperty( "brvbar", "\u00a6" );
    setProperty( "bull", "\u2022" );
    setProperty( "copy", "\u00a9" );
    setProperty( "dagger", "\u2020" );
    setProperty( "Dagger", "\u2021" );
    setProperty( "frasl", "\u2044" );
    setProperty( "hellip", "\u2026" );
    setProperty( "iexcl", "\u00a1" );
    setProperty( "image", "\u2111" );
    setProperty( "iquest", "\u00bf" );
    setProperty( "lrm", "\u200e" );
    setProperty( "mdash", "\u2014" );
    setProperty( "ndash", "\u2013" );
    setProperty( "not", "\u00ac" );
    setProperty( "oline", "\u203e" );
    setProperty( "ordf", "\u00aa" );
    setProperty( "ordm", "\u00ba" );
    setProperty( "para", "\u00b6" );
    setProperty( "permil", "\u2030" );
    setProperty( "prime", "\u2032" );
    setProperty( "Prime", "\u2033" );
    setProperty( "real", "\u211c" );
    setProperty( "reg", "\u00ae" );
    setProperty( "rim", "\u8207" );
    setProperty( "sect", "\u00a7" );
    setProperty( "shy", "\u00ad" );
    setProperty( "sup1", "\u00b9" );
    setProperty( "trade", "\u2122" );
    setProperty( "weierp", "\u2118" );

    setProperty( "bdquo", "\u201e" );
    setProperty( "laquo", "\u00ab" );
    setProperty( "ldquo", "\u201c" );
    setProperty( "lsaquo", "\u2039" );
    setProperty( "lsquo", "\u2018" );
    setProperty( "raquo", "\u00bb" );
    setProperty( "rdquo", "\u201d" );
    setProperty( "rsaquo", "\u203a" );
    setProperty( "rsquo", "\u2019" );
    setProperty( "sbquo", "\u201a" );

    setProperty( "emsp", "\u2003" );
    setProperty( "ensp", "\u2002" );
    setProperty( "nbsp", "\u00a0" );
    setProperty( "thinsp", "\u2009" );
    setProperty( "zwj", "\u200d" );
    setProperty( "zwnj", "\u200c" );
  }
}
