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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.invoice.model;

public class Customer
{
  private String firstName;
  private String lastName;
  private String street;
  private String postalCode;
  private String town;
  private String country;
  private String salutation;

  public Customer(final String firstName, final String lastName,
                  final String salutation, final String street,
                  final String postalCode, final String town,
                  final String country)
  {
    this.firstName = firstName;
    this.lastName = lastName;
    this.salutation = salutation;
    this.street = street;
    this.postalCode = postalCode;
    this.town = town;
    this.country = country;
  }

  public String getCountry()
  {
    return country;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getPostalCode()
  {
    return postalCode;
  }

  public String getStreet()
  {
    return street;
  }

  public String getTown()
  {
    return town;
  }

  public String getSalutation()
  {
    return salutation;
  }
}
