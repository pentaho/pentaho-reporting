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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Holds the active repository session in memory.
 * Used by {@link LoginTask} to reuse an existing session and skip login.
 * EE modules can set the session and listen for session changes.
 */
public final class RepositorySession {

  private static AuthenticationData activeSession;
  private static final List<Consumer<AuthenticationData>> listeners = new CopyOnWriteArrayList<>();

  private RepositorySession() {
    // static utility class
  }

  public static void addSessionChangeListener( final Consumer<AuthenticationData> listener ) {
    listeners.add( listener );
  }

  public static synchronized void setSession( final AuthenticationData session ) {
    activeSession = session;
    notifyListeners( session );
  }

  public static synchronized AuthenticationData getSession() {
    return activeSession;
  }

  public static synchronized boolean hasActiveSession() {
    return activeSession != null;
  }

  public static synchronized void clearSession() {
    activeSession = null;
    notifyListeners( null );
  }

  private static void notifyListeners( final AuthenticationData session ) {
    for ( Consumer<AuthenticationData> listener : listeners ) {
      listener.accept( session );
    }
  }
}
