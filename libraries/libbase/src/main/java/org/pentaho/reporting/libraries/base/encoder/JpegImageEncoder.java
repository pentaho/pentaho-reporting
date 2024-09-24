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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base.encoder;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JpegImageEncoder implements ImageEncoder {
  private static final String ENCODER_CLASS = "com.sun.image.codec.jpeg.JPEGCodec";
  private static final String ENCODER_PARAM_CLASS = "com.sun.image.codec.jpeg.JPEGEncodeParam";

  public JpegImageEncoder() {
  }

  public void encodeImage( final Image image,
                           final OutputStream outputStream,
                           final float quality,
                           final boolean encodeAlpha ) throws IOException, UnsupportedEncoderException {
    final BufferedImage bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ),
      BufferedImage.TYPE_INT_RGB );
    final Graphics g = bimage.createGraphics();
    g.drawImage( image, 0, 0, Color.WHITE, null );
    g.dispose();


    //// This is what we try to do via reflection. Yes, reflection is ugly, but it guarantees that
    //// we dont run into strange errors just because a non-Sun-JDK is used.
    //
    //    final JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bimage);
    //    jep.setQuality(quality, false);
    //    final JPEGImageEncoder jpegImageEncoder = JPEGCodec.createJPEGEncoder(outputStream, jep);
    //    jpegImageEncoder.encode(bimage);

    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( JpegImageEncoder.class );
      final Class codecClass = Class.forName( ENCODER_CLASS, false, loader );
      final Class paramClass = Class.forName( ENCODER_PARAM_CLASS, false, loader );
      final Method createParameterMethod = codecClass.getMethod
        ( "getDefaultJPEGEncodeParam", new Class[] { BufferedImage.class } );
      final Object encoderParam = createParameterMethod.invoke( null, new Object[] { bimage } );
      final Method setQualityMethod = paramClass.getMethod
        ( "setQuality", new Class[] { Float.TYPE, Boolean.TYPE } );
      setQualityMethod.invoke( encoderParam, new Object[] { new Float( quality ), Boolean.FALSE } );

      final Method createEncoderMethod = codecClass.getMethod
        ( "createJPEGEncoder", new Class[] { OutputStream.class, paramClass } );

      final Object encoder = createEncoderMethod.invoke( null, new Object[] { outputStream, encoderParam } );
      final Class encoderClass = encoder.getClass();
      final Method encodeMethod = encoderClass.getMethod( "encode", new Class[] { BufferedImage.class } );
      encodeMethod.invoke( encoder, new Object[] { bimage } );
    } catch ( InvocationTargetException ie ) {
      final Throwable throwable = ie.getTargetException();
      if ( throwable instanceof IOException ) {
        // Yeah, it is ugly, but the use of reflection hides the exception..
        throw (IOException) throwable;
      }
      // ignore the throwable ..
      throw new UnsupportedEncoderException( "Failed to run the encoder", throwable );
    } catch ( Throwable t ) {
      // ignore the throwable ..
      throw new UnsupportedEncoderException( "Failed to run the encoder", t );
    }

  }

  public void encodeImage( final Image image,
                           final OutputStream outputStream ) throws IOException, UnsupportedEncoderException {

    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( JpegImageEncoder.class );
      final Class codecClass = Class.forName( ENCODER_CLASS, false, loader );
      final Method createEncoderMethod =
        codecClass.getMethod( "createJPEGEncoder", new Class[] { OutputStream.class } );
      final Object encoder = createEncoderMethod.invoke( null, new Object[] { outputStream } );
      final Class encoderClass = encoder.getClass();
      final Method encodeMethod = encoderClass.getMethod( "encode", new Class[] { Image.class } );
      encodeMethod.invoke( encoder, new Object[] { image } );
    } catch ( InvocationTargetException ie ) {
      final Throwable throwable = ie.getTargetException();
      if ( throwable instanceof IOException ) {
        // Yeah, it is ugly, but the use of reflection hides the exception..
        throw (IOException) throwable;
      }
      // ignore the throwable ..
      throw new UnsupportedEncoderException( "Failed to run the encoder", throwable );
    } catch ( Throwable t ) {
      // ignore the throwable ..
      throw new UnsupportedEncoderException( "Failed to run the encoder", t );
    }
  }

  public String getMimeType() {
    return "image/jpg";
  }

  public static boolean isJpegEncodingAvailable() {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( JpegImageEncoder.class );
      final Class aClass = Class.forName( ENCODER_CLASS, false, loader );
      return aClass != null;
    } catch ( Throwable t ) {
      // ignore the throwable ..
      return false;
    }
  }
}
