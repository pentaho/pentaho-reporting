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
 * Base class for a card.
 *
 * @author Thomas Morgner.
 */
public abstract class Card
{
  /**
   * Default constructor.
   */
  public Card()
  {
  }

  /**
   * Returns the card type.
   *
   * @return The card type.
   */
  public abstract CardType getType();
}
