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
