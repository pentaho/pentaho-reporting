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
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
