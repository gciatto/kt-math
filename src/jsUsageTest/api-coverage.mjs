// Checks that a usage test covers the whole JS API: every export of kt-math.d.mts (each member of each class or
// interface, static ones included, and each function) must be mentioned in the section of the test source about its
// type, i.e. the lines from `// api:<Type>` to the next `// api:` marker (`// api:functions` for the functions).
import { readFileSync } from "node:fs";
import { createRequire } from "node:module";

const ignored = new Set(["KtList", "Pair"]); // Kotlin's, not kt-math's

/** The JS API: a map from each type (and "functions") to the names of its members. */
export function api() {
  const definitions = createRequire(import.meta.url).resolve("kt-math").replace(/\.mjs$/, ".d.mts");
  const types = new Map();
  const add = (type, name) => types.set(type, (types.get(type) ?? new Set()).add(name));
  let type = null;
  for (const line of readFileSync(definitions, "utf8").split("\n")) {
    let match;
    if ((match = line.match(/^export declare function (\w+)/))) {
      add("functions", match[1]);
    } else if ((match = line.match(/^export declare (?:abstract )?(?:interface|class|namespace) (\w+)/))) {
      type = ignored.has(match[1]) ? null : match[1];
    } else if (type && (match = line.match(/^ {4}(?:static )?(?:get |readonly |const |function )?(\w+)\s*[(:<]/))) {
      if (match[1] !== "__doNotUseOrImplementIt") add(type, match[1]);
    }
  }
  return types;
}

/** The members of the JS API not mentioned in their sections of [source], as `Type.member`. */
export function uncovered(source) {
  const sections = new Map();
  for (const part of source.split(/^\s*\/\/ api:/m).slice(1)) {
    const [type, ...lines] = part.split("\n");
    sections.set(type.trim(), (sections.get(type.trim()) ?? "") + lines.join("\n"));
  }
  const missing = [];
  for (const [type, members] of api()) {
    const section = sections.get(type) ?? "";
    for (const member of members) {
      const usage = member === "constructor" ? `new ${type}\\(` : `\\b${member}\\b`;
      if (!new RegExp(usage).test(section)) missing.push(`${type}.${member}`);
    }
  }
  return missing;
}
