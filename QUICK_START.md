# ⚡ Quick Start Guide

## 🎯 TL;DR - Just Run This

```bash
mvn clean javafx:run
```

That's it! If this doesn't work, read below.

---

## ✅ What Was Fixed

Your project wasn't running because:

1. ❌ **JavaFX modules weren't configured** for `exec:java`
2. ❌ **Java 21 preview features** weren't enabled (`StructuredTaskScope` needs this)
3. ❌ **Wrong Maven plugin** was being used

### What I Fixed:

1. ✅ **Updated `pom.xml`** with proper JavaFX configuration
2. ✅ **Enabled preview features** with `--enable-preview`
3. ✅ **Added `javafx-maven-plugin`** (the correct way to run JavaFX)
4. ✅ **Configured `exec-maven-plugin`** as backup
5. ✅ **Set up proper compiler arguments** for Java 21

---

## 🚀 How to Run (3 Methods)

### **Method 1: JavaFX Plugin (BEST)**
```bash
mvn clean javafx:run
```

### **Method 2: Maven Wrapper**
```bash
./mvnw clean javafx:run          # Linux/Mac
mvnw.cmd clean javafx:run         # Windows
```

### **Method 3: From Your IDE**
- **IntelliJ IDEA**: Right-click `HelloApplication.java` → Run
  - Make sure to add VM options: `--enable-preview --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`

- **Eclipse**: Run As → Java Application
  - Add `--enable-preview` to VM arguments

- **VS Code**: Use the Run button with proper launch.json configuration

---

## 🔍 Verify Your Setup

Run the setup checker:

```bash
# Linux/Mac
./check-setup.sh

# Windows
check-setup.bat
```

This will verify:
- ✅ Java 21+ is installed
- ✅ Maven is installed
- ✅ JAVA_HOME is set
- ✅ All required files exist

---

## ❗ Common Errors & Fixes

### Error: "JavaFX runtime components are missing"

**Problem:** Trying to use `exec:java` without proper module path.

**Fix:** Use `mvn javafx:run` instead.

---

### Error: "Preview features are not enabled"

**Problem:** StructuredTaskScope requires preview features.

**Fix:** Already fixed in the new `pom.xml`. Just run:
```bash
mvn clean javafx:run
```

---

### Error: "Module not found"

**Problem:** Missing JavaFX dependencies or module-info.java issues.

**Fix:** Rebuild from scratch:
```bash
mvn clean install
mvn javafx:run
```

---

### Error: "Cannot find symbol: StructuredTaskScope"

**Problem:** Not using Java 21 or preview not enabled.

**Fix:**
1. Verify Java version: `java --version` (must be 21+)
2. Clean and rebuild: `mvn clean compile`

---

## 📦 What the New pom.xml Includes

1. **Java 21 Configuration**
   ```xml
   <maven.compiler.release>21</maven.compiler.release>
   ```

2. **Preview Features Enabled**
   ```xml
   <compilerArgs>
       <arg>--enable-preview</arg>
   </compilerArgs>
   ```

3. **JavaFX Maven Plugin** (Handles all JavaFX complexity)
   ```xml
   <plugin>
       <groupId>org.openjfx</groupId>
       <artifactId>javafx-maven-plugin</artifactId>
       <version>0.0.8</version>
   </plugin>
   ```

4. **Exec Plugin Configured** (Alternative method)
   ```xml
   <plugin>
       <groupId>org.codehaus.mojo</groupId>
       <artifactId>exec-maven-plugin</artifactId>
       <version>3.1.1</version>
   </plugin>
   ```

---

## 🎓 Understanding Why `exec:java` Failed

The command `mvn exec:java` fails for JavaFX modular projects because:

1. **Module Path Issues**: JavaFX needs `--module-path` set
2. **Add Modules Required**: Needs `--add-modules javafx.controls,javafx.fxml`
3. **Preview Features**: StructuredTaskScope requires `--enable-preview`

The **JavaFX Maven Plugin** handles all this automatically!

---

## 💡 Pro Tips

### Faster Development Workflow
```bash
# Skip tests during development
mvn javafx:run -DskipTests

# Compile only (no run)
mvn compile

# Clean everything
mvn clean
```

### Building a JAR
```bash
# Create JAR file
mvn clean package

# JAR will be in: target/imageProcessing-1.0-SNAPSHOT.jar
```

### Debugging
```bash
# Run with debug output
mvn javafx:run -X

# Run with specific Java options
mvn javafx:run -Djavafx.options="--enable-preview"
```

---

## 📚 Project Structure

```
project/
├── pom.xml                          ← UPDATED with all fixes
├── RUN_INSTRUCTIONS.md              ← Detailed guide
├── QUICK_START.md                   ← This file
├── check-setup.sh                   ← Setup verification (Linux/Mac)
├── check-setup.bat                  ← Setup verification (Windows)
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/image/imageprocessing/
│       │       ├── HelloApplication.java
│       │       ├── concurrency/
│       │       ├── filter/
│       │       ├── ui/
│       │       ├── utils/
│       │       └── processor/
│       └── resources/
│           ├── enhanced-image-processing-view.fxml
│           └── test.jpg
└── output/                          ← Processed images saved here
```

---

## 🎉 Success Checklist

When you run `mvn javafx:run`, you should see:

1. ✅ **Maven downloads dependencies** (first time only)
2. ✅ **Compilation succeeds** with no errors
3. ✅ **JavaFX window opens** with the UI
4. ✅ **Drag-and-drop area** is visible
5. ✅ **Filter dropdown** shows 9 filters
6. ✅ **Default test image** loads automatically

---

## 🆘 Still Not Working?

1. **Check Java version**: `java --version` (must be 21+)
2. **Check Maven version**: `mvn --version` (must be 3.9+)
3. **Clean Maven cache**:
   ```bash
   rm -rf ~/.m2/repository/org/openjfx
   mvn clean install
   ```
4. **Check JAVA_HOME**:
   ```bash
   echo $JAVA_HOME          # Linux/Mac
   echo %JAVA_HOME%         # Windows
   ```

---

## 📖 More Information

- **Detailed Instructions**: See `RUN_INSTRUCTIONS.md`
- **Project Documentation**: See `README.md`
- **Source Code**: Browse `src/main/java/`

---

## 🎯 Bottom Line

**Just run this command and you're good to go:**

```bash
mvn clean javafx:run
```

**That's it!** Your JavaFX application with Java 21 concurrency features will start.

---

**Questions?** Check `RUN_INSTRUCTIONS.md` for comprehensive troubleshooting.

**Made with ❤️ using Java 21 and JavaFX**
