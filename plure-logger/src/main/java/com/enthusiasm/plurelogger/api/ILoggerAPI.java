package com.enthusiasm.plurelogger.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.SearchResults;

public interface ILoggerAPI {
    /**
     * Выполняет поиск в базе данных
     * @param params Параметры поиска для фильтрации результатов
     * @param page Страница с результатами, которую необходимо получить
     * @return Список результатов на указанной странице вместе с текущей страницей и общим количеством страниц
     */
    CompletableFuture<SearchResults> searchActions(ActionSearchParams params, int page);

    /**
     * Подсчитывает <b>все</b> действия, соответствующие параметрам поиска
     * @param params Параметры поиска для фильтрации результатов
     * @return Количество действий, соответствующих параметрам поиска
     */
    CompletableFuture<Long> countActions(ActionSearchParams params);

    /**
     * Выполняет откат для действий, соответствующих параметрам поиска
     * @param params Параметры поиска для фильтрации результатов
     * @return Список действий, откат которых не удался
     */
    CompletableFuture<List<IActionType>> rollbackActions(ActionSearchParams params);

    /**
     * Выполняет восстановление (отменяет откат) для действий, соответствующих параметрам поиска
     * @param params Параметры поиска для фильтрации результатов
     * @return Список действий, восстановление которых не удалось
     */
    CompletableFuture<List<IActionType>> restoreActions(ActionSearchParams params);

    /**
     * Логирует действие в базу данных
     * @param action Действие для логирования
     */
    void logAction(IActionType action);
}
