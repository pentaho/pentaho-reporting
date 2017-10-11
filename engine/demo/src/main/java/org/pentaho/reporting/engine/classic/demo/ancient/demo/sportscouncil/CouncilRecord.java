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


public class CouncilRecord extends Record
{
  private String orgName;
  private String internalWebsite;
  private String orgEmail;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String zip;
  private String phoneNumber;
  private String extension;
  private String faxNumber;
  private int yearEventCount;
  private int thisMonthEventCount;
  private int lastMonthEventCount;
  private int futureEventCount;

  public CouncilRecord(final String orgID,
                       final String orgName, final String internalWebsite,
                       final String orgEmail, final String street1,
                       final String street2, final String city,
                       final String state, final String zip,
                       final String phoneNumber, final String extension,
                       final String faxNumber, final int yearEventCount,
                       final int thisMonthEventCount,
                       final int lastMonthEventCount,
                       final int futureEventCount)
  {
    super("council", orgID);
    this.orgName = orgName;
    this.internalWebsite = internalWebsite;
    this.orgEmail = orgEmail;
    this.street1 = street1;
    this.street2 = street2;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.phoneNumber = phoneNumber;
    this.extension = extension;
    this.faxNumber = faxNumber;
    this.yearEventCount = yearEventCount;
    this.thisMonthEventCount = thisMonthEventCount;
    this.lastMonthEventCount = lastMonthEventCount;
    this.futureEventCount = futureEventCount;
  }

  public String getCity()
  {
    return city;
  }

  public String getExtension()
  {
    return extension;
  }

  public String getFaxNumber()
  {
    return faxNumber;
  }

  public int getFutureEventCount()
  {
    return futureEventCount;
  }

  public String getInternalWebsite()
  {
    return internalWebsite;
  }

  public int getLastMonthEventCount()
  {
    return lastMonthEventCount;
  }

  public String getOrgEmail()
  {
    return orgEmail;
  }

  public String getOrgName()
  {
    return orgName;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public String getState()
  {
    return state;
  }

  public String getStreet1()
  {
    return street1;
  }

  public String getStreet2()
  {
    return street2;
  }

  public int getThisMonthEventCount()
  {
    return thisMonthEventCount;
  }

  public int getYearEventCount()
  {
    return yearEventCount;
  }

  public String getZip()
  {
    return zip;
  }
}
