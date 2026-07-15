/*

	Copyright 2026 Sergio, Nuclr (https://nuclr.dev)

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

*/
package dev.nuclr.platform.plugin;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A live interactive shell, supplied by a {@link FilePanelNuclrPlugin} from
 * {@link FilePanelNuclrPlugin#openTerminal} and rendered by the commander in
 * its embedded console (Ctrl+O). The session is nothing more than a pair of
 * byte streams plus a lifecycle, so any transport can back it: a local pty, an
 * SSH shell channel, a container exec, a serial line.
 *
 * <p>The commander owns the session once {@code openTerminal} returns it: it
 * pumps {@link #output()} into the terminal, writes keystrokes to
 * {@link #input()}, reports window changes through {@link #resize}, and calls
 * {@link #close()} exactly once when the console is dismissed or the shell
 * exits on its own. Plugins must not close a session they have handed over.
 *
 * <p>Implementations are used from several threads — the reader thread drains
 * {@code output()}, the event dispatch thread writes to {@code input()} and
 * calls {@code resize} — so they must be safe to use concurrently. The streams
 * themselves are read and written by one thread each.
 *
 * <p>A session must own only what it opened. When it rides on a connection the
 * plugin shares elsewhere (for example one SSH session also serving the panel's
 * directory listings), {@link #close()} must tear down just this shell, never
 * the shared connection underneath it.
 */
public interface NuclrTerminalSession extends AutoCloseable {

	/**
	 * Return the stream carrying bytes <em>from</em> the shell, to be rendered
	 * in the terminal. The commander reads this until end-of-stream.
	 *
	 * @return the shell's output stream, never {@code null}
	 */
	InputStream output();

	/**
	 * Return the stream carrying bytes <em>to</em> the shell. The commander
	 * writes the user's keystrokes here and flushes after every write.
	 *
	 * @return the shell's input stream, never {@code null}
	 */
	OutputStream input();

	/**
	 * Return whether the shell is still running. Once this turns {@code false}
	 * the commander tears the console down and restores the file listing.
	 *
	 * @return {@code true} while the shell is alive
	 */
	boolean isAlive();

	/**
	 * Block until the shell terminates and return its exit status.
	 *
	 * @return the exit status, or {@code 0} when the transport does not report one
	 * @throws InterruptedException if the waiting thread is interrupted
	 */
	int waitFor() throws InterruptedException;

	/**
	 * Notify the shell that the terminal window was resized. The default
	 * implementation does nothing, which is fine for transports with no notion
	 * of a window size.
	 *
	 * @param columns the new width in character cells
	 * @param rows    the new height in character cells
	 */
	default void resize(int columns, int rows) {
		// default implementation does nothing, plugins can override if needed
	}

	/**
	 * Return a short label for this session, shown by the terminal widget.
	 *
	 * @return a human-readable name, never {@code null}
	 */
	default String name() {
		return "Terminal";
	}

	/**
	 * Return the directory the shell ended up in, so the panel can follow it
	 * when the console is dismissed. Called once, during {@link #close()}.
	 *
	 * <p>Return {@code null} (the default) when the transport cannot report it.
	 * The commander then falls back to the terminal's window title, which a
	 * shell configured to emit {@code OSC 0} keeps in sync with its working
	 * directory. Whatever is returned is handed back to the plugin as a plain
	 * string through the {@code filepanel.navigate.to.path} action, so it is
	 * the plugin's own path syntax — a remote POSIX path for an SSH panel, a
	 * local path for a local one — and the plugin resolves it into one of its
	 * resources (or ignores it).
	 *
	 * @return the shell's final working directory, or {@code null} if unknown
	 */
	default String finalWorkingDirectory() {
		return null;
	}

	/**
	 * Terminate the shell and release everything this session opened. Called
	 * exactly once by the commander; must be idempotent and must not throw.
	 */
	@Override
	void close();

}
