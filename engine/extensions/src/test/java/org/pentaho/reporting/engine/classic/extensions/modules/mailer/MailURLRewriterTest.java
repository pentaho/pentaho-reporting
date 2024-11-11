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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.libraries.repository.ContentEntity;

public class MailURLRewriterTest {

  private static final String TEST_URL = "test_url_str";

  private MailURLRewriter rewriter = new MailURLRewriter();

  @Test
  public void tesRrewrite() throws Exception {
    ContentEntity sourceDocument = mock( ContentEntity.class );
    ContentEntity dataEntity = mock( ContentEntity.class );
    doReturn( TEST_URL ).when( dataEntity ).getName();

    String result = rewriter.rewrite( sourceDocument, dataEntity );
    assertThat( result, is( equalTo( "cid:" + TEST_URL ) ) );
  }
}
