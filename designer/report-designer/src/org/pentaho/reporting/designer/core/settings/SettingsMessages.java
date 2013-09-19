package org.pentaho.reporting.designer.core.settings;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.12.2009
 * Time: 15:14:55
 *
 * @author Thomas Morgner.
 */
public class SettingsMessages extends ResourceBundleSupport
{
  private static SettingsMessages instance;
  /**
   * Creates a new instance.
   */
  private SettingsMessages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.core.settings.messages.messages",//NON-NLS
        ObjectUtilities.getClassLoader(SettingsMessages.class));
  }

  public static synchronized SettingsMessages getInstance()
  {
    if (instance == null)
    {
      instance = new SettingsMessages();
    }
    return instance;
  }
}

