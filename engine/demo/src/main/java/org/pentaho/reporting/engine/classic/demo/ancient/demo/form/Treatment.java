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
