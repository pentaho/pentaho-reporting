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

package org.pentaho.reporting.engine.classic.core.function;

public interface AggregationFunction extends Function {
  public String getGroup();

  public void setGroup( final String groupName );

  public String getCrosstabFilterGroup();

  public void setCrosstabFilterGroup( final String groupName );
}
