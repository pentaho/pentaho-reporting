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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.nogui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.MessageFormat;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource.OpenSourceProjects;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.PngEncoder;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * A demonstration that shows how to generate a report and save it to PDF without displaying the print preview or the
 * PDF save-as dialog.
 *
 * @author David Gilbert
 */
public class StraightToPNG
{
  private static final Log logger = LogFactory.getLog(StraightToPNG.class);

  /**
   * Creates a new demo application.
   *
   * @param filename the output filename.
   * @throws ParseException if the report could not be parsed.
   */
  public StraightToPNG(final String filename)
      throws ParseException
  {
    final URL in = ObjectUtilities.getResource
        ("org/pentaho/reporting/engine/classic/demo/opensource/opensource.xml", StraightToPNG.class);
    final MasterReport report = parseReport(in);
    final TableModel data = new OpenSourceProjects();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    final long startTime = System.currentTimeMillis();
    savePNG(report, filename);
  }

  /**
   * Reads the report from the specified template file.
   *
   * @param templateURL the template location.
   * @return a report.
   * @throws ParseException if the report could not be parsed.
   */
  private MasterReport parseReport(final URL templateURL)
      throws ParseException
  {
    try
    {
      final ResourceManager mgr = new ResourceManager();
      final Resource resource = mgr.createDirectly(templateURL, MasterReport.class);
      final MasterReport report = (MasterReport) resource.getResource();
      // this demo adds the image at runtime just to show how this could be
      // done. Usually such images get referenced from the XML itself without
      // using manual coding.
      final URL imageURL = ObjectUtilities.getResource
          ("org/pentaho/reporting/engine/classic/demo/opensource/gorilla.jpg", StraightToPNG.class);
      final Image image = Toolkit.getDefaultToolkit().createImage(imageURL);
      final WaitingImageObserver obs = new WaitingImageObserver(image);
      obs.waitImageLoaded();
      report.getParameterValues().put("logo", image);
      return report;
    }
    catch (Exception e)
    {
      throw new ParseException("Failed to parse the report", e);
    }
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report   the report.
   * @param fileName target file name.
   * @return true or false.
   */
  public boolean savePNG(final MasterReport report, final String fileName)
  {
    try
    {
      final PrintReportProcessor prc = new PrintReportProcessor(report);
      final int numberOfPages = prc.getNumberOfPages();
      for (int i = 0; i < numberOfPages; i++)
      {
        final String fileNameFormated =
            MessageFormat.format(fileName, new Object[]{new Integer(i)});
        final BufferedImage image = createImage(report.getPageDefinition());

        final Rectangle rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        // prepare the image by filling it ...
        final Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.white);
        g2.fill(rect);

        final PageDrawable pageDrawable = prc.getPageDrawable(i);
        pageDrawable.draw(g2, rect);
        g2.dispose();

        // convert to PNG ...
        final PngEncoder encoder = new PngEncoder(image, true, 0, 9);
        final byte[] data = encoder.pngEncode();

        final BufferedOutputStream out = new BufferedOutputStream
            (new FileOutputStream(fileNameFormated));
        out.write(data);
        out.close();
      }
      return true;
    }
    catch (Exception e)
    {
      System.err.println("Writing PDF failed.");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Create the empty image for the given page size.
   *
   * @param pd the page definition that defines the image bounds.
   * @return the generated image.
   */
  private BufferedImage createImage(final PageDefinition pd)
  {
    // in this simple case we know, that all pages have the same size..
    final PageFormat pf = pd.getPageFormat(0);

    final double width = pf.getWidth();
    final double height = pf.getHeight();
    //write the report to the temp file
    return new BufferedImage
        ((int) width, (int) height, BufferedImage.TYPE_BYTE_INDEXED);
  }

  /**
   * Demo starting point.
   *
   * @param args ignored.
   */
  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    try
    {
      //final StraightToPDF demo =
      new StraightToPNG(System.getProperty("user.home") + "/OpenSource-Demo-{0}.png");
      System.exit(0);
    }
    catch (Exception e)
    {
      logger.error("Failed to run demo", e);
      System.exit(1);
    }
  }

}
