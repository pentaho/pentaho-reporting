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
