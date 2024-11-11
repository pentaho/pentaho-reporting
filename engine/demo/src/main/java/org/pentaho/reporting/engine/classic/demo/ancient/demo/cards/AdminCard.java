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
 * An administrator's account card.
 *
 * @author Thomas Morgner.
 */
public class AdminCard extends UserCard
{

  /**
   * Creates a new administrator account card.
   *
   * @param firstName the first name.
   * @param lastName  the last name.
   * @param cardNr    the card number.
   * @param login     the login id.
   * @param password  the password.
   * @param expires   the card expiry date.
   */
  public AdminCard(final String firstName, final String lastName, final String cardNr,
                   final String login, final String password, final Date expires)
  {
    super(firstName, lastName, cardNr, login, password, expires);
  }

  /**
   * Returns the account type (<code>CardType.ADMIN</code>).
   *
   * @return The account type.
   */
  public CardType getType()
  {
    return CardType.ADMIN;
  }
}
