# Building the app

Project uses `jlink` gradle plugin for bundling the app.

## Gradle task

To build the app you can use the gradle task: `Tasks > build > jpackage`

## Gradle command

Alternatively, you can use the command line:

```bash
./gradlew jpackage
```

If you still encounter issues, make sure you have the correct JDK version set.
You can find the path to the JDK by running:

```bash
/usr/libexec/java_home -v 17
```

Then, set the `JAVA_HOME` environment variable to the path returned by the above command:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Alternatively, update the `gradle.properties` file with the returned path (see gradle.properties.example for reference)
