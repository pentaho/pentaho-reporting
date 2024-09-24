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
