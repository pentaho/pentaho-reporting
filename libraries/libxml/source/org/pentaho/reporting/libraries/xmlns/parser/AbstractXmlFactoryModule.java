package org.pentaho.reporting.libraries.xmlns.parser;

/**
 * A common base class for all XmlFactoryModules. Almost all XmlFactoryModules follow the same pattern and
 * this class provides a sensible default implementation for a module checking for a well-known namespace
 * and root-element.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractXmlFactoryModule implements XmlFactoryModule
{
  private String namespace;
  private String rootElementName;

  protected AbstractXmlFactoryModule(final String namespace,
                                     final String rootElementName)
  {
    if (namespace == null)
    {
      throw new NullPointerException();
    }
    if (rootElementName == null)
    {
      throw new NullPointerException();
    }
    
    this.namespace = namespace;
    this.rootElementName = rootElementName;
  }

  /**
   * Checks the given document data to compute the propability of whether this
   * factory module would be able to handle the given data.
   *
   * @param documentInfo the document information collection.
   * @return an integer value indicating how good the document matches the
   *         factories requirements.
   */
  public int getDocumentSupport(final XmlDocumentInfo documentInfo)
  {
    final String rootNamespace = documentInfo.getRootElementNameSpace();
    if (rootNamespace != null && rootNamespace.length() > 0)
    {
      if (namespace.equals(rootNamespace) == false)
      {
        return XmlFactoryModule.NOT_RECOGNIZED;
      }
      else if (rootElementName.equals(documentInfo.getRootElement()))
      {
        return XmlFactoryModule.RECOGNIZED_BY_NAMESPACE;
      }
    }
    else if (rootElementName.equals(documentInfo.getRootElement()))
    {
      return XmlFactoryModule.RECOGNIZED_BY_TAGNAME;
    }

    return XmlFactoryModule.NOT_RECOGNIZED;
  }

  /**
   * Returns the default namespace for a document with the characteristics
   * given in the XmlDocumentInfo.
   *
   * @param documentInfo the document information.
   * @return the default namespace uri for the document.
   */
  public String getDefaultNamespace(final XmlDocumentInfo documentInfo)
  {
    return namespace;
  }
}
