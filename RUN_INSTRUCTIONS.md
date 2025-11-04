# 🚀 Complete Guide to Run the Async Image Processing System

## ✅ Prerequisites Checklist

Before running the project, ensure you have:

1. **Java 21 or Higher** installed
   ```bash
   java --version
   # Should show: openjdk version "21" or higher
   ```

2. **Maven 3.9+** installed
   ```bash
   mvn --version
   # Should show: Apache Maven 3.9.x or higher
   ```

3. **JAVA_HOME** environment variable set correctly
   ```bash
   echo $JAVA_HOME
   # Should point to your JDK 21 installation
   ```

---

## 🎯 Recommended Ways to Run the Project

### **Method 1: Using JavaFX Maven Plugin (RECOMMENDED)**

This is the **best and easiest** method for JavaFX projects:

```bash
# Clean, compile, and run
mvn clean javafx:run
```

**Why this works:**
- Automatically handles JavaFX module path
- Properly configures preview features
- No need for manual module configuration

---

### **Method 2: Using Maven Wrapper (Recommended for portability)**

If you have the Maven wrapper (`mvnw`) in your project:

```bash
# On Linux/Mac
chmod +x mvnw
./mvnw clean javafx:run

# On Windows
mvnw.cmd clean javafx:run
```

---

### **Method 3: Using Exec Maven Plugin**

If you prefer the `exec` plugin:

```bash
# This will NOT work correctly for JavaFX modular projects
# mvn clean compile exec:java

# Instead, use this workaround:
mvn clean compile
mvn exec:java -Dexec.mainClass="com.image.imageprocessing.HelloApplication" \
              -Dexec.args="--enable-preview"
```

**⚠️ Note:** The exec plugin has limitations with JavaFX modules. Use Method 1 instead.

---

## 📋 Complete Command Reference

### **Development Commands**

```bash
# 1. Clean the project
mvn clean

# 2. Compile only
mvn clean compile

# 3. Run the application (BEST METHOD)
mvn javafx:run

# 4. Package as JAR
mvn clean package

# 5. Run tests
mvn test

# 6. Generate JavaDoc
mvn javadoc:javadoc

# 7. Display dependency tree
mvn dependency:tree
```

### **Combined Commands**

```bash
# Clean, compile, and run in one go
mvn clean javafx:run

# Clean, test, package, and run
mvn clean test package javafx:run

# Skip tests and run
mvn clean install -DskipTests javafx:run
```

---

## 🔧 If You Still Get Errors

### **Error 1: "JavaFX runtime components are missing"**

**Solution:** You're trying to run without proper JavaFX module path.

✅ **Fix:** Use `mvn javafx:run` instead of `mvn exec:java`

---

### **Error 2: "Preview features are not enabled"**

**Solution:** The preview features need to be enabled for StructuredTaskScope.

✅ **Fix:** The updated `pom.xml` now includes `--enable-preview` in compiler args.

```bash
# This should work now
mvn clean javafx:run
```

---

### **Error 3: "Module not found" or "Package does not exist"**

**Solution:** Missing JavaFX modules or incorrect module-info.java

✅ **Fix:** Verify your `module-info.java` has all required modules:

```java
module com.image.imageprocessing {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens com.image.imageprocessing to javafx.fxml;
    opens com.image.imageprocessing.ui to javafx.fxml;
    exports com.image.imageprocessing;
    exports com.image.imageprocessing.ui;
    exports com.image.imageprocessing.filter;
    exports com.image.imageprocessing.processor;
}
```

---

### **Error 4: "JAVA_HOME not set correctly"**

**Solution:** Java 21 needs to be properly configured.

✅ **Fix on Linux/Mac:**
```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

✅ **Fix on Windows:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
```

Or permanently in System Environment Variables.

---

### **Error 5: "Failed to load FXML"**

**Solution:** FXML file not found in resources.

✅ **Fix:** Ensure `enhanced-image-processing-view.fxml` exists in:
```
src/main/resources/enhanced-image-processing-view.fxml
```

---

## 🎯 Manual Execution (Advanced)

If you want to run without Maven (after building):

```bash
# Step 1: Build the project
mvn clean package

# Step 2: Run with Java directly (requires careful module path setup)
java --enable-preview \
     --module-path $PATH_TO_JAVAFX/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.swing \
     -jar target/imageProcessing-1.0-SNAPSHOT.jar
```

**Note:** Replace `$PATH_TO_JAVAFX` with your JavaFX SDK path.

This is complex, so **stick with `mvn javafx:run`**.

---

## 📊 What the Updated pom.xml Does

The new `pom.xml` includes:

### ✅ **Java 21 with Preview Features**
```xml
<compilerArgs>
    <arg>--enable-preview</arg>
</compilerArgs>
```

### ✅ **JavaFX Maven Plugin Configuration**
```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>${main.class}</mainClass>
        <options>
            <option>--enable-preview</option>
        </options>
    </configuration>
</plugin>
```

### ✅ **Exec Maven Plugin (Backup Method)**
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.1</version>
    <configuration>
        <mainClass>${main.class}</mainClass>
        <arguments>
            <argument>--enable-preview</argument>
        </arguments>
    </configuration>
</plugin>
```

### ✅ **Maven Surefire for Testing**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <argLine>--enable-preview</argLine>
    </configuration>
</plugin>
```

---

## 🎓 Understanding the Build Process

When you run `mvn javafx:run`, here's what happens:

1. **Clean Phase** - Removes old compiled files
2. **Compile Phase** - Compiles Java 21 code with preview features
3. **Process Resources** - Copies FXML and image files
4. **JavaFX Phase** - Launches JavaFX with proper module path
5. **Application Starts** - Your GUI appears!

---

## 🔍 Verification Steps

After running, verify everything works:

1. ✅ **Application Window Opens** - Should see the main UI
2. ✅ **Drag-and-Drop Works** - Drop an image file
3. ✅ **Filters Load** - Dropdown should show 9 filters
4. ✅ **Processing Works** - Click "Process Image"
5. ✅ **Metrics Display** - Check performance metrics appear
6. ✅ **Save Works** - Save button creates file in `output/`

---

## 📝 Quick Reference Card

| Task | Command |
|------|---------|
| **Run Application** | `mvn javafx:run` |
| **Clean Build** | `mvn clean compile` |
| **Full Build & Run** | `mvn clean javafx:run` |
| **Run with Wrapper** | `./mvnw clean javafx:run` |
| **Package JAR** | `mvn clean package` |
| **Run Tests** | `mvn test` |
| **Generate Docs** | `mvn javadoc:javadoc` |

---

## 🆘 Still Having Issues?

### Check these common problems:

1. **Wrong Java Version**
   ```bash
   java --version
   # Must be 21 or higher
   ```

2. **Wrong Maven Version**
   ```bash
   mvn --version
   # Must be 3.9 or higher
   ```

3. **Corrupted Maven Repository**
   ```bash
   # Delete .m2 repository and rebuild
   rm -rf ~/.m2/repository/org/openjfx
   mvn clean install
   ```

4. **IDE Issues**
   - IntelliJ IDEA: File → Invalidate Caches → Restart
   - Eclipse: Project → Clean → Clean All Projects
   - VS Code: Delete `.vscode` folder and reload

---

## 🎉 Success!

If you see the application window with the drag-and-drop interface, **congratulations!** Your setup is working perfectly.

Now you can:
- Load images via drag-and-drop or file chooser
- Apply 9 different filters
- View real-time performance metrics
- Save processed images to the `output/` directory

---

## 📚 Additional Resources

- [JavaFX Documentation](https://openjfx.io/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [Maven JavaFX Plugin](https://github.com/openjfx/javafx-maven-plugin)
- [Virtual Threads Guide](https://openjdk.org/jeps/444)
- [StructuredTaskScope Guide](https://openjdk.org/jeps/453)

---

**Need more help?** Check the `README.md` for detailed project documentation.

**Made with ❤️ using Java 21 and JavaFX**
