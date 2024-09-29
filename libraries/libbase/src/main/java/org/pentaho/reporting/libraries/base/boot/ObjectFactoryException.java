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
package org.pentaho.reporting.libraries.base.boot;

public class ObjectFactoryException extends RuntimeException {
  private String expectedType;
  private String configredClass;

  public ObjectFactoryException() {
  }

  public ObjectFactoryException( final String message ) {
    super( message );
  }

  public ObjectFactoryException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public ObjectFactoryException( final Throwable cause ) {
    super( cause );
  }

  public ObjectFactoryException( final String expectedType, final String configuredClass, final Throwable cause ) {
    super( String.format( "Unable to create object of type '%s' with configured class '%s'",
      expectedType, configuredClass ), cause );
    this.expectedType = expectedType;
    this.configredClass = configuredClass;
  }

  public ObjectFactoryException( final String expectedType, final String configuredClass ) {
    super( String.format( "Unable to create object of type '%s' with configured class '%s'",
      expectedType, configuredClass ) );
    this.expectedType = expectedType;
    this.configredClass = configuredClass;
  }
}
