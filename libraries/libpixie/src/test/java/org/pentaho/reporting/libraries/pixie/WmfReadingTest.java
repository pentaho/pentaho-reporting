package org.pentaho.reporting.libraries.pixie;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class WmfReadingTest extends TestCase {
  public WmfReadingTest() {
  }

  public void testReadingDoesNotCrash() throws IOException {
    InputStream resource = WmfReadingTest.class.getResourceAsStream( "anim0002.wmf" );
    assertNotNull( resource );
    WmfFile wmfFile = new WmfFile( resource, 800, 600 );
    BufferedImage bi = new BufferedImage( 800, 600, BufferedImage.TYPE_4BYTE_ABGR );
    Graphics2D graphics = bi.createGraphics();
    wmfFile.draw( graphics, new Rectangle2D.Double( 0, 0, 800, 600 ) );
    graphics.dispose();
  }
}
