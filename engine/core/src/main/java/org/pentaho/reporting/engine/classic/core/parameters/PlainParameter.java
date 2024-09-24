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

package org.pentaho.reporting.engine.classic.core.parameters;

/**
 * A simple parameter that represents a single value. This value is set by the user.
 *
 * @author Thomas Morgner
 */
public class PlainParameter extends AbstractParameter {
  public PlainParameter( final String name ) {
    super( name );
  }

  public PlainParameter( final String name, final Class valueType ) {
    super( name, valueType );
  }

}
