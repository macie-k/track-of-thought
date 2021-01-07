# Installation instructions for Windows

<br>

## 1. Check first 
- You may have java installed, check it by typing `java -version` or `java --version` in cmd  
- If your output contains `Java(TM) SE Runtime Environment` you can go to point `2.3`
![Screenshot - 05_01](https://user-images.githubusercontent.com/25122875/103594349-c215da00-4ef8-11eb-8fe4-4c72d319d172.png)

<br>

## 2. Java setup
- Download & install [JRE 1.8](https://java.com/en/download/)
- Add JRE's `bin` folder to `PATH` Environment Variable
- After that sign out & in
- Check if `java -version` command returns downloaded java version

<br>

## 3. Launching
- Using `.exe`
   - Just double-click
- Using `.jar`
   - Open cmd in the same directory and run: `java -jar track-of-thought_x.y.z`

<br>

## 4. Permissions
- The only required permission is `rw` for the `AppData` folder (allowed by default)

<br>

--- 

<br>

## Colored logs
- Windows doesn't enable ANSI by default, so your output may look like that
   ![a](https://user-images.githubusercontent.com/25122875/103597157-d4474680-4eff-11eb-98af-6060eaa13938.png)
- You can make it look better, by enabling ANSI in registry - [Read more](https://ss64.com/nt/syntax-ansi.html)
   ![b](https://user-images.githubusercontent.com/25122875/103597158-d5787380-4eff-11eb-9f16-74b7b606b996.png)

<br> 

- Optionally download the `.reg` script [here](https://pastebin.com/d0w5mVNg) or use [Windows Terminal](https://github.com/microsoft/terminal/releases)
- Or disable it by using `--nocolors` option
