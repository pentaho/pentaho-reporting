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


package org.pentaho.reporting.engine.classic.core.function;

/**
 * A tagging interface that allows the system to differentiate between functions that compute values and functions that
 * modify the report-definiton. Any function that <i>only</i> modifies report definitions and does not return a value
 * that can be in a computation should implement this interface.
 *
 * @author Thomas Morgner
 */
public interface LayoutProcessorFunction extends Function {
}
