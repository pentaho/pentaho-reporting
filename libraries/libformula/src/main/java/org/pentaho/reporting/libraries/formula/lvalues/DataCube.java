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

package org.pentaho.reporting.libraries.formula.lvalues;

/**
 * A multi-dimensional data collection. It should return LValues, but for now, this remains totally undefined until I
 * have a clue about what I'm doing here.
 *
 * @author Thomas Morgner
 */
// I guess we are talking about a range functionality as in this example:
// SUM([Sheet1.B4:Sheet2.C5]) : Simple 3D range, naturally with explicit sheet
// names
public interface DataCube {
}
