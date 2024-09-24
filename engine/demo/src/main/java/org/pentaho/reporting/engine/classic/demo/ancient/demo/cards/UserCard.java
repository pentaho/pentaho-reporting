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

import java.util.Date;

/**
 * A user account card.
 *
 * @author Thomas Morgner.
 */
public class UserCard extends PersonBoundCard
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
   * The expiry date.
   */
  private Date expires;

  /**
   * Creates a new user account card.
   *
   * @param firstName the first name.
   * @param lastName  the last name.
   * @param cardNr    the card number.
   * @param login     the login id.
   * @param password  the password.
   * @param expires   the expiry date.
   */
  public UserCard(final String firstName, final String lastName, final String cardNr,
                  final String login, final String password, final Date expires)
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
    if (expires == null)
    {
      throw new NullPointerException();
    }

    this.login = login;
    this.password = password;
    this.expires = expires;
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
   * Returns the expiry date.
   *
   * @return The expiry date.
   */
  public Date getExpires()
  {
    return expires;
  }

  /**
   * Returns the card type.
   *
   * @return The card type.
   */
  public CardType getType()
  {
    return CardType.USER;
  }
}
