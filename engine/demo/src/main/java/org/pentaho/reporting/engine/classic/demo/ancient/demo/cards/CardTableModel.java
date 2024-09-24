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

import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;

/**
 * A card table model.
 *
 * @author Thomas Morgner.
 */
public class CardTableModel extends AbstractTableModel
{
  /**
   * Storage for the cards.
   */
  private final ArrayList cards;

  /**
   * The type index.
   */
  private static final int POS_TYPE = 0;

  /**
   * The name index.
   */
  private static final int POS_NAME = 1;

  /**
   * The first name index.
   */
  private static final int POS_FIRSTNAME = 2;

  /**
   * The card number index.
   */
  private static final int POS_CARDNR = 3;

  /**
   * The login index.
   */
  private static final int POS_LOGIN = 4;

  /**
   * The password index.
   */
  private static final int POS_PASSWORD = 5;

  /**
   * The expiry date index.
   */
  private static final int POS_EXPIRES = 6;

  /**
   * The column names.
   */
  private static final String[] COL_NAMES =
      {
          "type", "name", "firstName", "cardNr", "login", "password", "expires"
      };

  /**
   * Default constructor.
   */
  public CardTableModel()
  {
    cards = new ArrayList();
  }

  /**
   * Adds a card.
   *
   * @param c the card.
   */
  public void addCard(final Card c)
  {
    if (c == null)
    {
      throw new NullPointerException();
    }
    cards.add(c);
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount()
  {
    return cards.size();
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount()
  {
    return COL_NAMES.length;
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class getColumnClass(final int columnIndex)
  {
    if (columnIndex == POS_TYPE)
    {
      return CardType.class;
    }
    if (columnIndex == POS_EXPIRES)
    {
      return Date.class;
    }
    return String.class;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(final int column)
  {
    return COL_NAMES[column];
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final Card c = (Card) cards.get(rowIndex);
    if (columnIndex == POS_TYPE)
    {
      return c.getType();
    }
    if (c.getType() == CardType.ACCOUNT)
    {
      final AccountCard ac = (AccountCard) c;
      if (columnIndex == POS_NAME)
      {
        return ac.getLastName();
      }
      if (columnIndex == POS_FIRSTNAME)
      {
        return ac.getFirstName();
      }
      if (columnIndex == POS_LOGIN)
      {
        return ac.getLogin();
      }
      if (columnIndex == POS_PASSWORD)
      {
        return ac.getPassword();
      }
    }
    else if ((c.getType() == CardType.ADMIN) || (c.getType() == CardType.USER))
    {
      final UserCard ac = (UserCard) c;
      if (columnIndex == POS_NAME)
      {
        return ac.getLastName();
      }
      if (columnIndex == POS_FIRSTNAME)
      {
        return ac.getFirstName();
      }
      if (columnIndex == POS_LOGIN)
      {
        return ac.getLogin();
      }
      if (columnIndex == POS_PASSWORD)
      {
        return ac.getPassword();
      }
      if (columnIndex == POS_CARDNR)
      {
        return ac.getCardNr();
      }
      if (columnIndex == POS_EXPIRES)
      {
        return ac.getExpires();
      }
    }
    else if (c.getType() == CardType.FREE)
    {
      final FreeCard ac = (FreeCard) c;
      if (columnIndex == POS_CARDNR)
      {
        return ac.getCardNr();
      }
      if (columnIndex == POS_EXPIRES)
      {
        return ac.getExpires();
      }
    }
    else if (c.getType() == CardType.PREPAID)
    {
      final PrepaidCard ac = (PrepaidCard) c;
      if (columnIndex == POS_NAME)
      {
        return ac.getLastName();
      }
      if (columnIndex == POS_FIRSTNAME)
      {
        return ac.getFirstName();
      }
      if (columnIndex == POS_CARDNR)
      {
        return ac.getCardNr();
      }
    }

    return null;
  }
}
