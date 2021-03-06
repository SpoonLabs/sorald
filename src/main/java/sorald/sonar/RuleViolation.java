package sorald.sonar;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sorald.Constants;

/** Representation of a violation of some Sonar rule */
public abstract class RuleViolation implements Comparable<RuleViolation> {

    /** @return The line the element that violates the rule starts on. */
    public abstract int getStartLine();

    /** @return The line the element that violates the rule ends on. */
    public abstract int getEndLine();

    /** @return The column the element that violates the rule starts on. */
    public abstract int getStartCol();

    /** @return The column the element that violates the rule ends on. */
    public abstract int getEndCol();

    /** @return Absolute and normalized path to the analyzed file. */
    public abstract Path getAbsolutePath();

    /** @return The name of the check class that generated this warning. */
    public abstract String getCheckName();

    /** @return The key of the violated rule. */
    public abstract String getRuleKey();

    /**
     * @param projectPath The root directory of the current project.
     * @return A violation specifier that is unique relative to the given project path.
     */
    public String relativeSpecifier(Path projectPath) {
        Path absPath = getAbsolutePath();
        Path normalizedProjectPath = projectPath.toAbsolutePath().normalize();
        Path idPath = normalizedProjectPath.relativize(absPath);
        return Stream.of(
                        getRuleKey(),
                        idPath,
                        getStartLine(),
                        getStartCol(),
                        getEndLine(),
                        getEndCol())
                .map(Object::toString)
                .collect(Collectors.joining(Constants.VIOLATION_SPECIFIER_SEP));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuleViolation)) {
            return false;
        }
        var other = (RuleViolation) obj;
        return getAbsolutePath().equals(other.getAbsolutePath())
                && getCheckName().equals(other.getCheckName())
                && getRuleKey().equals(other.getRuleKey())
                && getStartLine() == other.getStartLine()
                && getEndLine() == other.getEndLine()
                && getStartCol() == other.getStartCol()
                && getEndCol() == other.getEndCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getAbsolutePath(),
                getCheckName(),
                getRuleKey(),
                getStartLine(),
                getEndLine(),
                getStartCol(),
                getEndCol());
    }

    @Override
    public int compareTo(RuleViolation violation) {
        int fileCmp = getAbsolutePath().compareTo(violation.getAbsolutePath());
        int ruleCmp = getRuleKey().compareTo(violation.getRuleKey());
        int startLineCmp = Integer.compare(getStartLine(), violation.getStartLine());
        int startColCmp = Integer.compare(getStartCol(), violation.getStartCol());
        int endLineCmp = Integer.compare(getEndCol(), violation.getEndCol());
        int endColCmp = Integer.compare(getEndCol(), violation.getEndCol());

        return Stream.of(fileCmp, ruleCmp, startLineCmp, startColCmp, endLineCmp, endColCmp)
                .filter(i -> i != 0)
                .findFirst()
                .orElse(0);
    }
}
