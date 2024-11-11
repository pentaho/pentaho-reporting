/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
