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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.Graphics2DPageableModule;
import org.pentaho.reporting.libraries.repository.ContentIOException;

public class MailerTest extends TestCase
{
  public MailerTest()
  {
  }

  public MailerTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmptyBodyReport() throws ContentIOException, ReportProcessingException, MessagingException, IOException
  {
    final MailDefinition mdef = new MailDefinition(Graphics2DPageableModule.GRAPHICS_EXPORT_TYPE, new MasterReport());
    final MimeMessage message = MailProcessor.createReport(mdef, null);
    assertNotNull(message);
  }

  public void testEmptyBodyReportBursting() throws ContentIOException, ReportProcessingException, MessagingException, IOException
  {
    final MailDefinition mdef = new MailDefinition(Graphics2DPageableModule.GRAPHICS_EXPORT_TYPE, new MasterReport());
    MailProcessor.performBursting(mdef);
  }
}
