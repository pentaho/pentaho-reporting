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

import java.util.ArrayList;
import java.util.Date;

public class Invoice
{
  private ArrayList articles;
  private ArrayList articleCounts;

  private Customer customer;
  private Date date;
  private String invoiceNumber;

  public Invoice(final Customer customer, final Date date,
                 final String invoiceNumber)
  {
    this.customer = customer;
    this.date = date;
    this.invoiceNumber = invoiceNumber;
    this.articles = new ArrayList();
    this.articleCounts = new ArrayList();
  }

  public synchronized void addArticle(final Article article)
  {
    final int index = articles.indexOf(article);
    if (index == -1)
    {
      articles.add(article);
      articleCounts.add(new Integer(1));
    }
    else
    {
      final Integer oldCount = (Integer) articleCounts.get(index);
      articleCounts.set(index, new Integer(oldCount.intValue() + 1));
    }
  }

  public synchronized void removeArticle(final Article article)
  {
    final int index = articles.indexOf(article);
    if (index != -1)
    {
      final Integer oldCount = (Integer) articleCounts.get(index);
      if (oldCount.intValue() == 1)
      {
        articleCounts.remove(index);
        articles.remove(index);
      }
      else
      {
        articleCounts.set(index, new Integer(oldCount.intValue() - 1));
      }
    }
  }

  public Article getArticle(final int index)
  {
    return (Article) articles.get(index);
  }

  public int getArticleCount(final int index)
  {
    final Integer i = (Integer) articleCounts.get(index);
    return i.intValue();
  }

  public int getArticleCount()
  {
    return articles.size();
  }

  public Customer getCustomer()
  {
    return customer;
  }

  public Date getDate()
  {
    return date;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }
}
