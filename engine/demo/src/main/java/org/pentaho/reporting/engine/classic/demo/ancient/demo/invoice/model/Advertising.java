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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * A data object encapsulating an advertising letter. This simple data object holds all articles and a reference to the
 * customer.
 * <p/>
 * This class has a very bad design, but should be sufficient for demo purposes.
 *
 * @author Thomas Morgner
 */
public class Advertising
{
  /**
   * The customer, who should be addressed with that ad.
   */
  private Customer customer;
  /**
   * The list of articles which we want to offer to the customer.
   */
  private ArrayList articles;
  /**
   * A list with a reduced price for the given article.
   */
  private ArrayList articleReducedPrices;
  /**
   * The date when this ad was valid.
   */
  private Date date;
  /**
   * The ad number, the primary key.
   */
  private String adNumber;

  /**
   * Creates a new advertising for the given customer, which is valid for the given date and has the specified identity
   * number.
   *
   * @param customer a reference to an customer
   * @param date     the date
   * @param adNumber the advertising id-number.
   */
  public Advertising(final Customer customer, final Date date,
                     final String adNumber)
  {
    if (customer == null)
    {
      throw new NullPointerException("The customer must not be null.");
    }
    if (date == null)
    {
      throw new NullPointerException("The date for the ad must not be null.");
    }
    if (adNumber == null)
    {
      throw new NullPointerException("The Advertising Number must not be null.");
    }
    this.customer = customer;
    this.date = date;
    this.adNumber = adNumber;
    this.articles = new ArrayList();
    this.articleReducedPrices = new ArrayList();
  }

  /**
   * Adds an article with an reduced price to the advertising.
   *
   * @param article the reference to the article.
   * @param reduced the reduced price.
   */
  public synchronized void addArticle(final Article article, final double reduced)
  {
    if (article == null)
    {
      throw new NullPointerException("The given article must not be null");
    }
    final int index = articles.indexOf(article);
    if (index == -1)
    {
      articles.add(article);
      articleReducedPrices.add(new Double(reduced));
    }
  }

  /**
   * Removes the article from this advertising.
   *
   * @param article the article.
   */
  public synchronized void removeArticle(final Article article)
  {
    if (article == null)
    {
      throw new NullPointerException("Article must not be null.");
    }
    final int index = articles.indexOf(article);
    if (index != -1)
    {
      articleReducedPrices.remove(index);
      articles.remove(index);
    }
  }

  /**
   * Returns the article at the given index.
   *
   * @param index the index.
   * @return the article.
   */
  public Article getArticle(final int index)
  {
    return (Article) articles.get(index);
  }

  /**
   * Returns the reduced price for the article at the given index.
   *
   * @param index the index of the article
   * @return the reduced price of the article.
   */
  public double getArticleReducedPrice(final int index)
  {
    final Double i = (Double) articleReducedPrices.get(index);
    return i.doubleValue();
  }

  /**
   * Returns the number of articles in this ad.
   *
   * @return the article count.
   */
  public int getArticleCount()
  {
    return articles.size();
  }

  /**
   * Returns the customer assigned to the ad.
   *
   * @return the customer.
   */
  public Customer getCustomer()
  {
    return customer;
  }

  /**
   * Returns the date, when this ad was issued.
   *
   * @return the date of this ad.
   */
  public Date getDate()
  {
    return date;
  }

  /**
   * Returns the ad number.
   *
   * @return the ad number.
   */
  public String getAdNumber()
  {
    return adNumber;
  }
}
