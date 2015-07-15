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
