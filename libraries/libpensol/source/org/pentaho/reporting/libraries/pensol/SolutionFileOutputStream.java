package org.pentaho.reporting.libraries.pensol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SolutionFileOutputStream extends OutputStream
{
  private ByteArrayOutputStream outputStream;
  private boolean closed;
  private WebSolutionFileObject item;

  public SolutionFileOutputStream(final WebSolutionFileObject item, final byte[] existingData) throws IOException
  {
    this.item = item;
    this.outputStream = new ByteArrayOutputStream(Math.max (8192, existingData.length));
    this.outputStream.write(existingData);
  }

  public void write(final int b)
      throws IOException
  {
    if (closed)
    {
      throw new IOException("Already closed");
    }
    outputStream.write(b);
  }

  public void write(final byte[] b, final int off, final int len)
      throws IOException
  {
    if (closed)
    {
      throw new IOException("Already closed");
    }
    outputStream.write(b, off, len);
  }

  public void close()
      throws IOException
  {
    if (closed)
    {
      throw new IOException("Already closed");
    }

    closed = true;

    outputStream.close();
    final byte[] data = outputStream.toByteArray();
    item.writeData(data);


  }

  public void write(final byte[] b)
      throws IOException
  {
    if (closed)
    {
      throw new IOException("Already closed");
    }
    outputStream.write(b);
  }

  public void flush()
      throws IOException
  {
    if (closed)
    {
      throw new IOException("Already closed");
    }
    outputStream.flush();
  }
}
