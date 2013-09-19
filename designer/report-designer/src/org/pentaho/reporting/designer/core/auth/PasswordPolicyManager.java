package org.pentaho.reporting.designer.core.auth;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class PasswordPolicyManager
{
  private static PasswordPolicyManager instance;

  public static synchronized PasswordPolicyManager getInstance()
  {
    if (instance == null)
    {
      instance = new PasswordPolicyManager();
    }
    return instance;
  }

  private Preferences properties;
  private LinkedHashSet<String> hosts;

  private PasswordPolicyManager()
  {
    final Preferences preferences = Preferences.userNodeForPackage(PasswordPolicyManager.class);
    properties = preferences.node("PasswordPolicy"); // NON-NLS

    hosts = new LinkedHashSet<String>();
    try
    {
      hosts.addAll(getFilesAsList());
    }
    catch (BackingStoreException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
    }
  }

  public boolean isPasswordStoringAllowed(final String url)
  {
    if (WorkspaceSettings.getInstance().isRememberPasswords() == false)
    {
      return false;
    }

    // check the blacklist for urls to NOT store passwords for
    // if it is in the list, return false. otherwise return true.
    return !hosts.contains(url);
  }

  public void setPasswordStoringAllowed(final String url, final boolean allowed)
  {
    if (!allowed)
    {
      if (hosts.add(url))
      {
        try
        {
          store(hosts);
        }
        catch (BackingStoreException e)
        {
          UncaughtExceptionsModel.getInstance().addException(e);
        }
      }
    }
    else
    {
      if (hosts.remove(url))
      {
        try
        {
          store(hosts);
        }
        catch (BackingStoreException e)
        {
          UncaughtExceptionsModel.getInstance().addException(e);
        }
      }
    }
  }

  public String[] getManagedHosts()
  {
    return hosts.toArray(new String[hosts.size()]);
  }

  private LinkedHashSet<String> getFilesAsList()
      throws BackingStoreException
  {
    final LinkedHashSet<String> retval = new LinkedHashSet<String>();
    final String[] strings = properties.keys();
    final int maxFiles = Math.min(10, strings.length);
    for (int i = 0; i < maxFiles; i++)
    {
      final String key = strings[i];
      final String file = properties.get(key, null);

      if (file == null)
      {
        continue;
      }
      retval.add(file);
    }

    if (retval.size() != strings.length)
    {
      // store ..
      store(retval);
    }
    return retval;
  }

  private void store(final Collection<String> files) throws BackingStoreException
  {
    properties.clear();
    final String[] strings = files.toArray(new String[files.size()]);
    for (int i = 0; i < strings.length; i++)
    {
      final String string = strings[i];
      properties.put(String.valueOf(i), string);
    }
    properties.flush();
  }

}
