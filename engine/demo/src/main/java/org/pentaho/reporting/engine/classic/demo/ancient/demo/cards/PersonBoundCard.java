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

/**
 * A card that is bound to a person's identity.
 *
 * @author Thomas Morgner.
 */
public abstract class PersonBoundCard extends Card
{
  /**
   * The person's first name.
   */
  private String firstName;

  /**
   * The person's last name.
   */
  private String lastName;

  /**
   * The card number.
   */
  private String cardNr;

  /**
   * Creates a new card.
   *
   * @param firstName the first name.
   * @param lastName  the last name.
   * @param cardNr    the card number.
   */
  public PersonBoundCard(final String firstName, final String lastName,
                         final String cardNr)
  {
    if (firstName == null)
    {
      throw new NullPointerException("FirstName");
    }
    if (lastName == null)
    {
      throw new NullPointerException("LastName");
    }
    if (cardNr == null)
    {
      throw new NullPointerException("CardNr");
    }

    this.firstName = firstName;
    this.lastName = lastName;
    this.cardNr = cardNr;
  }

  /**
   * Returns the first name.
   *
   * @return The first name.
   */
  public String getFirstName()
  {
    return firstName;
  }

  /**
   * Returns the last name.
   *
   * @return The last name.
   */
  public String getLastName()
  {
    return lastName;
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

}
