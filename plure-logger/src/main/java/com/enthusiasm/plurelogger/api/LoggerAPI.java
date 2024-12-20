package com.enthusiasm.plurelogger.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.NotImplementedException;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.SearchResults;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public class LoggerAPI implements ILoggerAPI {
    @Override
    public CompletableFuture<SearchResults> searchActions(ActionSearchParams params, int page) {
        return CompletableFuture.supplyAsync(() -> DatabaseService.searchActions(params, page), ThreadUtils.getAsyncExecutor());
    }

    @Override
    public CompletableFuture<Long> countActions(ActionSearchParams params) {
        return CompletableFuture.supplyAsync(() -> DatabaseService.countActions(params), ThreadUtils.getAsyncExecutor());
    }

    @Override
    public CompletableFuture<List<IActionType>> rollbackActions(ActionSearchParams params) {
        throw new NotImplementedException("Not implemented yet");
    }

    @Override
    public CompletableFuture<List<IActionType>> restoreActions(ActionSearchParams params) {
        throw new NotImplementedException("Not implemented yet");
    }

    @Override
    public void logAction(IActionType action) {
        ActionQueueService.addToQueue(action);
    }
}
