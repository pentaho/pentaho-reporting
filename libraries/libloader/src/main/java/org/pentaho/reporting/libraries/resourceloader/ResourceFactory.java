/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
