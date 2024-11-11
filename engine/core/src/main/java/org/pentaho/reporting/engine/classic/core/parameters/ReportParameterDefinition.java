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

import java.io.Serializable;

/**
 * The ReportParameterDefinition provides user-supplied meta-data about the contents of the report-properties. It
 * contains all information to automatically generated a parameter-page for these parameters and contains a reference to
 * code that can validate the parameters.
 * <p/>
 * The reporting engine itself will never generate parameter-pages, but the engine can validate parameters to prevent
 * incomplete or ill-defined reports.
 *
 * @author Thomas Morgner
 */
public interface ReportParameterDefinition extends Serializable, Cloneable {
  public Object clone();

  public int getParameterCount();

  public ParameterDefinitionEntry[] getParameterDefinitions();

  public ParameterDefinitionEntry getParameterDefinition( int parameter );

  public ReportParameterValidator getValidator();
}
