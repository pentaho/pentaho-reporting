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

/**
 * Holds the active SSO session for the Pentaho Server repository.
 * <p>
 * When a user connects via the Repository &rarr; Connect menu,
 * the authenticated {@link AuthenticationData} is stored here so
 * that subsequent Open&nbsp;/&nbsp;Publish operations can skip
 * the login dialog and go directly to the file browser.
 * <p>
 * Thread-safe: all public methods are synchronised on the instance.
 */
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

  /**
   * Stores the SSO session after a successful Connect.
   *
   * @param session  the authenticated credentials (must not be {@code null})
   * @param username the display name of the logged-in user
   */
  public synchronized void setSession( final AuthenticationData session, final String username ) {
    this.activeSession = session;
    this.loggedInUser = username;
    pcs.firePropertyChange( "activeSession", null, session );
  }

  /**
   * @return the current SSO session, or {@code null} if not connected
   */
  public synchronized AuthenticationData getSession() {
    return activeSession;
  }

  /**
   * @return the display name of the currently logged-in user, or {@code null}
   */
  public synchronized String getLoggedInUser() {
    return loggedInUser;
  }

  /**
   * @return {@code true} when an SSO session is active
   */
  public synchronized boolean hasActiveSession() {
    return activeSession != null;
  }

  /**
   * Clears the stored session (Disconnect).
   */
  public synchronized void clearSession() {
    this.activeSession = null;
    this.loggedInUser = null;
    pcs.firePropertyChange( "activeSession", null, null );
  }
}
