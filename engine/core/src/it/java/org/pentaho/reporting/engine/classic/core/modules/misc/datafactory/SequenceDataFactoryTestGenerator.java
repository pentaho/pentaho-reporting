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



package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import java.util.Locale;
import java.util.TimeZone;

public class SequenceDataFactoryTestGenerator {
  public static void main( String[] args ) throws Exception {
    Locale.setDefault( Locale.US );
    TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );

    final SequenceDataFactoryIT test = new SequenceDataFactoryIT();
    test.setUp();
    test.runGenerate( SequenceDataFactoryIT.QUERIES_AND_RESULTS );
  }

}
