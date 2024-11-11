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


package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 01.12.2005, 19:11:23
 *
 * @author Thomas Morgner
 */
public class ListStyleTypeOther {
  public static final CSSConstant NORMAL =
    new CSSConstant( "normal" );


  public static final CSSConstant ASTERISKS =
    new CSSConstant( "asterisks" );
  public static final CSSConstant FOOTNOTES =
    new CSSConstant( "footnotes" );
  //  circled-decimal  | circled-lower-latin | circled-upper-latin |
  public static final CSSConstant CIRCLED_DECIMAL =
    new CSSConstant( "circled-decimal" );
  public static final CSSConstant CIRCLED_LOWER_LATIN =
    new CSSConstant( "circled-lower-latin" );
  public static final CSSConstant CIRCLED_UPPER_LATIN =
    new CSSConstant( "circled-upper-latin" );
  // dotted-decimal | double-circled-decimal | filled-circled-decimal |
  public static final CSSConstant DOTTED_DECIMAL =
    new CSSConstant( "dotted-decimal" );
  public static final CSSConstant DOUBLE_CIRCLED_DECIMAL =
    new CSSConstant( "double-circled-decimal" );
  public static final CSSConstant FILLED_CIRCLED_DECIMAL =
    new CSSConstant( "filled-circled-decimal" );
  // parenthesised-decimal | parenthesised-lower-latin
  public static final CSSConstant PARANTHESISED_DECIMAL =
    new CSSConstant( "parenthesised-decimal" );
  public static final CSSConstant PARANTHESISED_LOWER_LATIN =
    new CSSConstant( "parenthesised-lower-latin" );

  private ListStyleTypeOther() {
  }
}
