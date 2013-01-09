package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfWriter;

public class PolygonAnnotation extends PdfAnnotation
{
  private static final PdfName POLYGON = new PdfName("Polygon"); // NON-NLS
  private static final PdfName VERTICES = new PdfName("Vertices");// NON-NLS

  public PolygonAnnotation(final PdfWriter writer,
                           final float[] coords)
  {
    super(writer, null);
    put(PdfName.SUBTYPE, POLYGON);
    put(PdfName.RECT, createRec(coords));
    put(VERTICES, new PdfArray(coords));
  }

  private static PdfRectangle createRec(final float[] coords)
  {
    float minX = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    float minY = Integer.MAX_VALUE;
    float maxY = Integer.MIN_VALUE;

    for (int i = 0; i < coords.length; i += 2)
    {
      float x = coords[i];
      float y = coords[i + 1];
      if (x < minX)
      {
        minX = x;
      }
      if (y < minY)
      {
        minY = y;
      }

      if (x > maxX)
      {
        maxX = x;
      }
      if (y > maxY)
      {
        maxY = y;
      }
    }
    return new PdfRectangle(minX, minY, maxX, maxY);
  }
}
