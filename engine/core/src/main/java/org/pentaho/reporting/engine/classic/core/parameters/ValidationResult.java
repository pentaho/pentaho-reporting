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

import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.util.HashNMap;

import java.util.ArrayList;
import java.util.Set;

/**
 * A collection containing validation results. If there is at least one error, the collection indicates an error.
 *
 * @author Thomas Morgner
 */
public class ValidationResult {
  private ArrayList messages;
  private HashNMap propertyMessages;
  private ReportParameterValues parameterValues;

  public ValidationResult() {
    propertyMessages = new HashNMap();
    messages = new ArrayList();
  }

  public ReportParameterValues getParameterValues() {
    return parameterValues;
  }

  public void setParameterValues( final ReportParameterValues parameterValues ) {
    this.parameterValues = parameterValues;
  }

  public void addError( final ValidationMessage message ) {
    if ( message == null ) {
      throw new NullPointerException();
    }
    messages.add( message );
  }

  public void addError( final String parameterName, final ValidationMessage message ) {
    if ( parameterName == null ) {
      throw new NullPointerException();
    }
    if ( message == null ) {
      throw new NullPointerException();
    }
    propertyMessages.add( parameterName, message );
  }

  public ValidationMessage[] getErrors() {
    return (ValidationMessage[]) messages.toArray( new ValidationMessage[messages.size()] );
  }

  public ValidationMessage[] getErrors( final String parameter ) {
    return (ValidationMessage[]) propertyMessages.toArray( parameter, new ValidationMessage[propertyMessages
        .getValueCount( parameter )] );
  }

  public String[] getProperties() {
    final Set set = propertyMessages.keySet();
    return (String[]) set.toArray( new String[set.size()] );
  }

  public boolean isEmpty() {
    return propertyMessages.isEmpty();
  }

  public String[] toMessageList() {
    final ArrayList<String> l = new ArrayList<String>();
    final ValidationMessage[] messages1 = getErrors();
    for ( int i = 0; i < messages1.length; i++ ) {
      final ValidationMessage message = messages1[i];
      l.add( message.getMessage() );
    }
    final String[] names = getProperties();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[i];
      final ValidationMessage[] messages2 = getErrors( name );
      final ValidationMessage message = messages2[i];
      l.add( name + " => " + message.getMessage() );
    }
    return l.toArray( new String[l.size()] );
  }
}
