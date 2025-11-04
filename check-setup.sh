#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}Environment Setup Checker${NC}"
echo -e "${BLUE}================================${NC}\n"

# Check Java version
echo -e "${YELLOW}Checking Java version...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?\K[0-9]+' | head -1)
    if [ "$JAVA_VERSION" -ge 21 ]; then
        echo -e "${GREEN}✓ Java $JAVA_VERSION detected${NC}"
    else
        echo -e "${RED}✗ Java $JAVA_VERSION detected (Need Java 21+)${NC}"
        exit 1
    fi
else
    echo -e "${RED}✗ Java not found in PATH${NC}"
    exit 1
fi

# Check JAVA_HOME
echo -e "\n${YELLOW}Checking JAVA_HOME...${NC}"
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}✓ JAVA_HOME is set: $JAVA_HOME${NC}"
else
    echo -e "${RED}✗ JAVA_HOME not set${NC}"
    echo -e "${YELLOW}  Set it with: export JAVA_HOME=/path/to/jdk-21${NC}"
fi

# Check Maven
echo -e "\n${YELLOW}Checking Maven...${NC}"
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -v | grep "Apache Maven" | grep -oP '\d+\.\d+\.\d+')
    echo -e "${GREEN}✓ Maven $MVN_VERSION detected${NC}"
else
    echo -e "${RED}✗ Maven not found in PATH${NC}"
    exit 1
fi

# Check if pom.xml exists
echo -e "\n${YELLOW}Checking pom.xml...${NC}"
if [ -f "pom.xml" ]; then
    echo -e "${GREEN}✓ pom.xml found${NC}"
else
    echo -e "${RED}✗ pom.xml not found${NC}"
    exit 1
fi

# Check module-info.java
echo -e "\n${YELLOW}Checking module-info.java...${NC}"
if [ -f "src/main/java/module-info.java" ]; then
    echo -e "${GREEN}✓ module-info.java found${NC}"
else
    echo -e "${RED}✗ module-info.java not found${NC}"
fi

# Check FXML file
echo -e "\n${YELLOW}Checking FXML resources...${NC}"
if [ -f "src/main/resources/enhanced-image-processing-view.fxml" ]; then
    echo -e "${GREEN}✓ enhanced-image-processing-view.fxml found${NC}"
else
    echo -e "${RED}✗ enhanced-image-processing-view.fxml not found${NC}"
fi

# Check test image
echo -e "\n${YELLOW}Checking test image...${NC}"
if [ -f "src/main/resources/test.jpg" ]; then
    echo -e "${GREEN}✓ test.jpg found${NC}"
else
    echo -e "${YELLOW}⚠ test.jpg not found (optional)${NC}"
fi

# Summary
echo -e "\n${BLUE}================================${NC}"
echo -e "${GREEN}✓ All critical checks passed!${NC}"
echo -e "${BLUE}================================${NC}\n"

echo -e "${YELLOW}You can now run the project with:${NC}"
echo -e "${GREEN}  mvn clean javafx:run${NC}\n"

echo -e "${YELLOW}Or with Maven wrapper:${NC}"
echo -e "${GREEN}  ./mvnw clean javafx:run${NC}\n"
