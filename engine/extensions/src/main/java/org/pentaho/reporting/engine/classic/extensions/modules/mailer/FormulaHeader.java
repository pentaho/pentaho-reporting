/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;

public class FormulaHeader implements MailHeader {
  private String name;
  private String formula;

  public FormulaHeader( final String name, final String formula ) {
    this.name = name;
    this.formula = formula;
  }

  public String getName() {
    return name;
  }

  public String getValue( final ParameterContext parameterContext ) {
    return null;
  }
}
