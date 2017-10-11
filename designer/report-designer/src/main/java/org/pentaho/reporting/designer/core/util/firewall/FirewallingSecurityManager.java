/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.util.firewall;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.UtilMessages;

import java.net.SocketPermission;
import java.security.Permission;
import java.security.PermissionCollection;

/**
 * Todo: Document me!
 * <p/>
 * Date: 12.11.2009 Time: 17:33:12
 *
 * @author Thomas Morgner.
 */
public class FirewallingSecurityManager extends SecurityManager {
  private SecurityManager parent;
  private PermissionCollection permissionCollection;
  private static final String PERMISSION_VALUE = "accept,listen,connect,resolve";
  private static final String LOCALHOST = "localhost";
  private static final String LOCALHOST_IP = "127.0.0.1";
  private static final String LOCALHOST_LOCALDOMAIN = "localhost.localdomain";

  public FirewallingSecurityManager() {
    parent = System.getSecurityManager();

    final SocketPermission localHost = new SocketPermission( LOCALHOST, PERMISSION_VALUE );
    final SocketPermission localHostRaw = new SocketPermission( LOCALHOST_IP, PERMISSION_VALUE );
    final SocketPermission localHostFqn = new SocketPermission( LOCALHOST_LOCALDOMAIN, PERMISSION_VALUE );
    permissionCollection = localHost.newPermissionCollection();
    permissionCollection.add( localHost );
    permissionCollection.add( localHostRaw );
    permissionCollection.add( localHostFqn );
  }

  /**
   * Throws a <code>SecurityException</code> if the requested access, specified by the given permission, is not
   * permitted based on the security policy currently in effect.
   * <p/>
   * This method calls <code>AccessController.checkPermission</code> with the given permission.
   *
   * @param perm the requested permission.
   * @throws SecurityException    if access is not permitted based on the current security policy.
   * @throws NullPointerException if the permission argument is <code>null</code>.
   * @since 1.2
   */
  public void checkPermission( final Permission perm ) {
    if ( WorkspaceSettings.getInstance().isOfflineMode() && perm instanceof SocketPermission ) {
      if ( permissionCollection.implies( perm ) == false ) {
        throw new SecurityException( UtilMessages.getInstance().getString( "FirewallingProxySelector.FilterMessage" ) );
      }
    }
    if ( parent != null ) {
      parent.checkPermission( perm );
    }
  }

  /**
   * Throws a <code>SecurityException</code> if the specified security context is denied access to the resource
   * specified by the given permission. The context must be a security context returned by a previous call to
   * <code>getSecurityContext</code> and the access control decision is based upon the configured security policy for
   * that security context.
   * <p/>
   * If <code>context</code> is an instance of <code>AccessControlContext</code> then the
   * <code>AccessControlContext.checkPermission</code> method is invoked with the specified permission.
   * <p/>
   * If <code>context</code> is not an instance of <code>AccessControlContext</code> then a
   * <code>SecurityException</code> is thrown.
   *
   * @param perm    the specified permission
   * @param context a system-dependent security context.
   * @throws SecurityException    if the specified security context is not an instance of
   * <code>AccessControlContext</code>
   *                              (e.g., is <code>null</code>), or is denied access to the resource specified by the
   *                              given permission.
   * @throws NullPointerException if the permission argument is <code>null</code>.
   * @see SecurityManager#getSecurityContext()
   * @see java.security.AccessControlContext#checkPermission(java.security.Permission)
   * @since 1.2
   */
  public void checkPermission( final Permission perm, final Object context ) {
    if ( WorkspaceSettings.getInstance().isOfflineMode() && perm instanceof SocketPermission ) {
      if ( permissionCollection.implies( perm ) == false ) {
        throw new SecurityException( UtilMessages.getInstance().getString( "FirewallingProxySelector.FilterMessage" ) );
      }
    }
    if ( parent != null ) {
      parent.checkPermission( perm, context );
    }
  }
}
