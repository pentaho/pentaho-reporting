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

public class SubOrganizationRecord extends Record
{
  private String name;
  private String email;
  private int maleGenderCount;
  private int femaleGenderCount;

  public SubOrganizationRecord(final String orgID,
                               final String name, final String email,
                               final int maleGenderCount,
                               final int femaleGenderCount)
  {
    super("org", orgID);
    this.name = name;
    this.email = email;
    this.maleGenderCount = maleGenderCount;
    this.femaleGenderCount = femaleGenderCount;
  }

  public String getEmail()
  {
    return email;
  }

  public int getFemaleGenderCount()
  {
    return femaleGenderCount;
  }

  public int getMaleGenderCount()
  {
    return maleGenderCount;
  }

  public String getName()
  {
    return name;
  }
}
