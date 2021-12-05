package nu.mine.mosher.servlet;

import lombok.*;

import java.net.URI;
import java.util.*;

public record DirectoryEntry(
    String original,
    String display,
    String onlyName,
    List<String> fileTypes,
    URI link,
    boolean isDirectory
) implements Comparable<DirectoryEntry> {
    @NonNull
    @SneakyThrows
    public static DirectoryEntry create(@NonNull final String pathResource) {
        val u = new UrlPath(pathResource);
        val name = u.segmentLeaf();
        val dir = pathResource.endsWith(FileUtilities.SLASH);
        val url = new URI(name + (dir ? "/" : ""));
        val s = Arrays.stream(name.split("\\.", -1)).toList();
        val onlyName = s.get(0);
        val typs = s.subList(1, s.size());
        return new DirectoryEntry(pathResource, name, onlyName, typs, url, dir);
    }

    @NonNull
    @SneakyThrows
    public static DirectoryEntry up() {
        return new DirectoryEntry("../", "[up]", "..", List.of(), new URI("../"), true);
    }

    @Override
    public int compareTo(@NonNull final DirectoryEntry that) {
        return Comparator
            .comparing(DirectoryEntry::isDirectory).reversed()
            .thenComparing(DirectoryEntry::display)
            .compare(this, that);
    }

    @Override
    @NonNull
    public String toString() {
        return display();
    }
}
