/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.output.support.itext;

import com.lowagie.text.pdf.BaseFont;
import org.pentaho.reporting.libraries.resourceloader.CompoundResource;
import org.pentaho.reporting.libraries.resourceloader.DependencyCollector;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Map;

/**
 * Creation-Date: 16.05.2006, 17:19:38
 *
 * @author Thomas Morgner
 */
public class BaseFontResourceFactory implements ResourceFactory {
  public static final FactoryParameterKey FONTNAME = new FactoryParameterKey( "filename" );
  public static final FactoryParameterKey ENCODING = new FactoryParameterKey( "encoding" );
  public static final FactoryParameterKey EMBEDDED = new FactoryParameterKey( "embedded" );

  public BaseFontResourceFactory() {
  }

  public Resource create( final ResourceManager manager, final ResourceData data, final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException {
    final ResourceKey key = data.getKey();
    final Map factoryParameters = key.getFactoryParameters();
    final boolean embedded = Boolean.TRUE.equals( factoryParameters.get( BaseFontResourceFactory.EMBEDDED ) );
    final String encoding = String.valueOf( factoryParameters.get( BaseFontResourceFactory.ENCODING ) );
    final String fontType = String.valueOf( factoryParameters.get( BaseFontResourceFactory.FONTNAME ) );

    final DependencyCollector dc = new DependencyCollector( key, data.getVersion( manager ) );

    final byte[] ttfAfm = data.getResource( manager );
    byte[] pfb = null;
    if ( embedded && ( fontType.endsWith( ".afm" ) || fontType.endsWith( ".pfm" ) ) ) {
      final String pfbFileName = fontType.substring( 0, fontType.length() - 4 ) + ".pfb";
      try {
        final ResourceKey pfbKey = manager.deriveKey( key, pfbFileName );
        final ResourceData res = manager.load( pfbKey );
        pfb = res.getResource( manager );
        dc.add( pfbKey, res.getVersion( manager ) );
      } catch ( ResourceException e ) {
        // ignore ..
      }
    }

    try {
      final BaseFont baseFont = BaseFont.createFont( fontType, encoding, embedded, false, ttfAfm, pfb );
      return new CompoundResource( key, dc, baseFont, getFactoryType() );
    } catch ( Exception e ) {
      throw new ResourceCreationException( "Failed to create the font " + fontType, e );
    }
  }

  public Class getFactoryType() {
    return BaseFont.class;
  }

  public void initializeDefaults() {
    // nothing needed ...
  }
}
