# Instructions for compiling & building
  
<br>

## Requirements
- [JRE 1.8](https://java.com/download/manual.jsp)
- JDK
  - Up to [Version 10](https://www.oracle.com/java/technologies/java-archive-javase10-downloads.html) - contains JavaFX
  - Or [Newer](https://www.oracle.com/java/technologies/javase-downloads.html) - requires separate [JavaFX](https://openjfx.io/) download
  
<br>

## Compilation method
- [IDE](#1-compiling-using-ide)
- [Manual](#2-manual-compiling)

<br>

## 1. Compiling using IDE  
- Create new project
- Add `src` and `lib` folders
- Add **all** `.jar` files from `lib` folder to `Build Path`
- If your IDE doesn't support ANSI you can add `--nocolors` to default program arguments

<br>

## 2. Manual compiling
### 2.1 Windows
- Download the source code
- Navigate to game's `src`folder
- Create new `run.bat` file and paste the below code <br><br>
  ```batch
  @echo off
  SET CLASSPATH=".\base\*;.\obj\*;..\lib\*;"
  javac -target 8 -source 8 -cp %CLASSPATH% .\base\Window.java
  java -cp %CLASSPATH% base.Window
  pause
  ```
- Run it

<br>

### 2.2 Linux
- Download the source code
- Navigate to game's `src`folder
- Create new `run.sh` file and paste the below code <br><br>
  ```shell
  #!/bin/sh
  CLASSPATH="./base/*:./obj/*:../lib/*"
  javac -target 8 -source 8 -cp $CLASSPATH ./base/Window.java
  java -cp $CLASSPATH base.Window
  ```
- Run it
