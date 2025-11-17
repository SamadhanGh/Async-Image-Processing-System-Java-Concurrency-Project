# 🚀 Async Image Processing System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A high-performance, production-ready image processing application showcasing **Java 21's advanced concurrency features** including **Virtual Threads** and **StructuredTaskScope**, combined with a modern **JavaFX** user interface.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [Image Filters](#-image-filters)
- [Performance](#-performance)
- [Project Structure](#-project-structure)
- [Skills Demonstrated](#-skills-demonstrated)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

This project demonstrates **enterprise-level Java development** combining backend concurrency expertise with modern UI design. Built to showcase skills for Java developer roles, it implements parallel image processing using Java 21's cutting-edge concurrency APIs while maintaining clean architecture and professional code standards.

**Perfect for demonstrating:**
- Advanced Java 21 features (Virtual Threads, StructuredTaskScope)
- Concurrent programming and parallel processing
- JavaFX UI development with drag-and-drop
- Clean code architecture and design patterns
- Performance optimization and metrics tracking

---

## ✨ Key Features

### 🖼️ **Advanced Image Processing**
- **9 Professional Filters**: Grayscale, Sepia, Blur, Sharpen, Edge Detection, Brightness (±), Contrast (±)
- **Tile-Based Processing**: Images divided into 50x50 tiles for parallel execution
- **Real-time Progress**: Live status updates and progress indicators

### ⚡ **Java 21 Concurrency**
- **Virtual Threads**: Lightweight threads for massive concurrency
- **StructuredTaskScope**: Structured concurrency for reliable parallel execution
- **Async Processing**: Non-blocking UI with background image processing
- **Multi-Image Support**: Process multiple images concurrently

### 🎨 **Modern JavaFX UI**
- **Drag-and-Drop**: Intuitive image loading by dropping files
- **Side-by-Side View**: Compare original and processed images
- **Real-time Metrics**: Performance data displayed during processing
- **Activity Log**: Detailed operation logging with timestamps
- **Responsive Design**: Clean, professional interface with visual feedback

### 📊 **Performance Metrics**
- **Processing Time**: Millisecond-accurate timing
- **Thread Usage**: Track concurrent thread utilization
- **Memory Monitoring**: Real-time memory footprint tracking
- **Tile Progress**: Live count of processed image tiles

### 💾 **Export Functionality**
- **Auto-Save**: Processed images saved to `output/` directory
- **Timestamped Files**: Automatic naming with filter and timestamp
- **Multiple Formats**: Support for PNG, JPG, BMP, GIF

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Core language with Virtual Threads & StructuredTaskScope |
| **JavaFX** | 21 | Modern UI framework for desktop applications |
| **Maven** | 3.9+ | Build automation and dependency management |
| **AWT** | Built-in | Image manipulation and pixel processing |

---

## 🏗️ Architecture

### **Package Structure**

```
com.image.imageprocessing
│
├── 📦 concurrency/          # Async processing engine
│   └── AsyncImageProcessor  # StructuredTaskScope implementation
│
├── 📦 filter/               # Image filter implementations
│   ├── ImageFilter          # Filter interface
│   ├── GreyScaleFilter      # Grayscale conversion
│   ├── SepiaFilter          # Sepia tone effect
│   ├── BlurFilter           # Box blur algorithm
│   ├── SharpenFilter        # Edge sharpening
│   ├── EdgeDetectionFilter  # Sobel edge detection
│   ├── BrightnessFilter     # Brightness adjustment
│   └── ContrastFilter       # Contrast adjustment
│
├── 📦 ui/                   # JavaFX UI controllers
│   └── EnhancedImageProcessingController
│
├── 📦 utils/                # Utility classes
│   ├── PerformanceMetrics   # Metrics tracking (thread-safe)
│   └── ImageIOUtil          # File I/O operations
│
└── 📦 processor/            # Legacy processor (for reference)
    └── ImageProcessor       # Virtual thread-based processor
```

### **Design Patterns**
- **Strategy Pattern**: Interchangeable filter implementations
- **Factory Pattern**: Filter selection based on user choice
- **Observer Pattern**: UI updates via JavaFX properties

---

## 🚀 Getting Started

### **Prerequisites**

- **Java 21** or higher ([Download](https://openjdk.org/projects/jdk/21/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Git** (optional, for cloning)

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/SamadhanGh/Async-Image-Processing-System-Java-Concurrency-Project.git
   cd Async-Image-Processing-System-Java-Concurrency-Project
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

   Or using the Maven wrapper:
   ```bash
   chmod +x mvnw
   ./mvnw clean javafx:run
   ```

---

## 💡 Usage

### **Basic Workflow**

1. **Load Image**
   - Click "📁 Select Image" button, OR
   - Drag and drop an image file onto the interface
   - Supported formats: JPG, PNG, BMP, GIF

2. **Select Filter**
   - Choose from 9 available filters in the dropdown
   - Each filter offers unique image transformations

3. **Process Image**
   - Click "⚙ Process Image" button
   - Watch real-time progress and metrics
   - View results side-by-side with original

4. **Save Result**
   - Click "💾 Save Result" button
   - Image automatically saved to `output/` directory
   - Filename format: `{filter}_{timestamp}.png`

### **Example Commands**

```bash
# Clean build
mvn clean package

# Run tests (if implemented)
mvn test

# Generate JavaDoc
mvn javadoc:javadoc

# Run with specific Java version
JAVA_HOME=/path/to/jdk21 mvn javafx:run
```

---

## 🎨 Image Filters

| Filter | Description | Use Case |
|--------|-------------|----------|
| **Grayscale** | Converts to black & white using luminosity method | Classic B&W photography |
| **Sepia** | Vintage warm brown tone effect | Old photograph aesthetic |
| **Blur** | Box blur with 2-pixel radius | Soft focus, background blur |
| **Sharpen** | 3x3 convolution kernel enhancement | Detail enhancement |
| **Edge Detection** | Sobel operator edge highlighting | Object detection, analysis |
| **Brightness (+50)** | Increase image brightness | Lighten dark images |
| **Brightness (-50)** | Decrease image brightness | Darken overexposed images |
| **Contrast (High)** | 1.5x contrast enhancement | Dramatic effect |
| **Contrast (Low)** | 0.5x contrast reduction | Soft, muted tones |

---

## 📈 Performance

### **Concurrency Model**

- **Virtual Threads**: Thousands of lightweight threads per image
- **Tile Processing**: 50x50 pixel tiles processed in parallel
- **StructuredTaskScope**: Guarantees all tiles complete or fail together
- **Non-blocking UI**: Main JavaFX thread never blocked

### **Benchmark Results** (Example)

| Image Size | Tiles | Processing Time | Threads Used | Memory |
|------------|-------|-----------------|--------------|--------|
| 1920x1080 | 924 | ~150-300ms | 12-16 | 45-60 MB |
| 3840x2160 | 3696 | ~500-800ms | 12-16 | 120-150 MB |
| 640x480 | 130 | ~50-100ms | 8-12 | 20-30 MB |

*Results vary based on filter complexity and system hardware*

### **Key Metrics Tracked**

- ⏱️ **Processing Time**: End-to-end execution in milliseconds
- 🧵 **Thread Count**: Concurrent threads utilized
- 📊 **Tile Progress**: Completed tiles vs. total tiles
- 💾 **Memory Usage**: RAM consumption in MB

---

## 📁 Project Structure

```
Async-Image-Processing-System/
│
├── src/
│   ├── main/
│   │   ├── java/com/image/imageprocessing/
│   │   │   ├── HelloApplication.java           # Main entry point
│   │   │   ├── concurrency/
│   │   │   │   └── AsyncImageProcessor.java
│   │   │   ├── filter/
│   │   │   │   ├── ImageFilter.java
│   │   │   │   ├── GreyScaleFilter.java
│   │   │   │   ├── SepiaFilter.java
│   │   │   │   ├── BlurFilter.java
│   │   │   │   ├── SharpenFilter.java
│   │   │   │   ├── EdgeDetectionFilter.java
│   │   │   │   ├── BrightnessFilter.java
│   │   │   │   └── ContrastFilter.java
│   │   │   ├── ui/
│   │   │   │   └── EnhancedImageProcessingController.java
│   │   │   ├── utils/
│   │   │   │   ├── PerformanceMetrics.java
│   │   │   │   └── ImageIOUtil.java
│   │   │   └── processor/
│   │   │       └── ImageProcessor.java
│   │   └── resources/
│   │       ├── enhanced-image-processing-view.fxml
│   │       └── test.jpg
│   └── test/
│       └── java/                               # Unit tests (future)
│
├── output/                                     # Auto-generated saved images
├── pom.xml                                     # Maven configuration
├── README.md                                   # This file
└── LICENSE                                     # MIT License

```

---

## 🎓 Skills Demonstrated

This project showcases enterprise-level Java development skills highly valued in the industry:

### **Backend Engineering**
✅ **Java 21 Advanced Features**: Virtual Threads, StructuredTaskScope
✅ **Concurrent Programming**: Parallel processing, thread management
✅ **Design Patterns**: Strategy, Factory, Observer patterns
✅ **Clean Architecture**: Modular, maintainable code structure
✅ **Error Handling**: Comprehensive exception management
✅ **Performance Optimization**: Efficient algorithms and resource management

### **Software Engineering**
✅ **SOLID Principles**: Single Responsibility, Open/Closed, etc.
✅ **JavaDoc Documentation**: Professional API documentation
✅ **Build Automation**: Maven project management
✅ **Code Organization**: Clear package structure and naming
✅ **Version Control**: Git-ready project structure

### **UI Development**
✅ **JavaFX**: Modern desktop UI framework
✅ **Event-Driven Programming**: User interaction handling
✅ **Responsive Design**: Dynamic UI updates
✅ **UX Design**: Intuitive drag-and-drop interface

### **Problem Solving**
✅ **Algorithm Implementation**: Image processing algorithms
✅ **Performance Metrics**: Real-time monitoring and logging
✅ **Resource Management**: Memory and thread efficiency
✅ **Edge Case Handling**: Robust error scenarios

---

## 🔮 Future Enhancements

### **Phase 1: Advanced Features**
- [ ] **Batch Processing**: Process multiple images simultaneously
- [ ] **Custom Filters**: User-defined filter creation
- [ ] **Filter Chaining**: Combine multiple filters in sequence
- [ ] **Undo/Redo**: Operation history management
- [ ] **Image Comparison**: Before/after slider view

### **Phase 2: Backend Integration**
- [ ] **REST API**: Spring Boot backend for remote processing
- [ ] **Database Integration**: Store processing history
- [ ] **User Authentication**: Multi-user support
- [ ] **Cloud Storage**: AWS S3 / Azure Blob integration
- [ ] **Microservices**: Containerized filter services

### **Phase 3: DevOps & Deployment**
- [ ] **Docker Support**: Containerized deployment
- [ ] **CI/CD Pipeline**: GitHub Actions automation
- [ ] **Monitoring**: Prometheus + Grafana integration
- [ ] **Load Testing**: JMeter performance tests
- [ ] **Native Packaging**: GraalVM native image compilation

### **Phase 4: ML/AI Integration**
- [ ] **AI Filters**: TensorFlow-based style transfer
- [ ] **Object Detection**: YOLO integration
- [ ] **Image Enhancement**: Super-resolution upscaling
- [ ] **Face Detection**: OpenCV integration

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### **Coding Standards**
- Follow Java naming conventions
- Add JavaDoc for public methods
- Write clean, readable code
- Include comments for complex logic
- Update README for new features

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

🙏 Acknowledgments

Java 21 Documentation for Virtual Threads and StructuredTaskScope

JavaFX Community for UI design inspiration

Stack Overflow community for problem-solving support

Open-source contributors for image processing algorithms

Lovepreet Singh — Helpful insights and explanations from his video:
https://youtu.be/STLD89tMFjc?si=umhrNnnQwL9diRg7
---

## 📸 Screenshots

### Main Interface
*Drag-and-drop interface with side-by-side image comparison*

![Main Interface](/home/samadhan/Downloads/image1.png)

### Processing in Action
*Real-time metrics and progress indicators*

![Processing](https://via.placeholder.com/1200x600/27ae60/ffffff?text=Processing+Screenshot)

### Filter Examples
*Before and after comparison of various filters*

![Filters](https://via.placeholder.com/1200x600/3498db/ffffff?text=Filter+Comparison+Screenshot)

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

**Made with ❤️ using Java 21 and JavaFX**

[⬆ Back to Top](#-async-image-processing-system)

</div>
