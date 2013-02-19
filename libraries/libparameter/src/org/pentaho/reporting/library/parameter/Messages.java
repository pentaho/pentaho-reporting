package org.pentaho.reporting.library.parameter;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static Messages instance;

  public static Messages getInstance()
  {
    // its ok that this one is not synchronized. I dont care whether we have multiple instances of this
    // beast sitting around, as this is a singleton for convinience reasons.
    if (instance == null)
    {
      instance = new Messages();
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  private Messages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.library.parameter.messages",
        ObjectUtilities.getClassLoader(Messages.class));
  }
}
