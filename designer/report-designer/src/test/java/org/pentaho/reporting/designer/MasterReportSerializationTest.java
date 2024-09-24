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

package org.pentaho.reporting.designer;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.actions.global.NewReportAction;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MasterReportSerializationTest extends TestCase {
  public MasterReportSerializationTest() {
  }

  public MasterReportSerializationTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMasterReport() throws IOException, ClassNotFoundException {
    final MasterReport report = NewReportAction.prepareMasterReport();
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream( bout );
    oout.writeObject( report );

    final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bout.toByteArray() ) );
    final Object o = oin.readObject();
  }
}
