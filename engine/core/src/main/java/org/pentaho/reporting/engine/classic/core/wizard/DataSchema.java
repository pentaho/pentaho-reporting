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

/**
 * Represents a compiled data-schema. All rules have been evaluated and collapseded into a set of data-attributes. A
 * Data-Schema is always a column-schema, it does not change on each row (as it could happen with MetaTableModels).
 *
 * @author Thomas Morgner
 */
public interface DataSchema extends Serializable, Cloneable {
  public DataAttributes getTableAttributes();

  public DataAttributes getAttributes( final String name );

  public Object clone() throws CloneNotSupportedException;

  public String[] getNames();
}
