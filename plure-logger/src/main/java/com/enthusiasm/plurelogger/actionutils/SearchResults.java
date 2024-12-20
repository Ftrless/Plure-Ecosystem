package com.enthusiasm.plurelogger.actionutils;

import java.util.List;

import com.enthusiasm.plurelogger.actions.IActionType;

public record SearchResults(List<IActionType> actions, ActionSearchParams searchParams, int page, int pages) {}
