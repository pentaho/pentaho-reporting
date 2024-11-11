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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.cards;

/**
 * An empty card.
 *
 * @author Thomas Morgner.
 */
public class NoPrintCard extends Card
{
  /**
   * Creates an empty card.
   */
  public NoPrintCard()
  {
  }

  /**
   * Returns the card type.
   *
   * @return The card type.
   */
  public CardType getType()
  {
    return CardType.EMPTY;
  }
}
