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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany;

import javax.swing.table.DefaultTableModel;

/**
 * A simple tablemodel for the one-to-many reports.
 *
 * @author Thomas Morgner
 */
public class PeopleReportTableModel extends DefaultTableModel
{
  private static final String[] COLUMN_NAMES = new String[]{
      "person.name", "person.address",
      "recordType",
      "activitylog.Time", "activitylog.Task",
      "office.Name", "office.Annotations",
      "lunch.Meal", "lunch.Rating"
  };

  private static final Object[][] DATA = new Object[][]{
      {
          "Michael B. DK", "street 1, town",
          "activitylog",
          "Monday 10:00 - 12:00", "lunchbreak (Pizza)",
          null, null,
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "activitylog",
          "Monday 12:00 - 13:00", "some work",
          null, null,
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "activitylog",
          "Monday 13:00 - 15:00", "surfed the internet",
          null, null,
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "office",
          null, null,
          "Goofy", "loves to hide all pencils",
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "office",
          null, null,
          "Dagobert D.", "greedy bastard, would sell soul for payrise",
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "office",
          null, null,
          "Donald D.", "keep away from phone!!!",
          null, null
      },
      {
          "Michael B. DK", "street 1, town",
          "lunch",
          null, null,
          null, null,
          "Pizza", "A+"
      },
      {
          "Michael B. DK", "street 1, town",
          "lunch",
          null, null,
          null, null,
          "Toast", "F- (Am I in jail, or why do they serve dried bread and water?"
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Monday 10:00 - 11:00", "Meeting with major. Got assignment for headhunt.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Monday 11:00 - 13:00", "Meeting with bank director. Got assignment for headhunt.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Monday 13:00 - 17:00", "Meeting with shop keepers. Got assignment for headhunt.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Thursday 10:00 - 11:00", "Negotiate with Daltons. We'll share bounty 50:50.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Thursday 12:00 - 12:05", "Great showdown to earn some easy money.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "activitylog",
          "Thursday 14:00 - 17:00", "Daltons break free from jail. They stole my share.",
          null, null,
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "office",
          null, null,
          "Joe", "smart one, beware!",
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "office",
          null, null,
          "William", "does what Joe says ..",
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "office",
          null, null,
          "Jack", "does what Joe says ..",
          null, null
      },
      {
          "Lucky Luke", "Daisytown, Saloon",
          "office",
          null, null,
          "Avrell", "Stupid head.",
          null, null
      },
  };

  public PeopleReportTableModel()
  {
    super(DATA, COLUMN_NAMES);
  }
}
