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
package org.pentaho.reporting.libraries.base.boot;

import java.util.UUID;

@SingletonHint
public class ObjectFactorySingleton {
  private UUID id;

  public ObjectFactorySingleton() {
    this.id = UUID.randomUUID();
  }

  public UUID getId() {
    return id;
  }
}
