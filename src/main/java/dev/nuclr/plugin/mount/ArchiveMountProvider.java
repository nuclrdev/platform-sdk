package dev.nuclr.plugin.mount;

import java.io.IOException;
import java.nio.file.Path;

import dev.nuclr.plugin.panel.Capabilities;

public interface ArchiveMountProvider {

	boolean canHandle(Path file);

	Path mountAndGetRoot(Path file) throws IOException;

	default Path mountAndGetRoot(Path file, ArchiveMountRequest request) throws IOException {
		return mountAndGetRoot(file);
	}

	Capabilities capabilities();

	default int priority() {
		return 100;
	}
}
