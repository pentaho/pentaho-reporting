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

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.ContentsToken;
import org.pentaho.reporting.libraries.css.values.CSSGenericType;
import org.pentaho.reporting.libraries.css.values.CSSType;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class ContentSpecification implements CSSValue {
  private static final QuotesPair[] EMPTY_QUOTES = new QuotesPair[ 0 ];
  private static final ContentsToken[] EMPTY_CONTENT = new ContentsToken[ 0 ];

  private QuotesPair[] quotes;
  private ContentToken[] contents;
  private ContentToken[] strings;
  private ContentToken[] alternateText;
  private boolean allowContentProcessing;
  private boolean inhibitContent;
  private int quotingLevel;
  private String moveTarget;

  public ContentSpecification() {
    quotes = EMPTY_QUOTES;
    contents = EMPTY_CONTENT;
    strings = EMPTY_CONTENT;
    alternateText = EMPTY_CONTENT;
    allowContentProcessing = true;
  }

  public boolean isInhibitContent() {
    return inhibitContent;
  }

  public void setInhibitContent( final boolean inhibitContent ) {
    this.inhibitContent = inhibitContent;
  }

  public QuotesPair[] getQuotes() {
    return (QuotesPair[]) quotes.clone();
  }

  public ContentToken[] getStrings() {
    return (ContentToken[]) strings.clone();
  }

  public void setStrings( final ContentToken[] strings ) {
    this.strings = (ContentToken[]) strings.clone();
  }

  public ContentToken[] getAlternateText() {
    return (ContentToken[]) alternateText.clone();
  }

  public void setAlternateText( final ContentToken[] strings ) {
    this.alternateText = (ContentToken[]) strings.clone();
  }

  public String getOpenQuote( final int level ) {
    if ( level < 0 ) {
      return "";
    }
    if ( level >= quotes.length ) {
      if ( quotes.length == 0 ) {
        return "";
      }
      return quotes[ quotes.length - 1 ].getOpenQuote();
    }
    return quotes[ level ].getOpenQuote();
  }

  public String getCloseQuote( final int level ) {
    if ( level < 0 ) {
      return "";
    }
    if ( level >= quotes.length ) {
      if ( quotes.length == 0 ) {
        return "";
      }
      return quotes[ quotes.length - 1 ].getCloseQuote();
    }
    return quotes[ level ].getCloseQuote();
  }

  public void setQuotes( final QuotesPair[] quotes ) {
    if ( this.quotes == null ) {
      throw new NullPointerException();
    }
    this.quotes = (QuotesPair[]) quotes.clone();
  }

  public ContentToken[] getContents() {
    return (ContentToken[]) contents.clone();
  }

  public void setContents( final ContentToken[] contents ) {
    this.contents = (ContentToken[]) contents.clone();
  }


  public boolean isAllowContentProcessing() {
    return allowContentProcessing;
  }

  public void setAllowContentProcessing( final boolean allowContentProcessing ) {
    this.allowContentProcessing = allowContentProcessing;
  }

  public int getQuotingLevel() {
    return quotingLevel;
  }

  public void setQuotingLevel( final int quotingLevel ) {
    this.quotingLevel = quotingLevel;
  }

  public String getMoveTarget() {
    return moveTarget;
  }

  public void setMoveTarget( final String moveTarget ) {
    this.moveTarget = moveTarget;
  }

  public String getCSSText() {
    return "";
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
