# Инструкция по сборке MultiCrafter Lib

## Требования

- **JDK 17** или выше (рекомендуется JDK 17)
- **Gradle** (встроен в проект через wrapper)
- **Интернет** (для загрузки зависимостей при первой сборке)

## Локальная сборка

### 1. Быстрая сборка (только jar)

```bash
./gradlew :lib:jar
```

Скомпилированный файл будет в: `lib/build/libs/lib-2.0.0.jar`

### 2. Полная сборка (с тестами)

```bash
./gradlew :lib:build
```

Это создаст:
- `lib/build/libs/lib-2.0.0.jar` - основной jar файл мода
- `lib/build/libs/lib-2.0.0-sources.jar` - исходники
- `lib/build/libs/lib-2.0.0-javadoc.jar` - документация

### 3. Только тесты

```bash
./gradlew :lib:test
```

### 4. Очистка перед сборкой

```bash
./gradlew clean :lib:build
```

## Windows (PowerShell)

Если вы используете PowerShell на Windows:

```powershell
.\gradlew.bat :lib:jar
# или
.\gradlew.bat :lib:build
```

## Где находится готовый файл?

После сборки jar файл находится здесь:
```
lib/build/libs/lib-2.0.0.jar
```

Этот файл можно:
- Использовать в Mindustry как обычный мод
- Подключить как зависимость в других модах
- Загрузить в Mod Browser

## Структура готового jar

Jar файл содержит:
- Все классы из `lib.multicraft` пакета
- `mod.hjson` - метаданные мода
- `icon.png` - иконка мода
- `assets/scripts/multi-crafter/lib.js` - JavaScript библиотека для JS модов

## Возможные проблемы

### Ошибка с JDK версией

Если у вас JDK 24, могут быть предупреждения. Используйте JDK 17:

```bash
# Установка через sdkman (Linux/Mac)
sdk install java 17.0.10-tem

# Или установите JDK 17 с официального сайта Oracle/Adoptium
```

### Ошибка "Could not find dependency"

Убедитесь, что у вас есть интернет-соединение. Gradle загрузит зависимости автоматически при первой сборке.

## GitHub Actions

Actions настроены и будут работать автоматически:

1. **Build.yml** - запускается при каждом push/PR:
   - Запускает тесты
   - Собирает библиотеку
   - Загружает артефакты

2. **commitTest.yml** - запускается при push/PR:
   - Собирает библиотеку
   - Создает автоматический релиз с тегом номера билда

3. **Release.yml** - ручной запуск для создания релиза:
   - Собирает библиотеку
   - Создает релиз v2.0.0

Все workflows используют JDK 17 и не требуют Android SDK (так как это просто библиотека).



