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
import org.pentaho.reporting.libraries.base.util.PngEncoder;

import java.util.HashMap;

public class ImageEncoderRegistry {
  public static final String IMAGE_PNG = "image/png";
  public static final String IMAGE_JPEG = "image/jpeg";
  private static ImageEncoderRegistry instance;
  private HashMap<String, String> encoders;

  private ImageEncoderRegistry() {
    encoders = new HashMap<String, String>();
  }

  public static synchronized ImageEncoderRegistry getInstance() {
    if ( instance == null ) {
      instance = new ImageEncoderRegistry();
      instance.registerDefaults();
    }
    return instance;
  }

  private void registerDefaults() {
    encoders.put( IMAGE_PNG, PngEncoder.class.getName() );
    if ( JpegImageEncoder.isJpegEncodingAvailable() ) {
      encoders.put( IMAGE_JPEG, JpegImageEncoder.class.getName() );
      encoders.put( "image/jpg", JpegImageEncoder.class.getName() );
    }
  }

  public void addEncoder( final String mimeType, final String encoderClass ) {
    if ( mimeType == null ) {
      throw new NullPointerException();
    }
    if ( encoderClass == null ) {
      throw new NullPointerException();
    }
    encoders.put( mimeType, encoderClass );
  }

  public boolean isEncoderAvailable( final String mimeType ) {
    return encoders.containsKey( mimeType );
  }

  public String[] getRegisteredEncoders() {
    return encoders.keySet().toArray( new String[ encoders.size() ] );
  }

  public ImageEncoder createEncoder( final String mimeType ) throws UnsupportedEncoderException {
    final Object o = encoders.get( mimeType );
    if ( o == null ) {
      throw new UnsupportedEncoderException( "No encoder for mime-type " + mimeType );
    }
    final ImageEncoder imageEncoder =
      ObjectUtilities.loadAndInstantiate( (String) o, ImageEncoderRegistry.class, ImageEncoder.class );
    if ( imageEncoder == null ) {
      throw new UnsupportedEncoderException( "No encoder for mime-type " + mimeType );
    }
    return imageEncoder;
  }
}
