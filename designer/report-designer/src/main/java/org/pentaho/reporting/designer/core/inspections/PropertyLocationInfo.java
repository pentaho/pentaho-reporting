/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core.inspections;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PropertyLocationInfo extends LocationInfo {
  private String attributeName;

  public PropertyLocationInfo( final Object reportElement,
                               final String attributeName ) {
    super( reportElement );
    if ( attributeName == null ) {
      throw new NullPointerException();
    }

    this.attributeName = attributeName;
  }

  public String getAttributeName() {
    return attributeName;
  }

}
