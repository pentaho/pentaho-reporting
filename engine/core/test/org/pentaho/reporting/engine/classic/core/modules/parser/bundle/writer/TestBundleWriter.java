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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoder;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoderRegistry;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA. User: dkincade Date: Jun 24, 2009 Time: 4:35:18 PM To change this template use File |
 * Settings | File Templates.
 */
public class TestBundleWriter extends TestCase {
  private static final String mimeType = "image/png";

  public TestBundleWriter() {
  }

  public TestBundleWriter( String s ) {
    super( s );
  }


  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWriteReportToZipFileWithImage() throws Exception {
    final MasterReport report = new MasterReport();
    report.setName( "Write Report To Zip File With Image" );

    // Create the image element
    final ContentElementFactory factory = new ContentElementFactory();
    factory.setName( "T1" );
    factory.setAbsolutePosition( new Point2D.Float( 0, 0 ) );
    factory.setMinimumSize( new FloatDimension( 150, 12 ) );
    factory.setHorizontalAlignment( ElementAlignment.MIDDLE );
    factory.setVerticalAlignment( ElementAlignment.MIDDLE );
    final Element element = factory.createElement();
    assertNotNull( element );

    // Get a binary version of an image
    final byte[] image = createPngImage();
    assertTrue( image.length > 0 );

    // Create the factory parameters for the image resource
    final String source = "/tmp/image.png";
    final String pattern = "resources/image" + IOUtils.getInstance().getFileExtension( source );
    final Map parameters = new HashMap();
    parameters.put( ClassicEngineFactoryParameters.ORIGINAL_VALUE, source );
    parameters.put( ClassicEngineFactoryParameters.MIME_TYPE, mimeType );
    parameters.put( ClassicEngineFactoryParameters.PATTERN, pattern );
    parameters.put( ClassicEngineFactoryParameters.EMBED, "true" );

    // Add the image as the value attribute
    final ResourceManager resourceManager = report.getResourceManager();
    final ResourceKey imageResourceKey = resourceManager.createKey( image, parameters );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, imageResourceKey );

    // Add the element to the report
    report.getPageHeader().addElement( element );

    // Write the output file
    final File tempFile = File.createTempFile( "tst", ".prpt" );
    tempFile.deleteOnExit();
    BundleWriter.writeReportToZipFile( report, tempFile );
    assertTrue( tempFile.exists() );

    // Load the zip file (prpt) and make sure the image is there
    final ZipFile fileToTest = new ZipFile( tempFile );
    final ZipEntry imageFile = fileToTest.getEntry( pattern );
    assertNotNull( imageFile );

    // Make sure the file's contents are the same
    final InputStream in = fileToTest.getInputStream( imageFile );
    assertNotNull( in );
    final byte[] testImage = new byte[ image.length ];
    assertEquals( image.length, in.read( testImage ) );
    assertEquals( -1, in.read() );
    in.close();
    assertTrue( Arrays.equals( testImage, image ) );

    // Since the image was put in the page header, the serialized resource key should be in the styles.xml file
    final ZipEntry styleXmlZipEntry = fileToTest.getEntry( "styles.xml" );
    assertNotNull( styleXmlZipEntry );
    final InputStream in2 = fileToTest.getInputStream( styleXmlZipEntry );
    assertNotNull( in2 );
    final BufferedReader styleXmlReader = new BufferedReader( new InputStreamReader( in2 ) );

    boolean found = false;
    String line = null;
    while ( ( line = styleXmlReader.readLine() ) != null ) {
      if ( line.indexOf( pattern ) != -1 ) {
        found = true;
        break;
      }
    }
    assertTrue( found );
  }

  private byte[] createPngImage() throws UnsupportedEncoderException, IOException {
    final ImageEncoder imageEncoder = ImageEncoderRegistry.getInstance().createEncoder( mimeType );
    assertNotNull( imageEncoder );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    imageEncoder.encodeImage( new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB ), bout, 0.75f, false );
    return bout.toByteArray();
  }
}
