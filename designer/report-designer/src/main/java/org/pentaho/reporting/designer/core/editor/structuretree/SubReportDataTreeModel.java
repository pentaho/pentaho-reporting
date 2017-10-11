/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;

public class SubReportDataTreeModel extends AbstractReportDataTreeModel {
  private SubReport reportElement;
  private SubReportParametersNode reportParametersNode;

  public SubReportDataTreeModel( final ReportDocumentContext renderContext ) {
    super( renderContext );
    if ( renderContext.getReportDefinition() instanceof SubReport == false ) {
      throw new IllegalArgumentException( "Instantiating a SubReportDataTreeModel on a MasterReport-Context" );
    }

    this.reportElement = (SubReport) renderContext.getReportDefinition();
    this.reportParametersNode = new SubReportParametersNode();
  }

  protected SubReportParametersNode getReportParametersNode() {
    return reportParametersNode;
  }

  public Object getRoot() {
    return reportElement;
  }

  public boolean isLeaf( final Object node ) {
    if ( node == reportParametersNode ) {
      return false;
    }
    if ( node == reportParametersNode.getImportParametersNode() ) {
      return false;
    }
    if ( node == reportParametersNode.getExportParametersNode() ) {
      return false;
    }
    if ( node instanceof ParameterMapping ) {
      return true;
    }
    if ( node instanceof ParentDataFactoryNode ) {
      return false;
    }
    if ( node instanceof InheritedDataFactoryWrapper ) {
      return false;
    }
    return super.isLeaf( node );
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent == reportParametersNode ) {
      switch( index ) {
        case 0:
          return reportParametersNode.getImportParametersNode();
        case 1:
          return reportParametersNode.getExportParametersNode();
        default:
          throw new IndexOutOfBoundsException();
      }
    } else if ( parent == reportParametersNode.getImportParametersNode() ) {
      return reportElement.getInputMappings()[ index ];
    } else if ( parent == reportParametersNode.getExportParametersNode() ) {
      return reportElement.getExportMappings()[ index ];
    } else if ( parent == reportElement ) {
      switch( index ) {
        case 0:
          return reportElement.getDataFactory();
        case 1: {
          final Section parentSection = reportElement.getParentSection();
          if ( parentSection == null ) {
            throw new IllegalStateException();
          }
          final AbstractReportDefinition reportDefinition =
            (AbstractReportDefinition) parentSection.getReportDefinition();
          return new ParentDataFactoryNode( reportDefinition );
        }
        case 2:
          return getReportFunctionNode();
        case 3:
          return getReportEnvironmentDataRow();
        case 4:
          return reportParametersNode;
        default:
          throw new IndexOutOfBoundsException();
      }
    } else if ( parent instanceof ParentDataFactoryNode ) {
      final ParentDataFactoryNode pdfn = (ParentDataFactoryNode) parent;
      final CompoundDataFactory compoundDataFactory = pdfn.getDataFactory();
      if ( index == compoundDataFactory.size() ) {
        if ( pdfn.isSubReport() ) {
          return pdfn.getParentNode();
        }
        throw new IndexOutOfBoundsException();
      }
      return new InheritedDataFactoryWrapper( compoundDataFactory.getReference( index ) );
    } else if ( parent instanceof InheritedDataFactoryWrapper ) {
      final InheritedDataFactoryWrapper idf = (InheritedDataFactoryWrapper) parent;
      final DataFactory dataFactory = idf.getDataFactory();
      final String[] queryNames = dataFactory.getQueryNames();
      return new ReportQueryNode( dataFactory, queryNames[ index ], false );
    } else {
      return super.getChild( parent, index );
    }
  }

  public int getChildCount( final Object parent ) {
    if ( parent == reportElement ) {
      return 5;
    }
    if ( parent == reportParametersNode ) {
      return 2;
    }
    if ( parent == reportParametersNode.getImportParametersNode() ) {
      return reportElement.getInputMappings().length;
    }
    if ( parent == reportParametersNode.getExportParametersNode() ) {
      return reportElement.getExportMappings().length;
    }
    if ( parent instanceof ParentDataFactoryNode ) {
      final ParentDataFactoryNode pdfn = (ParentDataFactoryNode) parent;
      final CompoundDataFactory compoundDataFactory = pdfn.getDataFactory();
      if ( pdfn.isSubReport() ) {
        return compoundDataFactory.size() + 1;
      }
      return compoundDataFactory.size();
    }
    if ( parent instanceof InheritedDataFactoryWrapper ) {
      final InheritedDataFactoryWrapper idf = (InheritedDataFactoryWrapper) parent;
      return idf.getDataFactory().getQueryNames().length;
    }
    return super.getChildCount( parent );
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( parent == reportElement ) {
      if ( child == reportElement.getDataFactory() ) {
        return 0;
      }
      if ( child instanceof ParentDataFactoryNode ) {
        return 1;
      }
      if ( child == getReportFunctionNode() ) {
        return 2;
      }
      if ( child == getReportEnvironmentDataRow() ) {
        return 3;
      }
      if ( child == reportParametersNode ) {
        return 4;
      }
      return -1;
    }
    if ( parent instanceof ParentDataFactoryNode ) {
      final ParentDataFactoryNode pdfn = (ParentDataFactoryNode) parent;
      final CompoundDataFactory compoundDataFactory = pdfn.getDataFactory();
      if ( child instanceof ParentDataFactoryNode ) {
        return compoundDataFactory.size();
      }
      if ( child instanceof InheritedDataFactoryWrapper == false ) {
        return -1;
      }

      final InheritedDataFactoryWrapper wrapper = (InheritedDataFactoryWrapper) child;
      final CompoundDataFactory dataFactoryElement = getDataFactoryElement();
      for ( int i = 0; i < dataFactoryElement.size(); i++ ) {
        final DataFactory dataFactory = dataFactoryElement.getReference( i );
        if ( dataFactory == wrapper.getDataFactory() ) {
          return i;
        }
      }
      return -1;
    }

    if ( parent instanceof InheritedDataFactoryWrapper ) {
      final InheritedDataFactoryWrapper idf = (InheritedDataFactoryWrapper) parent;
      if ( child instanceof ReportQueryNode == false ) {
        return -1;
      }
      final ReportQueryNode rfn = (ReportQueryNode) child;
      if ( rfn.getDataFactory() != idf.getDataFactory() ) {
        return -1;
      }
      final String[] queryNames = rfn.getDataFactory().getQueryNames();
      return indexOf( queryNames, rfn.getQueryName() );
    }
    if ( parent == reportParametersNode ) {
      if ( child == reportParametersNode.getImportParametersNode() ) {
        return 0;
      }
      if ( child == reportParametersNode.getExportParametersNode() ) {
        return 1;
      }
      return -1;
    }
    return super.getIndexOfChild( parent, child );
  }

}
