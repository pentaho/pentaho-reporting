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
