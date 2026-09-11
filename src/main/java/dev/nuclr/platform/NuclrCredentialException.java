package dev.nuclr.platform;

/** A credential operation failed. Messages never contain the stored secret. */
public final class NuclrCredentialException extends Exception {
	/** Distinguishes unavailable storage from a failed operation. */
	public enum Reason { UNAVAILABLE, ACCESS_FAILED }
	private final Reason reason;

	/** Creates an error with a safe, user-facing message. */
	public NuclrCredentialException(Reason reason, String message) {
		super(message);
		this.reason = java.util.Objects.requireNonNull(reason);
	}

	/** Returns the category of failure. */
	public Reason getReason() { return reason; }
}
