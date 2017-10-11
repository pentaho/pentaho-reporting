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

import java.util.ArrayList;

public class Organization
{
  private CouncilRecord council;
  private ArrayList leaders;
  private ArrayList subOrganizations;

  public Organization(final CouncilRecord council)
  {
    this.council = council;
    this.leaders = new ArrayList();
    this.subOrganizations = new ArrayList();
  }

  public CouncilRecord getCouncil()
  {
    return council;
  }

  public int getLeaderCount()
  {
    return leaders.size();
  }

  public int getSubOrganzationsCount()
  {
    return subOrganizations.size();
  }

  public LeaderRecord getLeader(final int i)
  {
    return (LeaderRecord) leaders.get(i);
  }

  public SubOrganizationRecord getSubOrganization(final int i)
  {
    return (SubOrganizationRecord) subOrganizations.get(i);
  }

  public void addLeader(final LeaderRecord record)
  {
    leaders.add(record);
  }

  public void addSubOrganization(final SubOrganizationRecord record)
  {
    subOrganizations.add(record);
  }
}
