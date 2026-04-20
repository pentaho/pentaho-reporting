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


package org.pentaho.reporting.designer.extensions.pentaho.repository;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;

@SuppressWarnings( "java:S6548" )
public final class RepositorySessionManager {

  private static final RepositorySessionManager INSTANCE = new RepositorySessionManager();

  private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
  private AuthenticationData activeSession;
  private String loggedInUser;

  private RepositorySessionManager() {
    // singleton
  }

  public static RepositorySessionManager getInstance() {
    return INSTANCE;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    pcs.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    pcs.removePropertyChangeListener( listener );
  }

  public synchronized void setSession( final AuthenticationData session, final String username ) {
    final AuthenticationData oldSession = this.activeSession;
    this.activeSession = session;
    this.loggedInUser = username;
    pcs.firePropertyChange( "activeSession", oldSession, session );
  }

  public synchronized AuthenticationData getSession() {
    return activeSession;
  }

  public synchronized String getLoggedInUser() {
    return loggedInUser;
  }

  public synchronized boolean hasActiveSession() {
    return activeSession != null;
  }
  public synchronized void clearSession() {
    final AuthenticationData oldSession = this.activeSession;
    this.activeSession = null;
    this.loggedInUser = null;
    pcs.firePropertyChange( "activeSession", oldSession, null );
  }
}
