package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class ColorChooserMessages extends ResourceBundleSupport
{
  private static ColorChooserMessages instance;

  public static synchronized ColorChooserMessages getInstance()
  {
    if (instance == null)
    {
      instance = new ColorChooserMessages(Locale.getDefault());
    }
    return instance;
  }
  
  /**
   * Creates a new instance.
   */
  public ColorChooserMessages(final Locale locale)
  {
    super(locale, "org.pentaho.reporting.libraries.designtime.swing.colorchooser.messages",  // NON-NLS
        ObjectUtilities.getClassLoader(ColorChooserMessages.class));
  }
}
