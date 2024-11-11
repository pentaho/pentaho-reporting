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


package org.pentaho.reporting.libraries.css.resolver.tokens.statics;

import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;


/**
 * Static text. All CDATA and all constant strings from the 'content' style-definition result in StaticTextTokens.
 *
 * @author Thomas Morgner
 */
public class StaticTextToken extends StaticToken implements TextType {
  private String text;

  public StaticTextToken( final String text ) {
    if ( text == null ) {
      throw new NullPointerException();
    }
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public String toString() {
    return "org.jfree.layouting.layouter.content.statics.StaticTextToken=" +
      "{text='" + text + "'}";
  }
}
