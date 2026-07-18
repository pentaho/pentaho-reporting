/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.modules.misc.referencedoc;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * An application that generates reports that document properties of the JFreeReport extended parser.
 *
 * @author Thomas Morgner.
 */
public class ReferenceDocGenerator {
  /**
   * DefaultConstructor.
   */
  protected ReferenceDocGenerator() {
  }

  /**
   * The starting point for the application.
   *
   * @param args
   *          command line arguments.
   */
  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    StyleKeyReferenceGenerator.main( args );
    ObjectReferenceGenerator.main( args );
    DataSourceReferenceGenerator.main( args );
  }
}
