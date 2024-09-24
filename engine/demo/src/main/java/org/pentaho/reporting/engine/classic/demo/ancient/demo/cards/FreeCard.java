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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
