package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.xmlns.parser.LoggingErrorHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentHelper
{
  private DocumentHelper()
  {
  }

  public static Document loadDocumentFromBytes(final byte[] bytes) throws KettlePluginException
  {
    return loadDocument(new ByteArrayInputStream(bytes));
  }

  public static Document loadDocumentFromPlugin(String id)
      throws KettlePluginException
  {
    
    EmbeddedKettleDataFactoryMetaData md = (EmbeddedKettleDataFactoryMetaData)DataFactoryRegistry.getInstance().getMetaData(id);
    final InputStream in = new ByteArrayInputStream(md.getBytes());

    return loadDocument(in);
  }

  public static Document loadDocument(final InputStream in) throws KettlePluginException
  {
    try
    {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setValidating(false);

      final DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver(ParserEntityResolver.getDefaultResolver());
      db.setErrorHandler(new LoggingErrorHandler());
      final InputSource input = new InputSource(in);
      return db.parse(input);
    }
    catch (ParserConfigurationException e)
    {
      throw new KettlePluginException("Unable to initialize the XML-Parser", e);
    }
    catch (SAXException e)
    {
      throw new KettlePluginException("Unable to parse the document.", e);
    }
    catch (IOException e)
    {
      throw new KettlePluginException("Unable to read the document from stream.", e);
    }
    finally
    {
      try
      {
        in.close();
      }
      catch (IOException e)
      {
        // ignored ..
      }
    }
  }
}
