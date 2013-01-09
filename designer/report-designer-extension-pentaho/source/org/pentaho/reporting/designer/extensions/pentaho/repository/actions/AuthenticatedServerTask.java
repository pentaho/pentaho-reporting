package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;

public interface AuthenticatedServerTask extends Runnable
{
  public void setLoginData (AuthenticationData loginData, boolean storeUpdates);
}
