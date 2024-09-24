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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

public class KettleTransformationProducerReadHandlerFactory
  extends AbstractReadHandlerFactory<KettleTransformationProducerReadHandler> {
  private static final String PREFIX_SELECTOR =
    "org.pentaho.reporting.engine.classic.extensions.datasources.kettle.transformation-file-producer-prefix.";

  private static KettleTransformationProducerReadHandlerFactory readHandlerFactory;

  public KettleTransformationProducerReadHandlerFactory() {
  }

  protected Class<KettleTransformationProducerReadHandler> getTargetClass() {
    return KettleTransformationProducerReadHandler.class;
  }

  public static synchronized KettleTransformationProducerReadHandlerFactory getInstance() {
    if ( readHandlerFactory == null ) {
      readHandlerFactory = new KettleTransformationProducerReadHandlerFactory();
      readHandlerFactory.configureGlobal( ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR );
    }
    return readHandlerFactory;
  }

}
