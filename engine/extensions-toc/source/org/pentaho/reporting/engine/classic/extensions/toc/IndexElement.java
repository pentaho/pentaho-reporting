package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class IndexElement extends SubReport
{
  /**
   * Creates a new subreport instance.
   */
  public IndexElement()
  {
    setElementType(new IndexElementType());

    final Class[] columnTypes = new Class[4];
    final String[] columnNames = new String[4];
    columnNames[0] = "item-data";
    columnNames[1] = "item-pages";
    columnNames[2] = "item-pages-array";
    columnNames[3] = "item-key";
    columnTypes[0] = Object.class;
    columnTypes[1] = String.class;
    columnTypes[2] = Integer[].class;
    columnTypes[3] = String.class;
    final TypedTableModel sampleModel = new TypedTableModel(columnNames, columnTypes);

    final CompoundDataFactory compoundDataFactory = new CompoundDataFactory();
    compoundDataFactory.add(new TableDataFactory("design-time-data", sampleModel));
    setQuery("design-time-data");
    setDataFactory(compoundDataFactory);
  }


}