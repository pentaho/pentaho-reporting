package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Member;
import mondrian.olap.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.LoggingErrorHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MondrianUtil
{
  private static final Log logger = LogFactory.getLog(MondrianUtil.class);

  private MondrianUtil()
  {
  }

  public static String parseSchemaName(final ResourceManager resourceManager,
                                       final ResourceKey contextKey,
                                       final String designTimeFile)
  {
    try
    {
      final CubeFileProvider cubeFileProvider = ClassicEngineBoot.getInstance().getObjectFactory().get(CubeFileProvider.class);
      cubeFileProvider.setDesignTimeFile(designTimeFile);
      final InputStream inputStream = Util.readVirtualFile(cubeFileProvider.getCubeFile(resourceManager, contextKey));
      try
      {
        return parseXmlDocument(inputStream);
      }
      finally
      {
        inputStream.close();
      }
    }
    catch (Exception e)
    {
      logger.debug("Failed to parse mondrian schema file at " + designTimeFile, e);
    }
    return null;
  }

  private static String parseXmlDocument(final InputStream stream) throws ResourceCreationException
  {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);

    try
    {
      final DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver(ParserEntityResolver.getDefaultResolver());
      db.setErrorHandler(new LoggingErrorHandler());
      final InputSource input = new InputSource(stream);
      final Document document = db.parse(input);
      final Element documentElement = document.getDocumentElement();
      if ("Schema".equals(documentElement.getTagName()))// NON-NLS
      {
        return documentElement.getAttribute("name");
      }
      return null;
    }
    catch (ParserConfigurationException e)
    {
      throw new ResourceCreationException("Unable to initialize the XML-Parser", e);
    }
    catch (SAXException e)
    {
      throw new ResourceCreationException("Unable to parse the document.", e);
    }
    catch (IOException e)
    {
      throw new ResourceCreationException("Unable to parse the document.", e);
    }
  }

  public static String getUniqueMemberName(Member member)
  {
    String memberValue = Util.quoteMdxIdentifier(member.getName());
    while (member.getParentMember() != null)
    {
      memberValue = Util.quoteMdxIdentifier(member.getParentMember().getName()) + "." + memberValue;
      member = member.getParentMember();
    }
    final Hierarchy hierarchy = member.getHierarchy();
    final Dimension dimension = hierarchy.getDimension();
    if (hierarchy.getName().equals(dimension.getName()))
    {
      return Util.quoteMdxIdentifier(hierarchy.getName()) + "." + memberValue;
    }
    else
    {
      return Util.quoteMdxIdentifier(dimension.getName()) + "." + Util.quoteMdxIdentifier(hierarchy.getName()) + "." +
          memberValue;
    }
  }
}
