package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * A generic write handler for data elements and all sections that have no need for writing additional information.
 */
public class GenericElementWriteHandler extends AbstractElementWriteHandler {
  /**
   * Writes a single element as XML structure.
   *
   * @param bundle
   *          the bundle to which to write to.
   * @param state
   *          the current write-state.
   * @param xmlWriter
   *          the xml writer.
   * @param element
   *          the element.
   * @throws java.io.IOException
   *           if an IO error occured.
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException
   *           if an Bundle writer.
   */
  public void writeElement( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Element element ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( element instanceof MasterReport ) {
      throw new BundleWriterException();
    }
    if ( element instanceof SubReport ) {
      throw new BundleWriterException();
    }

    String name = element.getMetaData().getName();
    String namespace = element.getMetaData().getNamespace();

    copyStaticResources( bundle, state, element );

    final AttributeList attList = createMainAttributes( element, xmlWriter );
    ensureNamespaceDefined( xmlWriter, attList, namespace );
    xmlWriter.writeTag( namespace, name, attList, XmlWriterSupport.OPEN );
    writeElementBody( bundle, state, element, xmlWriter );
    if ( element instanceof Section ) {
      writeChildElements( bundle, state, xmlWriter, (Section) element );
    }
    xmlWriter.writeCloseTag();
  }

}
