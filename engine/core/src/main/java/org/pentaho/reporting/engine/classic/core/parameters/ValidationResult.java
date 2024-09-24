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
