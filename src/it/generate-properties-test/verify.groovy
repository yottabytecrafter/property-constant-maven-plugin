final String GENERATED_SOURCES_PATH = "target/generated-sources/java"
final String MESSAGE_PACKAGE_PATH = "io/github/yottabytecrafter/message"
final String CONFIG_PACKAGE_PATH = "io/github/yottabytecrafter/config"
final String MESSAGE_FILE_NAME = "MessageProperties.java"
final String CONFIG_FILE_NAME = "ConfigProperties.java"
def datePattern = ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{2}/

// message.properties && MessageProperties.java

File messageFile = new File(basedir, "${GENERATED_SOURCES_PATH}/${MESSAGE_PACKAGE_PATH}/${MESSAGE_FILE_NAME}")
assert messageFile.exists(), "Generated message file does not exist at: " + messageFile.absolutePath

def messageContent = messageFile.text
assert messageContent.contains("package io.github.yottabytecrafter.message")
assert messageContent.contains("@Generated(")
assert messageContent.contains("value = \"io.github.yottabytecrafter.PropertiesGeneratorMojo\"")

def messageContentMatcher = messageContent =~ /date = "(.+)"/
assert messageContentMatcher.find(), "Date not found in messageContent"
def dateString = messageContentMatcher[0][1]
assert dateString =~ datePattern, "Date does not match expected format: ${dateString}"

assert messageContent.contains("comments = \"Generated from message_*.properties") // Updated comment
assert messageContent.contains("public final class MessageProperties")

// Verify APP_TITLE
assert messageContent.contains("public static final java.util.Map<String, String> APP_TITLE;")
assert messageContent.contains("APP_TITLE = new java.util.HashMap<>();")
assert messageContent.contains("APP_TITLE.put(\"en\", \"My Test Application\");")

// Verify APP_WELCOME
assert messageContent.contains("public static final java.util.Map<String, String> APP_WELCOME;")
assert messageContent.contains("APP_WELCOME = new java.util.HashMap<>();")
assert messageContent.contains("APP_WELCOME.put(\"en\", \"Welcome to {0}!\");")

// Verify APP_VERSION
assert messageContent.contains("public static final java.util.Map<String, String> APP_VERSION;")
assert messageContent.contains("APP_VERSION = new java.util.HashMap<>();")
assert messageContent.contains("APP_VERSION.put(\"en\", \"1.0.0\");")

// config.properties && ConfigProperties.java

File configFile = new File(basedir, "${GENERATED_SOURCES_PATH}/${CONFIG_PACKAGE_PATH}/${CONFIG_FILE_NAME}")
assert configFile.exists(), "Generated config file does not exist at: " + configFile.absolutePath

def configContent = configFile.text
assert configContent.contains("package io.github.yottabytecrafter.config")
assert configContent.contains("@Generated(")
assert configContent.contains("value = \"io.github.yottabytecrafter.PropertiesGeneratorMojo\"")

def configContentMatcher = configContent =~ /date = "(.+)"/
assert configContentMatcher.find(), "Date not found in messageContent"
def configDateString = configContentMatcher[0][1]
assert configDateString =~ datePattern, "Date does not match expected format: ${configDateString}"

assert configContent.contains("comments = \"Generated from config_*.properties") // Updated comment
assert configContent.contains("public final class ConfigProperties")

// Verify DB_URL
assert configContent.contains("public static final java.util.Map<String, String> DB_URL;")
assert configContent.contains("DB_URL = new java.util.HashMap<>();")
assert configContent.contains("DB_URL.put(\"en\", \"jdbc:mysql://localhost:3306/mydb\");")

// Verify DB_USER
assert configContent.contains("public static final java.util.Map<String, String> DB_USER;")
assert configContent.contains("DB_USER = new java.util.HashMap<>();")
assert configContent.contains("DB_USER.put(\"en\", \"admin\");")

// Verify DB_PASSWORD
assert configContent.contains("public static final java.util.Map<String, String> DB_PASSWORD;")
assert configContent.contains("DB_PASSWORD = new java.util.HashMap<>();")
assert configContent.contains("DB_PASSWORD.put(\"en\", \"secret\");")