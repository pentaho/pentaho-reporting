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

import java.io.Serializable;

/**
 * This is a lightweight bean-converter that normalizes the PMD internal data-structures into their
 * string-representation so that we can then reconvert it back to a valid bean.
 *
 * @author Thomas Morgner
 */
public interface ConceptQueryMapper extends Serializable {
  /**
   * @param value
   *          can be null.
   * @param type
   *          can be null.
   * @param context
   *          never null.
   * @return
   */
  public Object getValue( final Object value, final Class type, final DataAttributeContext context );
}
