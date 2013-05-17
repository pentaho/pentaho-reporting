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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.function.ItemSumFunction;

public class CrosstabDetail implements Serializable
{
  private String title;
  private String field;
  private Class aggregation;

  public CrosstabDetail(final String fieldName)
  {
    this.field = fieldName;
    this.aggregation = ItemSumFunction.class;
  }

  public CrosstabDetail(final String field, final String title, final Class aggregation)
  {
    this.field = field;
    this.aggregation = aggregation;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(final String title)
  {
    this.title = title;
  }

  public String getField()
  {
    return field;
  }

  public void setField(final String field)
  {
    this.field = field;
  }

  public Class getAggregation()
  {
    return aggregation;
  }

  public void setAggregation(final Class aggregation)
  {
    this.aggregation = aggregation;
  }
}
