package cz.cvut.kbss.model;

/**
 * Object description for an inclusion of other file.
 *
 * @param file the file path to include
 * @param relativeToChangelogFile whether the path is relative to the root changelog
 * @see <a href="https://docs.liquibase.com/community/reference-guide-5-0/changelog-attributes/include">Liquibase Include Reference</a>
 */
public record FileInclude(String file, boolean relativeToChangelogFile) {
}
