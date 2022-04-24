# Rick_and_Morty

В приложении будет одно активити и 6 фрагментов (по 3 фрагмента для каждой вкладки и по 3 фрагмента для деталей по каждой вкладке).

Навигация между фрагментами будет с помощью nav_graph.

В активити будет поисковая строка вверху, фрагмент, BottomNavigationView внизу.
Фрагмент вкладки содержит поисковый фильтр и список на основе Jetpack Paging.
Фрагмент детали вкладки содержит ScrollView, floating action button.

Для для работы с интернетом будет один класс: NetworkConnector  - синглтон

Персонажи:

data class Person: Serializable (обычный дата класс со всеми данными)
interface PersonService (интерфейс  с командами запросами для сервера)
class PersonListFragment
class PersonListViewModel : ListViewModel(содержит всю логику PersonListFragment)
class PersonDetailInfoFragment
class PersonDetailInfoViewModel : DetailInfoViewModel(содержит всю логику PersonDetailInfoFragment)

Эпизоды:

data class Episode : Serializable  (обычный дата класс со всеми данными)
interface EpisodeService (интерфейс  с командами запросами для сервера)
class EpisodeListFragment
class EpisodeListViewModel : ListViewModel(содержит всю логику EpisodeListFragment)
class EpisodeDetailInfoFragment
class EpisodenDetailInfoViewModel : DetailInfoViewModel(содержит всю логику EpisodeDetailInfoFragment)

Локации:

data class Location: Serializable (обычный дата класс со всеми данными)
interface LocationService (интерфейс  с командами запросами для сервера)
class LocationListFragment
class LocationListViewModel : ListViewModel(содержит всю логику LocationListFragment)
class LocationDetailInfoFragment
class LocationDetailInfoViewModel : DetailInfoViewModel(содержит всю логику LocationDetailInfoFragment)

Разработка приложения:
1. Создание nav_graph, активити и 3 основных фрагментов со списками
В активити вставить поисковую строку, FrameLayout и BottomNavigationView
Реализовать переключение между фрагментами с помощью BottomNavigationView

2. Работа с Network. Используя retrofit и конвектор Moshi загрузить одного персонажа.
ViewModel будет получать данные используя NetworkConnector.
P.S. между ними ещё должен быть репозиторий, но что это такое и зачем он нужен, я пока что не разобрался

3. Работа со списком. Реализовать список Jetpack Paging только для персонажей. Загрузить вместо одного персонажа целый список
P.S. полностью не разобрался, что это за библиотека
 
4. Добавление пагинации, индикатора загрузки, опция Pull-to-Refresh, добавление возможности фильтрации контента.

5. После того, как все с вкладкой персонажей будет работать сделать аналогично для других вкладок.

создание pull request feature/network

6. Создание трёх фрагментов с деталями вкладок. Добавление в них floating action button (стрелочка назад).
фрагмент деталей вкладки должен принимать Serializable и использовать его для отображения.
Окончательно закончить логику переключения между всеми фрагментами.

7. Реализовать Splash Screen

8. Кэширование данных. Добавление кэширования данных.
P.S. какие классы  и локигу работу между ними ещё также не придумал.
      Первый вариант: можно скачать и закэшировать всю базу данных.
      Второй вариант: можно скачивать и кэшировать небольшое количество данных на 2-3 страницы впёред. 
 Если пользователь offline попробует открыть данные, которые не были прогруженны, - отобразить ему ошибку.
 Лучше второй вариант, не знаю можно ли так делать.
     
создание pull request feature/data_caching

9. Пересмотреть структуру и логику классов. Выбрать архитектурный паттерн.

создание pull request feature/mvvm

10.Основа приложения уже должна быть готова
По возможности изменить некоторые процессы вычисления c использованием многопоточности
Использовать coroutines IO при загрузке и кэшировании данных

создание pull request feature/coroutines

11. Реализация подхода Dependency Injection, использовав Dagger 2

создание pull request feature/dependency_injection

12. Написание Unit-тестов.

создание pull request feature/tests
