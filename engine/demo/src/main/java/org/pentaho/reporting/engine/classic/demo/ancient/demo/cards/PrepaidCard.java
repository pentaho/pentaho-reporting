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

/**
 * A 'prepaid' card.
 *
 * @author Thomas Morgner.
 */
public class PrepaidCard extends PersonBoundCard
{
  /**
   * Creates a new 'prepaid' card.
   *
   * @param firstName the first name.
   * @param lastName  the last name.
   * @param cardNr    the card number.
   */
  public PrepaidCard(final String firstName, final String lastName, final String cardNr)
  {
    super(firstName, lastName, cardNr);
  }

  /**
   * Returns the card type.
   *
   * @return The card type.
   */
  public CardType getType()
  {
    return CardType.PREPAID;
  }
}
