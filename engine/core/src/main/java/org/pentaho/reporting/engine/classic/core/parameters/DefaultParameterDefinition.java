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

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;

import java.util.ArrayList;

public class DefaultParameterDefinition implements ModifiableReportParameterDefinition {
  private ArrayList<ParameterDefinitionEntry> parameters;
  private ReportParameterValidator validator;
  private ReportAttributeMap<String> attributeMap;

  public DefaultParameterDefinition() {
    parameters = new ArrayList<ParameterDefinitionEntry>();
    validator = new DefaultReportParameterValidator();
    attributeMap = new ReportAttributeMap<String>();
  }

  public void addParameterDefinition( final ParameterDefinitionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }
    // Note: We can have multiple entries for the same parameter name.
    parameters.add( entry );
  }

  public void addParameterDefinition( final int index, final ParameterDefinitionEntry entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }
    // Note: We can have multiple entries for the same parameter name.
    parameters.add( index, entry );
  }

  public void removeParameterDefinition( final int index ) {
    parameters.remove( index );
  }

  public void setAttribute( final String domain, final String name, final String value ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    attributeMap.setAttribute( domain, name, value );
  }

  public String getAttribute( final String domain, final String name ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    return attributeMap.getAttribute( domain, name );
  }

  public int getParameterCount() {
    return parameters.size();
  }

  public ParameterDefinitionEntry[] getParameterDefinitions() {
    return parameters.toArray( new ParameterDefinitionEntry[parameters.size()] );
  }

  public ParameterDefinitionEntry getParameterDefinition( final int parameter ) {
    return parameters.get( parameter );
  }

  public void setValidator( final ReportParameterValidator validator ) {
    if ( validator == null ) {
      throw new NullPointerException();
    }
    this.validator = validator;
  }

  public ReportParameterValidator getValidator() {
    return validator;
  }

  public Object clone() {
    try {
      final DefaultParameterDefinition def = (DefaultParameterDefinition) super.clone();
      def.parameters = (ArrayList<ParameterDefinitionEntry>) parameters.clone();
      def.attributeMap = (ReportAttributeMap<String>) attributeMap.clone();
      return def;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public void removeParameterDefinition( final ParameterDefinitionEntry definitionEntry ) {
    parameters.remove( definitionEntry );
  }
}
