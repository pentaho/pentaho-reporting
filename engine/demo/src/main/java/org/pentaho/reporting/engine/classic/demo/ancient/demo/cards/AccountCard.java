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
 * A user account card.
 *
 * @author Thomas Morgner.
 */
public class AccountCard extends PersonBoundCard
{
  /**
   * The login id.
   */
  private String login;

  /**
   * The password.
   */
  private String password;

  /**
   * Creates a new user account card.
   *
   * @param firstName the first name.
   * @param lastName  the last name.
   * @param cardNr    the card number.
   * @param login     the login id.
   * @param password  the password.
   */
  public AccountCard(final String firstName, final String lastName, final String cardNr,
                     final String login, final String password)
  {
    super(firstName, lastName, cardNr);
    if (login == null)
    {
      throw new NullPointerException();
    }
    if (password == null)
    {
      throw new NullPointerException();
    }

    this.login = login;
    this.password = password;
  }

  /**
   * Returns the login id.
   *
   * @return The login id.
   */
  public String getLogin()
  {
    return login;
  }

  /**
   * Returns the password.
   *
   * @return The password.
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Returns the account type (<code>CardType.ACCOUNT</code>).
   *
   * @return The account type.
   */
  public CardType getType()
  {
    return CardType.ACCOUNT;
  }
}
