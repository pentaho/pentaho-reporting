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
 * Represents an uncompiled (raw) data-schema definition. The schema definition contains rules which, when applied to
 * the report enrich the report's existing meta-data.
 * <p/>
 * The schema definition rules never override the data-source meta-data attributes. External meta-data always wins.
 * Meta-Data will never be applied without an explicit definition - the element in question must have the attribute
 * "core:meta-data-formatting" set to true to start the automatic reconfiguration.
 *
 * @author Thomas Morgner
 */
public interface DataSchemaDefinition extends Serializable, Cloneable {
  /**
   * Returns all known rules.
   *
   * @return
   */
  public GlobalRule[] getGlobalRules();

  public MetaSelectorRule[] getIndirectRules();

  public DirectFieldSelectorRule[] getDirectRules();

  public Object clone();
}
