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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;

public class PasswordPropertyReadHandler extends PropertyReadHandler {
  public PasswordPropertyReadHandler() {
  }

  public String getResult() {
    return PasswordEncryptionService.getInstance().decrypt( getRootHandler(), super.getResult() );
  }

  public Object getObject() {
    return getResult();
  }
}
