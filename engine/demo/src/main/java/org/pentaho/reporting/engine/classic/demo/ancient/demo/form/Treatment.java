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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.form;

import java.util.Date;

public class Treatment
{
  private Date date;
  private String description;
  private String medication;
  private String success;

  public Treatment()
  {
  }

  public Treatment(final Date date, final String description,
                   final String medication, final String success)
  {
    this.date = date;
    this.description = description;
    this.medication = medication;
    this.success = success;
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(final Date date)
  {
    this.date = date;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(final String description)
  {
    this.description = description;
  }

  public String getMedication()
  {
    return medication;
  }

  public void setMedication(final String medication)
  {
    this.medication = medication;
  }

  public String getSuccess()
  {
    return success;
  }

  public void setSuccess(final String success)
  {
    this.success = success;
  }
}
