/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
