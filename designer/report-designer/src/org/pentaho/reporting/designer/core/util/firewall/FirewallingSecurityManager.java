package org.pentaho.reporting.designer.core.util.firewall;

import java.net.SocketPermission;
import java.security.Permission;
import java.security.PermissionCollection;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.UtilMessages;

/**
 * Todo: Document me!
 * <p/>
 * Date: 12.11.2009
 * Time: 17:33:12
 *
 * @author Thomas Morgner.
 */
public class FirewallingSecurityManager extends SecurityManager
{
  private SecurityManager parent;
  private PermissionCollection permissionCollection;
  private static final String PERMISSION_VALUE = "accept,listen,connect,resolve";
  private static final String LOCALHOST = "localhost";
  private static final String LOCALHOST_IP = "127.0.0.1";
  private static final String LOCALHOST_LOCALDOMAIN = "localhost.localdomain";

  public FirewallingSecurityManager()
  {
    parent = System.getSecurityManager();

    final SocketPermission localHost = new SocketPermission(LOCALHOST, PERMISSION_VALUE);
    final SocketPermission localHostRaw = new SocketPermission(LOCALHOST_IP, PERMISSION_VALUE);
    final SocketPermission localHostFqn = new SocketPermission(LOCALHOST_LOCALDOMAIN, PERMISSION_VALUE);
    permissionCollection = localHost.newPermissionCollection();
    permissionCollection.add(localHost);
    permissionCollection.add(localHostRaw);
    permissionCollection.add(localHostFqn);
  }

  /**
   * Throws a <code>SecurityException</code> if the requested
   * access, specified by the given permission, is not permitted based
   * on the security policy currently in effect.
   * <p/>
   * This method calls <code>AccessController.checkPermission</code>
   * with the given permission.
   *
   * @param perm the requested permission.
   * @throws SecurityException    if access is not permitted based on
   *                              the current security policy.
   * @throws NullPointerException if the permission argument is
   *                              <code>null</code>.
   * @since 1.2
   */
  public void checkPermission(final Permission perm)
  {
    if (WorkspaceSettings.getInstance().isOfflineMode() && perm instanceof SocketPermission)
    {
      if (permissionCollection.implies(perm) == false)
      {
        throw new SecurityException(UtilMessages.getInstance().getString("FirewallingProxySelector.FilterMessage"));
      }
    }
    if (parent != null)
    {
      parent.checkPermission(perm);
    }
  }

  /**
   * Throws a <code>SecurityException</code> if the
   * specified security context is denied access to the resource
   * specified by the given permission.
   * The context must be a security
   * context returned by a previous call to
   * <code>getSecurityContext</code> and the access control
   * decision is based upon the configured security policy for
   * that security context.
   * <p/>
   * If <code>context</code> is an instance of
   * <code>AccessControlContext</code> then the
   * <code>AccessControlContext.checkPermission</code> method is
   * invoked with the specified permission.
   * <p/>
   * If <code>context</code> is not an instance of
   * <code>AccessControlContext</code> then a
   * <code>SecurityException</code> is thrown.
   *
   * @param perm    the specified permission
   * @param context a system-dependent security context.
   * @throws SecurityException    if the specified security context
   *                              is not an instance of <code>AccessControlContext</code>
   *                              (e.g., is <code>null</code>), or is denied access to the
   *                              resource specified by the given permission.
   * @throws NullPointerException if the permission argument is
   *                              <code>null</code>.
   * @see SecurityManager#getSecurityContext()
   * @see java.security.AccessControlContext#checkPermission(java.security.Permission)
   * @since 1.2
   */
  public void checkPermission(final Permission perm, final Object context)
  {
    if (WorkspaceSettings.getInstance().isOfflineMode() && perm instanceof SocketPermission)
    {
      if (permissionCollection.implies(perm) == false)
      {
        throw new SecurityException(UtilMessages.getInstance().getString("FirewallingProxySelector.FilterMessage"));
      }
    }
    if (parent != null)
    {
      parent.checkPermission(perm, context);
    }
  }
}
