/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.event.async;

import java.util.UUID;

public class AsyncReportState implements IAsyncReportState {
  private final String path;
  private final UUID uuid;
  private final AsyncExecutionStatus status;
  private final int progress;
  private final int page;
  private final int totalPages;
  private final int row;
  private final int totalRows;
  private final String activity;
  private final String mimeType;
  private final String errorMessage;
  private final int generatedPage;
  private final boolean isQueryLimitReached;

  public AsyncReportState( final UUID id, final String path ) {
    this.status = AsyncExecutionStatus.QUEUED;
    this.uuid = id;
    this.path = path;
    this.progress = 0;
    this.page = 0;
    this.totalPages = 0;
    this.generatedPage = 0;
    this.row = 0;
    this.totalRows = 0;
    this.activity = null;
    this.mimeType = null;
    this.errorMessage = null;
    this.isQueryLimitReached = false;
  }

  public AsyncReportState( final UUID uuid,
                           final String path,
                           final AsyncExecutionStatus status,
                           final int progress,
                           final int row,
                           final int totalRows,
                           final int page,
                           final int totalPages,
                           final int generatedPage,
                           final String activity,
                           final String mimeType,
                           final String errorMessage,
                           final boolean isQueryLimitReached ) {
    this.uuid = uuid;
    this.path = path;
    this.status = status;
    this.progress = progress;
    this.row = row;
    this.totalRows = totalRows;
    this.page = page;
    this.totalPages = totalPages;
    this.generatedPage = generatedPage;
    this.activity = activity;
    this.mimeType = mimeType;
    this.errorMessage = errorMessage;
    this.isQueryLimitReached = isQueryLimitReached;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public AsyncExecutionStatus getStatus() {
    return status;
  }

  @Override
  public int getProgress() {
    return progress;
  }

  @Override
  public int getPage() {
    return page;
  }

  @Override
  public int getTotalPages() {
    return totalPages;
  }

  @Override public int getGeneratedPage() {
    return generatedPage;
  }

  @Override
  public int getRow() {
    return row;
  }

  @Override
  public int getTotalRows() {
    return totalRows;
  }

  @Override
  public String getActivity() {
    return activity;
  }

  @Override
  public String getMimeType() {
    return mimeType;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean getIsQueryLimitReached() {
    return isQueryLimitReached;
  }
}
