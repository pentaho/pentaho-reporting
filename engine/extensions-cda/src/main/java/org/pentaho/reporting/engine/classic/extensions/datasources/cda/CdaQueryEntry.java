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


package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;

import java.io.Serializable;
import java.util.Arrays;

public class CdaQueryEntry implements Serializable {
  private String id;
  private String name;
  private ParameterMapping[] parameters;

  public CdaQueryEntry( final String logicalQueryName, final String cdaId ) {
    if ( logicalQueryName == null ) {
      throw new NullPointerException();
    }
    if ( cdaId == null ) {
      throw new NullPointerException();
    }
    this.name = logicalQueryName;
    this.id = cdaId;
    this.parameters = new ParameterMapping[ 0 ];
  }

  public String getName() {
    return name;
  }

  public void setName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId( final String id ) {
    if ( id == null ) {
      throw new NullPointerException();
    }
    this.id = id;
  }

  public ParameterMapping[] getParameters() {
    return parameters.clone();
  }

  public void setParameters( final ParameterMapping[] parameters ) {
    this.parameters = parameters.clone();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CdaQueryEntry that = (CdaQueryEntry) o;

    if ( !id.equals( that.id ) ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }
    if ( !Arrays.equals( parameters, that.parameters ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + Arrays.hashCode( parameters );
    return result;
  }
}
