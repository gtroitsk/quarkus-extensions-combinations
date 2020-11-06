package quarkus.extensions.combinator.maven;

import java.io.File;
import java.util.Set;

import quarkus.extensions.combinator.Configuration;
import quarkus.extensions.combinator.utils.FileUtils;

public class MavenGenerator extends MavenCommand {

    private static final String QUARKUS_PLUGIN = "io.quarkus:quarkus-maven-plugin:%s:create";
    private static final String PROJECT_GROUP_ID = "projectGroupId";
    private static final String PROJECT_ARTIFACT_ID = "projectArtifactId";
    private static final String PROJECT_VERSION = "projectVersion";
    private static final String PLATFORM_ARTIFACT_ID = "platformArtifactId";

    private static final String EXTENSIONS_PARAM = "extensions";

    private final Set<String> extensions;

    private MavenGenerator(Set<String> extensions) {
        super(generateArtifactId(extensions), targetAsWorkingDirectory());
        this.extensions = extensions;
    }

    public MavenProject generate() {
        FileUtils.clearFileContent(getOutput());
        FileUtils.deleteFolder(projectAsWorkingDirectory());

        runMavenCommand(withQuarkusPlugin(), withProjectGroupId(), withProjectArtifactId(), withProjectVersion(),
                withPlatformArtifactId(), withExtensions());
        return new MavenProject(getArtifactId(), projectAsWorkingDirectory());
    }

    private String withPlatformArtifactId() {
        return withProperty(PLATFORM_ARTIFACT_ID, Configuration.COMBINATION_PLATFORM_ARTIFACT_ID.get());
    }

    private String withProjectVersion() {
        return withProperty(PROJECT_VERSION, Configuration.COMBINATION_PROJECT_VERSION.get());
    }

    private String withProjectArtifactId() {
        return withProperty(PROJECT_ARTIFACT_ID, getArtifactId());
    }

    private String withProjectGroupId() {
        return withProperty(PROJECT_GROUP_ID, Configuration.COMBINATION_PROJECT_GROUP_ID.get());
    }

    private String withExtensions() {
        return withProperty(EXTENSIONS_PARAM, String.join(",", extensions));
    }

    private String withQuarkusPlugin() {
        return String.format(QUARKUS_PLUGIN, Configuration.QUARKUS_VERSION.get());
    }

    public static final MavenGenerator withExtensions(Set<String> extensions) {
        return new MavenGenerator(extensions);
    }

    private static final String generateArtifactId(Set<String> extensions) {
        return String.join("-", extensions);
    }

    private File projectAsWorkingDirectory() {
        return new File("target/" + getArtifactId());
    }

    private static File targetAsWorkingDirectory() {
        return new File("target");
    }
}
