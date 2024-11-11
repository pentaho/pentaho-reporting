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
