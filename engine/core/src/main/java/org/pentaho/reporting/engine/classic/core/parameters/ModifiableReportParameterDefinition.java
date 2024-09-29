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

public interface ModifiableReportParameterDefinition extends ReportParameterDefinition {
  public void addParameterDefinition( final ParameterDefinitionEntry entry );

  public void addParameterDefinition( final int index, final ParameterDefinitionEntry entry );

  public void setAttribute( final String domain, final String name, final String value );

  public void setValidator( final ReportParameterValidator validator );

  public void removeParameterDefinition( final int index );
}
