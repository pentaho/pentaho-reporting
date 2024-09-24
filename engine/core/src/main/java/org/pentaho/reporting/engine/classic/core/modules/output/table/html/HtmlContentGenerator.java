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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultHtmlContentGenerator;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;

public interface HtmlContentGenerator {
  public ResourceManager getResourceManager();

  public void registerFailure( final ResourceKey source );

  public void registerContent( final ResourceKey source, final String name );

  public boolean isRegistered( final ResourceKey source );

  public String getRegisteredName( final ResourceKey source );

  public void setCopyExternalImages( final boolean copyExternalImages );

  public String writeRaw( final ResourceKey source ) throws ContentIOException, IOException;

  public ContentItem writeImage( final ImageContainer imageContainer, final String encoderType, final float quality,
    final boolean alpha ) throws ContentIOException, IOException;

  public String rewrite( final ImageContainer image, ContentItem contentItem );

  public DefaultHtmlContentGenerator.ImageData getImageData( final ImageContainer image, final String encoderType,
    final float quality, final boolean alpha ) throws IOException, UnsupportedEncoderException;
}
