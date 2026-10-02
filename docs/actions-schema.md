# Plugin actions (`actions.json`) — since SDK 6.1.0

A file-panel plugin can offer **actions**: named operations that Commander can run
on the user's behalf — from an AI agent or the command palette — without anyone
clicking through the plugin's UI. "Run a command on this server", "list this
bucket", "show this table's schema".

A plugin describes its actions in a JSON file and runs them through the `act(...)`
method it already has. Everything about *whether* and *when* an action may run —
approvals, permission modes, audit logging, masking secrets before they reach a
model — is Commander's job, not the plugin's.

## Where the description lives

`FilePanelNuclrPlugin.getActionsJson()` returns the JSON. Its default reads
`actions.json` from the **package of the plugin class**, so most plugins only add
files. For a plugin class `dev.nuclr.plugin.core.panel.net.NetFilePanelPlugin`:

```
src/main/resources/dev/nuclr/plugin/core/panel/net/
  actions.json            structure: ids, classes, schemas
  actions/net.exec.md     prose for one action (optional, one file per action)
  actions/net.read.md
```

The location is package-relative on purpose. A plugin's class loader consults
Commander's class loader first, so a root-level `/actions.json` could resolve to
a different file of the same name; nothing else has your plugin's package path.

Keep structure in `actions.json` and prose in the Markdown files: the JSON stays
short and diff-friendly, and the text is easy to edit. Override
`getActionsJson()` only to build the list at runtime.

## Example

```json
{
  "schemaVersion": 1,
  "actions": [
    {
      "id": "net.exec",
      "title": "Run a command on a server",
      "summary": "Runs a shell command on a saved SSH server and returns its output.",
      "doc": "actions/net.exec.md",
      "class": "unrestricted",
      "exposure": ["agent", "palette"],
      "input": {
        "type": "object",
        "required": ["server", "command"],
        "properties": {
          "server":  { "type": "string", "description": "Saved server name or id" },
          "command": { "type": "string", "description": "Shell command to run" },
          "cwd":     { "type": "string", "description": "Working directory; defaults to the login directory" },
          "timeoutSeconds": { "type": "integer", "minimum": 1, "maximum": 3600, "default": 300 }
        }
      },
      "output": {
        "type": "object",
        "properties": {
          "exitCode": { "type": "integer" },
          "stdout":   { "type": "string" },
          "stderr":   { "type": "string" }
        }
      }
    }
  ]
}
```

## Fields

| Field | Required | Meaning |
|---|---|---|
| `schemaVersion` | yes | Always `1` for this format. |
| `actions` | yes | Array of action objects (may be empty). |
| `id` | yes | `namespace.verb`, lowercase `[a-z0-9._-]`, at most 48 characters, unique across all installed plugins. Use your plugin's own namespace (`net.`, `s3.`, `sql.`). |
| `title` | yes | Short imperative phrase, for people and models. |
| `summary` | yes | One sentence: what the action does and returns. |
| `doc` | no | Path to a Markdown file, relative to `actions.json` (e.g. `actions/net.exec.md`): when to use the action, examples, caveats. Agents read it; write it for them as much as for people. |
| `class` | yes | What the action is *able* to do — see below. |
| `exposure` | no | Where the action is offered: `"agent"`, `"palette"`, both. **Omitted or empty means nowhere**: nothing reaches an agent unless listed here. |
| `input` | yes | Schema of the arguments (an `"object"`). |
| `output` | no | Schema of the result passed to `onResult`. |

### `class`

The class is a fixed fact about the action, not a judgement about any one call.
The plugin never tries to work out whether particular input is dangerous: shell
commands, queries and scripts have too many forms for that to be reliable.
Commander combines the class with the user's permission mode, its own rules and
the agent's stated purpose to decide whether to ask first.

| Class | Use it when the action… | Examples |
|---|---|---|
| `read` | cannot change anything anywhere | list a folder, read a file, describe a table |
| `write` | changes something specific that the call itself names | write one file, upload a file, create a folder |
| `unrestricted` | can do anything the account can | run a shell command, run arbitrary SQL |

When unsure, choose the more powerful class.

### Schema subset

`input` and `output` use this subset of JSON Schema, which Commander validates
itself: `type` (`object`, `string`, `integer`, `number`, `boolean`, `array`),
`properties`, `required`, `enum`, `items`, `description`, `default`, `minimum`,
`maximum`, `maxLength`. Give every property a `description` — agents rely on it.

### Reserved argument names

Argument names starting with `_` belong to Commander. It may add some to what an
agent sees — `_purpose`, `_risk`, `_reversible`, the agent's own account of the
call — and they may appear in `data`. Read them for logging if useful; never
declare or require them.

## Running an action

Commander calls the plugin's existing `act(...)`:

| Parameter | Value |
|---|---|
| `other` | `null` |
| `actionType` | the action's `id` |
| `selectedResources` | empty |
| `focusedResource` | `null` |
| `data` | the arguments, already validated against `input` |
| `callback` | reports progress, output, the result and approvals |

Report through the callback:

- `onOutput(chunk)` — text as it is produced (command output), any number of times
- `onResult(map)` — the result, matching `output`, at most once
- `onError(description, exception)` — the action failed; the description is shown to the caller
- `onComplete()` — finished
- `confirm(title, details, diff)` — ask the user to approve a step only the plugin
  can judge with certainty, such as overwriting an existing file (pass the change
  as a unified diff). Returns `true` only on an explicit "yes"; anything else,
  including callers with nobody to ask, is `false`. Never put secrets in it.
- `isCancelled()` — check regularly and stop cleanly

### Headless instances

Commander may create an instance of the plugin only to run its actions: `preinit`
and `init` run, the instance is never shown in a pane, and no resource is open.
Actions must work there. Keep state that panes and actions share — open
connections, caches — outside any single instance (the plugin's bundle has one
class loader, so static state is shared by every instance of its plugins).

## Compatibility

Everything here is additive. Plugins without `actions.json` are unaffected, and
Commander releases older than SDK 6.1.0 never read the file. A plugin whose code
calls `onResult`, `onOutput` or `confirm` should declare
`"platformSdkVersion": "6.1.0"` in `plugin.json`.
