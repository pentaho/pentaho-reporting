package org.pentaho.reporting.designer.extensions.pentaho.repository;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static Messages instance;
  /**
   * Creates a new instance.
   */
  private Messages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.extensions.pentaho.repository.messages",
        ObjectUtilities.getClassLoader(Messages.class));
  }

  public static synchronized Messages getInstance()
  {
    if (instance == null)
    {
      instance = new Messages();
    }
    return instance;
  }
}
