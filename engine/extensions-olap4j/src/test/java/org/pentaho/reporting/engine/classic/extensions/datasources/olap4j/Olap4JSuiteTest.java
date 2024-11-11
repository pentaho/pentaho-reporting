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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses( value = { BandedMDXTableModelT.class, BandedOlap4JDriverT.class, BandedOlap4JJndiT.class,
  DenormalizedOlap4JDriverT.class, DenormalizedOlap4JJndiT.class, LegacyBandedOlap4JDriverT.class,
  LegacyBandedOlap4JJndiT.class,
  Olap4JDataFactoryWriteT.class, Prd5276T.class } )
public class Olap4JSuiteTest {
}
