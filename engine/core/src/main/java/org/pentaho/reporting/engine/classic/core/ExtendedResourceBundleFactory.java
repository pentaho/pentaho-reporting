/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Locale;
import java.util.TimeZone;

public interface ExtendedResourceBundleFactory extends ResourceBundleFactory, Cloneable {
  public Object clone() throws CloneNotSupportedException;

  public void setLocale( Locale locale );

  public void setTimeZone( TimeZone timeZone );

  public void setResourceLoader( final ResourceManager resourceManager, final ResourceKey contextKey );
}
