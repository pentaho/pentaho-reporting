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

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.writer;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailDefinition;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;

public class MailDefinitionBundleWriterTest {

  private MailDefinitionBundleWriter writer = new MailDefinitionBundleWriter();

  @Test( expected = NullPointerException.class )
  public void testWriteMailDefinitionWithoutDocument() {
    WriteableDocumentBundle documentBundle = null;
    MailDefinition report = null;
    DocumentBundle globalBundle = null;

    writer.writeMailDefinition( documentBundle, report, globalBundle );
  }

  @Test( expected = NullPointerException.class )
  public void testWriteMailDefinitionWithoutReport() {
    WriteableDocumentBundle documentBundle = mock( WriteableDocumentBundle.class );
    MailDefinition report = null;
    DocumentBundle globalBundle = null;

    writer.writeMailDefinition( documentBundle, report, globalBundle );
  }

  @Test( expected = NullPointerException.class )
  public void testWriteMailDefinitionWithoutBundle() {
    WriteableDocumentBundle documentBundle = mock( WriteableDocumentBundle.class );
    MailDefinition report = mock( MailDefinition.class );
    DocumentBundle globalBundle = null;

    writer.writeMailDefinition( documentBundle, report, globalBundle );
  }

  @Test
  public void testWriteMailDefinition() {
    WriteableDocumentBundle documentBundle = mock( WriteableDocumentBundle.class );
    MailDefinition report = mock( MailDefinition.class );
    DocumentBundle globalBundle = mock( DocumentBundle.class );
    WriteableDocumentMetaData data = mock( WriteableDocumentMetaData.class );
    doReturn( data ).when( documentBundle ).getWriteableDocumentMetaData();

    writer.writeMailDefinition( documentBundle, report, globalBundle );

    verify( data ).setBundleType( "application/vnd.pentaho.reporting.mailer-definition" );
  }
}
