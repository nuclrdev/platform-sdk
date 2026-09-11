package dev.nuclr.platform;

import java.util.Optional;

/**
 * Persistent OS credential storage scoped by the host to a stable plugin ID.
 * Instances of the same plugin share entries; different plugin IDs do not.
 * Namespace separation is not a sandbox for arbitrary plugin code.
 *
 * <p>Operations may block or prompt to unlock the OS store. Call them outside
 * the UI thread. The host owns the lifetime; plugins must not close the store.
 * There is no plaintext settings fallback or secret cache. Keep returned strings
 * only as long as needed and never log or persist them.
 * Keys must be nonblank and contain neither NUL nor {@code |}.
 * @since 5.0.0
 */
public interface NuclrCredentialStore {
	/**
	 * Reads an entry. Empty means confirmed absence, not unavailable storage.
	 * @param key a stable key within this plugin's namespace
	 * @return the saved secret, or empty if it does not exist
	 * @throws NuclrCredentialException if storage is unavailable or access fails
	 */
	Optional<String> get(String key) throws NuclrCredentialException;

	/**
	 * Creates or replaces an entry. Null and empty secrets are rejected; use delete
	 * to remove an entry. Whitespace in a secret is preserved.
	 * @param key a stable key within this plugin's namespace
	 * @param secret the nonempty secret
	 * @throws NuclrCredentialException if storage is unavailable or access fails
	 */
	void set(String key, String secret) throws NuclrCredentialException;

	/**
	 * Deletes an entry; confirmed absence is a successful no-op.
	 * @param key a stable key within this plugin's namespace
	 * @throws NuclrCredentialException if storage is unavailable or access fails
	 */
	void delete(String key) throws NuclrCredentialException;
}
