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

package org.pentaho.reporting.engine.classic.core.layout.richtext;

import java.util.HashMap;

public class RichTextConverterRegistry {
  private static RichTextConverterRegistry registry;

  public static synchronized RichTextConverterRegistry getRegistry() {
    if ( registry == null ) {
      registry = new RichTextConverterRegistry();
    }
    return registry;
  }

  private HashMap<String, RichTextConverter> richTextConverters;

  private RichTextConverterRegistry() {
    richTextConverters = new HashMap<String, RichTextConverter>();
    richTextConverters.put( "text/html", new HtmlRichTextConverter() );
    richTextConverters.put( "text/rtf", new RtfRichTextConverter() );
  }

  public RichTextConverter getConverter( final String key ) {
    if ( key == null ) {
      return null;
    }

    return richTextConverters.get( key );
  }
}
