package io.github.yottabytecrafter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.ArrayList; // For sources list

// PropertiesGeneratorMojo is in this package

public class PropertiesGeneratorMojoTest {

    private PropertiesGeneratorMojo mojo;
    private Log mockLog;
    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        mojo = new PropertiesGeneratorMojo();
        mockLog = mock(Log.class);
        mojo.setLog(mockLog); // AbstractMojo provides setLog

        // Store original locale
        originalLocale = Locale.getDefault();
        // Specific tests will set the locale as needed
    }

    @AfterEach
    void tearDown() {
        // Restore original locale
        Locale.setDefault(originalLocale);
    }

    @Test
    void testGermanLogMessageForNoSourcesConfigured() throws Exception {
        Locale.setDefault(Locale.GERMAN); // Set locale to German for this test
        // Re-initialize mojo to ensure it picks up the German locale for ResourceBundle loading
        mojo = new PropertiesGeneratorMojo(); 
        mojo.setLog(mockLog);
        
        // sources is null by default in the Mojo if not configured in pom
        // This should trigger the "mojo.noSourcesConfigured" warning.
        mojo.execute(); 

        // Verify that the German "No sources configured" message was logged
        // Key: "mojo.noSourcesConfigured", German: "Keine Quellen konfiguriert"
        verify(mockLog).warn("Keine Quellen konfiguriert");
    }
    
    @Test
    void testGermanLogMessageForMojoGreeting() throws Exception {
        Locale.setDefault(Locale.GERMAN); // Set locale to German for this test
        // Re-initialize mojo to ensure it picks up the German locale
        mojo = new PropertiesGeneratorMojo();
        mojo.setLog(mockLog);
        
        // Set sources to an empty list to prevent processing beyond the greeting and "no sources" messages.
        mojo.setSources(new ArrayList<>()); // Assuming a setter or direct access if needed

        mojo.execute();

        // Verify that the German "Properties Generator Mojo" message was logged
        // Key: "mojo.greeting", German: "Properties Generator Mojo" (same as English)
        verify(mockLog).info("Properties Generator Mojo");
    }

    @Test
    void testEnglishLogMessageForNoSourcesConfigured() throws Exception {
        Locale.setDefault(Locale.ENGLISH); // Set locale to English for this test
        // Re-initialize mojo to ensure it picks up the English locale
        mojo = new PropertiesGeneratorMojo();
        mojo.setLog(mockLog);

        mojo.execute();

        // Verify that the English "No sources configured" message was logged
        // Key: "mojo.noSourcesConfigured", English: "No sources configured"
        verify(mockLog).warn("No sources configured");
    }
    
    @Test
    void testEnglishLogMessageForMojoGreeting() throws Exception {
        Locale.setDefault(Locale.ENGLISH); // Set locale to English for this test
        // Re-initialize mojo to ensure it picks up the English locale
        mojo = new PropertiesGeneratorMojo();
        mojo.setLog(mockLog);

        mojo.setSources(new ArrayList<>()); // Assuming a setter or direct access

        mojo.execute();

        // Verify that the English "Properties Generator Mojo" message was logged
        // Key: "mojo.greeting", English: "Properties Generator Mojo"
        verify(mockLog).info("Properties Generator Mojo");
    }

    // Helper method to allow setting sources if it's package-private or has a setter
    // This depends on the actual visibility of the 'sources' field in PropertiesGeneratorMojo.
    // If 'sources' is private and has no setter, this approach won't work directly
    // and reflection or a change to the Mojo might be needed.
    // For now, this method is a placeholder for how one might set 'sources'.
    // If direct field access is not possible (e.g. mojo.sources = new ArrayList<>()),
    // and there's no setter, the tests for "No sources configured" are the most reliable
    // as they often rely on 'sources' being null or empty by default.
}
