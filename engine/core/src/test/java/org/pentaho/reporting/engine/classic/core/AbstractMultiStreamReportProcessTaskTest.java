/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.SingleRepositoryURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriter;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.UrlRepository;
import org.pentaho.reporting.libraries.repository.dummy.DummyRepository;

public class AbstractMultiStreamReportProcessTaskTest {

  private class CheckerProcessTask extends AbstractMultiStreamReportProcessTask {

    @Override
    public String getReportMimeType() {
      return "test_mime";
    }

    @Override
    public void run() {
    }
  }

  @Test
  public void testComputeUrlRewriter() {
    ContentLocation bulkLocation = mock( ContentLocation.class );
    ContentLocation bodyContentLocation = mock( ContentLocation.class );
    NameGenerator bulkNameGenerator = mock( NameGenerator.class );
    URLRewriter urlRewriter = mock( URLRewriter.class );
    Repository bulkRepository = mock( UrlRepository.class );
    Repository bodyRepository = mock( UrlRepository.class );

    CheckerProcessTask task = new CheckerProcessTask();
    task.setBulkNameGenerator( bulkNameGenerator );
    task.setBodyContentLocation( bodyContentLocation );
    doReturn( bulkRepository ).when( bodyContentLocation ).getRepository();

    URLRewriter result = task.computeUrlRewriter();
    assertThat( result, instanceOf( SingleRepositoryURLRewriter.class ) );

    task.setBulkLocation( bulkLocation );
    doReturn( bulkRepository ).when( bulkLocation ).getRepository();
    result = task.computeUrlRewriter();
    assertThat( result, instanceOf( SingleRepositoryURLRewriter.class ) );

    doReturn( bodyRepository ).when( bodyContentLocation ).getRepository();
    result = task.computeUrlRewriter();
    assertThat( result, instanceOf( FileSystemURLRewriter.class ) );

    doReturn( mock( DummyRepository.class ) ).when( bulkLocation ).getRepository();
    result = task.computeUrlRewriter();
    assertThat( result, instanceOf( SingleRepositoryURLRewriter.class ) );

    doReturn( bulkRepository ).when( bulkLocation ).getRepository();
    doReturn( mock( DummyRepository.class ) ).when( bodyContentLocation ).getRepository();
    result = task.computeUrlRewriter();
    assertThat( result, instanceOf( SingleRepositoryURLRewriter.class ) );

    task.setUrlRewriter( urlRewriter );
    result = task.computeUrlRewriter();
    assertThat( result, is( equalTo( urlRewriter ) ) );
  }

  @Test
  public void testIsTaskAborted() {
    CheckerProcessTask task = new CheckerProcessTask();
    task.setError( null );
    assertThat( task.isTaskAborted(), is( equalTo( false ) ) );

    task.setError( new NullPointerException() );
    assertThat( task.isTaskAborted(), is( equalTo( false ) ) );

    task.setError( new InterruptedException() );
    assertThat( task.isTaskAborted(), is( equalTo( true ) ) );
  }

  @Test
  public void testIsTaskSuccessful() {
    CheckerProcessTask task = new CheckerProcessTask();
    task.setError( null );
    assertThat( task.isTaskSuccessful(), is( equalTo( true ) ) );

    task.setError( new NullPointerException() );
    assertThat( task.isTaskAborted(), is( equalTo( false ) ) );
  }

  @Test
  public void testIsValid() {
    CheckerProcessTask task = new CheckerProcessTask();
    task.setReport( null );
    task.setBodyContentLocation( null );
    task.setBodyNameGenerator( null );
    assertThat( task.isValid(), is( equalTo( false ) ) );

    task.setReport( mock( MasterReport.class ) );
    task.setBodyContentLocation( mock( ContentLocation.class ) );
    task.setBodyNameGenerator( mock( NameGenerator.class ) );
    assertThat( task.isValid(), is( equalTo( true ) ) );
  }
}
