package io.github.yottabytecrafter.source;

import org.apache.maven.plugins.annotations.Parameter;

public class Source {

    @Parameter
    private String path;

    @Parameter
    private String targetPackage;

    public Source() {
    }

    public Source(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }
}
