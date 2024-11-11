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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;

import java.io.Serializable;

public interface MailHeader extends Serializable, Cloneable {
  public String getName();

  public String getValue( final ParameterContext parameterContext );
}
