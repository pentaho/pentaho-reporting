package org.pentaho.reporting.designer.core.versionchecker;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static Messages messages;

  public static Messages getInstance()
  {
    if (messages == null)
    {
      messages = new Messages();
    }
    return messages;
  }

  /**
   * Creates a new instance.
   */
  public Messages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.core.versionchecker.messages",// NON-NLS
        ObjectUtilities.getClassLoader(Messages.class));
  }
}
