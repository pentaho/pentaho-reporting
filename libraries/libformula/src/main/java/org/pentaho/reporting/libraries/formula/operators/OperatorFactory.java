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


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 02.11.2006, 12:27:52
 *
 * @author Thomas Morgner
 */
public interface OperatorFactory {
  public InfixOperator createInfixOperator( String operator );

  public PostfixOperator createPostfixOperator( String operator );

  public PrefixOperator createPrefixOperator( String operator );

  public void initalize( Configuration configuration );
}
