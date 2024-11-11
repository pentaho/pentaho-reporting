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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.cards;

import java.util.Date;

/**
 * A card.
 *
 * @author Thomas Morgner.
 */
public class FreeCard extends Card
{
  /**
   * The expiry date.
   */
  private final Date expires;

  /**
   * The card number.
   */
  private final String cardNr;

  /**
   * Creates a new 'free' card.
   *
   * @param cardNr  the card number.
   * @param expires the expiry date.
   */
  public FreeCard(final String cardNr, final Date expires)
  {
    this.cardNr = cardNr;
    this.expires = expires;
  }

  /**
   * Returns the expiry date.
   *
   * @return The expiry date.
   */
  public Date getExpires()
  {
    return expires;
  }

  /**
   * Returns the card number.
   *
   * @return The card number.
   */
  public String getCardNr()
  {
    return cardNr;
  }

  /**
   * Returns the card type.
   *
   * @return The card type.
   */
  public CardType getType()
  {
    return CardType.FREE;
  }
}
