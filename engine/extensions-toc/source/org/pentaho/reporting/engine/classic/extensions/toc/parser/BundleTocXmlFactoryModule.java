package org.pentaho.reporting.engine.classic.extensions.toc.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.content.SubContentRootElementHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class BundleTocXmlFactoryModule implements XmlFactoryModule
{
  public BundleTocXmlFactoryModule()
  {
  }

  public int getDocumentSupport(final XmlDocumentInfo documentInfo)
  {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if (rootNamespace != null && rootNamespace.length() > 0)
    {
      if (BundleNamespaces.CONTENT.equals(rootNamespace) == false)
      {
        return XmlFactoryModule.NOT_RECOGNIZED;
      }
      else if ("content".equals(documentInfo.getRootElement()))
      {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    }
    else if ("content".equals(documentInfo.getRootElement()))
    {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;

  }

  public String getDefaultNamespace(final XmlDocumentInfo documentInfo)
  {
    return BundleNamespaces.CONTENT;
  }

  public XmlReadHandler createReadHandler(final XmlDocumentInfo documentInfo)
  {
    return new TocContentRootElementHandler();
  }

}
