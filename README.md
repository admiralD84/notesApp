**NoteAndToDoApp: README**

# Описание приложения

:memo: **NoteAndToDoApp** - универсальное приложение, объединяющее функции создания заметок, управления списками задач, отслеживания списков покупок и учета расходов. В приложении использованы современные паттерны архитектуры, такие как **ViewModel** и **LiveData**, интегрирован **Navigation Component** для *плавной навигации*, добавлена безопасность с входом по пин-коду, использован **SharedPreferences** для хранения данных, и внедрены диалоги для взаимодействия с пользователем.

## Ключевые функции

1. **Создание и управление заметками:**
    - Легкое создание, редактирование и удаление заметок с помощью интуитивного интерфейса.

2. **Функционал списка задач (To-Do):**
    - Эффективная организация задач с помощью отдельного списка задач с возможностью удобного добавления и отметки выполненных задач.

3. **Управление списками покупок:**
    - Создание и управление списками покупок для более эффективного планирования и организации походов в магазин.

4. **Безопасный вход с помощью пин-кода:**
    - Обеспечение безопасности данных с функцией входа по пин-коду.

5. **Учетные записи пользователей:**
    - Сохранение данных пользователя с использованием ViewModel и LiveData для стабильности и сохранения данных между сеансами использования.

6. **Поиск и фильтрация:**
    - Удобный поиск и фильтрация для быстрого доступа к нужной информации.

7. **Управление клавиатурой:**
    - Автоматическое скрытие виртуальной клавиатуры для удобства взаимодействия пользователя с приложением.

8. **SharedPreferences для хранения данных:**
    - Использование SharedPreferences для постоянного хранения и получения пользовательских данных.

9.  **Navigation Component:**
    - Реализация Navigation Component для плавной и структурированной навигации в приложении.

10. **Dialogs для взаимодействия с пользователем:**
    - Использование диалогов для интерактивного взаимодействия с пользователем, обеспечивая понятность и удобство использования.

11. **Room Database для хранения данных:**
    - Для работы с базой данных использована библиотека Room Database, включая создание сущностей (entity) и объектов доступа к данным (DAO).

## Зависимости

```gradle
dependencies {
    // ... Другие зависимости
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.3.1"
    implementation "androidx.lifecycle:lifecycle-livedata:2.3.1"
    implementation "androidx.navigation:navigation-fragment:2.7.5"
    implementation "androidx.navigation:navigation-ui:2.7.5"
    implementation "androidx.room:room-runtime:2.6.0"
    annotationProcessor "androidx.room:room-compiler:2.6.0"
}
```
___
**Поддерживаемые языки:**
- Узбекский
- Русский
- Английский

___
___
## **Фотки**
![tasks](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-31.jpg)
![products](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-32.jpg)
![add products](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-28.jpg)
![tasks menu](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-13.jpg)
![add note](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-34.jpg)
![settings page](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-37.jpg)
![notes page](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-27-43.jpg)
![note read](https://github.com/admiralD84/notesApp/blob/master/images/photo_2023-11-17_14-57-14.jpg)

## **Гифки**
![overview](https://github.com/admiralD84/notesApp/blob/master/images/tasks.gif)
![add note](https://github.com/admiralD84/notesApp/blob/master/images/add_notes.gif)
![add task](https://github.com/admiralD84/notesApp/blob/master/images/add_tasks.gif)
![mark product purchased](https://github.com/admiralD84/notesApp/blob/master/images/product_purchase.gif)



### **Разработчик:** Анвар Ахмедов

:earth_asia: [Telegram](https://t.me/admiralD)

___
___
*Версия: 1.0*
___
*Дата выпуска: 15.11.2023 г.*
