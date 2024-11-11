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


package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

public interface DataAttributes extends Serializable, Cloneable {
  public String[] getMetaAttributeDomains();

  public String[] getMetaAttributeNames( String domainName );

  /**
   * @param domain
   *          never null.
   * @param name
   *          never null.
   * @param type
   *          can be null.
   * @param context
   *          never null.
   * @return
   */
  public Object getMetaAttribute( String domain, String name, Class type, DataAttributeContext context );

  /**
   * @param domain
   *          never null.
   * @param name
   *          never null.
   * @param type
   *          can be null.
   * @param context
   *          never null.
   * @param defaultValue
   *          can be null
   * @return
   */
  public Object getMetaAttribute( String domain, String name, Class type, DataAttributeContext context,
      Object defaultValue );

  public ConceptQueryMapper getMetaAttributeMapper( String domain, String name );

  public Object clone() throws CloneNotSupportedException;
}
