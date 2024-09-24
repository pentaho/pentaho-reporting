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

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.libraries.resourceloader.LoaderParameterKey;

/**
 * Container class for the resource key factory parameters that the reporting engine has knowledge of.
 */
public class ClassicEngineFactoryParameters {
  public static final LoaderParameterKey EMBED = new LoaderParameterKey( "designtime::embedded" );
  public static final LoaderParameterKey ORIGINAL_VALUE = new LoaderParameterKey( "designtime::original_value" );
  public static final LoaderParameterKey PATTERN = new LoaderParameterKey( "designtime::pattern" );
  public static final LoaderParameterKey MIME_TYPE = new LoaderParameterKey( "designtime::mime_type" );
}
