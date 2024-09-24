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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model;

public class Article
{
  // how is the article called?
  private String name;
  // how much does it cost
  private float price;
  // the article number
  private String articleNumber;
  // may contain a serial number or special notes
  private String articleDetails;

  public Article(final String articleNumber, final String name, final float price)
  {
    this(articleNumber, name, price, null);
  }

  public Article(final String articleNumber, final String name,
                 final float price, final String articleDetails)
  {
    this.articleNumber = articleNumber;
    this.name = name;
    this.price = price;
    this.articleDetails = articleDetails;
  }

  public String getArticleDetails()
  {
    return articleDetails;
  }

  public String getArticleNumber()
  {
    return articleNumber;
  }

  public String getName()
  {
    return name;
  }

  public float getPrice()
  {
    return price;
  }
}
