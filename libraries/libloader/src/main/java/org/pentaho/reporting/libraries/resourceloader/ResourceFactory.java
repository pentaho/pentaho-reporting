/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

public interface ResourceFactory {
  public static final String CONFIG_PREFIX = "org.pentaho.reporting.libraries.resourceloader.factory.modules.";

  /**
   * Creates a resource by interpreting the data given in the resource-data object. If additional datastreams need to be
   * parsed, the provided resource manager should be used.
   *
   * @param manager the resource manager used for all resource loading.
   * @param data    the resource-data from where the binary data is read.
   * @param context the resource context used to resolve relative resource paths.
   * @return the parsed result, never null.
   * @throws ResourceCreationException if the resource could not be parsed due to syntaxctial or logical errors in the
   *                                   data.
   * @throws ResourceLoadingException  if the resource could not be accessed from the physical storage.
   */
  public Resource create( final ResourceManager manager,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException;

  /**
   * Returns the expected result type.
   *
   * @return the result type.
   */
  public Class getFactoryType();

  /**
   * Initializes the resource factory. This usually loads all system resources from the environment and maybe sets up
   * and initializes any factories needed during the parsing.
   */
  public void initializeDefaults();
}
