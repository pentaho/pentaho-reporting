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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.webapp.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FlowHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Generates a ZIP file that contains the report. The ZIP file is stored in the session context and a
 * selected file is served from that context.
 *
 * @author Thomas Morgner
 */
public class HtmlServlet extends HttpServlet
{
  public HtmlServlet()
  {
  }

  protected void doGet(final HttpServletRequest request,
                       final HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
      String reportName = request.getParameter("report.name");
      if (reportName == null)
      {
        reportName = generateReport(request);
        if (reportName == null)
        {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST);
          return;
        }
      }
      String fileName = request.getParameter("filename");
      if (fileName == null)
      {
        fileName = "report.html";
      }
      serveFile(request, response, reportName, fileName);
    }
    catch (ResourceException e)
    {
      log("Failed to parse report", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    catch (ReportProcessingException e)
    {
      log("Failed to process the report", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    catch (ContentIOException e)
    {
      log("Failed to process the report", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private void serveFile (final HttpServletRequest request,
                          final HttpServletResponse response,
                          final String reportName,
                          final String fileName) throws ContentIOException, IOException
  {
    final Object data = request.getSession().getAttribute("report:" + reportName);
    if (data instanceof byte[] == false)
    {
      throw new ContentIOException("No such repository");
    }
    final ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream((byte[]) data));
    ZipEntry ze = zin.getNextEntry();
    while (ze != null)
    {
      final String fullName = ze.getName();
      if (fullName.equals(fileName) == false)
      {
        zin.closeEntry();
        ze = zin.getNextEntry();
        continue;
      }

      response.setContentLength((int) ze.getSize());

      final String contextMimeType = getServletContext().getMimeType(fileName);
      if (contextMimeType != null)
      {
        response.setContentType(contextMimeType);
      }

      final ServletOutputStream outputStream = response.getOutputStream();
      IOUtils.getInstance().copyStreams(zin, outputStream);
      outputStream.flush();
    }
    zin.close();
  }


  private String generateReport(final HttpServletRequest request)
      throws IOException, ResourceException, ContentIOException, ReportProcessingException
  {
    final String reportDefinition = request.getParameter("name");
    final URL reportUrl = getServletContext().getResource(reportDefinition);
    if (reportUrl == null)
    {
      return null;
    }

    final ResourceManager resourceManager = new ResourceManager();
    final Resource resource = resourceManager.createDirectly(reportUrl, MasterReport.class);
    final MasterReport report = (MasterReport) resource.getResource();

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ZipRepository zipRepository = new ZipRepository(out);
    final ContentLocation root = zipRepository.getRoot();
    final ContentLocation data = RepositoryUtilities.createLocation
        (zipRepository, RepositoryUtilities.splitPath("data", "/"));

    final FlowHtmlOutputProcessor outputProcessor = new FlowHtmlOutputProcessor();

    final HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
    printer.setContentWriter(root, new DefaultNameGenerator(root, "report.html"));
    printer.setDataWriter(data, new DefaultNameGenerator(data, "content"));

    String urlPattern = getInitParameter("url-pattern");
    if (urlPattern == null)
    {
      final String requestURL = String.valueOf(request.getRequestURL());
      final int parameterStart = requestURL.indexOf('?');
      final String params = String.format("?report.name=%s&filename={0}",
          URLEncoder.encode(report.getTitle(), request.getCharacterEncoding()));

      if (parameterStart == -1)
      {
        urlPattern = requestURL + params;
      }
      else
      {
        urlPattern = requestURL.substring(0, parameterStart) + params;
      }
    }
    printer.setUrlRewriter(new WebAppURLRewriter(urlPattern, request.getCharacterEncoding()));
    outputProcessor.setPrinter(printer);

    final FlowReportProcessor sp = new FlowReportProcessor(report, outputProcessor);
    sp.processReport();
    sp.close();
    zipRepository.close();
    request.getSession().setAttribute("report:" + report.getTitle(), out.toByteArray());
    return report.getTitle();
  }

  protected void doPost(final HttpServletRequest httpServletRequest,
                        final HttpServletResponse httpServletResponse) throws ServletException, IOException
  {
    doGet(httpServletRequest, httpServletResponse);
  }
}