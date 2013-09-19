package org.pentaho.reporting.libraries.formula;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  /**
   * Creates a new instance.
   */
  public Messages(final Locale locale)
  {
    super(locale, "org.pentaho.reporting.libraries.formula.messages",  // NON-NLS
        ObjectUtilities.getClassLoader(Messages.class));
  }
}
