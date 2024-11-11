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


package org.pentaho.reporting.engine.classic.core.filter;

/**
 * A data filter is a combined input-/out target. Use filters to perform stateless data transformation. If you need to
 * access the report state to perform a task, use functions instead.
 *
 * @author Thomas Morgner
 */
public interface DataFilter extends DataSource, DataTarget {
}
