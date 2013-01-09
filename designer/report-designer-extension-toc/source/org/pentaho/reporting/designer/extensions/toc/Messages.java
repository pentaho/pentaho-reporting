package org.pentaho.reporting.designer.extensions.toc;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static Messages instance;

  public static synchronized Messages getInstance()
  {
    if (instance == null)
    {
      instance = new Messages(Locale.getDefault());
    }
    return instance;
  }
  /**
   * Creates a new instance.
   */
  public Messages(final Locale locale)
  {
    super(locale, "org.pentaho.reporting.designer.extensions.toc.messages",  // NON-NLS
        ObjectUtilities.getClassLoader(Messages.class));
  }
}
