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


package org.pentaho.reporting.designer.core.editor;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;

public class ResourceLoaderListenerTest extends TestCase {
  public ResourceLoaderListenerTest() {
  }

  public ResourceLoaderListenerTest( String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmbedDocument() {
    final MasterReport report = new MasterReport();
    final ResourceLoaderListener listener = new ResourceLoaderListener( report, report );

    final PageHeader pageHeader = report.getPageHeader();
    final Element element = new Element();
    pageHeader.addElement( element );


  }
}
