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

package org.pentaho.reporting.libraries.css.resolver.values;

public class QuotesPair {
  private String openQuote;
  private String closeQuote;

  public QuotesPair( final String openQuote, final String closeQuote ) {
    if ( openQuote == null ) {
      throw new NullPointerException();
    }
    if ( closeQuote == null ) {
      throw new NullPointerException();
    }
    this.openQuote = openQuote;
    this.closeQuote = closeQuote;
  }

  public String getCloseQuote() {
    return closeQuote;
  }

  public String getOpenQuote() {
    return openQuote;
  }
}
