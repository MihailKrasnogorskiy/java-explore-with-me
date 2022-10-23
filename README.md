# java-explore-with-me

Проект java-explore-with-me представляет собой приложение по
размещению и поиску событий.

Для неавторизованных пользователей доступны:

* поиск событий по определенным пользователем критериям
* поиск события по id.

Для авторизованного пользователя:

* размещение события, его редактирование и отмена
* размещение запроса на участие в событии, размещенном другим пользователем
* параметризованный поиск
* подтверждение запросов на участие в созданных им событиях
* и д.р.

Администратор может выполнять следующие действия в приложениее:

* создавать пользователей;
* публиковать, редактировать и отклонять события;
* осуществлять поиск по пользователям и событиям;
* создавать, редактировать и удалять категории и подборки событий;
* закреплять и откреплять подборки событий.

Полное описание api можно увидеть в файлах:

* ewm-main-service-spec.json
* ewm-stats-service-spec.json

Данные файлы размещены в корне проекта и открываются в Swagger редакторе.

Проект реализован по микросервисной архитектуре:

* ewm-service - реализация бизнес-логики - покрытие тестами 93%
* statistic - сбор и возвращение статистики с публичных эндпоинтов - покрытие тестами 91%

В качестве бд используется PostgreSql.

Для запуска запустить файлы скриптов:

* Windows - run.bat
* Linux - run.sh
  Файлы скриптов расположены в корневой папке проекта

В качестве дополнительного функционала разработана система сбора обратной связи и модерация её администратором.

Так же усовершенствована система модерации событий администратором:

* Добавлена возможность отправки запроса на исправление ошибок и повторную модерацию

Проект является дипломной работой по курсу Яндекс.Практикум.Java-разработчик.

Разработал Красногорский Михаил mikser256@yandex.ru