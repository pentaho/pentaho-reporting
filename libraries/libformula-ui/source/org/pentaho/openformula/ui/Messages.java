package org.pentaho.openformula.ui;

import java.util.HashMap;
import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static final String BUNDLENAME = "org.pentaho.openformula.ui.messages"; //$NON-NLS-1$
  private static HashMap<Locale, Messages> locales;

  public static Messages getInstance()
  {
    return getInstance(Locale.getDefault());
  }

  public static synchronized Messages getInstance(final Locale locale)
  {
    if (locales == null)
    {
      locales = new HashMap<Locale, Messages>();
      final Messages retval = new Messages(locale, BUNDLENAME);
      locales.put(locale, retval);
      return retval;
    }

    final Messages o = locales.get(locale);
    if (o != null)
    {
      return o;
    }

    final Messages retval = new Messages(locale, BUNDLENAME);
    locales.put(locale, retval);
    return retval;
  }

  private Messages(final Locale locale, final String s)
  {
    super(locale, s, ObjectUtilities.getClassLoader(Messages.class));
  }
}
