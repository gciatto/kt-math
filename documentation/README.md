# Kt-Math documentation

The website at <https://gciatto.github.io/kt-math/>: [MkDocs Material](https://squidfunk.github.io/mkdocs-material/)
pages (`docs/`, organised per the [Divio system](https://docs.divio.com/documentation-system/): tutorials, how-to
guides, reference, explanation) plus the [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) API reference
of every module, mounted at `/api/`.

```bash
pip install mkdocs-material
../gradlew :documentation:assembleSite   # the whole site, in build/assembledSite
mkdocs serve                             # the pages only, with live reload
```

The code examples are quoted from the usage tests of the repository (`--8<--` markers, see
`pymdownx.snippets` in `mkdocs.yml`), so that CI compiles and runs every one of them: to change an example, change
the test. CI builds the site on every run and publishes it from `master` (`.github/workflows/docs.yml`).
