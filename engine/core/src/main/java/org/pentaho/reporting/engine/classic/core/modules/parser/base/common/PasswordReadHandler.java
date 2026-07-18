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



package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;

public class PasswordReadHandler extends StringReadHandler {
  public PasswordReadHandler() {
  }

  public String getResult() {
    return PasswordEncryptionService.getInstance().decrypt( getRootHandler(), super.getResult() );
  }

  public Object getObject() {
    return getResult();
  }
}
