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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

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
