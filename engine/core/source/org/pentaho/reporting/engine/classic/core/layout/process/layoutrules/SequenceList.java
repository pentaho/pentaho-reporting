package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public interface SequenceList
{
  RenderNode getNode(int index);

  InlineSequenceElement getSequenceElement(int index);

  long getMinimumLength (int index);

  void clear();

  void add(InlineSequenceElement element, RenderNode node);

  int size();

  InlineSequenceElement[] getSequenceElements(InlineSequenceElement[] target);

  RenderNode[] getNodes(RenderNode[] target);
}
