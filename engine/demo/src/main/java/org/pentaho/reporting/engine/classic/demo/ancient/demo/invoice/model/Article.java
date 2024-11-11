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
