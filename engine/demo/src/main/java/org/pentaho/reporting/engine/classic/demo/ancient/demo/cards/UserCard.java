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
