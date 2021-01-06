# Installation instructions for Linux

<br>

## 1. Check first 
- You may have java installed, but it's probably JDK
- Type `java -version` or `java --version`
- If output contains `Java(TM) SE Runtime Environment` you can skip to point `3`, else follow point `2`

<br>

## 2. Java setup
- Download [JRE](https://java.com/en/download/manual.jsp) for your Linux version
- Open terminal in the same directory and type `sudo tar zxvf jre-8uXXX-linux-x64.tar.gz`
  - Replace XXX with downloaded version
- Add java to path: `export PATH=/usr/java/jre1.8.0_271/bin:$PATH`
  - If you set custom JRE's path, just replace the one above
<br>

## 3. Launching
- Open terminal in game's directory
- Type `java -jar track-of-thought_x.y.z`


<br>

## 4. Permissions
- The only required permission is `rw` for `~/.local/share/` directory 
