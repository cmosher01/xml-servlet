package nu.mine.mosher.servlet;

import lombok.NonNull;

import java.util.List;

public class UrlPath {
    private final boolean slashLeading;
    private final boolean slashTrailing;
    private final List<String> segments;
    private final String segmentLeaf;
    private final boolean hidden;
    private final boolean invalid;

    public UrlPath(@NonNull final String path) {
        this.slashLeading = path.startsWith(FileUtilities.SLASH);
        this.slashTrailing = path.endsWith(FileUtilities.SLASH);
        this.segments = FileUtilities.segs(path).toList();
        if (this.segments.size() == 0) {
            this.segmentLeaf = "";
        } else {
            this.segmentLeaf = this.segments.get(this.segments.size()-1);
        }
        boolean hidden = false;
        boolean invalid = false;
        for (final String segment : this.segments) {
            if (FileUtilities.hidden(segment)) {
                hidden = true;
            }
            if (FileUtilities.invalidName(segment)) {
                invalid = true;
            }
        }
        this.hidden = hidden;
        this.invalid = invalid;
    }

    public boolean slashLeading() {
        return this.slashLeading;
    }

    public boolean slashTrailing() {
        return this.slashTrailing;
    }

    public String segmentLeaf() {
        return this.segmentLeaf;
    }

    public boolean invalid() {
        return this.hidden || this.invalid;
    }

    @Override
    public String toString() {
        return FileUtilities.buildPath(this.segments, this.slashLeading, this.slashTrailing);
    }

    public String withTrailingSlash() {
        if (isRoot()) {
            return FileUtilities.SLASH;
        }
        return FileUtilities.buildPath(this.segments, this.slashLeading, true);
    }

    public boolean isRoot() {
        return this.segments.isEmpty();
    }

    public String withoutTrailingSlash() {
        if (isRoot()) {
            return "";
        }
        return FileUtilities.buildPath(this.segments, this.slashLeading, false);
    }
}
