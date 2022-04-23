# Rick_and_Morty

В приложении будет одно активити и 6 фрагментов (по 3 фрагмента для каждой вкладки и по 3 фрагмента для деталей по каждой вкладке).

Навигация между фрагментами будет с помощью nav_graph.

В активити будет поисковая строка вверху, фрагмент, BottomNavigationView внизу.
Фрагмент вкладки содержит поисковый фильтр и список на основе Jetpack Paging.
Фрагмент детали вкладки содержит ScrollView, floating action button.

Персонажи:

data class Person: Serializable (обычный дата класс со всеми данными)
class PersonListFragment
class PersonListViewModel : ListViewModel(содержит всю логику PersonListFragment)
class PersonDetailInfoFragment
class PersonDetailInfoViewModel : DetailInfoViewModel(содержит всю логику PersonDetailInfoFragment)

Эпизоды:

data class Episode : Serializable  (обычный дата класс со всеми данными)
class EpisodeListFragment
class EpisodeListViewModel : ListViewModel(содержит всю логику EpisodeListFragment)
class EpisodeDetailInfoFragment
class EpisodenDetailInfoViewModel : DetailInfoViewModel(содержит всю логику EpisodeDetailInfoFragment)

Локации:

data class Location: Serializable (обычный дата класс со всеми данными)
class LocationListFragment
class LocationListViewModel : ListViewModel(содержит всю логику LocationListFragment)
class LocationDetailInfoFragment
class LocationDetailInfoViewModel : DetailInfoViewModel(содержит всю логику LocationDetailInfoFragment)

Разработка приложения:
1. Создание nav_graph, активити и 3 основных фрагментов со списками
В активити вставить поисковую строку, FrameLayout и BottomNavigationView
Реализовать переключение между фрагментами с помощью BottomNavigationView

2. Работа с Network. Используя retrofit загружать данные в список Jetpack Paging.
Реализовать для трёх вкладок загрузку и  отображение списка.
P.S. какие классы  и локигу работу между ними ещё не придумал

3. Работа со списком. Добавление пагинации, индикатора загрузки, опция Pull-to-Refresh, добавление возможности фильтрации контента.

создание pull request feature/network

4. Создание трёх фрагментов с деталями вкладок. Добавление в них floating action button (стрелочка назад).
DetailInfoViewModel (интерфейс) должен принимать Serializable и использовать его для отображения.
Окончательно закончить логику переключения между всеми фрагментами.

5. Кэширование данных. Добавление кэширования данных.
P.S. какие классы  и локигу работу между ними ещё также не придумал.
      Первый вариант: можно скачать и закэшировать всю базу данных.
      Второй вариант: можно скачивать и кэшировать небольшое количество данных на 2-3 страницы впёред. 
 Если пользователь offline попробует открыть данные, которые не были прогруженны, - отобразить ему ошибку.
 Лучше второй вариант, не знаю можно ли так делать.
     
создание pull request feature/data_caching

6.Основа приложения уже должна быть готова
По возможности изменить некоторые процессы вычисления c использованием многопоточности
Использовать coroutines IO при загрузке и кэшировании данных

создание pull request feature/coroutines

7. Реализация подхода Dependency Injection, использовав Dagger 2

создание pull request feature/dependency_injection

8. Написание Unit-тестов.

создание pull request feature/tests
