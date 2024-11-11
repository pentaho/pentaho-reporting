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


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;

public final class ReplacedContentUtil {
  private ReplacedContentUtil() {
  }

  public static long computeWidth( final RenderableReplacedContentBox content ) {
    final long bcw = ProcessUtility.computeBlockContextWidth( content );
    final long width = computeWidthInternal( content, bcw );
    final RenderableReplacedContent replacedContent = content.getContent();
    return ProcessUtility.computeLength( replacedContent.getMinimumWidth().resolve( bcw ), replacedContent
        .getMaximumWidth().resolve( bcw ), width );
  }

  public static long
    computeHeight( final RenderableReplacedContentBox content, final long bcw, final long computedWidth ) {
    final RenderableReplacedContent replacedContent = content.getContent();
    final long height = computeHeightInternal( replacedContent, bcw, computedWidth );
    return ProcessUtility.computeLength( replacedContent.getMinimumHeight().resolve( bcw ), replacedContent
        .getMaximumHeight().resolve( bcw ), height );
  }

  private static long
    computeWidthInternal( final RenderableReplacedContentBox contentBox, final long blockContextWidth ) {
    final RenderableReplacedContent content = contentBox.getContent();
    final RenderLength requestedWidth = content.getRequestedWidth();
    final RenderLength requestedHeight = content.getRequestedHeight();
    if ( RenderLength.AUTO.equals( requestedWidth ) ) {
      // if width is auto, and height is auto,
      if ( RenderLength.AUTO.equals( requestedHeight ) ) {
        // use the intrinsic width ..
        return content.getContentWidth();
      } else {
        // if height is not auto, but the width is, then compute a width that
        // preserves the aspect ratio. (
        final long contentHeight = content.getContentHeight();
        if ( contentHeight > 0 ) {
          final long height = requestedHeight.resolve( blockContextWidth );
          return height * blockContextWidth / contentHeight;
        } else {
          return 0;
        }
      }
    } else {
      // width is not auto.
      return requestedWidth.resolve( blockContextWidth );
    }
  }

  private static long computeHeightInternal( final RenderableReplacedContent content, final long blockContextWidth,
      final long computedWidth ) {
    final RenderLength requestedHeight = content.getRequestedHeight();
    if ( RenderLength.AUTO.equals( content.getRequestedWidth() ) ) {
      // if width is auto, and height is auto,
      if ( RenderLength.AUTO.equals( requestedHeight ) ) {
        final long contentWidth = content.getContentWidth();
        if ( contentWidth > 0 && computedWidth != 0 ) {
          // Intrinsic height must be computed to preserve the aspect ratio.
          return computedWidth * content.getContentHeight() / contentWidth;
        }

        // use the intrinsic height ..
        return content.getContentHeight();
      } else { // if height is not auto, then use the declared height.
        // A percentage is now relative to the intrinsinc size.
        // And yes, I'm aware that this is not what the standard says ..
        return requestedHeight.resolve( blockContextWidth );
      }
    } else {
      // width is not auto.
      // If the height is auto, we have to preserve the aspect ratio ..
      if ( RenderLength.AUTO.equals( requestedHeight ) ) {
        final long contentWidth = content.getContentWidth();
        if ( contentWidth > 0 ) {
          // Requested height must be computed to preserve the aspect ratio.
          return computedWidth * content.getContentHeight() / contentWidth;
        } else {
          return 0;
        }
      } else {
        // height is something fixed ..
        return requestedHeight.resolve( blockContextWidth );
      }
    }
  }
}
