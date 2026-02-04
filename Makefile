# Windows-версия (для CMD/PowerShell)
install:
	.\gradlew.bat clean installDist

run-dist: install
	.\app\build\install\app\bin\app.bat

check-updates:
	.\gradlew.bat dependencyUpdates

lint:
	.\gradlew.bat checkstyleMain

build:
	.\gradlew.bat clean build

# Unix-версия (для Git Bash/WSL)
ifeq ($(OS),Windows_NT)
    # Команды выше
else
install:
	./gradlew clean installDist

run-dist: install
	./app/build/install/app/bin/app

check-updates:
	./gradlew dependencyUpdates

lint:
	./gradlew checkstyleMain

build:
	./gradlew clean build
endif

.PHONY: build install run-dist check-updates lint