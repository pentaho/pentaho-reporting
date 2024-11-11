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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.sportscouncil;

public class LeaderRecord extends Record
{
  private String firstName;
  private String lastName;
  private String position;
  private String leadershipPhoneNumber;
  private String email;

  public LeaderRecord(final String orgID,
                      final String firstName, final String lastName,
                      final String position, final String leadershipPhoneNumber,
                      final String email)
  {
    super("leader", orgID);
    this.firstName = firstName;
    this.lastName = lastName;
    this.position = position;
    this.leadershipPhoneNumber = leadershipPhoneNumber;
    this.email = email;
  }

  public String getEmail()
  {
    return email;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getLeadershipPhoneNumber()
  {
    return leadershipPhoneNumber;
  }

  public String getPosition()
  {
    return position;
  }
}
