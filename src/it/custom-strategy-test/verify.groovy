import java.io.File

// 'basedir' is injected by the maven-invoker-plugin as a File object
def projectBaseDir = basedir
// The IT pom.xml does not specify an outputDirectory for the plugin, so it should use the default
// from the plugin's pom.xml: ${project.build.directory}/generated-sources/java
// The project.build.directory for an IT project is target/
def generatedFile = new File(projectBaseDir, "target/generated-sources/java/com/example/it/generated/Custom_test.java")

if (!generatedFile.exists()) {
    throw new AssertionError("Expected generated file does not exist: " + generatedFile.getAbsolutePath())
}

// Check file content
String content = generatedFile.getText("UTF-8") // Assuming UTF-8, which is what PropertiesClassGenerator writes

if (!content.contains("public static final String KEY1 = \"value1\";")) {
    throw new AssertionError("Generated file does not contain expected constant KEY1. Content: \n" + content)
}
if (!content.contains("public static final String KEY_TWO = \"value2\";")) {
    throw new AssertionError("Generated file does not contain expected constant KEY_TWO. Content: \n" + content)
}

// Check for class Javadoc (added in previous task)
if (!content.contains("Contains constants generated from the properties file: 'test.properties'.")) {
  throw new AssertionError("Generated file is missing expected class Javadoc with source file name. Content: \n" + content)
}

// Check for constant Javadoc (added in previous task)
if (!content.contains("Constant for property key: 'key1'.")) {
  throw new AssertionError("Generated file is missing expected Javadoc for KEY1. Content: \n" + content)
}
if (!content.contains("Constant for property key: 'key.two'.")) {
  throw new AssertionError("Generated file is missing expected Javadoc for KEY_TWO. Content: \n" + content)
}


println "Custom strategy IT verification successful."
return true
