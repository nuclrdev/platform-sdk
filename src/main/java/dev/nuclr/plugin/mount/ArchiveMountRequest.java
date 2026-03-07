package dev.nuclr.plugin.mount;

/**
 * Optional mount-time parameters supplied by the host.
 */
public record ArchiveMountRequest(
		String password) {
}
