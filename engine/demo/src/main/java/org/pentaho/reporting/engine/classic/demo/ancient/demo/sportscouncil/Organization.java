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
