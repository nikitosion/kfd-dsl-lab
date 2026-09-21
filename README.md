# Свой DSL

Перепишите десять проверок HTTP-ответа из `lesson09.legacy` с помощью DSL на лямбдах с получателем.

Для тестов подключён JUnit 6.1.3.

| Действие | Bash | PowerShell |
|---|---|---|
| Сборка | `bash ./gradlew build` | `.\gradlew.bat build` |
| Тесты | `bash ./gradlew :app:test` | `.\gradlew.bat :app:test` |

Выполняйте команды из корня проекта. Условие работы — в [docs/lesson09.md](docs/lesson09.md).

Если продолжаете работу в своём проекте курса, скопируйте в него каталоги `app/src/main/kotlin/lesson09`, `app/src/test/kotlin/lesson09` и файл `docs/lesson09.md`.
