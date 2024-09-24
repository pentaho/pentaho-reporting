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
 * An enumeration of card types.
 *
 * @author Thomas Morgner.
 */
public final class CardType
{
  /**
   * An 'account' card.
   */
  public static final CardType ACCOUNT = new CardType("Account");

  /**
   * An 'admin' card.
   */
  public static final CardType ADMIN = new CardType("Admin");

  /**
   * A 'user' card.
   */
  public static final CardType USER = new CardType("User");

  /**
   * A 'prepaid' card.
   */
  public static final CardType PREPAID = new CardType("Prepaid");

  /**
   * A 'free' card.
   */
  public static final CardType FREE = new CardType("Free");

  /**
   * A 'empty' card.
   */
  public static final CardType EMPTY = new CardType("Empty");

  /**
   * The type name.
   */
  private final String myName;

  /**
   * Creates a new card type.
   * <p/>
   * This constructor is private to prevent new types being constructed - only the predefined types are valid.
   *
   * @param name the type name.
   */
  private CardType(final String name)
  {
    myName = name;
  }

  /**
   * Returns the type name.
   *
   * @return The type name.
   */
  public String getTypeName()
  {
    return myName;
  }

  /**
   * Returns a string representing the type.
   *
   * @return A string.
   */
  public String toString()
  {
    return myName;
  }
}
