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

package org.pentaho.reporting.engine.classic.core.wizard;

public class EmptyDataAttributes implements DataAttributes {
  public static final EmptyDataAttributes INSTANCE = new EmptyDataAttributes();
  private static final String[] EMPTY_STRING = new String[0];

  public EmptyDataAttributes() {
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String[] getMetaAttributeDomains() {
    return EmptyDataAttributes.EMPTY_STRING;
  }

  public String[] getMetaAttributeNames( final String domainName ) {
    return EmptyDataAttributes.EMPTY_STRING;
  }

  public Object getMetaAttribute( final String domain, final String name, final Class type,
      final DataAttributeContext context ) {
    return null;
  }

  public Object getMetaAttribute( final String domain, final String name, final Class type,
      final DataAttributeContext context, final Object defaultValue ) {
    return defaultValue;
  }

  public ConceptQueryMapper getMetaAttributeMapper( String domain, String name ) {
    return DefaultConceptQueryMapper.INSTANCE;
  }
}
