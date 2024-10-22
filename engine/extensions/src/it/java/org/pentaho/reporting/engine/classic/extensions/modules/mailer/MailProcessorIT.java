///*! ******************************************************************************
// *
// * Pentaho
// *
// * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
// *
// * Use of this software is governed by the Business Source License included
// * in the LICENSE.TXT file.
// *
// * Change Date: 2029-07-20
// ******************************************************************************/
//
//
//package org.pentaho.reporting.engine.classic.extensions.modules.mailer;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.junit.Assert.assertThat;
//
//import java.io.IOException;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
//import org.pentaho.reporting.engine.classic.core.MasterReport;
//import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
//import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.Graphics2DPageableModule;
//import org.pentaho.reporting.libraries.repository.ContentIOException;
//
//public class MailProcessorIT {
//
//  @BeforeClass
//  public static void setUp() throws Exception {
//    ClassicEngineBoot.getInstance().start();
//  }
//
//  @Test
//  public void testEmptyBodyReport() throws ContentIOException, ReportProcessingException, MessagingException,
//    IOException {
//    final MailDefinition mdef = new MailDefinition( Graphics2DPageableModule.GRAPHICS_EXPORT_TYPE, new MasterReport() );
//    final MimeMessage message = MailProcessor.createReport( mdef, null );
//    assertThat( message, is( notNullValue() ) );
//  }
//
//  @Test
//  public void testEmptyBodyReportBursting() throws ContentIOException, ReportProcessingException, MessagingException,
//    IOException {
//    final MailDefinition mdef = new MailDefinition( Graphics2DPageableModule.GRAPHICS_EXPORT_TYPE, new MasterReport() );
//    MailProcessor.performBursting( mdef );
//  }
//
//}
