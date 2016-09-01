# Change Log
All notable changes to this project will be documented in this file.
This change log follows the conventions of
[keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Changed
- no longer removes soy files from fileset. simplifies soy task
- updated soy task docstring

## [0.1.1] - 2016-08-31
### Added
- this CHANGELOG.md
- comments in `mrmcc3.boot-soy.impl`

### Changed
- render now supports third argument for injected data
- keyword keys in maps passed to render are converted to strings.
kebab style keywords are available as camelCase in templates

## 0.1.0 - 2016-08-29
### Added
- Initial project files
- Initial implementation

[Unreleased]: https://github.com/mrmcc3/boot-soy/compare/0.1.1...HEAD
[0.1.1]: https://github.com/mrmcc3/boot-soy/compare/0.1.0...0.1.1
